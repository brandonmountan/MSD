import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args) throws IOException {
        int server_port = 5678;
        InetAddress server_host = InetAddress.getByName("localhost");
        System.out.println("Sending to " + server_host + " " + server_port);
        DatagramSocket socket = new DatagramSocket();

        System.out.println("I am " + socket.getLocalPort());

        byte[] data = new byte[3];
        data[0] = 10;
        data[1] = 20;
        data[2] = 30;
        DatagramPacket pkt = new DatagramPacket(data, data.length, server_host, server_port);
        socket.send(pkt);
    }

}


















 /*
    // recorded reply from 8.8.8.8 for www.cs.utah.edu:
    static byte[] ans =  { 29, -79, -127, -128, 0, 1, 0, 3, 0, 0, 0, 1, 3, 119, 119, 119,
            2, 99, 115, 4, 117, 116, 97, 104, 3, 101, 100, 117, 0, 0, 1, 0,
            1, -64, 12, 0, 5, 0, 1, 0, 0, 14, 16, 0, 24, 2, 119, 112,
            15, 119, 112, 101, 110, 103, 105, 110, 101, 112, 111, 119, 101, 114, 101, 100,
            3, 99, 111, 109, 0, -64, 45, 0, 1, 0, 1, 0, 0, 1, 44, 0,
            4, -115, -63, -43, 11, -64, 45, 0, 1, 0, 1, 0, 0, 1, 44, 0,
            4, -115, -63, -43, 10, 0, 0, 41, 2, 0, 0, 0, 0, 0, 0, 0 };

*/