/**
 * -0                   1                   2                   3
 * -0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      STUN Message Type        |         Message Length        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |~~~~~~~~~~~~~~~~~~~~Transaction ID~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */

public enum Len {
    MSG_TYPE(2),
    MSG_LEN(2),
    MSG_ID(16),
    ATB_TYPE(2),
    ATB_LEN(2),
    FAMILY_LEN(2),
    PORT_LEN(2),
    IP4_LEN(4);

    int len;

    private Len(int len) {
        this.len = len;
    }
}
