package tcpclient;

import java.net.*;
import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;
    Integer maxTime;
    boolean noTimeOut;

    Integer maxLimit;
    boolean noLimit;

    boolean isShutdownOn;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        // If there's a timeout value, save it to the global vairable
        if (shutdown) {
            isShutdownOn = true;
        }

        if (timeout != null) {
            // System.out.println("Har timeout. Input value for timeout is: " + timeout);
            maxTime = timeout;
            noTimeOut = false;
        } else { // Else that it doesn't exist
            noTimeOut = true;
        }

        if (limit != null) {
            maxLimit = limit;
            noLimit = false;
        } else { // Else if it doesnt exist
            noLimit = true;
        }
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {

        // Connect to server
        Socket clientSocket = new Socket(hostname, port);

        if (!noTimeOut) {
            clientSocket.setSoTimeout(maxTime);
        }

        // Send the given argument to the server if its not null
        if (toServerBytes != null) {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(toServerBytes);
            System.out.println("Data skickat");

        } else {
            System.out.println("ingen data");
        }

        if (isShutdownOn) {
            clientSocket.shutdownOutput();
            System.out.println("Closing output");
        }

        InputStream inputStream = clientSocket.getInputStream();
        ByteArrayOutputStream fromServerStorage = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFERSIZE];

        // long lastRecieved = System.nanoTime();
        int bufferLength = 0;
        // Until length is 0, keep reading and buffering
        while (true) {
            
            try {
                bufferLength = inputStream.read(buffer);
                // Read data
                if (bufferLength != -1) {

                    if (noLimit) {
                        fromServerStorage.write(buffer, 0, bufferLength);
                    } else {

                        if (maxLimit >= bufferLength) {
                            fromServerStorage.write(buffer, 0, bufferLength);
                            maxLimit = maxLimit - bufferLength;
                        } else {
                            fromServerStorage.write(buffer, 0, maxLimit);
                            break;
                        }

                        if (maxLimit <= 0) {
                            break;
                        }

                    }
                } else {
                    break;
                }
            } catch (SocketTimeoutException exception1) {
                break;
            }

        }

        clientSocket.close();
        return fromServerStorage.toByteArray();
    }
}