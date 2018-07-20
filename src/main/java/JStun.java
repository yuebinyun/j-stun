import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class JStun {

    static final String[] STUN_SERVERS = {
            "stun.ekiga.net",
            "stun.ideasip.com",
            "stun.voiparound.com",
            "stun.voipbuster.com",
            "stun.voipstunt.com",
            "stun.voxgratia.org"
    };

    static String IP = "118.178.236.183";
    static int PORT = 3478;

    public static void main(String[] args) {

        for (String server : STUN_SERVERS) {
            try {
                DatagramSocket socket = new DatagramSocket(0);
                socket.setSoTimeout(3000);

                InetAddress address = InetAddress.getByName(server);
                byte[] bind = PacketProvider.bindingRequest();

                DatagramPacket request = new DatagramPacket(bind, bind.length, address, PORT);
                DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                socket.send(request);
                socket.receive(response);

                ByteBuffer buffer = ByteBuffer.wrap(response.getData(), 0, response.getLength());
                buffer.position(0);
                PacketProvider.parse(buffer);

            } catch (IOException e) {
                Log.p(e.toString());
            }


        }
    }
}
