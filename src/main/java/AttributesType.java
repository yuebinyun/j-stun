public enum AttributesType {

    MAPPED_ADDRESS("0001"),
    RESPONSE_ADDRESS("0002"),
    CHANGE_REQUEST("0003"),
    SOURCE_ADDRESS("0004"),
    CHANGED_ADDRESS("0005"),
    USERNAME("0006"),
    PASSWORD("0007"),
    MESSAGE_INTEGRITY("0008"),
    ERROR_CODE("0009"),
    UNKNOWN_ATTRIBUTES("000a"),
    REFLECTED_FROM("000b"),
    SERVER("8022");

    String hex;

    AttributesType(String hex) {
        this.hex = hex;
    }

    public static AttributesType getTyeByString(String hex) {
        for (AttributesType e : AttributesType.values()) {
            if (e.hex.equals(hex)) return e;
        }
        return null;
    }

}
