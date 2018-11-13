package tp2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

public class Receiver {
    public static void main(String[] args) throws IOException {
        Receiver r = new Receiver();
        r.mainLoop();

        try (FileOutputStream out = new FileOutputStream("destination.txt")) {
            out.write(r.data.toBytes());
        }
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

        boolean finished = false;
        Frame lastFrame = null; // keep the last frame in memory to avoid sending the same frame multiple times

        while (!finished) {
            Frame sendFrame = null;
            try {
                Frame f = stream.readFrame();

                if (f.type == Frame.TYPE_CONNECTION) {
                    // ready to receive first frame
                    windowSize = f.num;
                    sendFrame = Frame.newAcknoledge(0);
                } else if (f.type == Frame.TYPE_INFO) {
                    // if frame is not the one we expect, reject
                    if (f.num != expected) {
                        sendFrame = Frame.newReject(expected);
                    } else {
                        // frame is the one we expect, add to buffer and send ready to receive next
                        // frame
                        data.push(f.data);
                        expected = (expected + 1) % windowSize;
                        sendFrame = Frame.newAcknoledge(expected);
                    }
                } else if (f.type == Frame.TYPE_POLL) {
                    lastFrame = null; // forget was the last frame was
                    sendFrame = Frame.newAcknoledge(expected);
                } else if (f.type == Frame.TYPE_END) {
                    // we got the final frame, stop receiving
                    finished = true;
                }
            } catch (DeserializationException | CRCValidationException e) {
                // deserialization/CRC check error, reject the frame
                lastFrame = null;
                sendFrame = Frame.newReject(expected);
            }

            // avoid sending duplicate frames
            if (sendFrame != null && !sendFrame.equals(lastFrame)) {
                stream.writeFrame(sendFrame);
                lastFrame = sendFrame;
            }

        }
    }

    public Buffer getData() {
        return data;
    }

}
