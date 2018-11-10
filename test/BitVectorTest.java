import static org.junit.Assert.assertEquals;
import org.junit.Test;

import tp2.BitVector;

public class BitVectorTest {

    @Test
    public void testToBitString() {
        BitVector bv1 = new BitVector();
        bv1.push(true, false, false, true);

        BitVector bv2 = BitVector.fromBitString("1001");

        assertEquals(bv1, bv2);
    }

    @Test
    public void testBytes() {
        BitVector bv1 = new BitVector();
        bv1.push(true, false, false, true);

        BitVector bv2 = BitVector.fromBytes(bv1.toBytes());
        bv2.truncate(bv1.length()); // needed because we lose the length when converting to bytes, we get padding

        assertEquals(bv1, bv2);
    }

}
