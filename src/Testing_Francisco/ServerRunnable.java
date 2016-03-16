

package Testing_Francisco;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


class ServerRunnable implements Runnable {
    private final Socket SOCKET;

    public ServerRunnable(Socket socket) {
        this.SOCKET = socket;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
        }
    }

    @Override
    public void run() {
        while (true) {            
            
        }
        
    }

}
