package lab02.network;

import java.net.InetAddress;

public interface INetwork {
    void receiveMessage();
    void sendMessage(byte[] mess, InetAddress inetAddress) throws Exception;
}
