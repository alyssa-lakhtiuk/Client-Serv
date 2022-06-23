package lab02.network;

import lab02.inOut.FakeMessageGenerator;
import lab02.Processor;
import lab02.entity.Pack;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class Network implements INetwork {
    @Override
    public void receiveMessage() {
        try {
            Processor.process(FakeMessageGenerator.generateFakeMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(byte[] mess, InetAddress inetAddress) {
        try {
            Pack pack = new Pack(mess);
            System.out.println("Thread id: " + Thread.currentThread().getId() + " Response to user: " +
                    new String(pack.getbMsq().getMessageBMsq(),
                    StandardCharsets.UTF_8) + " date/time: " + LocalDateTime.now());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
