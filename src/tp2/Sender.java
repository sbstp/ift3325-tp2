package tp2;

import java.awt.Window;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.ArrayList;

public class Sender {

    public static void main(String[] args) throws IOException {
        DataLinkStream stream = new DataLinkStream("localhost", 6969);
        stream.setPrintLog(true);
        stream.setTimeout(3000);
        Sender s = new Sender(stream, 5);
        s.send(new Buffer(Files.readAllBytes(Paths.get("source.txt"))));
        s.mainLoop();
    }

    public static class WindowItem {
        public byte num;
        public Buffer data;

        public WindowItem(byte num, Buffer data) {
            this.num = num;
            this.data = data;
        }
    }

    private LinkedList<Buffer> queue = new LinkedList<>();
    private LinkedList<WindowItem> window = new LinkedList<>();
    private int seqNum = 0;
    private DataLinkStream stream;
    private int windowSize;

    public Sender(DataLinkStream stream, int n) throws IOException {
        this.stream = stream;
        this.windowSize = n;
    }

    public void send(Buffer b) {
        for (int start = 0; start < b.length(); start += Frame.MTU) {
            queue.add(b.slice(start, Math.min(start + Frame.MTU, b.length())));
        }
    }

    public void mainLoop() throws IOException {
        stream.writeFrame(Frame.newConnection(windowSize));

        boolean pollRequested = false;
        // loop as long as there is data in the queue or the window
        while (!queue.isEmpty() || !window.isEmpty()) {
            // fill and send the window
            refillAndSendWindow();
            // process replies until the window is empty
            while (!window.isEmpty()) {
                try {
                    Frame f = stream.readFrame();

                    if (f.type == Frame.TYPE_ACKNOWLEDGE) {
                        // ready to receive, clear every frame that was received
                        clearWindow(f.num);
                        if (pollRequested) {
                            // if a poll was requested we must send the window from where the receiver is at
                            pollRequested = false;
                            refillAndSendWindow();
                        }
                    } else if (f.type == Frame.TYPE_REJECT) {
                        // clear everything up to the rejected frame
                        clearWindow(f.num);
                        // send rest of window
                        refillAndSendWindow();
                    }
                } catch (SocketTimeoutException e) {
                    // read timeout, send poll
                    stream.writeFrame(Frame.newPoll());
                    pollRequested = true;
                } catch (DeserializationException | CRCValidationException e) {
                    // frame dropped
                    // System.out.println("deserialization error " + e);
                }
            }
        }
        stream.writeFrame(Frame.newEnd());
    }

    private int nextSeqNum() {
        int temp = seqNum;
        seqNum = (seqNum + 1) % windowSize;
        return temp;
    }

    private void refillAndSendWindow() throws IOException {
        // refill window if necessary
        if (window.size() < windowSize) {
            while (!queue.isEmpty() && window.size() < windowSize) {
                window.add(new WindowItem((byte) nextSeqNum(), queue.removeFirst()));
            }
        }
        // send every item in the window that has not been acked
        for (WindowItem item : window) {
            stream.writeFrame(Frame.newInfo(item.num, item.data));
        }
    }

    private void clearWindow(byte num) {
        // remove every frame from the window until we hit the next frame to send
        while (!window.isEmpty()) {
            if (window.getFirst().num != num) {
                window.removeFirst();
            } else {
                break;
            }
        }
    }

}
