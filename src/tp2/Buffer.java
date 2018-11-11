package tp2;

import java.util.ArrayList;

public class Buffer {
    private ArrayList<Byte> buff;

    public Buffer() {
        buff = new ArrayList<>();
    }

    public void put(byte b) {
        buff.add(b);
    }

    public void put(byte[] bs) {
        buff.ensureCapacity(buff.size() + bs.length);
        for (byte b : bs) {
            put(b);
        }
    }

    public int length() {
        return buff.size();
    }

    public byte[] bytes() {
        byte[] buf = new byte[length()];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = buff.get(i);
        }
        return buf;
    }
}
