import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class JStun {

    private static final Logger logger = LogManager.getLogger(JStun.class);
    private static DatagramSocket socket;

    private static final String[] STUN_SERVERS = {
            "stun.ekiga.net",
            "stun.voiparound.com",
    };

    @SuppressWarnings("unused")
    private static void iterateUsingEntrySet(Map<String, Address> map) {
        for (Map.Entry<String, Address> entry : map.entrySet()) {
            logger.debug("[" + entry.getKey() + "] :" + entry.getValue());
        }
    }

    @NotNull
    private String getLocalIp() {
        String localIP = "";
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("baidu.com"), 10002);
            localIP = socket.getLocalAddress().getHostAddress();
            socket.disconnect();
        } catch (Exception e) {
            logger.error("[获取本地联网 ip 地址失败]" + e.getMessage());
        }
        return localIP;
    }

    public static void main(String[] args) {

        JStun jStun = new JStun();
        String localIp = jStun.getLocalIp();
        if (localIp.equals("")) {
            System.exit(1);
        } else {
            logger.trace("Local IP = " + localIp);
        }

        try {

            String externalIp = "";

            socket = new DatagramSocket(0);
            socket.setSoTimeout(5000);

            Map<String, Address> map1 = jStun.test(STUN_SERVERS[0], false, false);
            if (map1 == null) {

                System.exit(-1);

            } else if (map1.isEmpty()) {

                logger.warn("[UDP blocked]");
                return;

            } else {
                externalIp = map1.get(AttributesType.MAPPED_ADDRESS.name()).ip;

                logger.warn("[External IP detected: " + externalIp + "]");

                if (externalIp.equals(localIp)) {
                    logger.warn("[No NAT: Check for firewall]");
                } else {
                    logger.warn("[NAT detected: Remember external IP]");
                }
            }

            Map<String, Address> map2 = jStun.test(STUN_SERVERS[0], true, true);
            if (map2 == null) {
                System.exit(-1);
            }


            if (externalIp.equals(localIp)) {

                if (map2.isEmpty()) {
                    logger.warn("[Symmetric Firewall]");
                    return;
                } else {
                    logger.warn("[Open Internet]");
                    return;
                }

            } else {

                if (map2.isEmpty()) {
                    logger.warn("[Do more test...]");
                } else {
                    logger.warn("[Full-cone NAT]");
                    return;
                }
            }

            Map<String, Address> map3 = jStun.test(STUN_SERVERS[1], false, false);
            if (map3 == null) {
                System.exit(-1);
            }

            String externalIP2 = map3.get(AttributesType.MAPPED_ADDRESS.name()).ip;
            if (!externalIp.equals(externalIP2)) {
                logger.warn("[Symmetric NAT]");
                return;
            }

            Map<String, Address> map4 = jStun.test(STUN_SERVERS[1], false, true);
            if (map4 == null) {
                System.exit(-1);
            }

            if (map4.isEmpty()) {
                logger.warn("Restricted port NAT");
            } else {
                logger.warn("Restricted cone NAT");
            }

        } catch (SocketException e) {
            logger.trace("[建立 socket 失败.... 无法检测网络类型]--> " + e.getMessage());
            System.exit(-1);
        }
    }


    @Nullable
    private Map<String, Address> test(String stunServer, boolean changeIP, boolean changePort) {

        logger.trace("[Bing test server : " + stunServer
                + " <chang ip ? " + changeIP
                + "> <change port ? " + changePort + ">]");

        try {
            InetAddress address = InetAddress.getByName(stunServer);

            byte[] bind;
            if (changeIP || changePort) {
                bind = PacketProvider.bindingChangeRequest(changeIP);
            } else {
                bind = PacketProvider.bindingRequest();
            }

            DatagramPacket request = new DatagramPacket(bind, bind.length, address, 3478);
            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
            socket.send(request);
            socket.receive(response);
            ByteBuffer buffer = ByteBuffer.wrap(response.getData(), 0, response.getLength());
            logger.debug("[Receive form " + stunServer + "]");
            return PacketProvider.parse(buffer);
        } catch (SocketTimeoutException e) {
            logger.error("[" + e.getMessage() + "]");
            return new HashMap<>(0);
        } catch (Exception e) {
            logger.error("[程序异常，无法检测 NAT 类型]" + e.getMessage());
            return null;
        }
    }

}
