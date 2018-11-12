package tp2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Frame {
    // 100 octets maximum de données par trames
    public static final int MTU = 50;

    public static final byte FRAME_FLAG = Byte.parseByte("01111110", 2);

    public static final byte TYPE_INFO = (byte) 'I';
    public static final byte TYPE_CONNECTION = (byte) 'C';
    public static final byte TYPE_ACKNOWLEDGE = (byte) 'A';
    public static final byte TYPE_REJECT = (byte) 'R';
    public static final byte TYPE_END = (byte) 'F';
    public static final byte TYPE_POLL = (byte) 'P';

    public byte type;
    public byte num;
    public Buffer data;
    public BitVector crc;

    private Frame(int type, int num, Buffer data) {
        this.type = (byte) type;
        this.num = (byte) num;
        this.data = data;
        this.crc = PolynomialGeneration.polynomialGenerator((byte) type, (byte) num, data).padLeft(16);
        if (this.crc.length() > 16) {
            throw new IllegalStateException("CRC code is larger than 16 bits: " + this.crc.length());
        }
    }

    private Frame(int type, int num) {
        this(type, num, new Buffer());
    }

    private Frame(int type) {
        this(type, 0);
    }

    private Frame() {
    }

    public static Frame newInfo(int num, Buffer data) {
        return new Frame(TYPE_INFO, num, data);
    }

    public static Frame newConnection(int num) {
        return new Frame(TYPE_CONNECTION, num);
    }

    public static Frame newAcknoledge(int num) {
        return new Frame(TYPE_ACKNOWLEDGE, num);
    }

    public static Frame newReject(int num) {
        return new Frame(TYPE_REJECT, num);
    }

    public static Frame newEnd() {
        return new Frame(TYPE_END);
    }

    public static Frame newPoll() {
        return new Frame(TYPE_POLL);
    }

    /**
     * Convert this frame to bytes.
     */
    public Buffer serialize() {
        Buffer buf = new Buffer();
        buf.push(FRAME_FLAG);
        buf.push(generateStuffedData());
        buf.push(FRAME_FLAG);
        return buf;
    }

    private byte[] generateStuffedData() {
        Buffer buf = new Buffer();
        buf.push(type);
        buf.push(num);
        buf.push(data);
        buf.push(crc.toBytes());

        // System.out.format("buf=%d, data=%d, crc=%s\n", buf.length(), data.length(),
        // crc.toBitString());

        BitVector bv = BitVector.fromBuffer(buf);
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
    public static Frame deserialize(Buffer bytes) throws DeserializationException, CRCValidationException {
        if (bytes.first() != FRAME_FLAG || bytes.last() != FRAME_FLAG) {
            throw new DeserializationException("invalid frame (flags)");
        }

        BitVector stuffedBits = BitVector.fromBuffer(bytes, 1, bytes.length() - 1);
        BitVector decodedBits = BitStuffing.decode(stuffedBits);
        decodedBits.autoTruncate(); // remove between 1 to 7 bits that were added by byte padding if necessary

        if (decodedBits.length() % 8 != 0) {
            throw new DeserializationException("decoding bit stuffed data did not yield a multiple of 8");
        }

        Buffer data = decodedBits.toBuffer();

        Frame f = new Frame();
        f.type = data.get(0);
        f.num = data.get(1);
        f.data = data.slice(2, data.length() - 2);
        f.crc = BitVector.fromBuffer(data, data.length() - 2, data.length());

        // System.out.println("received CRC " + f.crc);

        if (!PolynomialGeneration.polynomialVerification(f.type, f.num, f.data, f.crc)) {
            throw new CRCValidationException();
        }

        return f;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Frame) {
            Frame f = (Frame) o;
            return f.type == type && f.num == num && f.data.equals(data) && f.crc.equals(crc);
        }
        return false;
    }

    @Override
    public String toString() {
        switch (type) {
        case Frame.TYPE_ACKNOWLEDGE:
            return String.format("Frame(ACK, num=%d)", num);
        case Frame.TYPE_CONNECTION:
            return String.format("Frame(CONNECTION)");
        case Frame.TYPE_END:
            return String.format("Frame(END)");
        case Frame.TYPE_INFO:
            return String.format("Frame(INFO, num=%d, data='%s')", num,
                    new String(data.toBytes()).replace("\n", "\\n"));
        case Frame.TYPE_POLL:
            return String.format("Frame(POLL)");
        case Frame.TYPE_REJECT:
            return String.format("Frame(REJECT, num=%d)", num);
        default:
            throw new IllegalStateException();
        }
    }

}
