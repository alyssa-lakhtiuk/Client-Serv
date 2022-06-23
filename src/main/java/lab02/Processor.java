package lab02;

import lab02.entity.Pack;
import lab02.inOut.ResponseBuilder;
import lab02.network.Network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Processor implements Runnable{
    private static ExecutorService service = Executors.newFixedThreadPool(4);
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
            Thread.sleep(4000);
            InetAddress inetAddress = InetAddress.getLocalHost();
            new Network().sendMessage(ResponseBuilder.response(pack), inetAddress);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
