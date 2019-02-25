package handling.netty;

import client.MapleClient;
import constants.ServerConstants;
import handling.RecvPacketOpcode;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;
import server.ServerProperties;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.StringUtil;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericLittleEndianAccessor;

/**
 * 拆包（解码客户端发送过来的消息）
 */
public class MaplePacketDecoder extends ByteToMessageDecoder {

    private static final Logger log = Logger.getLogger(MaplePacketDecoder.class);
    public static final AttributeKey<DecoderState> DECODER_STATE_KEY = AttributeKey.newInstance(MaplePacketDecoder.class.getName() + ".STATE");


    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> message) throws Exception {
        DecoderState decoderState = channelHandlerContext.channel().attr(DECODER_STATE_KEY).get();
        MapleClient client = channelHandlerContext.channel().attr(MapleClient.CLIENT_KEY).get();

        if (decoderState.packetlength == -1) {
            if (byteBuf.readableBytes() >= 4) {
                int packetHeader = byteBuf.readInt();
                if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                    channelHandlerContext.channel().disconnect();
                    return;
                }
                decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
            } else {
                log.trace("decode... not enough data");
                return;
            }
        }

        if (byteBuf.readableBytes() >= decoderState.packetlength) {
            byte[] decryptedPacket = new byte[decoderState.packetlength];
            byteBuf.readBytes(decryptedPacket, 0, decoderState.packetlength);
            decoderState.packetlength = -1;
            client.getReceiveCrypto().crypt(decryptedPacket);
            message.add(decryptedPacket);

            if (ServerProperties.ShowPacket() || ServerConstants.Use_Localhost || (Boolean.parseBoolean(ServerProperties.getProperty("world.lvkejian")))) {
                int packetLen = decryptedPacket.length;
                int pHeader = readFirstShort(decryptedPacket);
                String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
                String op = lookupSend(pHeader);
                String Send = "客户端发送 " + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                if (packetLen <= 6000) {
                    String SendTo = Send + HexTool.toString(decryptedPacket) + "\r\n" + HexTool.toStringFromAscii(decryptedPacket);
                    if (!ServerProperties.RecvPacket(op, pHeaderStr)) {
                        System.out.println(SendTo + "\r\n");
                        FileoutputUtil.packetLog("log\\PacketLog.log", SendTo);
                        if ((op.equals("CLOSE_RANGE_ATTACK")) || (op.equals("RANGED_ATTACK")) || (op.equals("MAGIC_ATTACK"))) {
                            String SendTos = "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "\r\n";
                            FileoutputUtil.packetLog("log\\AttackLog.log", SendTos + SendTo);
                        }
                    }
                } else {
                    System.out.println(Send + HexTool.toString(new byte[]{decryptedPacket[0], decryptedPacket[1]}) + "...\r\n");
                }
            }
            return ;
        }
        log.trace("decode... not enough data to decode (need +" + decoderState.packetlength + ")");
    }

    /*protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        DecoderState decoderState = (DecoderState) session.getAttribute(DECODER_STATE_KEY);

        MapleClient client = (MapleClient) session.getAttribute("CLIENT");
        if (decoderState.packetlength == -1) {
            if (in.remaining() >= 4) {
                int packetHeader = in.getInt();
                if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                    session.close(true);
                    return false;
                }
                decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
            } else {
                log.trace("decode... not enough data");
                return false;
            }
        }
        if (in.remaining() >= decoderState.packetlength) {
            byte[] decryptedPacket = new byte[decoderState.packetlength];
            in.get(decryptedPacket, 0, decoderState.packetlength);
            decoderState.packetlength = -1;
            client.getReceiveCrypto().crypt(decryptedPacket);

            out.write(decryptedPacket);
            if (ServerProperties.ShowPacket() || ServerConstants.Use_Localhost || (Boolean.parseBoolean(ServerProperties.getProperty("world.lvkejian")))) {
                int packetLen = decryptedPacket.length;
                int pHeader = readFirstShort(decryptedPacket);
                String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
                String op = lookupSend(pHeader);
                String Send = "客户端发送 " + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                if (packetLen <= 6000) {
                    String SendTo = Send + HexTool.toString(decryptedPacket) + "\r\n" + HexTool.toStringFromAscii(decryptedPacket);
                    if (!ServerProperties.RecvPacket(op, pHeaderStr)) {
                        System.out.println(SendTo + "\r\n");
                        FileoutputUtil.packetLog("log\\PacketLog.log", SendTo);
                        if ((op.equals("CLOSE_RANGE_ATTACK")) || (op.equals("RANGED_ATTACK")) || (op.equals("MAGIC_ATTACK"))) {
                            String SendTos = "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "\r\n";
                            FileoutputUtil.packetLog("log\\AttackLog.log", SendTos + SendTo);
                        }
                    }
                } else {
                    System.out.println(Send + HexTool.toString(new byte[]{decryptedPacket[0], decryptedPacket[1]}) + "...\r\n");
                }
            }
            return true;
        }
        log.trace("decode... not enough data to decode (need +" + decoderState.packetlength + ")");
        return false;
    }*/

    private String lookupSend(int val) {
        for (RecvPacketOpcode op : RecvPacketOpcode.values()) {
            if (op.getValue() == val) {
                return op.name();
            }
        }
        return "UNKNOWN";
    }

    private int readFirstShort(byte[] arr) {
        return new GenericLittleEndianAccessor(new ByteArrayByteStream(arr)).readShort();
    }


    public static class DecoderState {

        public int packetlength = -1;
    }
}
