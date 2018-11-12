package tp2;

import java.util.ArrayList;
import java.lang.Math;
import java.lang.Iterable;
import java.util.Iterator;

public class BitVector implements Iterable<Boolean> {

    private ArrayList<Boolean> bits;

    public BitVector() {
        bits = new ArrayList<>();
    }

    public BitVector(int capacity) {
        bits = new ArrayList<>(capacity);
    }

    /**
     * @return Number of bits in this vector.
     */
    public int length() {
        return bits.size();
    }

    /**
     * Push a single bit into this vector.
     *
     * @param bit
     */
    public void push(boolean bit) {
        bits.add(bit);
    }

    public void push(boolean bit, int times) {
        for (int i = 0; i < times; i++) {
            push(bit);
        }
    }

    /**
     * Push an array of bits into this vector.
     *
     * @param bits
     */
    public void push(boolean... bits) {
        for (boolean bit : bits) {
            push(bit);
        }
    }

    /**
     * Push every bit from the given BitVector into this vector.
     *
     * @param bits
     */
    public void push(BitVector bits) {
        for (boolean bit : bits) {
            push(bit);
        }
    }

    /**
     * Push every bit for each byte into this vector.
     *
     * @param bytes
     */
    public void push(byte... bytes) {
        push(BitVector.fromBytes(bytes));
    }

    /**
     * Push every bit for each byte into this vector.
     *
     * @param buf
     */
    public void push(Buffer buf) {
        push(BitVector.fromBuffer(buf));
    }

    /**
     * Get the bit at the given position.
     *
     * @param index
     * @return
     */
    public boolean get(int index) {
        return bits.get(index);
    }

    /**
     * Set the bit at the given position.
     *
     * @param index
     * @param bit
     */
    public void set(int index, boolean bit) {
        bits.set(index, bit);
    }

    /**
     * Truncate the vector to the given length, dropping the extra bits if any.
     *
     * @param length
     */
    public void truncate(int length) {
        bits.subList(length, bits.size()).clear();
    }

    /**
     * Truncate the vector to a power of 8, dropping extra bits if any.
     *
     * Example: 10 bits, becomes 8 bits.
     */
    public void autoTruncate() {
        truncate(length() - length() % 8);
    }

    /**
     * Pads a vector to it the given number of bits from the left using 0s.
     *
     * Example: 10011 becomes 00010011
     *
     * @return A new vector that is padded.
     */
    public BitVector padLeft(int bits) {
        BitVector copy = new BitVector(bits);
        for (int i = 0; i < bits - length(); i++) {
            copy.push(false);
        }
        copy.push(this);
        return copy;
    }

    /**
     * Create an iterator over the bits in this vector.
     */
    public Iterator<Boolean> iterator() {
        final int size = bits.size();
        return new Iterator<Boolean>() {
            private int count = 0;

            public boolean hasNext() {
                return count < size;
            }

            public Boolean next() {
                return bits.get(count++);
            }
        };
    }

    /**
     * Create a bit vector from a sequence of bytes using part of the sequence.
     *
     * @param buf   a buffer of bytes
     * @param start the start index in the buffer of bytes
     * @param end   the end index in the buffer of bytes
     * @return
     */
    public static BitVector fromBytes(byte[] buf, int start, int end) {
        BitVector bv = new BitVector((end - start) * 8);
        for (int i = start; i < end; i++) {
            for (int j = 0; j < 8; j++) {
                if ((buf[i] & (1 << j)) == (1 << j)) {
                    bv.push(true);
                } else {
                    bv.push(false);
                }
            }
        }
        return bv;
    }

    /**
     * Create a bit vector from a sequence of bytes using the whole sequence.
     *
     * @param buf
     * @return
     */
    public static BitVector fromBytes(byte[] buf) {
        return fromBytes(buf, 0, buf.length);
    }

    /**
     * Create a bit vector from a buffer using part of it.
     *
     * @param buf   a buffer of bytes
     * @param start the start index in the buffer of bytes
     * @param end   the end index in the buffer of bytes
     * @return
     */
    public static BitVector fromBuffer(Buffer buf, int start, int end) {
        BitVector bv = new BitVector((end - start) * 8);
        for (int i = start; i < end; i++) {
            for (int j = 0; j < 8; j++) {
                if ((buf.get(i) & (1 << j)) == (1 << j)) {
                    bv.push(true);
                } else {
                    bv.push(false);
                }
            }
        }
        return bv;
    }

    /**
     * Create a bit vector from a sequence of bytes using the whole sequence.
     *
     * @param buf
     * @return
     */
    public static BitVector fromBuffer(Buffer buf) {
        return fromBuffer(buf, 0, buf.length());
    }

    /**
     * Convert this bit vector to a sequence of bytes. If the length of the vector
     * is not a multiple of 8, the last byte will get automatic padding (0s) in the
     * high bits.
     *
     * @return
     */
    public byte[] toBytes() {
        int bufsize = bits.size() / 8;
        if (bits.size() % 8 != 0)
            bufsize++;
        byte[] buf = new byte[bufsize];
        for (int i = 0; i < bits.size(); i++) {
            boolean bit = get(i);
            if (bit) {
                int byteIndex = i / 8;
                int byteOffset = i % 8;
                // System.out.println(byteIndex + " " + byteOffset);
                buf[byteIndex] |= (1 << byteOffset);
            }
        }
        return buf;
    }

    public Buffer toBuffer() {
        return new Buffer(toBytes());
    }

    /**
     * Create a vector from a String filled with 0s and 1s.
     *
     * @param bitString
     * @return
     */
    public static BitVector fromBitString(String bitString) {
        BitVector bv = new BitVector(bitString.length());
        for (int i = 0; i < bitString.length(); i++) {
            char c = bitString.charAt(i);
            if (c == '0') {
                bv.push(false);
            } else if (c == '1') {
                bv.push(true);
            } else {
                throw new IllegalArgumentException("bit string contains invalid characters");
            }
        }
        return bv;
    }

    /**
     * Convert this vector to a String filled with 0s and 1s.
     *
     * @return
     */
    public String toBitString() {
        StringBuilder sb = new StringBuilder();
        for (Boolean bit : this) {
            sb.append(bit ? "1" : "0");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof BitVector) {
            BitVector other = (BitVector) o;
            if (length() != other.length()) {
                return false;
            }
            for (int i = 0; i < length(); i++) {
                if (get(i) != other.get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("BitVector(%s)", toBitString());
    }
}
