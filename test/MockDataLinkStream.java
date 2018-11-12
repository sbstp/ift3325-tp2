import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tp2.*;

public class MockDataLinkStream extends DataLinkStream {

    private static class LogEntry {
        public String source;
        public Frame frame;

        public LogEntry(String source, Frame frame) {
            this.source = source;
            this.frame = frame;
        }
    }

    private LinkedList<Frame> readFrames = new LinkedList<>();
    private ArrayList<Frame> writeFrames = new ArrayList<>();
    private ArrayList<LogEntry> eventLog = new ArrayList<>();

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
            eventLog.add(new LogEntry("err:  timeout", null));
            throw new SocketTimeoutException();
        } else {
            eventLog.add(new LogEntry("recv: ", f));
        }
        return f;
    }

    @Override
    public void writeFrame(Frame f) throws IOException {
        eventLog.add(new LogEntry("sent: ", f));
        writeFrames.add(f);
    }

    public Iterator<Frame> writeIter() {
        return writeFrames.iterator();
    }

    public void printLog() {
        for (LogEntry e : eventLog) {
            if (e.frame != null) {
                System.out.println(e.source + e.frame);
            } else {
                System.out.println(e.source);
            }
        }
    }
}
