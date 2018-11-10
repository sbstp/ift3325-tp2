package tp2;

public class Receiver {
    public static void main(String[] args) {
        System.out.println("receiver");
        BitVector b = new BitVector();
        b.push(true);
        b.push(false);
        b.push(false);
        b.push(true);
        b.push(false);
        b.push(false);
        b.push(true);
        b.push(false);
        b.push(false);
        System.out.println(b);
        System.out.println(BitVector.fromBytes(b.toBytes()));
    }
}
