package tcpclient;

import java.net.*;
import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;

    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {

        // Connect to server
        Socket clientSocket = new Socket(hostname, port);

        // Send the given argument to the server if its not null
        if (toServerBytes != null) {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(toServerBytes);
        }

        // Initialize stream and buffer read
        InputStream inputStream = clientSocket.getInputStream();
        ByteArrayOutputStream fromServerStream = new ByteArrayOutputStream();

        //Create a buffer
        byte[] buffer = new byte[BUFFERSIZE];

        int bufferLength = inputStream.read(buffer);

        //Until length is 0, keep reading and buffering
        while (bufferLength != -1) {
            fromServerStream.write(buffer, 0, bufferLength);
            bufferLength = inputStream.read(buffer);
        }

        clientSocket.close();
        return fromServerStream.toByteArray();
    }
}
