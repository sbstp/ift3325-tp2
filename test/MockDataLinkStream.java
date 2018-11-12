import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import tp2.*;

public class MockDataLinkStream extends DataLinkStream {

    private static class Event {
        public String source;
        public Frame frame;

        public Event(String source, Frame frame) {
            this.source = source;
            this.frame = frame;
        }
    }

    private LinkedList<Frame> readFrames = new LinkedList<>();
    public ArrayList<Frame> writeFrames = new ArrayList<>();
    public ArrayList<Event> eventLog = new ArrayList<>();

    public MockDataLinkStream() {
    }

    public void addReadFrame(Frame f) {
        readFrames.add(f);
    }

    @Override
    public Frame readFrame() throws IOException {
        Frame f = readFrames.removeFirst();
        eventLog.add(new Event("read:  ", f));
        return f;
    }

    @Override
    public void writeFrame(Frame f) throws IOException {
        eventLog.add(new Event("wrote: ", f));
        writeFrames.add(f);
    }

    public void printLog() {
        for (Event e : eventLog) {
            System.out.println(e.source + e.frame);
        }
    }
}
