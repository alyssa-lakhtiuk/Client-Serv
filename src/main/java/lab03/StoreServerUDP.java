package lab03;

import lab03.entity.Pack;
import lab03.inOut.ResponseBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class StoreServerUDP extends Thread {
    private static final int Port = 1373;
    private static final int ByteArrSize = 256;
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[ByteArrSize];

    public StoreServerUDP()  {
        try {
            socket = new DatagramSocket(Port);
        } catch (SocketException e){
            throw new RuntimeException("", e);
        }

    }

    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException("Can't receive the packet", e);
            }
            try {
                buf = ResponseBuilder.response(new Pack(buf));
            } catch (IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException
                     | InvalidAlgorithmParameterException | InvalidKeyException e) {
                throw new RuntimeException("Unable to set pack for response", e);
            }
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);

            String received
                    = new String(packet.getData(), 0, packet.getLength());

            if (received.equals("end")) {
                running = false;
                continue;
            }
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException("Can't send the packet", e);
            }
        }
        socket.close();
    }
}
