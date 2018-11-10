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

    public int length() {
        return bits.size();
    }

    public void push(boolean bit) {
        bits.add(bit);
    }

    public void push(boolean... bits) {
        for (boolean bit : bits) {
            push(bit);
        }
    }

    public boolean get(int i) {
        return bits.get(i);
    }

    public void truncate(int length) {
        bits.subList(length, bits.size()).clear();
    }

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

    public static BitVector fromBytes(byte[] buf) {
        BitVector bv = new BitVector(buf.length * 8);
        for (int i = 0; i < buf.length; i++) {
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

    public String toBitString() {
        StringBuilder sb = new StringBuilder();
        for (Boolean bit : this) {
            sb.append(bit ? "1" : "0");
        }
        return sb.toString();
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof BitVector) {
            BitVector other = (BitVector) o;
            if (length() != other.length())
                return false;
            for (int i = 0; i < length(); i++) {
                if (get(i) != other.get(i))
                    return false;
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
