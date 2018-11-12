package tp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DataLinkStream {

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

    public Frame readFrame() throws IOException, DeserializationException, CRCValidationException {
        Buffer buf = new Buffer();
        byte b;

        // consume bytes until we see the start frame marker
        do {
            b = in.readByte();
        } while (b != Frame.FRAME_FLAG);
        buf.push(b);

        // append to the buffer until we see the end marker
        b = in.readByte();
        while (b != Frame.FRAME_FLAG) {
            b = in.readByte();
            buf.push(b);
        }
        return Frame.deserialize(buf);
    }

    public void writeFrame(Frame f) throws IOException {
        Buffer buf = f.serialize();
        buf.writeTo(out);
        out.flush();
    }
}
