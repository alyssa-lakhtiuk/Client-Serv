package lab02;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException {
        Cryptor.setSecretKey();
        ExecutorService executorService = Executors.newFixedThreadPool(12);
        for(int i = 0; i < 2; i++)
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
        System.out.println("End of main");
    }
}
