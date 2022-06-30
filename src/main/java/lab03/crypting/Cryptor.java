package lab03.crypting;

import javax.crypto.*;
import java.security.*;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptor {
    private static final String secretKey = "AHPSIOSDADFJDIOD";
    private static final String secretIV = "ASGHTYUIOPLKGHTB";

    public static byte[] encipher(final byte[] messageToEncode) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(secretIV.getBytes());
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(messageToEncode);
    }

    public static byte[] decipher(final byte[] messageToDecode) throws InvalidAlgorithmParameterException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(secretIV.getBytes());
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(messageToDecode);
    }
}
