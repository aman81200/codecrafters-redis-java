public class RedisEncoder {

    public static String encodeString(String inputString){
        StringBuilder sb = new StringBuilder();
        sb.append('$');
        sb.append(inputString.length());
        sb.append("\r\n");
        sb.append(inputString);
        sb.append("\r\n");
        return sb.toString();
    }
}
