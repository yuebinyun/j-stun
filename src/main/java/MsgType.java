
public enum MsgType {

    Binding_Request("0001"),
    Binding_Response("0101"),
    Binding_Error_Response("0111");
    String hex;

    private MsgType(String hex) {
        this.hex = hex;
    }

    public static MsgType getTyeByString(String hex) {
        for (MsgType e : MsgType.values()) {
            if (e.hex.equals(hex)) return e;
        }
        return null;
    }
}
