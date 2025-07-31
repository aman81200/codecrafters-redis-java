import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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
                          returnOutput(clientSocket);
                      }
              ).start();
          }
      } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
      }
  }

  public static void returnOutput(Socket clientSocket) {
      try(clientSocket;
          OutputStream outputStream = clientSocket.getOutputStream();
          InputStream in = clientSocket.getInputStream();
      ) {
          while (true) {
              RedisParser redisParser = new RedisParser(in);
              List<String> array = (List<String>) redisParser.parse();
//          for(int i=0;i<parsedObject.size();i++){
//              if(parsedObject.get(i) != null)
//              {
//                  System.out.print(parsedObject.get(i) + ", ");
//              }
//          }
            if(array != null) {
                if (array.getFirst().equals("ECHO")) {
                    outputStream.write(RedisEncoder.encodeString(array.get(1)).getBytes());
                } else
                    outputStream.write("+PONG\r\n".getBytes());
            }
          }
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }

  public static void readInput(InputStream in) throws IOException {
      StringBuilder sb = new StringBuilder();
      int b;
      while((b = in.read()) != -1){

          if((char) b == '\n'){
              sb.append("\\n");
          }
          else if((char) b == '\r'){
              sb.append("\\r");
          }else{
              sb.append((char) b);
          }
      }

      String ans = sb.toString();
      System.out.println("String In input stream = " + sb.toString());
  }



}
