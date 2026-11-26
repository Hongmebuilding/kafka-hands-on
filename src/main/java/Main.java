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

      // 요청을 읽어준다 (파싱은 안 해도 됨, 그냥 읽기만)
      InputStream in = clientSocket.getInputStream();
      byte[] buffer = new byte[1024];
      in.read(buffer);

      OutputStream out = clientSocket.getOutputStream();
      out.write(new byte[] {0, 0, 0, 0, 0, 0, 0, 7});
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
