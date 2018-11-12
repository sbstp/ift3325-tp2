import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tp2.*;

public class MockDataLinkStream extends DataLinkStream {

    private LinkedList<Frame> readFrames = new LinkedList<>();
    private ArrayList<Frame> writeFrames = new ArrayList<>();

    public MockDataLinkStream() {
    }

    public void addReadFrame(Frame f) {
        readFrames.add(f);
    }

    public void addTimeout() {
        readFrames.add(null);
    }

    @Override
    public Frame readFrame() throws IOException {
        Frame f = readFrames.removeFirst();
        if (f == null) {
            appendLog("err:  timeout");
            throw new SocketTimeoutException();
        } else {
            appendLog("recv: " + f);
        }
        return f;
    }

    @Override
    public void writeFrame(Frame f) throws IOException {
        appendLog("sent: " + f);
        writeFrames.add(f);
    }

    public Iterator<Frame> writeIter() {
        return writeFrames.iterator();
    }
}
