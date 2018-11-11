package tp2;

public class BitStuffing {
    public static final int ONE_SEQUENCE = 5;

    /**
     * Apply bit stuffing to the given vecotr, returning a new vector with stuffed
     * bits.
     *
     * @param src
     * @return
     */
    public static BitVector encode(BitVector src) {
        BitVector dst = new BitVector();
        int ones = 0;
        for (boolean bit : src) {
            // If the bit is set, increase the count.
            // If it's not set, reset it.
            if (bit) {
                ones++;
            } else {
                ones = 0;
            }

            dst.push(bit);

            // If the 1 count hits ONE_SEQUENCE, we insert a 0 and reset the counter
            if (ones == ONE_SEQUENCE) {
                ones = 0;
                dst.push(false);
            }
        }
        return dst;
    }

    /**
     * Remove bit stuffing from the given vector, returning a new vector without
     * stuffed bits.
     *
     * @param src
     * @return
     */
    public static BitVector decode(BitVector src) {
        BitVector dst = new BitVector();
        int ones = 0;
        for (boolean bit : src) {
            // If the 1 count hits ONE_SEQUENCE, we skip this bit and do not put it in the
            // result vector. We also reset the counter.
            if (ones == ONE_SEQUENCE && !bit) {
                ones = 0;
            } else {
                // The bit isn't a stuffed bit so we copy it to the result.
                dst.push(bit);

                // If the bit is set we increase the count.
                // If the bit is not set, we reset the count.
                if (bit) {
                    ones++;
                } else {
                    ones = 0;
                }
            }
        }
        return dst;
    }
}
