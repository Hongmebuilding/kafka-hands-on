import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
    System.err.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);

      clientSocket = serverSocket.accept();

      InputStream in = clientSocket.getInputStream();
      byte[] buffer = new byte[1024];
      in.read(buffer);

      // 요청: message_size(0~3) + api_key(4~5) + api_version(6~7) + correlation_id(8~11)
      short apiVersion = ByteUtils.readInt16(buffer, 6);
      int correlationId = ByteUtils.readInt32(buffer, 8);

      // 지원 범위: 0~4. 아니면 error_code 35 (UNSUPPORTED_VERSION)
      short errorCode = (apiVersion >= 0 && apiVersion <= 4) ? (short) 0 : (short) 35;

      // --- body 조립 ---
      // 00 00 00 13   // message_size: 19
      // ab cd ef 12   // correlation_id
      // 00 00         // error_code: 0
      // 02            // array length (1개 항목 + 1)
      // 00 12         // api_key: 18
      // 00 00         // min_version: 0
      // 00 04         // max_version: 4
      // 00            // TAG_BUFFER (빈 값)
      // 00 00 00 00   // throttle_time_ms: 0
      // 00            // TAG_BUFFER (빈 값)

      ByteArrayOutputStream body = new ByteArrayOutputStream();
      ByteUtils.writeInt16(body, errorCode); // error_code
      ByteUtils.writeInt8(body, 2);          // api_keys 배열 길이 = 실제 1개 + 1
      ByteUtils.writeInt16(body, 18);        //   api_key = 18 (ApiVersions)
      ByteUtils.writeInt16(body, 0);         //   min_version
      ByteUtils.writeInt16(body, 4);         //   max_version
      ByteUtils.writeInt8(body, 0);          //   TAG_BUFFER (empty)
      ByteUtils.writeInt32(body, 0);         // throttle_time_ms
      ByteUtils.writeInt8(body, 0);          // TAG_BUFFER (empty)

      // --- 응답 조립: message_size + header + body ---
      ByteArrayOutputStream response = new ByteArrayOutputStream();
      ByteUtils.writeInt32(response, Integer.BYTES + body.size()); // message_size
      ByteUtils.writeInt32(response, correlationId);               // header: correlation_id
      body.writeTo(response);                                      // body

      OutputStream out = clientSocket.getOutputStream();
      response.writeTo(out);
      out.flush();
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }
}
