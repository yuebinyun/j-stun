import org.apache.calcite.util.BitString;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class JStun {


    // Define a static logger variable so that it references the
    // Logger instance named "JStun".
    private static final Logger logger = LogManager.getLogger(JStun.class);

    private static final String[] STUN_SERVERS = {
            "stun.ekiga.net",
            "stun.ideasip.com",
            "stun.voiparound.com",
            "stun.voipbuster.com",
            "stun.voipstunt.com",
            "stun.voxgratia.org",
            "118.178.236.183"
    };

    private static int PORT = 3478;

    public static void main(String[] args) {

        for (String server : STUN_SERVERS) {
            try {
                DatagramSocket socket = new DatagramSocket(3456);
                socket.setSoTimeout(3000);

                logger.trace("Test server : " + server);
                InetAddress address = InetAddress.getByName(server);
//                byte[] bind = PacketProvider.bindingRequest();
                byte[] bind = PacketProvider.bindingChangeRequest();

                DatagramPacket request = new DatagramPacket(bind, bind.length, address, PORT);
                DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                socket.send(request);
                socket.receive(response);

                ByteBuffer buffer = ByteBuffer.wrap(response.getData(), 0, response.getLength());
                buffer.position(0);
                PacketProvider.parse(buffer);

                System.exit(1);
            } catch (IOException e) {
                logger.debug(e);
                System.exit(0);
            }
        }
    }
}
