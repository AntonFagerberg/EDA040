package server;

import se.lth.cs.fakecamera.Axis211A;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

public class PictureSender extends Thread {
	private final Axis211A camera = new Axis211A();
	private int port;
    private OutputStream outputStream;

    public PictureSender(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            outputStream = serverSocket.accept().getOutputStream();
            System.out.println("Started server at port: " + port + ".");

            if (camera.connect()){
                System.out.println("Connected to camera.");
                transferJPEG();
            } else {
                System.out.println("Could not connect to camera.");
                System.exit(1);
            }
        } catch (IOException e) {
            camera.close();
            e.printStackTrace();
        }

    }
	
	private void transferJPEG() throws IOException {
		int length;
		byte[] jpegData = new byte[Axis211A.IMAGE_BUFFER_SIZE];

        while (true) {
            length = camera.getJPEG(jpegData, 0);
            outputStream.write(jpegData, 0, length);
        }
	}
}
