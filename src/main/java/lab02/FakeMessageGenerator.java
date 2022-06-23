package lab02;

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
    public static Pack generateFakeMessage() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Random random = new Random();
        int command = random.nextInt(Message.cTypes.values().length);
        String commandMsg = (Message.cTypes.values()[command]).toString();
        Message testMessage = new Message(command , random.nextInt(maxUsers), Cryptor.encipher(commandMsg.getBytes()));
        long leftLimit = 10L;
        long rightLimit = 100L;
        long generatedLong = new RandomDataGenerator().nextLong(leftLimit, rightLimit);
        Pack pack = new Pack((byte)1, generatedLong, testMessage.getMessageBMsq().length, testMessage);
        return pack;
    }
}
