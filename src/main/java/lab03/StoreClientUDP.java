package lab03;

import lab03.entity.Pack;
import lab03.inOut.FakeMessageGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StoreClientUDP {
    private static final int Port = 1373;
    private static final int ByteArrSize = 256;
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public StoreClientUDP() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (SocketException | UnknownHostException e){
            throw new RuntimeException("", e);
        }

    }

    public String sendEcho() throws IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        Map<Long, byte[]> allSent = new HashMap<>();
        Map<Long, byte[]> allReceived = new HashMap<>();
        Pack packFromClient = FakeMessageGenerator.generateFakeMessage();
        buf = packFromClient.packToBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, Port);
        socket.send(packet);
        Pack sent = new Pack(packet.getData());
        System.out.println("\n Message sent by client to server: \n" + sent.toString());
        allSent.put(sent.getbPktId(), buf);

        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        Pack rec = new Pack(packet.getData());
        System.out.println("\n Message received by client from server: \n" + rec.toString());
        allReceived.put(rec.getbPktId(), buf);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    private void tryToResend(Map<Long, byte[]> allReceived, Map<Long, byte[]> allSent) throws IOException {
        if(!allSent.isEmpty()){
            Set<Long> pktIds = allReceived.keySet();
            for(Long id : pktIds){
                allSent.remove(id);
            }
            allReceived.clear();
            Set<Long> idsSent = allSent.keySet();
            for (Long id : idsSent) {
                byte[] bytes = allSent.get(id);

                final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(null), Port);
                socket.send(packet);

                allSent.put(id, bytes);

                final byte[] inputMessage = new byte[ByteArrSize];
                final DatagramPacket response = new DatagramPacket(inputMessage, inputMessage.length);

                try {
                    socket.receive(response);

                    final int realMessageSize = response.getLength();
                    byte[] responseBytes = new byte[realMessageSize];
                    System.arraycopy(response.getData(), 0, responseBytes, 0, responseBytes.length);
                    Pack responsePacket;
                    try {
                        responsePacket = new Pack(responseBytes);
                    } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException |
                             NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                        throw new RuntimeException("", e);
                    }

                    allReceived.put(responsePacket.getbPktId(), responseBytes);

                    System.out.println("Response for " + responsePacket.getbSrc() + " : " + new String(responsePacket.getbMsq().getMessageBMsq()));

                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timeout");
                }
            }
        }
    }

    public void close() {
        socket.close();
    }
}
