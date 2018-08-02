public class Address {

    String ip = "";
    int port = 0;

    @SuppressWarnings("unused")
    public boolean equals(Address obj) {
        return ip.equals(obj.ip) && port == obj.port;
    }

    @Override
    public String toString() {
        return " [" +
                " IP = " + ip + '\'' +
                ", port = " + port +
                ']';
    }
}
