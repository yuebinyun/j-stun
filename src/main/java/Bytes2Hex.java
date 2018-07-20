public class Bytes2Hex {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    static String p(byte[] bytes) {
        return p(bytes, true);
    }

    static String p(byte[] bytes, boolean split) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        for (int i = 0; i < hexChars.length; ++i) {
            System.out.print(hexChars[i]);
            if (split && i % 8 == 7) {
                System.out.println();
            }
        }
        System.out.println();
        return new String(hexChars);
    }
}
