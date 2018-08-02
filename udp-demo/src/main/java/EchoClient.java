import java.io.IOException;
import java.net.*;

public class EchoClient {

    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public EchoClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        address = InetAddress.getByName("localhost");
//        address = InetAddress.getByName("118.178.236.183");
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9527);
        socket.send(packet);
        if (msg.equals("end")) {
            return "";
        }
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());
    }

    public void close() {
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        EchoClient client = new EchoClient();
        String echo = client.sendEcho("abc");
        System.out.println(echo);
        echo = client.sendEcho("end");
        System.out.println(echo);
        client.close();
    }
}
