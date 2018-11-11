import static org.junit.Assert.*;
import org.junit.Test;

import tp2.*;

public class FrameTest {

    @Test
    public void testFrameBasic() throws CRCValidationException, DeserializationException {
        Frame f1 = Frame.newInfo((byte) 5, "Hello world!".getBytes());
        byte[] data = f1.serialize();

        Frame f2 = Frame.deserialize(data);

        assertEquals(f1.type, Frame.TYPE_INFO);
        assertEquals(f2.num, 5);
        assertEquals(new String(f2.data), "Hello world!");
    }

    @Test
    public void testFrameStuffed() throws CRCValidationException, DeserializationException {
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

    @Test(expected = CRCValidationException.class)
    public void testBrokenFrame() throws CRCValidationException, DeserializationException {
        // worst case stuffing payload
        byte[] payload = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        Frame f1 = Frame.newInfo((byte) 5, payload);
        byte[] data = f1.serialize();

        data[5] = 8; // mess with the data

        Frame f2 = Frame.deserialize(data);
    }

    @Test(expected = DeserializationException.class)
    public void testBrokenFlagsBegin() throws CRCValidationException, DeserializationException {
        // worst case stuffing payload
        byte[] payload = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        Frame f1 = Frame.newInfo((byte) 5, payload);
        byte[] data = f1.serialize();

        data[0] = 0; // should be Frame.FRAME_FLAG

        Frame f2 = Frame.deserialize(data);
    }

    @Test(expected = DeserializationException.class)
    public void testBrokenFlagsEnd() throws CRCValidationException, DeserializationException {
        // worst case stuffing payload
        byte[] payload = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        Frame f1 = Frame.newInfo((byte) 5, payload);
        byte[] data = f1.serialize();

        data[data.length - 1] = 0; // should be Frame.FRAME_FLAG

        Frame f2 = Frame.deserialize(data);
    }

}
