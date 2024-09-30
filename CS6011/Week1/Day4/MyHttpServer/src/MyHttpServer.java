import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MyHttpServer {
    public static void main(String[] args) throws IOException {
        // create server socket for client (web browser) to interact with
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            // print in the console that the server is listening for client requests
            System.out.println("Server live on port 8080");
            // while the server is still running
            while (true) {
                // creat connection between server and client
                try (Socket clientSocket = serverSocket.accept();
                     // buffered reader reads text from a character-input stream (from the client)
                     BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     // PrintWriter is for output streams
                     PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    // create string object that stores the line of input that is read
                    String requestLine = input.readLine();
                    // as long as the request line is not blank or in other words as long as there is a request
                    if (requestLine != null) {
                        // using regular expressions to separate the request line word by wored
                        String[] parts = requestLine.split(" ");
                        // the length of the array of words of the request needs to be greater than or equal to two
                        // with the first part being the method (GET) and the second part is the path?
                        if (parts.length >= 2 && parts[0].equals("GET") && parts[1].equals("/")) {
                            // create file variable from File class that specifies path
                            File file = new File("./resources/index.html");
                            // make sure the file exists
                            if (file.exists()) {
                                // open connection to my file (index.html)
                                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                    // parse out the data
                                    byte[] fileData = new byte[(int) file.length()];
                                    //
                                    fileInputStream.read(fileData);
                                    // send response using headers
                                    output.println("HTTP/1.1 200 OK");
                                    output.println("Content-Type: text/html");
                                    output.println("Content-Length: " + fileData.length);

                                    output.println(); // Blank line to separate headers from body
                                    output.flush(); // flush to make sure data is actually sent out over the network

                                    clientSocket.getOutputStream().write(fileData);
                                } catch (IOException e) {
                                    System.out.println("Error: " + e);
                                }
                            } else {
                                output.println("HTTP/1.1 404 Not Found");
                                output.println();
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e);
                }
            }
        }
    }
}