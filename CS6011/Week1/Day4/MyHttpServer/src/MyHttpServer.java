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
                     // buffered reader reads text from a character-httpRequest stream (from the client)
                     BufferedReader httpRequest = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     // PrintWriter is for httpResponse streams
                     PrintWriter httpResponse = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    // create string object that stores the line of httpRequest that is read
                    String requestLine = httpRequest.readLine();
                    // as long as the request line is not blank or in other words as long as there is a request
                    if (requestLine != null) {
                        // using regular expressions to separate the request line word by word
                        String[] parts = requestLine.split(" ");
                        // the length of the array of words of the request needs to be greater than or equal to two
                        // with the first part being the method (GET) and the second part is the path
                        if (parts.length >= 2 && parts[0].equals("GET") && parts[1].equals("/")) {
                            // create file variable from File class that specifies path
                            File file = new File("./resources/index.html");
                            // make sure the file exists
                            if (file.exists()) {
                                // open connection to my file (index.html)
                                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                    // parse out the data into bytes
                                    byte[] fileData = new byte[(int) file.length()];
                                    //
                                    fileInputStream.read(fileData);
                                    // send response using headers
                                    httpResponse.println("HTTP/1.1 200 OK");
                                    httpResponse.println("Content-Type: text/html");
                                    httpResponse.println("Content-Length: " + fileData.length);

                                    httpResponse.println(); // Blank line to separate headers from body
                                    httpResponse.flush(); // flush to make sure data is actually sent out over the network

                                    clientSocket.getOutputStream().write(fileData);
                                } catch (IOException e) {
                                    // catch if error with httpRequest or httpResponse stream dealing with file
                                    System.out.println("Error: " + e);
                                }
                            }
                        } else {
                            // 404 message if request is anything other than "/" for example /home
                            httpResponse.println("HTTP/1.1 404 Not Found");
                            httpResponse.println();
                            httpResponse.flush();
                            clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found".getBytes());
                        }
                    }
                } catch (IOException e) {
                    // catch if error with input or output stream
                    System.out.println("Error: " + e);
                }
            }
        }
    }
}