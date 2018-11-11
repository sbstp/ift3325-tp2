import static org.junit.Assert.*;
import org.junit.Test;

import tp2.*;

public class FrameTest {

    @Test
    public void testFrameBasic() {
        Frame f1 = Frame.newInfo((byte) 5, "Hello world!".getBytes());
        byte[] data = f1.serialize();

        Frame f2 = Frame.deserialize(data);

        assertEquals(f1.type, Frame.TYPE_INFO);
        assertEquals(f2.num, 5);
        assertEquals(new String(f2.data), "Hello world!");
    }

    @Test
    public void testFrameStuffed() {
        // worst case stuffing payload
        byte[] payload = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        Frame f1 = Frame.newInfo((byte) 5, payload);
        byte[] data = f1.serialize();

        Frame f2 = Frame.deserialize(data);

        assertEquals(BitVector.fromBytes(new byte[] { (byte) 0xFF }).toBitString(), "11111111");
        assertEquals(f1.type, Frame.TYPE_INFO);
        assertEquals(f2.num, 5);
        assertArrayEquals(payload, f2.data);
    }

}
