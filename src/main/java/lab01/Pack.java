package lab01;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Pack {
    public byte getbMagic() {
        return bMagic;
    }

    private byte bMagic = 0x13;
    private byte bSrc;
    private long bPktId;
    private int wLen;
    private short wCrc16;
    private Message bMsq;

    public byte getbSrc() {
        return bSrc;
    }

    public long getbPktId() {
        return bPktId;
    }

    public int getwLen() {
        return wLen;
    }

    public short getwCrc16() {
        return wCrc16;
    }

    public Message getbMsq() {
        return bMsq;
    }

    public short getwCrc16Last() {
        return wCrc16Last;
    }

    private short wCrc16Last;

    public Pack(byte[] bytes, Cryptor cr) throws IllegalBlockSizeException, BadPaddingException {
        if (bytes[0] != bMagic) {
            throw new IllegalArgumentException("Invalid magic byte");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 1, bytes.length - 1);
        this.bSrc = buffer.get();
        this.bPktId = buffer.order(ByteOrder.BIG_ENDIAN).getLong();
        this.wLen = buffer.order(ByteOrder.BIG_ENDIAN).getInt();
        this.wCrc16 = buffer.order(ByteOrder.BIG_ENDIAN).getShort();
        final short wCrc16Counted = CRC16.crc16(bytes, 0, Long.BYTES+Integer.BYTES+Short.BYTES);
        if(wCrc16Counted != this.wCrc16){
            throw new IllegalArgumentException("wCrc16 expected: " + wCrc16Counted + ", but get " + wCrc16);
        }
        this.bMsq = new Message(buffer, this.wLen, cr);
        this.wCrc16Last = buffer.order(ByteOrder.BIG_ENDIAN).getShort();
        final short wCrc16LastCounted = CRC16.crc16(bytes, 0, bytes.length - 2);
        if(wCrc16LastCounted != wCrc16Last){
            throw new IllegalArgumentException("wCrc16Last expected: " + wCrc16LastCounted + ", but get " + wCrc16Last);
        }
    }


    public Pack(byte bSrc, long bPktId, int wLen, Message bMsq, Cryptor cr) throws IllegalBlockSizeException, BadPaddingException {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.wLen = wLen;
        this.bMsq = bMsq;
        byte[] header = ByteBuffer.allocate(2 + Long.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES)
                .put(this.bMagic)
                .put(this.bSrc)
                .putLong(this.bPktId)
                .putInt(this.wLen)
                .putInt(bMsq.getMessageCType())
                .putInt(bMsq.getMessageBUserId())
                .array();
        this.wCrc16 = CRC16.crc16(header, 0, header.length - Integer.BYTES * 2);
        byte[] headerWithCrc = ByteBuffer.allocate(2 + Long.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Short.BYTES + cr.encipher(new String(bMsq.getMessageBMsq())).length)
                .put(this.bMagic)
                .put(this.bSrc)
                .putLong(this.bPktId)
                .putInt(this.wLen)
                .putShort(this.wCrc16)
                .putInt(bMsq.getMessageCType())
                .putInt(bMsq.getMessageBUserId())
                .put(cr.encipher(new String(bMsq.getMessageBMsq())))
                .array();
        this.wCrc16Last = CRC16.crc16(headerWithCrc, 0, headerWithCrc.length);
    }
    @Override
    public String toString(){
        return (
                "Client app number: " + this.getbSrc() +
                        "\nMessage number: " + this.getbPktId() +
                        "\nPack length: " + this.getwLen() +
                        "\nMessage: " + new String(this.getbMsq().getMessageBMsq()) +
                        "\ncType: " + this.getbMsq().getMessageCType() +
                        "\nUser id: " + this.getbMsq().getMessageBUserId());
    }
}
