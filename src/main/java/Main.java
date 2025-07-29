import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ClientInfoStatus;

public class Main {
  public static void main(String[] args) {
      // You can use print statements as follows for debugging, they'll be visible when running tests.
      System.out.println("Logs from your program will appear here!");

      int port = 6379;
      boolean listening = true;

      try (ServerSocket serverSocket = new ServerSocket(port)) {

          // Since the tester restarts your program quite often, setting SO_REUSEADDR
          // ensures that we don't run into 'Address already in use' errors
          serverSocket.setReuseAddress(true);

          // Wait for connection from client.
          while (listening) {
              Socket clientSocket = serverSocket.accept();
              System.out.println("New Client Connected");
              new Thread(
                      () -> {
                          returnPong(clientSocket);
                      }
              ).start();
          }
      } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
      }
  }

  public static void returnPong(Socket clientSocket) {
      try(clientSocket;
          OutputStream outputStream = clientSocket.getOutputStream();
          BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
      ) {
          while (true) {
              if(in.readLine() == null){
                  break;
              }

              in.readLine();
              String line = in.readLine();
              System.out.println("Last Line "+ line);
              outputStream.write("+PONG\r\n".getBytes());
              System.out.println("Wrote Pong");
          }
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }
}
