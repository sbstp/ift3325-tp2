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

    public byte type;
    public byte num;
    public byte[] data;
    public BitVector crc;

    private Frame(byte type, byte num, byte[] data) {
        this.type = type;
        this.num = num;
        this.data = data;
        this.crc = PolynomialGeneration.polynomialGenerator(type, num, data).padLeft();
    }

    private Frame(byte type, byte num) {
        this(type, num, new byte[] {});
    }

    private Frame(byte type) {
        this(type, (byte) 0);
    }

    private Frame() {
    }

    public static Frame newInfo(byte num, byte[] data) {
        return new Frame(TYPE_INFO, num, data);
    }

    public static Frame newConnection() {
        return new Frame(TYPE_CONNECTION);
    }

    public static Frame newAcknoledge(byte num) {
        return new Frame(TYPE_INFO, num);
    }

    public static Frame newReject(byte num) {
        return new Frame(TYPE_REJECT, num);
    }

    public static Frame newEnd() {
        return new Frame(TYPE_END);
    }

    public static Frame newPbit() {
        return new Frame(TYPE_PBIT);
    }

    /**
     * Convert this frame to bytes.
     */
    public byte[] serialize() {
        Buffer buf = new Buffer();
        buf.put(FRAME_FLAG);
        buf.put(getStuffedData());
        buf.put(FRAME_FLAG);
        return buf.bytes();
    }

    private byte[] getStuffedData() {
        Buffer buf = new Buffer();
        buf.put(type);
        buf.put(num);
        buf.put(data);
        buf.put(crc.toBytes());
        // System.out.println("crc size " + crc.toBytes().length);
        // System.out.println("crc:" + crc.toString());

        BitVector bv = BitVector.fromBytes(buf.bytes());
        // The bit stuffing might add a few bits to this vector, which means that the
        // amount of bits won't be dividible by 8 anymore. So padding is created
        // inherently by the call to .toBytes(), the padding must be removed later
        // to retrieve the original message.
        return BitStuffing.encode(bv).toBytes();
    }

    /**
     * Convert bytes to a Frame. The sequence of bytes should start and end with the
     * flag.
     */
    public static Frame deserialize(byte[] b) throws DeserializationException, CRCValidationException {
        if (b[0] != FRAME_FLAG || b[b.length - 1] != FRAME_FLAG) {
            throw new DeserializationException("invalid frame (flags)");
        }

        BitVector stuffedData = BitVector.fromBytes(b, 1, b.length - 1);
        BitVector data = BitStuffing.decode(stuffedData);
        data.autoTruncate(); // remove between 1 to 7 bits that were added by byte padding if necessary

        if (data.length() % 8 != 0) {
            throw new DeserializationException("decoding bit stuffed data did not yield a multiple of 8");
        }

        byte[] bytes = data.toBytes();

        Frame f = new Frame();
        f.type = bytes[0];
        f.num = bytes[1];
        f.data = Arrays.copyOfRange(bytes, 2, bytes.length - 2);
        f.crc = BitVector.fromBytes(bytes, bytes.length - 2, bytes.length);

        // System.out.println("received CRC " + f.crc);

        if (!PolynomialGeneration.polynomialVerification(f.type, f.num, f.data, f.crc)) {
            throw new CRCValidationException();
        }

        return f;
    }

}
