package tp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class DataLinkStream {

    private ArrayList<String> log = new ArrayList<>();
    private boolean printLog = false;
    private Socket sock;
    private DataInputStream in;
    private DataOutputStream out;

    public DataLinkStream(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    DataLinkStream(Socket sock) throws IOException {
        this.sock = sock;
        in = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
    }

    protected DataLinkStream() {
    }

    public void setPrintLog(boolean printLog) {
        this.printLog = printLog;
    }

    public void setTimeout(int timeout) throws IOException {
        sock.setSoTimeout(timeout);
    }

    public Frame readFrame() throws IOException, DeserializationException, CRCValidationException {
        Buffer buf = new Buffer();
        byte b;

        // consume bytes until we see the start frame marker
        do {
            b = in.readByte();
        } while (b != Frame.FRAME_FLAG);
        buf.push(b);

        // append to the buffer until we see the end marker
        do {
            b = in.readByte();
            buf.push(b);
        } while (b != Frame.FRAME_FLAG);

        Frame f = Frame.deserialize(buf);
        appendLog("recv: " + f);
        return f;
    }

    public void writeFrame(Frame f) throws IOException {
        appendLog("sent: " + f);
        Buffer buf = f.serialize();
        buf.writeTo(out);
        out.flush();
    }

    protected void appendLog(String msg) {
        log.add(msg);
        if (this.printLog) {
            System.out.println(msg);
        }
    }

    public void printLog() {
        for (String msg : log) {
            System.out.println(msg);
        }
    }
}
