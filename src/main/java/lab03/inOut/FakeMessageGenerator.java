package lab03.inOut;

import lab03.crypting.Cryptor;
import lab03.entity.Message;
import lab03.entity.Pack;
import org.apache.commons.math3.random.RandomDataGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class FakeMessageGenerator {
    final static int maxUsers = 60000;
    private static int num = 0;
    public static Pack generateFakeMessage() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        num ++;
        Random random = new Random();
        int command = random.nextInt(Message.cTypes.values().length);
        String commandMsg = (Message.cTypes.values()[command]).toString();
        if (num == 40){
            commandMsg = ("end");
        }
        Message testMessage = new Message(command , random.nextInt(maxUsers), Cryptor.encipher(commandMsg.getBytes()));
        long leftLimit = 10L;
        long rightLimit = 100L;
        long generatedLong = new RandomDataGenerator().nextLong(leftLimit, rightLimit);
        return new Pack((byte)1, generatedLong, testMessage.getMessageBMsq().length, testMessage);
    } // example of pack we can get from user
}
