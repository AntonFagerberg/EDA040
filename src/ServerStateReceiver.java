import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ServerStateReceiver extends Thread {
    ServerStateMonitor serverStateMonitor;
    String url;
    int port, mode;

    public ServerStateReceiver(String url, int port, ServerStateMonitor serverStateMonitor) {
        this.serverStateMonitor = serverStateMonitor;
        this.port = port;
        this.url = url;
        System.out.println("[" + currentThread().getId() + "] ServerStateReceiver: started.");
    }

    public void run() {
        Socket socket = null;
        InputStream inputStream = null;

        while (true) {
            try {
                socket = new Socket(url, port);
            } catch (IOException e) {
                System.err.println("[" + currentThread().getId() + "] ServerStateReceiver: failed to create Socket: " + url + ":" + port + ".");
            }

            if (socket != null) {
                try {
                    inputStream = socket.getInputStream();
                } catch (IOException e) {
                    System.err.println("[" + currentThread().getId() + "] ServerStateReceiver: failed to get InputStream.");
                }

                if (inputStream != null) {

                    try {
                        while (true) {
                            System.out.println("[" + currentThread().getId() + "] ServerStateReceiver: waiting for input.");
                            switch (inputStream.read()) {
                                case ServerStateMonitor.IDLE:
                                case ServerStateMonitor.MOVIE:
                                    serverStateMonitor.unsetForcedMode();
                                    break;
                                case ServerStateMonitor.IDLE_FORCED:
                                    serverStateMonitor.setMode(ServerStateMonitor.IDLE_FORCED);
                                    break;
                                case ServerStateMonitor.MOVIE_FORCED:
                                    serverStateMonitor.setMode(ServerStateMonitor.MOVIE_FORCED);
                                    break;
                            }
                            System.out.println("[" + currentThread().getId() + "] ServerStateReceiver: got input.");
                        }
                    } catch (IOException e) {
                        System.err.println("[" + currentThread().getId() + "] ServerStateReceiver: InputStream aborted.");
                    }

                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        System.err.println("[" + currentThread().getId() + "] ServerStateReceiver: failed to close InputStream.");
                    }
                    inputStream = null;
                }

                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("[" + currentThread().getId() + "] ServerStateReceiver: failed to close Socket.");
                }
                socket = null;
            }
        }
    }
}
