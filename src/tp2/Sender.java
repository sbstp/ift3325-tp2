package tp2;

import java.awt.Window;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.ArrayList;

public class Sender {

    public static void main(String[] args) {
        System.out.println("sender");
    }

    private DataLinkStream stream;
    private LinkedList<Buffer> queue = new LinkedList<>();
    private ArrayList<WindowItem> window = new ArrayList<>();
    private int windowSize;

    public class WindowItem {
        public byte num;
        public boolean acked;
        public Buffer data;

        public WindowItem(byte num, Buffer data) {
            this.num = num;
            this.data = data;
            this.acked = false;
        }

        public boolean isAck() {
            return this.acked;
        }

        public void ack() {
            this.acked = true;
        }

        public void noack() {
            this.acked = false;
        }
    }

    public Sender(DataLinkStream stream, int n) throws IOException {
        this.stream = stream;
        windowSize = n;
    }

    public void send(Buffer b) {
        queue.add(b);
    }

    public void mainLoop() throws IOException {
        stream.writeFrame(Frame.newConnection());
        while (!queue.isEmpty()) {
            refillWindow(); // if every frame in the window has been acked, load a new window
            sendWindow(); // send every frame not acked in order
            System.out.println("after send");
            // // receive messages until every frame is acked or an error is sent
            boolean hasErrors = false;
            while (!isWindowAcked() && !hasErrors) {
                try {
                    Frame f = stream.readFrame();
                    System.out.println("received: " + f);
                    if (f.type == Frame.TYPE_ACKNOLEDGE) {
                        // ack the packet with the given num
                        window.get(f.num).ack();
                    } else if (f.type == Frame.TYPE_REJECT) {
                        // an error occured, set every frame after num as not acked
                        hasErrors = true;
                        for (WindowItem item : window) {
                            if (item.num < f.num) {
                                item.ack();
                            } else {
                                item.noack();
                            }
                        }
                    }
                } catch (DeserializationException e) {
                    System.out.println("deserialization error " + e);
                } catch (CRCValidationException e) {
                    System.out.println("crc error" + e);
                }
            }
        }
        stream.writeFrame(Frame.newEnd());
    }

    private boolean isWindowAcked() {
        return window.stream().allMatch(item -> item.isAck());
    }

    private void refillWindow() {
        if (window.isEmpty() || isWindowAcked()) {
            window.clear();
            byte num = 0;
            while (!queue.isEmpty() && window.size() < windowSize) {
                window.add(new WindowItem(num++, queue.removeFirst()));
            }
        }
    }

    private void sendWindow() throws IOException {
        // send every item in the window that has not been acked
        for (WindowItem item : window) {
            if (!item.acked) {
                stream.writeFrame(Frame.newInfo(item.num, item.data));
            }
        }
    }

}
