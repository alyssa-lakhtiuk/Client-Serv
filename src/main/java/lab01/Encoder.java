package lab01;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;

public class Encoder {
    public static byte[] servResponse(Pack decodedPack, Cryptor cr) throws BadPaddingException, IllegalBlockSizeException {
        final byte[] my_message = cr.encipher(new String(decodedPack.getbMsq().getMessageBMsq()));


        return ByteBuffer.allocate(Integer.BYTES * 3 + Long.BYTES + 2 + Short.BYTES * 2 + my_message.length)
                .put(decodedPack.getbMagic())
                .put(decodedPack.getbSrc())
                .putLong(decodedPack.getbPktId())
                .putInt(my_message.length)
                .putShort(decodedPack.getwCrc16())
                .putInt(decodedPack.getbMsq().getMessageCType())
                .putInt(decodedPack.getbMsq().getMessageBUserId())
                .put(my_message)
                .putShort(decodedPack.getwCrc16Last())
                .array();
    }
}
