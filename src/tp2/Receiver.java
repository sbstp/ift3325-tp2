package tp2;

import java.io.IOException;

public class Receiver {
    public static void main(String[] args) throws IOException {
        new Receiver().mainLoop();
    }

    private Buffer data = new Buffer();
    private int expected = 0;
    private int windowSize = -1;
    private DataLinkListener listener;

    public Receiver() throws IOException {
        listener = new DataLinkListener(6969);
    }

    public void mainLoop() throws IOException {
        DataLinkStream stream = listener.accept();
        stream.setPrintLog(true);

        while (true) {
            try {
                Frame f = stream.readFrame();

                if (f.type == Frame.TYPE_CONNECTION) {
                    windowSize = f.num;
                } else if (f.type == Frame.TYPE_INFO) {
                    if (f.num != expected) {
                        stream.writeFrame(Frame.newReject(expected));
                    } else {
                        data.push(f.data);
                        expected = (expected + 1) % windowSize;
                        stream.writeFrame(Frame.newAcknoledge(expected));
                    }
                } else if (f.type == Frame.TYPE_END) {
                    break;
                }
            } catch (DeserializationException | CRCValidationException e) {
                // pass
                // System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
