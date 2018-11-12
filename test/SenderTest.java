import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import tp2.*;

public class SenderTest {
    @Test
    public void testSendSimple() throws IOException {
        MockDataLinkStream mock = new MockDataLinkStream();
        mock.addReadFrame(Frame.newAcknoledge((byte) 0));

        Sender s = new Sender(mock, 5);
        s.send(new Buffer("hello"));
        s.mainLoop();

        assertEquals(mock.writeFrames.get(0).type, Frame.TYPE_CONNECTION);
        assertEquals(mock.writeFrames.get(1).type, Frame.TYPE_INFO);
        assertEquals(mock.writeFrames.get(1).data, new Buffer("hello"));
        assertEquals(mock.writeFrames.get(2).type, Frame.TYPE_END);
    }

    @Test
    public void testSendReject() throws IOException {
        MockDataLinkStream mock = new MockDataLinkStream();
        mock.addReadFrame(Frame.newAcknoledge((byte) 0)); // ack 0
        mock.addReadFrame(Frame.newReject((byte) 1)); // reject 1
        mock.addReadFrame(Frame.newAcknoledge((byte) 1));
        mock.addReadFrame(Frame.newAcknoledge((byte) 2));

        Sender s = new Sender(mock, 5);
        s.send(new Buffer("hello 1"));
        s.send(new Buffer("hello 2"));
        s.send(new Buffer("hello 3"));
        s.mainLoop();

        assertEquals(mock.writeFrames.get(0).type, Frame.TYPE_CONNECTION);
        assertEquals(mock.writeFrames.get(1).type, Frame.TYPE_INFO); // 0
        assertEquals(mock.writeFrames.get(2).type, Frame.TYPE_INFO); // 1
        assertEquals(mock.writeFrames.get(3).type, Frame.TYPE_INFO); // 2
        // whole window is sent
        // now it should get an ack and then a rejection
        assertEquals(mock.writeFrames.get(4).type, Frame.TYPE_INFO); // 1
        assertEquals(mock.writeFrames.get(5).type, Frame.TYPE_INFO); // 2
        assertEquals(mock.writeFrames.get(6).type, Frame.TYPE_END);

        // mock.printLog();
    }
}
