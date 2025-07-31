import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RedisParser {

    private InputStream inputStream;

    public RedisParser(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public Object parse() throws IOException{
            int firstByte = inputStream.read();

            //End Of stream
            if(firstByte == -1)
                return null;

            char type = ((char) firstByte);

            switch (type) {
                case '*':
                    return parseArray();
                case '$':
                    return parseString();
                default:
                    throw new IOException("Unknown Character of " + type);
            }
    }

    private Object parseArray() throws IOException{
        String lengthStr = readLine();
        int length;
        try {
            length = Integer.parseInt(lengthStr);
        }catch (NumberFormatException e){
            throw new IOException("Invalid array length: " + lengthStr, e);
        }

        if(length == -1){
            return null;
        }

        List<Object> array = new ArrayList<>(length);
        for(int i=0;i<length;i++){
            Object parsedObject = parse();
            if(parsedObject != null) {
               // System.out.println("Adding object "+ parsedObject);
                array.add(parsedObject);
            }
        }

        return array;
    }

    private Object parseString() throws IOException {
        String lengthStr = readLine();
        int length;
        try {
            length = Integer.parseInt(lengthStr);
        }catch (NumberFormatException e){
            throw new IOException("Invalid String length: " + lengthStr, e);
        }

        if(length == -1){
            return null;
        }

        byte[] buffer = new byte[length];
        int bytesRead = 0;
        while (bytesRead < length) {
            int result = inputStream.read(buffer, bytesRead, length - bytesRead);
            if (result == -1) {
                throw new IOException("Unexpected end of stream while reading bulk string data.");
            }
            bytesRead += result;
        }

        // Consume the trailing \r\n
        consumeCRLF();

        return new String(buffer,StandardCharsets.UTF_8);
    }

    private void consumeCRLF() throws IOException {
        int r = inputStream.read();
        int n = inputStream.read();
        if (r != '\r' || n != '\n') {
            throw new IOException("Malformed RESP: Expected \\r\\n");
        }
    }

    private String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = inputStream.read()) != -1) {
            if (b == '\r') {
                int nextB = inputStream.read();
                //System.out.println("readLine: Reading next byte after \\r: '" + (char)nextB + "' (int: " + nextB + ")");
                if (nextB == -1) {
                    throw new IOException("Unexpected end of stream after \\r");
                }
                if (nextB == '\n') {
                    return sb.toString();
                } else {
                    // Malformed protocol: \r not followed by \n
                    throw new IOException("Malformed RESP: \\r not followed by \\n");
                }
            } else {
                sb.append((char) b);
            }
        }
        throw new IOException("End of stream reached before \\r\\n");
    }
}
