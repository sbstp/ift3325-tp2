package tp2;

import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

public class DataLinkListener {

    private ServerSocket server;

    public DataLinkListener(int port) throws IOException {
        server = new ServerSocket(port);
    }

    public DataLinkStream accept() throws IOException {
        Socket sock = server.accept();
        return new DataLinkStream(sock);
    }

}
