package tp2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Buffer implements Iterable<Byte> {

    private ArrayList<Byte> buff = new ArrayList<>();

    public Buffer() {
    }

    public Buffer(byte[] bs) {
        push(bs);
    }

    public Buffer(byte[] bs, int start, int end) {
        push(bs, start, end);
    }

    public Buffer(String s) {
        try {
            push(s.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Buffer repeat(byte b, int times) {
        Buffer buf = new Buffer();
        buf.reserve(times);
        for (int i = 0; i < times; i++) {
            buf.push(b);
        }
        return buf;
    }

    public static Buffer zeroes(int times) {
        return Buffer.repeat((byte) 0, times);
    }

    public void push(byte b) {
        buff.add(b);
    }

    public void push(byte[] bs) {
        push(bs, 0, bs.length);
    }

    public void push(byte[] bs, int start, int end) {
        reserve(end - start);
        for (int i = start; i < end; i++) {
            push(bs[i]);
        }
    }

    public void push(Buffer other) {
        other.copyInto(this);
    }

    public void set(int index, byte val) {
        buff.set(index, val);
    }

    public byte get(int index) {
        return buff.get(index);
    }

    public byte first() {
        return buff.get(0);
    }

    public byte last() {
        return buff.get(length() - 1);
    }

    public Iterator<Byte> iterator() {
        return buff.iterator();
    }

    public int length() {
        return buff.size();
    }

    public void reserve(int additional) {
        buff.ensureCapacity(buff.size() + additional);
    }

    public byte[] toBytes() {
        byte[] buf = new byte[length()];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = buff.get(i);
        }
        return buf;
    }

    public void copyInto(Buffer target) {
        target.reserve(length());
        for (byte b : this) {
            target.push(b);
        }
    }

    public Buffer slice(int start, int end) {
        Buffer buf = new Buffer();
        buf.reserve(end - start);
        for (int i = start; i < end; i++) {
            buf.push(get(i));
        }
        return buf;
    }

    public Buffer sliceFrom(int start) {
        return slice(start, length());
    }

    public Buffer sliceTo(int end) {
        return slice(0, end);
    }

    public void writeTo(DataOutputStream out) throws IOException {
        for (byte b : this) {
            out.writeByte(b);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof byte[]) {
            byte[] bytes = (byte[]) o;
            if (bytes.length != length()) {
                return false;
            }
            for (int i = 0; i < length(); i++) {
                if (bytes[i] != get(i)) {
                    return false;
                }
            }
            return true;
        } else if (o instanceof Buffer) {
            Buffer buf = (Buffer) o;
            if (buf.length() != length()) {
                return false;
            }
            for (int i = 0; i < length(); i++) {
                if (buf.get(i) != get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
