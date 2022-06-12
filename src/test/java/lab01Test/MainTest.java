package lab01Test;
import lab01.Cryptor;
import lab01.Pack;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {

    @Test
    void test_InvalidMagicByte() throws NoSuchAlgorithmException, NoSuchPaddingException {
        Cryptor mc = new Cryptor();
        String str = "00A0BF";
        char[] test = str.toCharArray();
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pack(Hex.decodeHex(test),mc)
        );
    }

    @Test
    void test_Cryptor() throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        Cryptor cr = new Cryptor();
        final String message = "test message";
        final String result = new String(cr.decipher(cr.encipher(message)));
        assertEquals(message, result);
    }

    @Test
    void test_CRC16() throws NoSuchAlgorithmException, NoSuchPaddingException {
        Cryptor cr = new Cryptor();
        final String input = "1300000000000000000a000000300a8b6c0221f35d79ec1715362980276b7c96a5ec7b0f8e40428fff0f7f54652c00dce9ea";

        char[] test = input.toCharArray();
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pack(Hex.decodeHex(test),cr)
        );
    }
}
