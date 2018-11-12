package tp2;

public class PolynomialGeneration {

    // The polynomial the algorithm uses
    private static final BitVector POLYNOMIAL = BitVector.fromBitString("10001000000100001");

    public static BitVector polynomialGenerator(byte type, byte num, Buffer data) {
        // RECEIVES: the type, number and data of the frame
        // RETURNS: the CRC

        // Concatenate the type, num, data and the 16 zeroes
        BitVector message = new BitVector(data.length() + 4);
        message.push(type);
        message.push(num);
        message.push(data);
        message.push(Buffer.repeat((byte) 0, 2)); // 16 bits at 0
        // BitVector message = BitVector
        // .fromBitString(type.toBitString() + num.toBitString() + data.toBitString() +
        // "0000000000000000");

        // Executes the Algorithm
        while (message.length() > 17) {
            BitVector temp = new BitVector();
            for (int i = 0; i < POLYNOMIAL.length(); i++) {
                if (!(message.get(i) == POLYNOMIAL.get(i))) {
                    // XOR results in a 1
                    temp.push(true);
                } else if (message.get(i) == POLYNOMIAL.get(i) && temp.length() > 0) {
                    // temp is not empty and the XOR results in a 0
                    // add 0
                    temp.push(false);
                }
            }
            for (int i = POLYNOMIAL.length(); i < message.length(); i++) {
                temp.push(message.get(i));
            }
            message = temp;
        }

        return message;
    }

    public static boolean polynomialVerification(byte type, byte num, Buffer data, BitVector crc) {
        // RECEIVES: the type, num, data and crc of the frame
        // RETURNS: True if the end value is all zeroes, otherwise it returns false

        // Concatenate the type, num, data and the crc
        BitVector message = new BitVector(data.length() + 4);
        message.push(type);
        message.push(num);
        message.push(data);
        message.push(crc);
        // BitVector message = BitVector
        // .fromBitString(type.toBitString() + num.toBitString() + data.toBitString() +
        // crc.toBitString());

        // Executes the Algorithm
        while (message.length() > 17) {
            BitVector temp = new BitVector();
            for (int i = 0; i < POLYNOMIAL.length(); i++) {
                if (!(message.get(i) == POLYNOMIAL.get(i))) {
                    // XOR results in a 1
                    temp.push(true);
                } else if (message.get(i) == POLYNOMIAL.get(i) && temp.length() > 0) {
                    // temp is not empty and the XOR results in a 0
                    // add 0
                    temp.push(false);
                }
            }
            for (int i = POLYNOMIAL.length(); i < message.length(); i++) {
                temp.push(message.get(i));
            }
            message = temp;
        }

        // Verifies if the message has all zeroes
        boolean error = false;
        for (int i = 0; i < message.length(); i++) {
            if (message.get(i)) {
                error = true;
            }
        }

        return !error;
    }
}
