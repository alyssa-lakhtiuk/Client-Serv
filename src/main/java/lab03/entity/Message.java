package lab03.entity;

import lab03.crypting.Cryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Message {
    private int cType;
    private int bUserId;
    private byte[] bMsq;

    public enum cTypes{
        GetProdQuantity,
        RemoveProducts,
        AddProducts,
        AddProductGroup,
        AddProdToGroup,
        SetProductPrice
    }
    public int getMessageCType() {
        return cType;
    }

    public int getMessageBUserId() {
        return bUserId;
    }

    public byte[] getMessageBMsq() {
        return bMsq;
    }

    public Message(ByteBuffer buffer, int wLen) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        // gets encoded bytes and decode
        this.cType = buffer.order(ByteOrder.BIG_ENDIAN).getInt();
        this.bUserId = buffer.order(ByteOrder.BIG_ENDIAN).getInt();
        byte[] encodedMessage = new byte[wLen];
        buffer.get(encodedMessage);
        this.bMsq = Cryptor.decipher(encodedMessage);
    }

    public Message(int cType, int bUserId, byte[] message) throws IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        this.cType = cType;
        this.bUserId = bUserId;
        this.bMsq = Cryptor.decipher(message);
       // this.bMsq = message;
    }
}