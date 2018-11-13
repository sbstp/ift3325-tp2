package tp2;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class UnreliableDataLinkStream extends DataLinkStream {

    private static final double ERROR_RATE = 0.05;

    public UnreliableDataLinkStream(String host, int port) throws IOException {
        super(host, port);
    }

    public UnreliableDataLinkStream(Socket sock) throws IOException {
        super(sock);
    }

    @Override
    public Frame readFrame() throws IOException, DeserializationException, CRCValidationException {
        Frame f = super.readFrame(true);
        if (f.type != Frame.TYPE_CONNECTION && f.type != Frame.TYPE_END && Math.random() < ERROR_RATE) {
            if (Math.random() < 0.5) {
                appendLog("recv-drop", f);
                f = super.readFrame();
            } else {
                appendLog("recv-corrupt", f);
                if (f.data.length() > 0) {
                    // corrupt data
                    f.data.set(0, (byte) (Math.random() * 255));
                } else {
                    // corrupt metadata
                    f.type = Frame.TYPE_CONNECTION;
                }
            }
            appendLog("--->", f);
        } else {
            appendLog("recv", f);
        }

        return f;
    }

    @Override
    public void writeFrame(Frame f) throws IOException {
        if (f.type != Frame.TYPE_CONNECTION && f.type != Frame.TYPE_END && Math.random() < ERROR_RATE) {
            if (Math.random() < 0.5) {
                appendLog("send-drop", f);
                return;
            } else {
                appendLog("send-corrupt", f);
                if (f.data.length() > 0) {
                    // corrupt data
                    f.data.set(0, (byte) (Math.random() * 255));
                } else {
                    // corrupt metadata
                    f.type = Frame.TYPE_CONNECTION;
                }

            }
        }
        super.writeFrame(f);
    }

}
