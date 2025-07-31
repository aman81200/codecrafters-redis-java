public class RedisValue {

    private String value;

    private Long ttl;

    public RedisValue(String value) {
        this.value = value;
        this.ttl = (long) -1;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "RedisValue{" +
                "value='" + value + '\'' +
                ", ttl=" + ttl +
                '}';
    }
}
