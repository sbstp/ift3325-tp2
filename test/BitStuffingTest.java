import static org.junit.Assert.*;
import org.junit.Test;

import tp2.*;

public class BitStuffingTest {

    @Test
    public void testEncodingShort() {
        BitVector bv1 = BitVector.fromBitString("11111");
        BitVector bv2 = BitStuffing.encode(bv1);

        assertEquals(bv2.toBitString(), "111110");
    }

    @Test
    public void testEncodingLong() {
        BitVector bv1 = BitVector.fromBitString("1111111111");
        BitVector bv2 = BitStuffing.encode(bv1);

        assertEquals(bv2.toBitString(), "111110111110");
    }

    @Test
    public void testEncodingTrailing() {
        BitVector bv1 = BitVector.fromBitString("11111111110101");
        BitVector bv2 = BitStuffing.encode(bv1);

        assertEquals(bv2.toBitString(), "1111101111100101");
    }

    @Test
    public void testEncodingNothing() {
        BitVector bv1 = BitVector.fromBitString("01010101");
        BitVector bv2 = BitStuffing.encode(bv1);

        assertEquals(bv2.toBitString(), "01010101");
    }

    @Test
    public void testDecodingShort() {
        BitVector bv1 = BitVector.fromBitString("111110");
        BitVector bv2 = BitStuffing.decode(bv1);

        assertEquals(bv2.toBitString(), "11111");
    }

    @Test
    public void testDecodingLong() {
        BitVector bv1 = BitVector.fromBitString("111110111110");
        BitVector bv2 = BitStuffing.decode(bv1);

        assertEquals(bv2.toBitString(), "1111111111");
    }

    @Test
    public void testDecodingTrailing() {
        BitVector bv1 = BitVector.fromBitString("1111101111100101");
        BitVector bv2 = BitStuffing.decode(bv1);

        assertEquals(bv2.toBitString(), "11111111110101");
    }

    @Test
    public void testDecodingNothing() {
        BitVector bv1 = BitVector.fromBitString("01010101");
        BitVector bv2 = BitStuffing.decode(bv1);

        assertEquals(bv2.toBitString(), "01010101");
    }

    @Test
    public void testFullLong() {
        byte[] payload = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        BitVector bv1 = BitVector.fromBytes(payload);
        BitVector bv2 = BitStuffing.encode(bv1);
        BitVector bv3 = BitStuffing.decode(bv2);
        // System.out.println(bv1);
        // System.out.println(bv2);
        // System.out.println(bv3);
        assertArrayEquals(payload, bv3.toBytes());
    }

}
