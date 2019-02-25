package handling.netty;

import client.MapleClient;
import constants.ServerConstants;
import handling.SendPacketOpcode;

import java.util.concurrent.locks.Lock;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;
import server.ServerProperties;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.StringUtil;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericLittleEndianAccessor;

public class MaplePacketEncoder extends MessageToByteEncoder<Object> {

    private static Logger log = Logger.getLogger(MaplePacketEncoder.class);

    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf byteBuf) {
        MapleClient client = channelHandlerContext.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null) {
            MapleAESOFB send_crypto = client.getSendCrypto();
            byte[] input = (byte[]) (byte[]) message;
            if (ServerProperties.ShowPacket() || ServerConstants.Use_Localhost || (Boolean.parseBoolean(ServerProperties.getProperty("world.lvkejian")))) {
                int packetLen = input.length;
                int pHeader = readFirstShort(input);
                String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
                String op = lookupRecv(pHeader);
                String Recv = "服务端发送 " + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                if (packetLen <= 50000) {
                    if (!ServerProperties.SendPacket(op, pHeaderStr)) {
                        String RecvTo = Recv + HexTool.toString(input) + "\r\n" + HexTool.toStringFromAscii(input);
                        System.out.println(RecvTo + "\r\n");
                        FileoutputUtil.packetLog("log\\PacketLog.log", RecvTo);
                        if ((op.equals("GIVE_BUFF")) || (op.equals("CANCEL_BUFF"))) {
                            String SendTos = "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "\r\n";
                            FileoutputUtil.packetLog("log\\SkillBuffLog.log", SendTos + RecvTo);
                        }
                    }
                } else {
                    System.out.println(Recv + HexTool.toString(new byte[]{input[0], input[1]}) + "...\r\n");
                }
            }
            byte[] unencrypted = new byte[input.length];
            System.arraycopy(input, 0, unencrypted, 0, input.length);
            byte[] ret = new byte[unencrypted.length + 4];
            Lock mutex = client.getLock();
            mutex.lock();
            try {
                byte[] header = send_crypto.getPacketHeader(unencrypted.length);

                send_crypto.crypt(unencrypted);
                System.arraycopy(header, 0, ret, 0, 4);
            } finally {
                mutex.unlock();
            }
            System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
            byteBuf.writeBytes(ret);
        } else {
            byteBuf.writeBytes((byte[]) message);
        }
    }



    /*public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
            throws Exception {
        MapleClient client = (MapleClient) session.getAttribute("CLIENT");

        if (client != null) {
            MapleAESOFB send_crypto = client.getSendCrypto();
            byte[] input = (byte[]) (byte[]) message;
            if (ServerProperties.ShowPacket() || ServerConstants.Use_Localhost || (Boolean.parseBoolean(ServerProperties.getProperty("world.lvkejian")))) {
                int packetLen = input.length;
                int pHeader = readFirstShort(input);
                String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
                String op = lookupRecv(pHeader);
                String Recv = "服务端发送 " + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                if (packetLen <= 50000) {
                    if (!ServerProperties.SendPacket(op, pHeaderStr)) {
                        String RecvTo = Recv + HexTool.toString(input) + "\r\n" + HexTool.toStringFromAscii(input);
                        System.out.println(RecvTo + "\r\n");
                        FileoutputUtil.packetLog("log\\PacketLog.log", RecvTo);
                        if ((op.equals("GIVE_BUFF")) || (op.equals("CANCEL_BUFF"))) {
                            String SendTos = "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "\r\n";
                            FileoutputUtil.packetLog("log\\SkillBuffLog.log", SendTos + RecvTo);
                        }
                    }
                } else {
                    System.out.println(Recv + HexTool.toString(new byte[]{input[0], input[1]}) + "...\r\n");
                }
            }
            byte[] unencrypted = new byte[input.length];
            System.arraycopy(input, 0, unencrypted, 0, input.length);
            byte[] ret = new byte[unencrypted.length + 4];
            Lock mutex = client.getLock();
            mutex.lock();
            try {
                byte[] header = send_crypto.getPacketHeader(unencrypted.length);

                send_crypto.crypt(unencrypted);
                System.arraycopy(header, 0, ret, 0, 4);
            } finally {
                mutex.unlock();
            }
            System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
            out.write(IoBuffer.wrap(ret));
        } else {
            out.write(IoBuffer.wrap((byte[]) (byte[]) message));
        }
    }*/


    private String lookupRecv(int val) {
        for (SendPacketOpcode op : SendPacketOpcode.values()) {
            if (op.getValue() == val) {
                return op.name();
            }
        }
        return "UNKNOWN";
    }

    private int readFirstShort(byte[] arr) {
        return new GenericLittleEndianAccessor(new ByteArrayByteStream(arr)).readShort();
    }


}
