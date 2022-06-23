package lab02;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ResponseBuilder {
    public static byte[] response(Pack pack) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] response = ("OK!").getBytes();
        byte[] encipheredResponse = Cryptor.encipher(response);
        Message answer = new Message(pack.getbMsq().getMessageCType(),pack.getbMsq().getMessageBUserId(),encipheredResponse);
        Pack packResponse = new Pack((byte)1, pack.getbPktId(), answer.getMessageBMsq().length, answer);
        return packResponse.packToBytes();
    }
}
