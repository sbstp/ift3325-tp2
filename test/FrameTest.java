import static org.junit.Assert.*;
import org.junit.Test;

import tp2.*;

public class FrameTest {

    @Test
    public void testFrameBasic() throws CRCValidationException, DeserializationException {
        Frame f1 = Frame.newInfo((byte) 5, new Buffer("Hello world!"));
        Buffer data = f1.serialize();

        Frame f2 = Frame.deserialize(data);

        assertEquals(f1.type, Frame.TYPE_INFO);
        assertEquals(f2.num, 5);
        assertEquals(new String(f2.data.toBytes()), "Hello world!");
    }

    @Test
    public void testControlFrame() throws CRCValidationException, DeserializationException {
        Frame f1 = Frame.newConnection(5);
        Buffer data = f1.serialize();

        Frame f2 = Frame.deserialize(data);

        assertEquals(f1.type, Frame.TYPE_CONNECTION);
        assertEquals(f2.num, 5);
    }

    @Test
    public void testFrameStuffed() throws CRCValidationException, DeserializationException {
        // worst case stuffing payload
        Buffer payload = Buffer.repeat((byte) 0xFF, 20);
        Frame f1 = Frame.newInfo((byte) 5, payload);
        Buffer data = f1.serialize();

        Frame f2 = Frame.deserialize(data);

        assertEquals(BitVector.fromBytes(new byte[] { (byte) 0xFF }).toBitString(), "11111111");
        assertEquals(f1.type, Frame.TYPE_INFO);
        assertEquals(f2.num, 5);
        assertEquals(payload, f2.data);
    }

    @Test(expected = CRCValidationException.class)
    public void testBrokenFrame() throws CRCValidationException, DeserializationException {
        // worst case stuffing payload
        Buffer payload = Buffer.repeat((byte) 0xFF, 20);
        Frame f1 = Frame.newInfo((byte) 5, payload);
        Buffer data = f1.serialize();

        data.set(5, (byte) 8); // mess with the data

        Frame f2 = Frame.deserialize(data);
    }

    @Test(expected = DeserializationException.class)
    public void testBrokenFlagsBegin() throws CRCValidationException, DeserializationException {
        // worst case stuffing payload
        Buffer payload = Buffer.repeat((byte) 0xFF, 20);
        Frame f1 = Frame.newInfo((byte) 5, payload);
        Buffer data = f1.serialize();

        data.set(0, (byte) 0); // should be Frame.FRAME_FLAG

        Frame f2 = Frame.deserialize(data);
    }

    @Test(expected = DeserializationException.class)
    public void testBrokenFlagsEnd() throws CRCValidationException, DeserializationException {
        // worst case stuffing payload
        Buffer payload = Buffer.repeat((byte) 0xFF, 20);
        Frame f1 = Frame.newInfo((byte) 5, payload);
        Buffer data = f1.serialize();

        data.set(0, (byte) 0); // should be Frame.FRAME_FLAG

        Frame f2 = Frame.deserialize(data);
    }

}
