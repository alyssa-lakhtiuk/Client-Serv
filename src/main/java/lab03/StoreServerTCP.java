package lab03;

import lab03.entity.Pack;
import lab03.inOut.ResponseBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class StoreServerTCP implements Runnable {
    // const
    private static final int NumberOfClients = 20;
    private static final int ByteArrSize = 256;
    private static final int Port = 1337;
    private ServerSocket serverSocket;


    public Socket getSocket() {
        return socket;
    }

    private Socket socket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        for (int i = 0; i < NumberOfClients; i++){
            socket = serverSocket.accept();
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            byte[] buf = new byte[ByteArrSize];
            int inputPackSize = in.read(buf);
            Pack userPack;
            try {
                userPack = new Pack(buf);
            } catch (IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException
                     | InvalidAlgorithmParameterException | InvalidKeyException e) {
                throw new RuntimeException("Unable to set pack for response", e);
            }
            try{
            out.write(ResponseBuilder.response(userPack));
            } catch (IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException
                     | InvalidAlgorithmParameterException | InvalidKeyException e) {
                throw new RuntimeException("Unable to set pack for response", e);
            }
        }
    }

    @Override
    public void run() {
        StoreServerTCP server = new StoreServerTCP();
        try {
            server.start(Port);
        } catch (IOException e){
            throw new RuntimeException("", e);
        }
    }
}
