public class RedisEncoder {

    public static String encodeString(String inputString){
        if(inputString == "$-1\n") return inputString;
        StringBuilder sb = new StringBuilder();
        sb.append('$');
        sb.append(inputString.length());
        sb.append("\r\n");
        sb.append(inputString);
        sb.append("\r\n");
        return sb.toString();
    }
}
