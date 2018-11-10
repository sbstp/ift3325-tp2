package tp2;

public class Frame {
    // 100 octets maximum de données par trames
    public static final int MTU = 100;

    public static final byte FRAME_FLAG = Byte.parseByte("01111110", 2);

    public static final byte TYPE_INFO = (byte) 'I';
    public static final byte TYPE_CONNECTION = (byte) 'C';
    public static final byte TYPE_ACKNOLEDGE = (byte) 'A';
    public static final byte TYPE_REJECT = (byte) 'R';
    public static final byte TYPE_END = (byte) 'F';
    public static final byte TYPE_PBIT = (byte) 'P';

    public byte flag = FRAME_FLAG;
    public byte type;
    public byte num;
    public byte[] data;
    public short crc;

    private Frame() {
    }

    public static Frame newInfo(byte num, byte[] data) {
        Frame f = new Frame();
        f.type = TYPE_INFO;
        f.num = num;
        f.data = data;
        f.crc = computeCRC(data);
        return f;
    }

    private static short computeCRC(byte[] data) {
        return 0;
    }

}
