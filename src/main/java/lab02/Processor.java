package lab02;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Processor implements Runnable{
    private static ExecutorService service = Executors.newFixedThreadPool(6);
    private Pack pack;
    public Processor(Pack pack){
        this.pack = pack;
    }

    public static void process(Pack pack) {
        service.submit(new Processor(pack));
    }
    public static void shutdown(){
        try{
            service.shutdown();
            while(!service.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            InetAddress inetAddress = InetAddress.getLocalHost();
            new Network().sendMessage(ResponseBuilder.response(pack), inetAddress);
        } catch (UnknownHostException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
