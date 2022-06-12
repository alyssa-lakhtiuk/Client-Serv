package lab01;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Message {
    private int cType;
    private int bUserId;

    public int getMessageCType() {
        return cType;
    }

    public int getMessageBUserId() {
        return bUserId;
    }

    public byte[] getMessageBMsq() {
        return bMsq;
    }

    private byte[] bMsq;


    public Message(ByteBuffer buffer, int wLen, Cryptor cr) throws IllegalBlockSizeException, BadPaddingException {
        this.cType = buffer.order(ByteOrder.BIG_ENDIAN).getInt();
        this.bUserId = buffer.order(ByteOrder.BIG_ENDIAN).getInt();
        byte[] encodedMessage = new byte[wLen];
        buffer.get(encodedMessage);
        this.bMsq = cr.decipher(encodedMessage);
    }

    public Message(int cType, int bUserId, String message) {
        this.cType = cType;
        this.bUserId = bUserId;
        Charset charset = StandardCharsets.UTF_8;
        this.bMsq = message.getBytes(charset);
    }
}
