package Testing_Francisco;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

    private final int PORT = 8080;
    private Socket socket = null;
    private ServerSocket serverSocket = null;

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        while (true) {
            socket = serverSocket.accept();//blocking
            ServerRunnable sr = new ServerRunnable(socket);
            Thread t = new Thread(sr);
            t.start();
        }
    }

    public static void main(String[] args) throws IOException {
        TestServer server = new TestServer();
        server.startServer();
    }
}
