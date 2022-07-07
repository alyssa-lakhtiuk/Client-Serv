package lab04;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {
    private static final int Port = 1337;
    private static final int NumberOfClients = 20;
    public static void main(String[] args) throws InvalidAlgorithmParameterException, IllegalBlockSizeException,
            NoSuchPaddingException, IOException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {

        String dbFileName = "storeDB";
        DBConnection db = new DBConnection(dbFileName);

        //        // TCP
//        System.out.println("-----TCP WORK-----");
//        StoreServerTCP newTCPServer = new StoreServerTCP();
//        Thread server = new Thread(newTCPServer);
//        server.start();
//        for (int i = 0; i < NumberOfClients; i++){
//            StoreClientTCP newClient = new StoreClientTCP(new Socket(InetAddress.getByName(null), Port));
//            newClient.start();
//        }
//        server.join();
//
//
//        // UDP
//        System.out.println("-----UDP WORK-----");
//        new StoreServerUDP().start();
//        for (int j = 0; j < NumberOfClients; j++){
//            StoreClientUDP client = new StoreClientUDP();
//            client.sendEcho();
//        }
    }
}
