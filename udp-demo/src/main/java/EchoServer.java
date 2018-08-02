import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class EchoServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public EchoServer() throws SocketException {
        socket = new DatagramSocket(9527);
    }

    public void run() {
        System.out.println("Listen....");
        try {
            running = true;
            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength()).trim();
                if (received.equals("end")) running = false;
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }

    public static void main(String[] argv) throws SocketException, InterruptedException {
        EchoServer server = new EchoServer();
        server.start();
        server.join();
        System.out.println("Exit...");
    }
}