//package handling.mina;
//
//
//
//
//public class MapleCodecFactory
//        implements ProtocolCodecFactory {
//
//    private final ProtocolEncoder encoder;
//    private final ProtocolDecoder decoder;
//
//    public MapleCodecFactory() {
//        this.encoder = new MaplePacketEncoder();
//        this.decoder = new MaplePacketDecoder();
//    }
//
//    public ProtocolEncoder getEncoder() throws Exception {
//        return this.encoder;
//    }
//
//    public ProtocolDecoder getDecoder() throws Exception {
//        return this.decoder;
//    }
//
//    @Override
//    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
//        return this.encoder;
//    }
//
//    @Override
//    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
//        return this.decoder;
//    }
//}