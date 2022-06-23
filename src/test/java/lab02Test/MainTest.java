package lab02Test;

import lab02.*;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
        Cryptor.setSecretKey();
        ExecutorService executorService = Executors.newFixedThreadPool(12);
        for(int i = 0; i < 15; i++)
            executorService.submit(()->{
                Network tcpNetwork = new Network();
                tcpNetwork.receiveMessage();
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
