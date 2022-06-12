package lab01;

import javax.crypto.*;
import java.security.Key;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;

public class Cryptor {
    private Cipher cipher;
    private Key secretKey;
    public Cryptor() throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.cipher = Cipher.getInstance("AES");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);
        this.secretKey = keyGenerator.generateKey();
    }

    public byte[] encipher(String message) throws BadPaddingException, IllegalBlockSizeException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] cipherText = cipher.doFinal(message.getBytes());
        return cipherText;
    }

    public byte[] decipher(byte[] message) throws BadPaddingException, IllegalBlockSizeException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] decipherText = cipher.doFinal(message);
        return decipherText;
    }
}
