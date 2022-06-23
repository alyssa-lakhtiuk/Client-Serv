package lab02;

import javax.crypto.*;
import java.security.*;
import javax.crypto.NoSuchPaddingException;

public class Cryptor {
    private static SecretKey secretKey;
    private static Cipher cipher;

    public static void setSecretKey() throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("AES");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);
        secretKey = keyGenerator.generateKey();
    }

    public static byte[] encipher(byte[] message) throws BadPaddingException, IllegalBlockSizeException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] cipherText = cipher.doFinal(message);
        return cipherText;
    }

    public static byte[] decipher(byte[] message) throws BadPaddingException, IllegalBlockSizeException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] decipherText = cipher.doFinal(message);
        return decipherText;
    }
}
