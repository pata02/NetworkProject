import java.net.*;
import java.io.*;

// import java.net.URISyntaxException;
public class HTTPAsk {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Created socket on: " + port);

        while (true) {
            // waiting for new socket
            System.out.println("Waiting for new client");
            Socket clientSocket = serverSocket.accept();

            // Accepting new socket
            System.out.println("Socket Accepted");
            System.out.println("");

            InputStream searchBarSocket = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            // Reading searchBar--------------------------------
            StringBuilder stringbuilder = new StringBuilder();
            int bufferLength = 0;
            char currentChar;

            while (true) {
                System.out.println("");
                bufferLength = searchBarSocket.read();

                if (bufferLength == -1) {
                    break;
                }

                currentChar = (char) bufferLength;
                stringbuilder.append(currentChar);

                if (stringbuilder.toString().endsWith("\r\n")) {
                    break;
                }
            }

            String result = stringbuilder.toString().trim();
            System.out.println("All data read, Starting to split");

            // Reading all done-----------------------------------
            // Extracting Param-----------------------------------

            // Must have
            String hostname = null;
            Integer portParam = null;

            // Optional
            Integer limitParam = null;
            String stringParam = "";
            Boolean shutDownParam = false;
            Integer timeOutParam = null;

            String response = "";

            System.out.println("----Result: ." + result + ".");
            String[] parts = result.split("[\\?=\\s& ]");

            for (int i = 0; i < parts.length; i++) {
                System.out.println("P" + i + ": ." + parts[i] + ".");
            }
            System.out.println("");

            if (!parts[1].equals("/ask")) {
                // throw new Exception();
                System.out.println("#ERROR 404 /ask is not found");
                response = "HTTP/1.1 404 Not Found\r\n\r\n";
                // "Content-Type: text/plain\r\n\r\n"
                // + "404 Not Found";

                output.write(response.getBytes());
                output.flush();
                clientSocket.close();
                continue;
            }

            try {
                if (!parts[0].equals("GET")) {
                    System.out.println("GET not found");
                    throw new Exception();
                }

                if (!parts[2].equals("hostname")) {
                    System.out.println("hostname not found");
                    throw new Exception();
                }
                hostname = parts[3];

                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("port")) {
                        portParam = Integer.parseInt(parts[i + 1]);
                    }

                    if (parts[i].equals("string")) {
                        stringParam = parts[i + 1];
                    }

                    if (parts[i].equals("shutdown")) {
                        shutDownParam = Boolean.parseBoolean(parts[i + 1]);
                    }

                    if (parts[i].equals("limit")) {
                        limitParam = Integer.parseInt(parts[i + 1]);
                    }

                    if (parts[i].equals("timeout")) {
                        timeOutParam = Integer.parseInt(parts[i + 1]);
                    }
                }

                if (hostname == null) {
                    System.out.println("Hostname not found");
                    throw new Exception();
                }

                if (portParam == null) {
                    System.out.println("Port not found");
                    throw new Exception();
                }

            } catch (Exception e) {
                System.out.println("#ERROR bad Request");
                response = "HTTP/1.1 400 Bad Request\r\n";
                // + "Content-Type: text/plain\r\n\r\n"
                // + "400 Bad Request";

                output.write(response.getBytes());
                output.flush();
                clientSocket.close();
                continue;
            }

            System.out.println("Hostname is: ." + hostname + ".");
            System.out.println("Port is: ." + portParam + ".");

            System.out.println("Limit is: ." + limitParam + ".");
            System.out.println("String is: ." + stringParam + ".");
            System.out.println("Shutdown is: ." + shutDownParam + ".");
            System.out.println("TimeOut is: ." + timeOutParam + ".");
            System.out.println("");
            // -------------------------------------------
            TCPClient tcpClient = new TCPClient(shutDownParam, timeOutParam, limitParam);
            byte[] serverBytes = "Default string for serverBytes".getBytes();

            try {
                System.out.println("Trying to connect to " + hostname);
                serverBytes = tcpClient.askServer(hostname, portParam,
                        (stringParam + "\n").getBytes());

            } catch (UnknownHostException e1) {
                System.out.println("Unknown host exception");

            } catch (IOException e) {
                System.out.println("IO Exception, Unknown host catchades inte");
            }

            System.out.println("Serverbytes:" + new String() + serverBytes);

            response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n" +
                    "\r\n" + new String(serverBytes);

            System.out.println("Writing response: " + response);
            output.write(response.getBytes());
            output.flush();
            System.out.println("Response sent");

            clientSocket.close();
        }

    }

}
