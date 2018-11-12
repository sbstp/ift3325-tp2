import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import tp2.*;

public class SenderTest {
    @Test
    public void testSend() throws IOException {
        MockDataLinkStream mock = new MockDataLinkStream();
        mock.addReadFrame(Frame.newAcknoledge((byte) 0));

        Sender s = new Sender(mock, 5);
        s.send(new Buffer("hello"));
        s.mainLoop();

        assertEquals(mock.writeFrames.get(0).type, Frame.TYPE_CONNECTION);
        assertEquals(mock.writeFrames.get(1).type, Frame.TYPE_INFO);
        assertEquals(mock.writeFrames.get(1).data, new Buffer("hello"));
        assertEquals(mock.writeFrames.get(2).type, Frame.TYPE_END);

        mock.printLog();
    }
}
