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

      // correlation_id은 요청의 8~11번째 바이트 (4바이트)
      byte[] correlation_id = new byte[4];
      System.arraycopy(buffer, 8, correlation_id, 0, 4);

      // api_version은 요청의 6~7번째 바이트 (2바이트)
      int apiVersion = ((buffer[6] & 0xFF) << 8) | (buffer[7] & 0xFF);

      // 0~4면 지원 가능(errorCode=0), 아니면 UNSUPPORTED_VERSION(35)
      short errorCode = (apiVersion >= 0 && apiVersion <= 4) ? (short) 0 : (short) 35;

      OutputStream out = clientSocket.getOutputStream();
      out.write(new byte[] {0, 0, 0, 0});
      out.write(correlation_id);
      out.write(new byte[] { (byte) (errorCode >> 8), (byte) errorCode }); // error_code
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
