import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.*;

public class TCPServer {
    public static void main(String[] args) throws NoSuchAlgorithmException {

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9999); //creates a socket and binds it to port 9999
            // serverSocket = new ServerSocket(0); // Creates a socket and binds it to next available port
            while (true) {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket connectionSocket = serverSocket.accept(); //listens for connection and creates a connection socket for communication
                System.out.println("Just connected server port # " + connectionSocket.getLocalSocketAddress() +
                        " to client port # " + connectionSocket.getRemoteSocketAddress());

                DataInputStream in = new DataInputStream(connectionSocket.getInputStream()); //get incoming data in bytes from connection socket
                int fileSize = in.readInt();
                byte[] fileBytes = new byte[fileSize];
                in.readFully(fileBytes);

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] serverHash = digest.digest(fileBytes);
                String hashString = Base64.getEncoder().encodeToString(serverHash);

                System.out.println("Received file size in bits = " + (fileSize * 8));
                System.out.println("Received file SHA256 hash: " + hashString);

                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream()); //setup a stream for outgoing bytes of data
                out.writeUTF(hashString);

                connectionSocket.close(); //close connection socket after this exchange
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
