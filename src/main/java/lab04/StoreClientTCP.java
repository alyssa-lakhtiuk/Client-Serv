package lab04;

import lab04.entity.Pack;
import lab04.inOut.FakeMessageGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class StoreClientTCP {
    // const
    private static final int NumberReconnections = 2;
    private static final int ByteArrSize = 256;
    private static final int Port = 1337;

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public StoreClientTCP(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void start() {
        int currentReconnection = 0;
        try {
            proceedConnection();
        } catch (IOException e){
            System.out.println("Please, wait. Trying to reconnect");
            reconnect(currentReconnection);
        }
    }

    private void proceedConnection() throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();
        out = new PrintWriter(outputStream, true);
        in = new BufferedReader(new InputStreamReader(inputStream));
        Pack packFromUser;
        try {
            packFromUser = FakeMessageGenerator.generateFakeMessage();
        } catch (IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("", e);
        }
        System.out.println("\n Message sent by client to server: \n" + packFromUser.toString());
        try {
            outputStream.write(packFromUser.packToBytes());
        } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("", e);
        }
        byte[] serverResponse = new byte[ByteArrSize];
        int serverResponseSize = inputStream.read(serverResponse);
        Pack received;
        try {
            received = new Pack(serverResponse);
        } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("", e);
        }
        System.out.println("\n Message sent by client to server: \n" + received.toString());
    }

    private void reconnect(int currentReconnection){
        try {
            final Socket socket = new Socket(InetAddress.getByName(null), Port);
            socket.setSoTimeout(3_000*NumberReconnections);
            start();
        } catch (IOException e){
            if (currentReconnection == NumberReconnections){
                System.out.println("Server is disabled");
            } else {
                currentReconnection += 1;
                reconnect(currentReconnection);
            }
        }
    }

}
