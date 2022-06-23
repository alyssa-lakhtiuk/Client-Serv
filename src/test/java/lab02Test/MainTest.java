package lab02Test;

import lab02.*;
import lab02.crypting.Cryptor;
import lab02.entity.Pack;
import lab02.network.Network;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {
    @Test
    void checkWhether_InvalidMagicByte() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pack(Hex.decodeHex("15"))
        );
    }

    @Test
    void checkWhether_SuccessfulFinished() throws NoSuchPaddingException, NoSuchAlgorithmException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(int i = 0; i < 10; i++)
            executorService.submit(()->{
                Network Network = new Network();
                Network.receiveMessage();
            });
        try{
            executorService.shutdown();
            while(!executorService.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Processor.shutdown();
    }
}
