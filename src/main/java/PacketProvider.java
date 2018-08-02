import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("all")
class PacketProvider {

    private static final Logger logger = LogManager.getLogger(PacketProvider.class);

    /**
     * 把16进制字符串转换成字节数组
     *
     * @param hex 16进制字符串
     * @return byte array
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    // build a empty message
    static byte[] bindingRequest() {

        ByteBuffer buffer = ByteBuffer.allocate(20);

        // message type
        buffer.put(hexStringToByte(MsgType.Binding_Request.hex));

        // message len = 0
        buffer.put(new byte[]{0, 0});

        // message id
        BigInteger bigInteger = new BigInteger(127, new Random());
        buffer.put(bigInteger.toByteArray());
        buffer.position(0);

        return buffer.array();
    }

    static byte[] bindingChangeRequest(boolean changeIp) {
        int len_head = Len.MSG_TYPE.len + Len.MSG_LEN.len       // 2 + 2
                + Len.MSG_ID.len;                               // 16
        int len_attribute = Len.ATB_TYPE.len + Len.ATB_LEN.len  // 2 + 2
                + Len.CHANGE_LEN.len;                           // 4
        int len = len_head + len_attribute;
        ByteBuffer buffer = ByteBuffer.allocate(len);
        ArrayUtils.addAll(new byte[2], new byte[2]);
        // message type
        buffer.put(hexStringToByte(MsgType.Binding_Request.hex));
        // message len = 0
        buffer.put(new byte[]{0, (byte) len_attribute});
        // message id
        BigInteger bigInteger = new BigInteger(127, new Random());
        buffer.put(bigInteger.toByteArray());
        buffer.position(20);
        if (changeIp) {
            buffer.put(new byte[]{0, 3, 0, 4, 0, 0, 0, 6});
        } else {
            buffer.put(new byte[]{0, 3, 0, 4, 0, 0, 0, 2});
        }

        buffer.position(0);
        return buffer.array();
    }


    static Map<String, Address> parse(ByteBuffer buffer) throws UnknownHostException {

        Map<String, Address> map = new HashMap<>();

        byte[] bytes = new byte[Len.MSG_TYPE.len];
        buffer.get(bytes);
        String hex = Hex.encodeHexString(bytes);
        MsgType type = MsgType.getTyeByString(hex);

        bytes = new byte[Len.MSG_LEN.len];
        int mesLen = buffer.getShort();
//        Log.p("message len: " + mesLen);

        bytes = new byte[Len.MSG_ID.len];
        buffer.get(bytes);
//        Log.p("message id: " + Hex.encodeHexString(bytes));

        while (buffer.remaining() > 0) {

//            Log.p("");

            bytes = new byte[Len.ATB_TYPE.len];
            buffer.get(bytes);
            hex = Hex.encodeHexString(bytes);
            AttributesType aType = AttributesType.getTyeByString(hex);

            if (aType != null) {
//                Log.p("attribute : " + aType.name());
            } else {
//                Log.p("attribute : " + hex);
            }

            bytes = new byte[Len.ATB_LEN.len];
            int len = buffer.getShort();
//            Log.p("attribute len " + len);

            if (aType == AttributesType.MAPPED_ADDRESS
                    || aType == AttributesType.SOURCE_ADDRESS
                    || aType == AttributesType.CHANGED_ADDRESS) {

                Address address = new Address();

                bytes = new byte[Len.FAMILY_LEN.len];
                buffer.get(bytes);
//                Log.p("Family:" + Hex.encodeHexString(bytes));

                bytes = new byte[Len.PORT_LEN.len];
                buffer.get(bytes);
                address.port = Integer.parseInt(Hex.encodeHexString(bytes), 16);


                bytes = new byte[len - Len.FAMILY_LEN.len - Len.PORT_LEN.len];
                buffer.get(bytes);
                InetAddress ip = InetAddress.getByAddress(bytes);
                address.ip = ip.toString().replace("/", "");

                map.put(aType.name(), address);

            } else if (aType == AttributesType.SERVER) {
                bytes = new byte[len];
                buffer.get(bytes);
//                Log.p("Server version : " + new String(bytes));
            } else {
                bytes = new byte[len];
                buffer.get(bytes);
//                Log.p("unknow attribute : " + new String(bytes));
            }
        }

        return map;
    }
}
