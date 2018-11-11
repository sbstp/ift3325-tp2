package tp2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

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

    public byte[] serialize() {
        Buffer buf = new Buffer();
        buf.put(flag);
        buf.put(getStuffedData());
        buf.put(flag);
        return buf.bytes();
    }

    private byte[] getStuffedData() {
        Buffer buf = new Buffer();
        buf.put(type);
        buf.put(num);
        buf.put(data);
        // TODO: crc
        buf.put((byte) 0);
        buf.put((byte) 0);

        BitVector bv = BitVector.fromBytes(buf.bytes());
        // The bit stuffing might add a few bits to this vector, which means that the
        // amount of bits won't be dividible by 8 anymore. So padding is created
        // inherently by the call to .toBytes(), the padding must be removed later
        // to retrieve the original message.
        return BitStuffing.encode(bv).toBytes();
    }

    public static Frame deserialize(byte[] b) {
        if (b[0] != FRAME_FLAG || b[b.length - 1] != FRAME_FLAG) {
            throw new IllegalArgumentException("invalid frame (flags)");
        }

        BitVector stuffedData = BitVector.fromBytes(b, 1, b.length - 1);
        BitVector data = BitStuffing.decode(stuffedData);
        data.autoTruncate(); // remove between 1 to 7 bits that were added by byte padding if necessary

        if (data.length() % 8 != 0) {
            throw new IllegalArgumentException("decoding bit stuffed data did not yield a multiple of 8");
        }

        byte[] bytes = data.toBytes();

        Frame f = new Frame();
        f.type = bytes[0];
        f.num = bytes[1];
        f.data = Arrays.copyOfRange(bytes, 2, bytes.length - 2);
        // TODO: crc
        // f.crc = bytes[bytes.length - 1];

        return f;
    }

    private static short computeCRC(byte[] data) {
        return 0;
    }

}
