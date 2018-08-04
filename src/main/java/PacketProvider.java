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
        addAll(new byte[2], new byte[2]);
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
        String hex = encodeHexString(bytes);
        MsgType type = MsgType.getTyeByString(hex);

        bytes = new byte[Len.MSG_LEN.len];
        int mesLen = buffer.getShort();

        bytes = new byte[Len.MSG_ID.len];
        buffer.get(bytes);

        while (buffer.remaining() > 0) {

            bytes = new byte[Len.ATB_TYPE.len];
            buffer.get(bytes);
            hex = encodeHexString(bytes);
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

                bytes = new byte[Len.PORT_LEN.len];
                buffer.get(bytes);

                address.port = Integer.parseInt(encodeHexString(bytes), 16);

                bytes = new byte[len - Len.FAMILY_LEN.len - Len.PORT_LEN.len];
                buffer.get(bytes);
                InetAddress ip = InetAddress.getByAddress(bytes);
                address.ip = ip.toString().replace("/", "");

                map.put(aType.name(), address);

            } else if (aType == AttributesType.SERVER) {
                bytes = new byte[len];
                buffer.get(bytes);
            } else {
                bytes = new byte[len];
                buffer.get(bytes);
            }
        }

        return map;
    }

    public static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        return (byte[]) array.clone();
    }

    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char[] DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }
}
