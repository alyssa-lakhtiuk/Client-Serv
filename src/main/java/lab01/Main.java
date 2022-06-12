package lab01;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cryptor cr = new Cryptor();
        // test data
        byte bSrc = 1;
        long bPktld = 17;
        String message = "Server response";
        byte [] encodedMessage = cr.encipher(message);
        int wLen = encodedMessage.length;
        int cType = 3;
        int bUserId = 7;
        Message mess = new Message(cType, bUserId, message);


        Pack pc = new Pack(bSrc, bPktld, wLen, mess, cr);
        final byte[] respond = Encoder.servResponse(pc, cr);
        System.out.println("Package to send: "+Hex.encodeHexString(respond));

        Pack received = new Pack(respond, cr);
        System.out.println(
                "Client app number: " + received.getbSrc() +
                "\nMessage number: " + received.getbPktId() +
                "\nPack length: " + received.getwLen() +
                "\nMessage: " + new String(received.getbMsq().getMessageBMsq()) +
                "\ncType: " + received.getbMsq().getMessageCType() +
                "\nUser id: " + received.getbMsq().getMessageBUserId());

    }
}
