package handling;

import client.MapleClient;
import constants.ServerConstants;
import handling.cashshop.CashShopServer;
import handling.cashshop.handler.BuyCashItemHandler;
import handling.cashshop.handler.CashShopOperation;
import handling.cashshop.handler.CouponCodeHandler;
import handling.cashshop.handler.MTSOperation;
import handling.channel.ChannelServer;
import handling.channel.handler.AllianceHandler;
import handling.channel.handler.BBSHandler;
import handling.channel.handler.BuddyListHandler;
import handling.channel.handler.ChatHandler;
import handling.channel.handler.DueyHandler;
import handling.channel.handler.FamilyHandler;
import handling.channel.handler.GuildHandler;
import handling.channel.handler.HiredMerchantHandler;
import handling.channel.handler.InterServerHandler;
import handling.channel.handler.InventoryHandler;
import handling.channel.handler.ItemMakerHandler;
import handling.channel.handler.MobHandler;
import handling.channel.handler.MonsterCarnivalHandler;
import handling.channel.handler.NPCHandler;
import handling.channel.handler.PartyHandler;
import handling.channel.handler.PetHandler;
import handling.channel.handler.PlayerHandler;
import handling.channel.handler.PlayerInteractionHandler;
import handling.channel.handler.PlayersHandler;
import handling.channel.handler.StatsHandling;
import handling.channel.handler.SummonHandler;
import handling.channel.handler.UseCashItemHandler;
import handling.channel.handler.UseHammerHandler;
import handling.channel.handler.UserInterfaceHandler;
import handling.login.LoginServer;
import handling.login.handler.CharSelectedHandler;
import handling.login.handler.CharlistRequestHandler;
import handling.login.handler.CheckCharNameHandler;
import handling.login.handler.ClientErrorLogHandler;
import handling.login.handler.CreateCharHandler;
import handling.login.handler.CreateUltimateHandler;
import handling.login.handler.DeleteCharHandler;
import handling.login.handler.LicenseRequestHandler;
import handling.login.handler.LoginPasswordHandler;
import handling.login.handler.MapLoginHandler;
import handling.login.handler.PacketErrorHandler;
import handling.login.handler.ServerStatusRequestHandler;
import handling.login.handler.ServerlistRequestHandler;
import handling.login.handler.SetGenderHandler;
import handling.login.handler.ViewCharHandler;
import handling.login.handler.WithSecondPasswordHandler;
import handling.login.handler.WithoutSecondPasswordHandler;
import handling.netty.MaplePacketDecoder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import handling.netty.MapleSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import scripting.NPCScriptManager;
import server.MTSStorage;
import server.ServerProperties;
import server.Start;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.Pair;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.LoginPacket;

public class MapleServerHandler extends ChannelInboundHandlerAdapter implements MapleServerHandlerMBean {

    public static boolean Log_Packets = true;
    private int channel = -1;
    private static int numDC = 0;
    private static long lastDC = System.currentTimeMillis();
    private boolean cs;
    private final List<String> BlockedIP = new ArrayList();
    private final Map<String, Pair<Long, Byte>> tracker = new ConcurrentHashMap();
    private static final String nl = System.getProperty("line.separator");
    private static final File loggedIPs = new File(System.getProperty("resourcePath") + File.separator + "LogIPs.txt");
    private static final HashMap<String, FileWriter> logIPMap = new HashMap<String, FileWriter>();
    private static final EnumSet<RecvPacketOpcode> blocked = EnumSet.noneOf(RecvPacketOpcode.class);
    private static final EnumSet<RecvPacketOpcode> sBlocked = EnumSet.noneOf(RecvPacketOpcode.class);
    private static final int Log_Size = 10000;
    private static final int Packet_Log_Size = 25;
    private static final ArrayList<LoggedPacket> Packet_Log = new ArrayList<LoggedPacket>(Log_Size);
    private static final ReentrantReadWriteLock Packet_Log_Lock = new ReentrantReadWriteLock();
    private static String Packet_Log_Output = "Packet/PacketLog";
    private static int Packet_Log_Index = 0;

    private MapleServerHandler() {
    }

    public MapleServerHandler(int channel, boolean cs) {
        this.channel = channel;
        this.cs = cs;
    }

    /**
     * 通道失效
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null) {
            byte state = 3;
            if ((Log_Packets) && (!LoginServer.isShutdown()) && (!this.cs) && (this.channel > -1)) {
                state = client.getLoginState();
            }
            if (state != 3) {
                log("Data: " + numDC, "CLOSED", client);
                if (System.currentTimeMillis() - lastDC < 60000L) {
                    numDC += 1;
                    if (numDC > 100) {
                        System.out.println("Writing log...");
                        writeLog();
                        numDC = 0;
                        lastDC = System.currentTimeMillis();
                    }
                } else {
                    numDC = 0;
                    lastDC = System.currentTimeMillis();
                }
            }
            try {
                FileWriter fw = isLoggedIP(ctx);
                if (fw != null) {
                    fw.write("=== Session Closed ===");
                    fw.write(nl);
                    fw.flush();
                }
                client.disconnect(true, this.cs);
            } finally {
                ctx.channel().close();
                ctx.channel().attr(MapleClient.CLIENT_KEY).remove();
            }
        }
        super.channelInactive(ctx);
    }

    /**
     * 用户事件触发
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();

        if (client != null) {
            client.sendPing();
        } else {
            ctx.channel().close();
            return;
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 通道生效 即 sessionOpen
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel nettyChannel = ctx.channel();
        String address = nettyChannel.remoteAddress().toString().split(":")[0];
        if (BlockedIP.contains(address)) {
            nettyChannel.close();
            return;
        }
        final Pair<Long, Byte> track = tracker.get(address);
        byte count;
        if (track == null) {
            count = 1;
        } else {
            count = track.right;
            final long difference = System.currentTimeMillis() - track.left;
            if (difference < 2000) {
                count++;
            } else if (difference > 20000) {
                count = 1;
            }
            if (count >= 10) {
                BlockedIP.add(address);
                tracker.remove(address);
                nettyChannel.close();
                return;
            }
        }
        this.tracker.put(address, new Pair<Long, Byte>(System.currentTimeMillis(), count));

        String IP = address.substring(address.indexOf('/') + 1);
        if (this.channel > -1) {
            if (ChannelServer.getInstance(this.channel).isShutdown()) {
                nettyChannel.close();
                return;
            }
            if (!LoginServer.containsIPAuth(IP)) {
                nettyChannel.close();
                return;
            }
        } else if (cs) {
            if (CashShopServer.isShutdown()) {
                nettyChannel.close();
                return;
            }
        } else if (LoginServer.isShutdown()) {
            nettyChannel.close();
            return;
        }
        LoginServer.removeIPAuth(IP);
        byte[] ivRecv = {70, 114, 122, 82};
        byte[] ivSend = {82, 48, 120, 115};
        ivRecv[3] = (byte) (int) (Math.random() * 255.0D);
        ivSend[3] = (byte) (int) (Math.random() * 255.0D);
        MapleAESOFB sendCypher = new MapleAESOFB(ivSend, (short) (0xFFFF - ServerConstants.MAPLE_VERSION));
        MapleAESOFB recvCypher = new MapleAESOFB(ivRecv, ServerConstants.MAPLE_VERSION);
        MapleSession mapleSession = new MapleSession(nettyChannel);
        final MapleClient client = new MapleClient(sendCypher, recvCypher, mapleSession);
        client.setChannel(this.channel);

        MaplePacketDecoder.DecoderState decoderState = new MaplePacketDecoder.DecoderState();
        nettyChannel.attr(MaplePacketDecoder.DECODER_STATE_KEY).set(decoderState);

        if ((ServerConstants.isVpn(address)) || (!Start.instance.isIvCheck())) {
            nettyChannel.writeAndFlush(LoginPacket.getHello(ServerConstants.MAPLE_VERSION, ivSend, ivRecv));
        } else {
            nettyChannel.writeAndFlush(LoginPacket.getHello(ServerConstants.MAPLE_VERSION, ivSend, ivRecv, false));
        }
        nettyChannel.attr(MapleClient.CLIENT_KEY).set(client);
        if (ServerProperties.ShowPacket()) {
            ServerProperties.loadSettings();
            ServerProperties.loadSkills();
            RecvPacketOpcode.reloadValues();
            SendPacketOpcode.reloadValues();
        }
        StringBuilder sb = new StringBuilder();
        if (this.channel > -1) {
            sb.append("[Channel Server] Channel ").append(this.channel).append(" : ");
        } else if (this.cs) {
            sb.append("[Cash Server] ");
        } else {
            sb.append("[系统提示] ");
        }
        sb.append("玩家连接IP ").append(address);
        System.out.println(sb.toString());

        FileWriter fw = isLoggedIP(ctx);
        if (fw != null) {
            if (this.channel > -1) {
                client.setMonitored(true);
                fw.write("=== Logged Into Channel " + this.channel + " ===");
                fw.write(nl);
            } else if (this.cs) {
                client.setMonitored(true);
                fw.write("=== Logged Into CashShop Server ===");
                fw.write(nl);
            } else {
                fw.write("=== Logged Into Login Server ===");
                fw.write(nl);
            }
            fw.flush();
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if ((msg == null) || (ctx == null) || ctx.channel() == null) {
            return;
        }
        LittleEndianAccessor slea = new LittleEndianAccessor(new ByteArrayByteStream((byte[]) msg));
        if (slea.available() < 2L) {
            return;
        }
        Channel nettyChannel = ctx.channel();
        MapleClient c = nettyChannel.attr(MapleClient.CLIENT_KEY).get();
        if ((c == null) || (!c.isReceiving())) {
            return;
        }
        short header_num = slea.readShort();
        for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            if (recv.getValue() == header_num) {
                if ((recv.NeedsChecking())
                        && (!c.isLoggedIn())) {
                    return;
                }
                try {
                    if ((c.isLocalhost()) && ((!blocked.contains(recv)) || (ServerConstants.Use_Localhost))) {
                        String str = "Received data : " + recv.toString() + "\r\n" + HexTool.toString((byte[]) msg) + "\r\n" + HexTool.toStringFromAscii((byte[]) msg);
                        System.out.println(str);
                    }
                    if ((c.getPlayer() != null) && (c.isMonitored()) && (!blocked.contains(recv))) {
                        FileWriter fw = new FileWriter(new File("MonitorLogs/" + c.getPlayer().getName() + "_log.txt"), true);
                        fw.write(String.valueOf(recv) + " (" + Integer.toHexString(header_num) + ") Handled: \r\n" + slea.toString() + "\r\n");
                        fw.flush();
                        fw.close();
                    }

                    if ((Log_Packets) && (!blocked.contains(recv)) && (!sBlocked.contains(recv)) && ((this.cs) || (this.channel > -1))) {
                        log(slea.toString(), recv.toString(), c);
                    }
                    handlePacket(recv, slea, c, this.cs);

                    FileWriter fw = isLoggedIP(ctx);
                    if ((fw != null) && (!blocked.contains(recv))) {
                        if (recv == RecvPacketOpcode.PLAYER_LOGGEDIN) {
                            fw.write(">> [AccountName: " + (c.getAccountName() == null ? "null" : c.getAccountName()) + "] | [IGN: " + ((c.getPlayer() == null) || (c.getPlayer().getName() == null) ? "null" : c.getPlayer().getName()) + "] | [Time: " + FileoutputUtil.CurrentReadable_Time() + "]");

                            fw.write(nl);
                        }
                        fw.write("[" + recv.toString() + "]" + slea.toString(true));
                        fw.write(nl);
                        fw.flush();
                    }
                } catch (Exception e) {
                    FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
                    FileoutputUtil.log("log\\Packet_Except.log", "Packet: " + header_num + "\r\n" + slea.toString(true));
                }
                return;
            }
        }
        if (c.isLocalhost()) {
            System.out.println("Received data : (Unhandled)\r\n" + HexTool.toString((byte[]) msg) + "\r\n" + HexTool.toStringFromAscii((byte[]) msg));
        }
        super.channelRead(ctx, msg);
    }


    public static void reloadLoggedIPs() {
        for (FileWriter fw : logIPMap.values()) {
            if (fw != null) {
                try {
                    fw.write("=== Closing Log ===");
                    fw.write(nl);
                    fw.flush();
                    fw.close();
                } catch (IOException ex) {
                    System.out.println("Error closing Packet Log.");
                    ex.printStackTrace();
                }
            }
        }
        logIPMap.clear();
        try {
            Scanner sc = new Scanner(loggedIPs);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.length() > 0) {
                    addIP(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not reload packet logged IPs.");
            e.printStackTrace();
        }
    }

    private static FileWriter isLoggedIP(ChannelHandlerContext ctx) {
        String a = ctx.channel().remoteAddress().toString();
        String realIP = a.substring(a.indexOf('/') + 1, a.indexOf(':'));
        return logIPMap.get(realIP);
    }

    public static void addIP(String theIP) {
        try {
            FileWriter fw = new FileWriter(new File("PacketLog_" + theIP + ".txt"), true);
            fw.write("=== Creating Log ===");
            fw.write(nl);
            fw.flush();
            logIPMap.put(theIP, fw);
        } catch (IOException e) {
            FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
        }
    }


    public static void log(String packet, String op, MapleClient c) {
        try {
            Packet_Log_Lock.writeLock().lock();
            LoggedPacket logged = null;
            if (Packet_Log.size() == Log_Size) {
                logged = Packet_Log.remove(0);
            }

            if (logged == null) {
                logged = new LoggedPacket(packet, op, c.getTempIP(), c.getAccID(), FileoutputUtil.CurrentReadable_Time(), c.getAccountName() == null ? "[Null]" : c.getAccountName(), c.getPlayer() == null || c.getPlayer().getName() == null ? "[Null]" : c.getPlayer().getName(), c.getPlayer() == null || c.getPlayer().getMap() == null ? "[Null]" : String.valueOf(c.getPlayer().getMapId()), NPCScriptManager.getInstance().getCM(c) == null ? "[Null]" : String.valueOf(NPCScriptManager.getInstance().getCM(c).getNpc()));
            } else {
                logged.setInfo(packet, op, c.getTempIP(), c.getAccID(), FileoutputUtil.CurrentReadable_Time(), c.getAccountName() == null ? "[Null]" : c.getAccountName(), c.getPlayer() == null || c.getPlayer().getName() == null ? "[Null]" : c.getPlayer().getName(), c.getPlayer() == null || c.getPlayer().getMap() == null ? "[Null]" : String.valueOf(c.getPlayer().getMapId()), NPCScriptManager.getInstance().getCM(c) == null ? "[Null]" : String.valueOf(NPCScriptManager.getInstance().getCM(c).getNpc()));
            }

            Packet_Log.add(logged);
        } finally {
            Packet_Log_Lock.writeLock().unlock();
        }
    }

    private static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            MapleServerHandler mbean = new MapleServerHandler();

            mBeanServer.registerMBean(mbean, new ObjectName("handling:type=MapleServerHandler"));
        } catch (Exception e) {
            System.out.println("Error registering PacketLog MBean");
        }
    }

    @Override
    public void writeLog() {
        writeLog(false);
    }

    private void writeLog(boolean crash) {
        Packet_Log_Lock.readLock().lock();
        try {
            FileWriter fw = new FileWriter(new File(Packet_Log_Output + Packet_Log_Index + (crash ? "_DC.txt" : ".txt")), true);
            String nl = System.getProperty("line.separator");
            for (LoggedPacket loggedPacket : Packet_Log) {
                fw.write(loggedPacket.toString());
                fw.write(nl);
            }
            String logString = "Log has been written at " + lastDC + " [" + FileoutputUtil.CurrentReadable_Time() + "] - " + numDC + " have disconnected, within " + (System.currentTimeMillis() - lastDC) + " milliseconds. (" + System.currentTimeMillis() + ")";
            System.out.println(logString);
            fw.write(logString);
            fw.write(nl);
            fw.flush();
            fw.close();
            Packet_Log.clear();
            Packet_Log_Index += 1;
            if (Packet_Log_Index > Packet_Log_Size) {
                Packet_Log_Index = 0;
                Log_Packets = false;
            }
        } catch (IOException ex) {
            System.out.println("Error writing log to file.");
        } finally {
            Packet_Log_Lock.readLock().unlock();
        }
    }

    public static void initiate() {
        reloadLoggedIPs();
        RecvPacketOpcode[] block = {RecvPacketOpcode.PONG};
        RecvPacketOpcode[] serverBlock = {RecvPacketOpcode.PONG};
        blocked.addAll(Arrays.asList(block));
        sBlocked.addAll(Arrays.asList(serverBlock));
        if (Log_Packets) {
            for (int i = 1; i <= Packet_Log_Size; i++) {
                if ((!new File(Packet_Log_Output + i + ".txt").exists()) && (!new File(Packet_Log_Output + i + "_DC.txt").exists())) {
                    Packet_Log_Index = i;
                    break;
                }
            }
            if (Packet_Log_Index <= 0) {
                Log_Packets = false;
            }
        }
        registerMBean();
    }

    public static void handlePacket(RecvPacketOpcode header, LittleEndianAccessor slea, MapleClient c, boolean cs)  {
        switch (header) {
            case PONG:
                c.pongReceived();
                break;
            case RSA_KEY:
                break;
            case CLIENT_ERROR:
                ClientErrorLogHandler.handlePacket(slea, c);
                break;
            case CLIENT_ERROR1:
                PacketErrorHandler.handlePacket(slea, c);
                break;
            case LOGIN_PASSWORD:
                LoginPasswordHandler.handlePacket(slea, c);
                break;
            case LICENSE_REQUEST:
                LicenseRequestHandler.handlePacket(slea, c);
                break;
            case SET_GENDER:
                SetGenderHandler.handlePacket(slea, c);
                break;
            case SEND_ENCRYPTED:
                if (c.isLocalhost()) {
                    LoginPasswordHandler.handlePacket(slea, c);
                } else {
                    c.getSession().write(LoginPacket.getCustomEncryption());
                }
                break;
            case CLIENT_START:
            case CLIENT_FAILED:
                c.getSession().write(LoginPacket.getCustomEncryption());
                break;
            case VIEW_SERVERLIST:
                if (slea.readByte() != 0) {
                    break;
                }
                ServerlistRequestHandler.handlePacket(c);
                break;
            case REDISPLAY_SERVERLIST:
            case SERVERLIST_REQUEST:
                ServerlistRequestHandler.handlePacket(c);
                break;
            case CLIENT_HELLO:
                MapLoginHandler.handlePacket(slea, c);
                break;
            case CHARLIST_REQUEST:
                CharlistRequestHandler.handlePacket(slea, c);
                break;
            case SERVERSTATUS_REQUEST:
                ServerStatusRequestHandler.handlePacket(c);
                break;
            case CHECK_CHAR_NAME:
                CheckCharNameHandler.handlePacket(slea, c);
                break;
            case CREATE_CHAR:
                CreateCharHandler.handlePacket(slea, c);
                break;
            case CREATE_ULTIMATE:
                CreateUltimateHandler.handlePacket(slea, c);
                break;
            case DELETE_CHAR:
                DeleteCharHandler.handlePacket(slea, c);
                break;
            case VIEW_ALL_CHAR:
                ViewCharHandler.handlePacket(slea, c);
                break;
            case PICK_ALL_CHAR:
                WithoutSecondPasswordHandler.handlePacket(slea, c, false, true);
                break;
            case CHAR_SELECT_NO_PIC:
                WithoutSecondPasswordHandler.handlePacket(slea, c, false, false);
                break;
            case VIEW_REGISTER_PIC:
                WithoutSecondPasswordHandler.handlePacket(slea, c, true, true);
                break;
            case CHAR_SELECT:
                CharSelectedHandler.handlePacket(slea, c);
                break;
            case VIEW_SELECT_PIC:
                WithSecondPasswordHandler.handlePacket(slea, c, true);
                break;
            case AUTH_SECOND_PASSWORD:
                WithSecondPasswordHandler.handlePacket(slea, c, false);
                break;
            // case 28:
            //  break;
            case CHANGE_CHANNEL:
                InterServerHandler.ChangeChannel(slea, c, c.getPlayer());
                break;
            case PLAYER_LOGGEDIN:
                int playerid = slea.readInt();
                if (cs) {
                    CashShopOperation.EnterCS(playerid, c);
                } else {
                    InterServerHandler.Loggedin(playerid, c);
                }
                break;
            case ENTER_PVP:
            case ENTER_PVP_PARTY:
                PlayersHandler.EnterPVP(slea, c);
                break;
            case PVP_RESPAWN:
                PlayersHandler.RespawnPVP(slea, c);
                break;
            case LEAVE_PVP://进入pvp
                PlayersHandler.LeavePVP(slea, c);
                break;
            case PVP_ATTACK://PVP攻击
                PlayersHandler.AttackPVP(slea, c);
                break;
            case PVP_SUMMON:
                SummonHandler.SummonPVP(slea, c);
                break;
            case ENTER_CASH_SHOP:
                InterServerHandler.EnterCS(c, c.getPlayer());
                break;
            case ENTER_MTS:
                InterServerHandler.EnterMTS(c, c.getPlayer());
                break;
            case MOVE_PLAYER:
                PlayerHandler.MovePlayer(slea, c, c.getPlayer());
                break;
            case CHAR_INFO_REQUEST:
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.CharInfoRequest(slea.readInt(), c, c.getPlayer());
                break;
            case CLOSE_RANGE_ATTACK:
                PlayerHandler.closeRangeAttack(slea, c, c.getPlayer(), false);
                break;
            case RANGED_ATTACK:
                PlayerHandler.rangedAttack(slea, c, c.getPlayer());
                break;
            case MAGIC_ATTACK:
                PlayerHandler.MagicDamage(slea, c, c.getPlayer());
                break;
            case SPECIAL_MOVE:
                PlayerHandler.SpecialMove(slea, c, c.getPlayer());
                break;
            case PASSIVE_ENERGY:
                PlayerHandler.closeRangeAttack(slea, c, c.getPlayer(), true);
                break;
            case GET_BOOK_INFO:
                PlayersHandler.MonsterBookInfoRequest(slea, c, c.getPlayer());
                break;
            case CHANGE_SET:
                PlayersHandler.ChangeSet(slea, c, c.getPlayer());
                break;
            case PROFESSION_INFO:
                ItemMakerHandler.ProfessionInfo(slea, c);
                break;
            case CRAFT_DONE:
                ItemMakerHandler.CraftComplete(slea, c, c.getPlayer());
                break;
            case CRAFT_MAKE:
                ItemMakerHandler.CraftMake(slea, c, c.getPlayer());
                break;
            case CRAFT_EFFECT:
                ItemMakerHandler.CraftEffect(slea, c, c.getPlayer());
                break;
            case START_HARVEST:
                ItemMakerHandler.StartHarvest(slea, c, c.getPlayer());
                break;
            case STOP_HARVEST:
                ItemMakerHandler.StopHarvest(slea, c, c.getPlayer());
                break;
            case MAKE_EXTRACTOR:
                ItemMakerHandler.MakeExtractor(slea, c, c.getPlayer());
                break;
            case USE_BAG:
                ItemMakerHandler.UseBag(slea, c, c.getPlayer());
                break;
            case USE_FAMILIAR:
                MobHandler.UseFamiliar(slea, c, c.getPlayer());
                break;
            case SPAWN_FAMILIAR:
                MobHandler.SpawnFamiliar(slea, c, c.getPlayer());
                break;
            case RENAME_FAMILIAR:
                MobHandler.RenameFamiliar(slea, c, c.getPlayer());
                break;
            case MOVE_FAMILIAR:
                MobHandler.MoveFamiliar(slea, c, c.getPlayer());
                break;
            case ATTACK_FAMILIAR:
                MobHandler.AttackFamiliar(slea, c, c.getPlayer());
                break;
            case TOUCH_FAMILIAR:
                MobHandler.TouchFamiliar(slea, c, c.getPlayer());
                break;
            case USE_RECIPE:
                ItemMakerHandler.UseRecipe(slea, c, c.getPlayer());
                break;
            // case USE_NEBULITE://使用星岩
            case USE_NEBULITE://使用星岩    
                InventoryHandler.UseNebulite(slea, c, c.getPlayer());
                break;
            case MOVE_ANDROID://安卓移动
                PlayerHandler.MoveAndroid(slea, c, c.getPlayer());
                break;
            case FACE_EXPRESSION://人物面部表情
                PlayerHandler.ChangeEmotion(slea.readInt(), c.getPlayer());
                break;
            case FACE_ANDROID://安卓面部表情
                PlayerHandler.ChangeAndroidEmotion(slea.readInt(), c.getPlayer());
                break;
            case TAKE_DAMAGE://角色受到伤害
                PlayerHandler.TakeDamage(slea, c, c.getPlayer());
                break;
            case HEAL_OVER_TIME:
                PlayerHandler.Heal(slea, c.getPlayer());
                break;
            case CANCEL_BUFF:
                PlayerHandler.CancelBuffHandler(slea.readInt(), c.getPlayer());
                break;
            case MECH_CANCEL:
                PlayerHandler.CancelMech(slea, c.getPlayer());
                break;
            case CANCEL_ITEM_EFFECT:
                PlayerHandler.CancelItemEffect(slea.readInt(), c.getPlayer());
                break;
            case USE_CHAIR:
                PlayerHandler.UseChair(slea.readInt(), c, c.getPlayer());
                break;
            case CANCEL_CHAIR:
                PlayerHandler.CancelChair(slea.readShort(), c, c.getPlayer());
                break;
            case USE_ITEM_EFFECT:
                PlayerHandler.UseItemEffect(slea.readInt(), c, c.getPlayer());
                break;
            //  case 75:
            //    break;
            case USE_TITLE_EFFECT:
                PlayerHandler.UseTitleEffect(slea.readInt(), c, c.getPlayer());
                break;
            //   case 77:
            //   break;
            case SKILL_EFFECT:
                PlayerHandler.SkillEffect(slea, c.getPlayer());
                break;
            case QUICK_SLOT:
                PlayerHandler.QuickSlot(slea, c.getPlayer());
                break;
            case MESO_DROP:
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.DropMeso(slea.readInt(), c.getPlayer());
                break;
            case CHANGE_KEYMAP:
                PlayerHandler.ChangeKeymap(slea, c.getPlayer());
                break;
            case CHANGE_MAP:
                if (cs) {
                    CashShopOperation.LeaveCS(slea, c, c.getPlayer());
                } else {
                    PlayerHandler.ChangeMap(slea, c, c.getPlayer());
                }
                break;
            case CHANGE_MAP_SPECIAL:
                slea.skip(1);
                PlayerHandler.ChangeMapSpecial(slea.readMapleAsciiString(), c, c.getPlayer());
                break;
            case USE_INNER_PORTAL:
                slea.skip(1);
                PlayerHandler.InnerPortal(slea, c, c.getPlayer());
                break;
            case TROCK_ADD_MAP:
                PlayerHandler.TrockAddMap(slea, c, c.getPlayer());
                break;
            //   case 86:
            //     break;
            case LIE_DETECTOR:
                PlayersHandler.LieDetector(slea, c, c.getPlayer(), false);
                break;
            case LIE_DETECTOR_RESPONSE:
                PlayersHandler.LieDetectorResponse(slea, c);
                break;
            case LIE_DETECTOR_REFRESH:
                PlayersHandler.LieDetectorRefresh(slea, c);
                break;
            case ARAN_COMBO:
                PlayerHandler.AranCombo(c, c.getPlayer(), 1);
                break;
            case SKILL_MACRO:
                PlayerHandler.ChangeSkillMacro(slea, c.getPlayer());
                break;
            case GIVE_FAME:
                PlayersHandler.GiveFame(slea, c, c.getPlayer());
                break;
            case TRANSFORM_PLAYER:
                PlayersHandler.TransformPlayer(slea, c, c.getPlayer());
                break;
            case NOTE_ACTION:
                PlayersHandler.Note(slea, c.getPlayer());
                break;
            case USE_DOOR:
                PlayersHandler.UseDoor(slea, c.getPlayer());
                break;
            case USE_MECH_DOOR:
                PlayersHandler.UseMechDoor(slea, c.getPlayer());
                break;
            case DAMAGE_REACTOR:
                PlayersHandler.HitReactor(slea, c);
                break;
            case CLICK_REACTOR://重新领取勋章
            case TOUCH_REACTOR://双击反应堆
                PlayersHandler.TouchReactor(slea, c);
                break;
            case CLOSE_CHALKBOARD:
                c.getPlayer().setChalkboard(null);
                break;
            case ITEM_SORT:
                InventoryHandler.ItemSort(slea, c);
                break;
            case ITEM_GATHER:
                InventoryHandler.ItemGather(slea, c);
                break;
            case ITEM_MOVE:
                InventoryHandler.ItemMove(slea, c);
                break;
            case MOVE_BAG:
                InventoryHandler.MoveBag(slea, c);
                break;
            case SWITCH_BAG:
                InventoryHandler.SwitchBag(slea, c);
                break;
            case ITEM_MAKER:
                ItemMakerHandler.ItemMaker(slea, c);
                break;
            case ITEM_PICKUP:
                InventoryHandler.Pickup_Player(slea, c, c.getPlayer());
                break;
            case USE_CASH_ITEM:
                UseCashItemHandler.handlePacket(slea, c, c.getPlayer());
                break;
            case USE_ITEM:
                InventoryHandler.UseItem(slea, c, c.getPlayer());
                break;
            case USE_COSMETIC:
                InventoryHandler.UseCosmetic(slea, c, c.getPlayer());
                break;
            case USE_MAGNIFY_GLASS://放大镜处理
                InventoryHandler.UseMagnify(slea, c, c.getPlayer());
                break;
            case USE_SCRIPTED_NPC_ITEM:
                InventoryHandler.UseScriptedNPCItem(slea, c, c.getPlayer());
                break;
            case USE_RETURN_SCROLL:
                InventoryHandler.UseReturnScroll(slea, c, c.getPlayer());
                break;
            case USE_UPGRADE_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll(slea.readShort(), slea.readShort(), slea.readShort(), c, c.getPlayer(), false);
                break;
            case USE_FLAG_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll(slea.readShort(), slea.readShort(), (short) 0, c, c.getPlayer(), true);
                break;
            case USE_POTENTIAL_SCROLL:
            case USE_EQUIP_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll(slea.readShort(), slea.readShort(), (short) 0, c, c.getPlayer(), false);
                break;
            case USE_SUMMON_BAG:
                InventoryHandler.UseSummonBag(slea, c, c.getPlayer());
                break;
            case USE_TREASUER_CHEST:
                InventoryHandler.UseTreasureChest(slea, c, c.getPlayer());
                break;
            case USE_SKILL_BOOK:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseSkillBook((byte) slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            case USE_SP_RESET:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseSpReset((byte) slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            case USE_AP_RESET:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseApReset((byte) slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            case USE_CATCH_ITEM:
                InventoryHandler.UseCatchItem(slea, c, c.getPlayer());
                break;
            case USE_MOUNT_FOOD:
                InventoryHandler.UseMountFood(slea, c, c.getPlayer());
                break;
            case REWARD_ITEM:
                InventoryHandler.UseRewardItem((byte) slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            case HYPNOTIZE_DMG:
                MobHandler.HypnotizeDmg(slea, c.getPlayer());
                break;
            case MOB_NODE:
                MobHandler.MobNode(slea, c.getPlayer());
                break;
            case DISPLAY_NODE:
                MobHandler.DisplayNode(slea, c.getPlayer());
                break;
            case MOVE_LIFE:
                MobHandler.MoveMonster(slea, c, c.getPlayer());
                break;
            case AUTO_AGGRO:
                MobHandler.AutoAggro(slea.readInt(), c.getPlayer());
                break;
            case FRIENDLY_DAMAGE:
                MobHandler.FriendlyDamage(slea, c.getPlayer());
                break;
            case REISSUE_MEDAL:
                PlayerHandler.ReIssueMedal(slea, c, c.getPlayer());
                break;
            case MONSTER_BOMB:
                MobHandler.MonsterBomb(slea.readInt(), c.getPlayer());
                break;
            case MOB_BOMB:
                MobHandler.MobBomb(slea, c.getPlayer());
                break;
            case NPC_SHOP:
                NPCHandler.NPCShop(slea, c, c.getPlayer());
                break;
            case NPC_TALK:
                NPCHandler.NPCTalk(slea, c, c.getPlayer());
                break;
            case NPC_TALK_MORE:
                NPCHandler.NPCMoreTalk(slea, c);
                break;
            case NPC_ACTION:
                NPCHandler.NPCAnimation(slea, c);
                break;
            case QUEST_ACTION:
                NPCHandler.QuestAction(slea, c, c.getPlayer());
                break;
            case MEDAL_QUEST_ACTION:
                NPCHandler.MedalQuestAction(slea, c, c.getPlayer());
                break;
            case STORAGE://仓库操作
                NPCHandler.Storage(slea, c, c.getPlayer());
                break;
            case GENERAL_CHAT:
                if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
                    break;
                }
                c.getPlayer().updateTick(slea.readInt());
                ChatHandler.GeneralChat(slea.readMapleAsciiString(), slea.readByte(), c, c.getPlayer());
                break;
            case PARTYCHAT:
                ChatHandler.Others(slea, c, c.getPlayer());
                break;
            case WHISPER:
                ChatHandler.Whisper_Find(slea, c);
                break;
            case MESSENGER:
                ChatHandler.Messenger(slea, c);
                break;
            case AUTO_ASSIGN_AP:
                StatsHandling.AutoAssignAP(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_AP:
                StatsHandling.DistributeAP(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_SP:
                c.getPlayer().updateTick(slea.readInt());
                StatsHandling.DistributeSP(slea.readInt(), c, c.getPlayer());
                break;
            case PLAYER_INTERACTION:
                PlayerInteractionHandler.PlayerInteraction(slea, c, c.getPlayer());
                break;
            case GUILD_OPERATION:
                GuildHandler.Guild(slea, c);
                break;
            case DENY_GUILD_REQUEST:
                slea.skip(1);
                GuildHandler.DenyGuildRequest(slea.readMapleAsciiString(), c);
                break;
            case ALLIANCE_OPERATION:
                AllianceHandler.HandleAlliance(slea, c, false);
                break;
            case DENY_ALLIANCE_REQUEST:
                AllianceHandler.HandleAlliance(slea, c, true);
                break;
            case QUICK_MOVE:
                NPCHandler.OpenQuickMoveNpc(slea, c);
                break;
            case BBS_OPERATION:
                BBSHandler.BBSOperation(slea, c);
                break;
            case PARTY_OPERATION:
                PartyHandler.PartyOperation(slea, c);
                break;
            case DENY_PARTY_REQUEST:
                PartyHandler.DenyPartyRequest(slea, c);
                break;
            case ALLOW_PARTY_INVITE:
                PartyHandler.AllowPartyInvite(slea, c);
                break;
            case SIDEKICK_OPERATION:
                PartyHandler.SidekickOperation(slea, c);
                break;
            case DENY_SIDEKICK_REQUEST:
                PartyHandler.DenySidekickRequest(slea, c);
                break;
            case BUDDYLIST_MODIFY:
                BuddyListHandler.BuddyOperation(slea, c);
                break;
            case CYGNUS_SUMMON:
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                break;
            case SHIP_OBJECT:
                UserInterfaceHandler.ShipObjectRequest(slea.readInt(), c);
                break;
            case BUY_CS_ITEM:
                BuyCashItemHandler.BuyCashItem(slea, c, c.getPlayer());
                break;
            case COUPON_CODE:
                CouponCodeHandler.handlePacket(slea, c, c.getPlayer());
                break;
            case CS_UPDATE:
                CashShopOperation.CSUpdate(c);
                break;
            case SEND_CS_GIFI:
                BuyCashItemHandler.商城送礼(slea, c, c.getPlayer());
                break;
            case TOUCHING_MTS:
                MTSOperation.MTSUpdate(MTSStorage.getInstance().getCart(c.getPlayer().getId()), c);
                break;
            case MTS_TAB:
                MTSOperation.MTSOperation(slea, c);
                break;
            case USE_POT:
                ItemMakerHandler.UsePot(slea, c);
                break;
            case CLEAR_POT:
                ItemMakerHandler.ClearPot(slea, c);
                break;
            case FEED_POT:
                ItemMakerHandler.FeedPot(slea, c);
                break;
            case CURE_POT:
                ItemMakerHandler.CurePot(slea, c);
                break;
            case REWARD_POT:
                ItemMakerHandler.RewardPot(slea, c);
                break;
            case DAMAGE_SUMMON:
                slea.skip(4);
                SummonHandler.DamageSummon(slea, c.getPlayer());
                break;
            case MOVE_SUMMON:
                SummonHandler.MoveSummon(slea, c.getPlayer());
                break;
            case SUMMON_ATTACK:
                SummonHandler.SummonAttack(slea, c, c.getPlayer());
                break;
            case MOVE_DRAGON:
                SummonHandler.MoveDragon(slea, c.getPlayer());
                break;
            case SUB_SUMMON:
                SummonHandler.SubSummon(slea, c.getPlayer());
                break;
            case REMOVE_SUMMON:
                SummonHandler.RemoveSummon(slea, c);
                break;
            case SPAWN_PET:
                PetHandler.SpawnPet(slea, c, c.getPlayer());
                break;
            case PET_AUTO_BUFF:
                PetHandler.Pet_AutoBuff(slea, c, c.getPlayer());
                break;
            case MOVE_PET:
                PetHandler.MovePet(slea, c.getPlayer());
                break;
            case PET_CHAT:
                PetHandler.PetChat(slea, c, c.getPlayer());
                break;
            case PET_COMMAND:
                PetHandler.PetCommand(slea, c, c.getPlayer());
                break;
            case PET_FOOD:
                PetHandler.PetFood(slea, c, c.getPlayer());
                break;
            case PET_LOOT:
                InventoryHandler.Pickup_Pet(slea, c, c.getPlayer());
                break;
            case PET_AUTO_POT:
                PetHandler.Pet_AutoPotion(slea, c, c.getPlayer());
                break;
            case PET_EXCEPTION_LIST:
                PetHandler.PetExcludeItems(slea, c, c.getPlayer());
                break;
            case MONSTER_CARNIVAL:
                MonsterCarnivalHandler.MonsterCarnival(slea, c);
                break;
            case DUEY_ACTION:
                DueyHandler.DueyOperation(slea, c);
                break;
            case USE_HIRED_MERCHANT:
                HiredMerchantHandler.UseHiredMerchant(c, true);
                break;
            case MERCH_ITEM_STORE:
                HiredMerchantHandler.MerchantItemStore(slea, c);
                break;
            case CANCEL_DEBUFF:
                break;
            case MAPLETV:
                break;
            case LEFT_KNOCK_BACK:
                PlayerHandler.leftKnockBack(slea, c);
                break;
            case SNOWBALL:
                PlayerHandler.snowBall(slea, c);
                break;
            case COCONUT:
                PlayersHandler.hitCoconut(slea, c);
                break;
            case REPAIR:
                NPCHandler.repair(slea, c);
                break;
            case REPAIR_ALL:
                NPCHandler.repairAll(c);
                break;
            case GAME_POLL:
                UserInterfaceHandler.InGame_Poll(slea, c);
                break;
            case OWL:
                InventoryHandler.Owl(slea, c);
                break;
            case OWL_WARP:
                InventoryHandler.OwlWarp(slea, c);
                break;
            case USE_OWL_MINERVA:
                InventoryHandler.OwlMinerva(slea, c);
                break;
            case RPS_GAME:
                NPCHandler.RPSGame(slea, c);
                break;
            case UPDATE_QUEST:
                NPCHandler.UpdateQuest(slea, c);
                break;
            case USE_ITEM_QUEST:
                NPCHandler.UseItemQuest(slea, c);
                break;
            case FOLLOW_REQUEST:
                PlayersHandler.FollowRequest(slea, c);
                break;
            case AUTO_FOLLOW_REPLY:
            case FOLLOW_REPLY:
                PlayersHandler.FollowReply(slea, c);
                break;
            case RING_ACTION:
                PlayersHandler.RingAction(slea, c);
                break;
            case REQUEST_FAMILY:
                FamilyHandler.RequestFamily(slea, c);
                break;
            case OPEN_FAMILY:
                FamilyHandler.OpenFamily(slea, c);
                break;
            case FAMILY_OPERATION:
                FamilyHandler.FamilyOperation(slea, c);
                break;
            case DELETE_JUNIOR:
                FamilyHandler.DeleteJunior(slea, c);
                break;
            case DELETE_SENIOR:
                FamilyHandler.DeleteSenior(slea, c);
                break;
            case USE_FAMILY:
                FamilyHandler.UseFamily(slea, c);
                break;
            case FAMILY_PRECEPT:
                FamilyHandler.FamilyPrecept(slea, c);
                break;
            case FAMILY_SUMMON:
                FamilyHandler.FamilySummon(slea, c);
                break;
            case ACCEPT_FAMILY:
                FamilyHandler.AcceptFamily(slea, c);
                break;
            case SOLOMON:
                PlayersHandler.Solomon(slea, c);
                break;
            case GACH_EXP:
                PlayersHandler.GachExp(slea, c);
                break;
            case PARTY_SEARCH_START:
                PartyHandler.PartySearchStart(slea, c);
                break;
            case PARTY_SEARCH_STOP:
                PartyHandler.PartySearchStop(slea, c);
                break;
            case EXPEDITION_LISTING:
                PartyHandler.PartyListing(slea, c);
                break;
            case EXPEDITION_OPERATION:
                PartyHandler.Expedition(slea, c);
                break;
            case USE_TELE_ROCK:
                InventoryHandler.TeleRock(slea, c);
                break;
            case PAM_SONG:
                InventoryHandler.PamSong(slea, c);
                break;
            case REPORT:
                PlayersHandler.Report(slea, c);
                break;
            case REMOTE_STORE:
                HiredMerchantHandler.RemoteStore(slea, c);
                break;
            case SHIKONGJUAN:
                PlayerHandler.超时空卷(slea, c, c.getPlayer());
                break;
            case PLAYER_UPDATE:
                PlayerHandler.PlayerUpdate(c, c.getPlayer());
                break;
            case CHANGE_MARKET_MAP://自由市场换图
                PlayerHandler.ChangeMarketMap(slea, c, c.getPlayer());
                break;
            case PHANTOM_EQUIP_RECV://印技树 装备 删除 技能
                PlayerHandler.getphantomequip(slea, c.getPlayer());
                break;
            case PHANTOM_VIEW_RECV://幻影复制技能成功
                PlayerHandler.getphantomview(slea, c.getPlayer());
                break;
            case PHANTOM_SKILL_RECV://幻影复制技能
                PlayerHandler.getphantomskill(slea.readInt(), c.getPlayer());
                break;
            case TEACH_SKILL://传授技能
                PlayerHandler.TeachSkill(slea, c, c.getPlayer());
                break;
            case USE_HAMMER:
                UseHammerHandler.handlePacket(slea, c);
                break;
            default:
                System.out.println("[UNHANDLED] Recv [" + header.toString() + "] found");
        }
    }

    private static class LoggedPacket {

        private static final String nl = System.getProperty("line.separator");
        private String ip;
        private String accName;
        private String accId;
        private String chrName;
        private String packet;
        private String mapId;
        private String npcId;
        private String op;
        private String time;
        private long timestamp;

        public LoggedPacket(String p, String op, String ip, int id, String time, String accName, String chrName, String mapId, String npcId) {
            setInfo(p, op, ip, id, time, accName, chrName, mapId, npcId);
        }

        public LoggedPacket(SeekableLittleEndianAccessor packet, RecvPacketOpcode op, String toString, int i, String s, String s1) {
        }

        public final void setInfo(String p, String op, String ip, int id, String time, String accName, String chrName, String mapId, String npcId) {
            this.ip = ip;
            this.op = op;
            this.time = time;
            this.packet = p;
            this.accName = accName;
            this.chrName = chrName;
            this.mapId = mapId;
            this.npcId = npcId;
            this.timestamp = System.currentTimeMillis();
            this.accId = String.valueOf(id);
        }

        @Override
        public String toString() {
            return "[IP: " + this.ip + "] [" + this.accId + '|' + this.accName + '|' + this.chrName + "] [" + this.npcId + '|' + this.mapId + "] [Time: " + this.timestamp + "] [" + this.time + ']' +
                    nl + "[Op: " + this.op + "] [" + this.packet + ']';
        }
    }
}