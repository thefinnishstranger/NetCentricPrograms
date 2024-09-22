import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.*;
import java.math.BigInteger;

import javax.swing.JOptionPane;

public class TCPClient {
    public static void main(String[] args) throws NoSuchAlgorithmException {

        String server = args[0];
        String file = args[1];

        int port = 9999;

        try {
            byte[] data = Files.readAllBytes(Paths.get(file));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            String hashString = Base64.getEncoder().encodeToString(hash);

            System.out.println("Connecting to " + server + " on port " + port);
            Socket clientSocket = new Socket(server, port); // Create socket for connecting to server
            System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());

            OutputStream outToServer = clientSocket.getOutputStream(); // Stream of bytes
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeInt(data.length);
            out.write(data);

            long start = System.nanoTime();
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            String serverHash = in.readUTF();
            System.out.println(hash);
            System.out.println(serverHash);

            long end = System.nanoTime();
            double duration = (end - start) / 1000000;

            if (hashString.equals(serverHash)) {
                System.out.println("Successfully Sent!");
            } else {
                System.out.println("Error!");
            }

            double bits = data.length * 8;

            System.out.println("File name: " + file);
            System.out.println("SHA256 hash: " + hash);
            System.out.println("File size in bits = " + (data.length * 8));
            System.out.println("Time taken (approx. one way) = " + duration / 2 + " ms");

            double throughput = bits / (duration / 2) / 1000000;
            System.out.println("Throughput = " + String.format("%.2f", throughput) + " Mbps");

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
