import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
  public static void main(String[] args) {
      // You can use print statements as follows for debugging, they'll be visible when running tests.
      System.out.println("Logs from your program will appear here!");

      int port = 6379;
      boolean listening = true;
      ConcurrentHashMap<String,RedisValue> map= new ConcurrentHashMap<>();

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
                          returnOutput(clientSocket, map);
                      }
              ).start();
          }
      } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
      }
  }

  public static void returnOutput(Socket clientSocket, ConcurrentHashMap<String, RedisValue> map) {
      try(clientSocket;
          OutputStream outputStream = clientSocket.getOutputStream();
          InputStream in = clientSocket.getInputStream();
      ) {
          while (true) {
              RedisParser redisParser = new RedisParser(in);
              List<String> array = (List<String>) redisParser.parse();
              if(array != null) {
                  array.forEach(System.out::println);
                  if (array.getFirst().equals("ECHO")) {
                    outputStream.write(RedisEncoder.encodeString(array.get(1)).getBytes());
                  }else if (array.getFirst().equals("SET")){
                    setValueInMap(array,map);
                    outputStream.write("+OK\r\n".getBytes());
                  } else if (array.getFirst().equals("GET")){
                      outputStream.write(RedisEncoder.encodeString(getValueInMap(array,map)).getBytes());
                  }else
                    outputStream.write("+PONG\r\n".getBytes());
              }
          }
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }

  public static void setValueInMap(List<String> array, ConcurrentHashMap<String, RedisValue> map){
      String key = array.get(1);
      String value = array.get(2);
      RedisValue rv = new RedisValue(value);
      if(array.size() > 3){
          String command = array.get(3);
          if(command.equals("px")) {
              Long ttl = Instant.now().toEpochMilli() + Long.parseLong(array.get(4));
              rv.setTtl(ttl);
          }
      }
      System.out.println("Value Set"+ rv);
      map.put(key,rv);
  }

  public static String getValueInMap(List<String> array, ConcurrentHashMap<String, RedisValue> map){
      String key = array.get(1);
      if(map.containsKey(key)){
          RedisValue val = map.get(key);
          System.out.println("Value "+ val.getValue() + " Time Now " + Instant.now().toEpochMilli() + " TTL: " + val.getTtl());
          if(val.getTtl() == -1 || val.getTtl() > Instant.now().toEpochMilli()){
              return val.getValue();
          }else{
              map.remove(key);
          }
      }
      return "$-1\r\n";
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
