import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import tp2.*;

public class SenderTest {
    @Test
    public void testSendSimple() throws IOException {
        MockDataLinkStream mock = new MockDataLinkStream();
        mock.addReadFrame(Frame.newAcknoledge(0));
        mock.addReadFrame(Frame.newAcknoledge(1));

        Sender s = new Sender(mock, 5);
        s.send(new Buffer("hello"));
        s.mainLoop();

        Iterator<Frame> it = mock.writeIter();
        assertEquals(it.next(), Frame.newConnection(5));
        assertEquals(it.next(), Frame.newInfo(0, new Buffer("hello")));
        assertEquals(it.next(), Frame.newEnd());

        // mock.printLog();
    }

    @Test
    public void testSendReject() throws IOException {
        MockDataLinkStream mock = new MockDataLinkStream();
        mock.addReadFrame(Frame.newAcknoledge(0)); // ready to receive 0
        mock.addReadFrame(Frame.newAcknoledge(1)); // ready to receive 1
        mock.addReadFrame(Frame.newReject(1)); // reject 1
        mock.addReadFrame(Frame.newAcknoledge(2)); // ready to receive 2
        mock.addReadFrame(Frame.newAcknoledge(3)); // ready to receive 3

        Sender s = new Sender(mock, 5);
        s.send(new Buffer("hello 0"));
        s.send(new Buffer("hello 1"));
        s.send(new Buffer("hello 2"));
        s.mainLoop();

        Iterator<Frame> it = mock.writeIter();
        assertEquals(it.next(), Frame.newConnection(5));
        assertEquals(it.next(), Frame.newInfo(0, new Buffer("hello 0"))); // 0
        assertEquals(it.next(), Frame.newInfo(1, new Buffer("hello 1"))); // 1
        assertEquals(it.next(), Frame.newInfo(2, new Buffer("hello 2"))); // 2
        // whole window is sent
        // now it should get an ack and then a rejection
        assertEquals(it.next(), Frame.newInfo(1, new Buffer("hello 1"))); // 1
        assertEquals(it.next(), Frame.newInfo(2, new Buffer("hello 2"))); // 2
        assertEquals(it.next(), Frame.newEnd());

        // mock.printLog();
    }

    @Test
    public void testSendSmallWindow() throws IOException {
        MockDataLinkStream mock = new MockDataLinkStream();
        mock.addReadFrame(Frame.newAcknoledge(0)); // ready to receive 0
        mock.addReadFrame(Frame.newAcknoledge(1)); // ready to receive 1
        mock.addReadFrame(Frame.newAcknoledge(0)); // ready to receive 0
        mock.addReadFrame(Frame.newAcknoledge(1)); // ready to receive 1
        mock.addReadFrame(Frame.newAcknoledge(0)); // ready to receive 0
        mock.addReadFrame(Frame.newAcknoledge(1)); // ready to receive 1

        Sender s = new Sender(mock, 2);
        s.send(new Buffer("hello 0"));
        s.send(new Buffer("hello 1"));
        s.send(new Buffer("hello 2"));
        s.send(new Buffer("hello 3"));
        s.send(new Buffer("hello 4"));
        s.mainLoop();

        Iterator<Frame> it = mock.writeIter();
        assertEquals(it.next(), Frame.newConnection(2));
        assertEquals(it.next(), Frame.newInfo(0, new Buffer("hello 0"))); // 0
        assertEquals(it.next(), Frame.newInfo(1, new Buffer("hello 1"))); // 1
        assertEquals(it.next(), Frame.newInfo(0, new Buffer("hello 2"))); // 0
        assertEquals(it.next(), Frame.newInfo(1, new Buffer("hello 3"))); // 1
        assertEquals(it.next(), Frame.newInfo(0, new Buffer("hello 4"))); // 0
        assertEquals(it.next(), Frame.newEnd());

        // mock.printLog();
    }

    @Test
    public void testSendTimeout() throws IOException {
        MockDataLinkStream mock = new MockDataLinkStream();
        mock.addReadFrame(Frame.newAcknoledge(0)); // ready to receive 0
        mock.addReadFrame(Frame.newAcknoledge(1)); // ready to receive 1
        mock.addTimeout(); // raise timeout
        mock.addReadFrame(Frame.newAcknoledge(1)); // ready to receive 1
        mock.addReadFrame(Frame.newAcknoledge(2)); // ready to receive 2
        mock.addReadFrame(Frame.newAcknoledge(3)); // ready to receive 3

        Sender s = new Sender(mock, 5);
        s.send(new Buffer("hello 0"));
        s.send(new Buffer("hello 1"));
        s.send(new Buffer("hello 2"));
        s.mainLoop();

        Iterator<Frame> it = mock.writeIter();
        assertEquals(it.next(), Frame.newConnection(5));
        // sender sends the whole window
        assertEquals(it.next(), Frame.newInfo(0, new Buffer("hello 0"))); // 0
        assertEquals(it.next(), Frame.newInfo(1, new Buffer("hello 1"))); // 1
        assertEquals(it.next(), Frame.newInfo(2, new Buffer("hello 2"))); // 2
        // waits for confirmation, gets a timeout, sends a poll
        assertEquals(it.next(), Frame.newPoll()); // 1
        assertEquals(it.next(), Frame.newInfo(1, new Buffer("hello 1"))); // 1
        assertEquals(it.next(), Frame.newInfo(2, new Buffer("hello 2"))); // 2
        assertEquals(it.next(), Frame.newEnd());

        // mock.printLog();
    }
}
