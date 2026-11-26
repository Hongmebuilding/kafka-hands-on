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

      // api_version은 요청의 6~7번째 바이트 (2바이트)
      byte[] api_version = new byte[2];
      System.arraycopy(buffer, 6, api_version, 0, 2);

      OutputStream out = clientSocket.getOutputStream();
      // message_size(4바이트, 값은 아무거나) + correlation_id(요청에서 읽은 값)
      out.write(new byte[] {0, 0, 0, 0});
      out.write(api_version);
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
