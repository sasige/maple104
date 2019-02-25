package handling.channel.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.anticheat.CheatTracker;
import client.messages.CommandProcessor;
import constants.ServerConstants;
import constants.ServerConstants.CommandType;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.World;
import handling.world.World.Alliance;
import handling.world.World.Broadcast;
import handling.world.World.Buddy;
import handling.world.World.Find;
import handling.world.World.Guild;
import handling.world.World.Messenger;
import handling.world.World.Party;
import org.apache.log4j.Logger;

import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;

public class ChatHandler {

    private static final Logger log = Logger.getLogger(ChatHandler.class);

    public static void GeneralChat(String text, byte unk, MapleClient c, MapleCharacter chr) {
        if ((text.length() > 0) && (chr != null) && (chr.getMap() != null)) {
            if (!CommandProcessor.processCommand(c, text, chr.getBattle() == null ? ServerConstants.CommandType.NORMAL : ServerConstants.CommandType.POKEMON)) {
                if ((!chr.isIntern()) && (text.length() >= 80)) {
                    return;
                }
                log.info("[信息] " + chr.getName() + " : " + text);
                if ((chr.getCanTalk()) || (chr.isStaff())) {
                    if (chr.isHidden()) {
                        if ((chr.isIntern()) && (!chr.isSuperGM()) && (unk == 0)) {
                            chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, false, 1), true);
                            if (unk == 0) {
                                chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.serverNotice(2, chr.getName() + " : " + text), true);
                            }
                        } else {
                            chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), unk), true);
                        }
                    } else {
                        chr.getCheatTracker().checkMsg();
                        if ((chr.isIntern()) && (!chr.isSuperGM()) && (unk == 0)) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, false, 1), c.getPlayer().getTruePosition());
                            if (unk == 0) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(2, chr.getName() + " : " + text), c.getPlayer().getTruePosition());
                            }
                        } else {
                            chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), unk), c.getPlayer().getTruePosition());
                        }
                    }
                    if (text.equalsIgnoreCase("我喜欢" + c.getChannelServer().getServerName())) {
                        chr.finishAchievement(11);
                    }
                } else {
                    c.getSession().write(MaplePacketCreator.serverNotice(6, "You have been muted and are therefore unable to talk."));
                }
            }
        }
    }

    public static void Others(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        int type = slea.readByte();
        byte numRecipients = slea.readByte();
        if (numRecipients <= 0) {
            return;
        }
        int[] recipients = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i = (byte) (i + 1)) {
            recipients[i] = slea.readInt();
        }
        String chattext = slea.readMapleAsciiString();
        if ((chr == null) || (!chr.getCanTalk())) {
            c.getSession().write(MaplePacketCreator.serverNotice(6, "You have been muted and are therefore unable to talk."));
            return;
        }
        log.info("[信息] " + chr.getName() + " : " + chattext);
        if (c.isMonitored()) {
            String chattype = "未知";
            switch (type) {
                case 0:
                    chattype = "好友";
                    break;
                case 1:
                    chattype = "组队";
                    break;
                case 2:
                    chattype = "家族";
                    break;
                case 3:
                    chattype = "联盟";
                    break;
                case 4:
                    chattype = "远征";
            }

            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(chr.getName()) + " said (" + chattype + "): " + chattext));
        }
        if (chattext.length() > 0) {
            if (!CommandProcessor.processCommand(c, chattext, chr.getBattle() == null ? ServerConstants.CommandType.NORMAL : ServerConstants.CommandType.POKEMON));
        } else {
            return;
        }

        chr.getCheatTracker().checkMsg();
        switch (type) {
            case 0:
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                break;
            case 1:
                if (chr.getParty() == null) {
                    break;
                }
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                break;
            case 2:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 3:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 4:
                if ((chr.getParty() == null) || (chr.getParty().getExpeditionId() <= 0)) {
                    break;
                }
                World.Party.expedChat(chr.getParty().getExpeditionId(), chattext, chr.getName());
        }
    }

    public static void Messenger(LittleEndianAccessor slea, MapleClient c) {
        MapleMessenger messenger = c.getPlayer().getMessenger();

        switch (slea.readByte()) {
            case 0:
                if (messenger != null) {
                    break;
                }
                int messengerid = slea.readInt();
                if (messengerid == 0) {
                    c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                } else {
                    messenger = World.Messenger.getMessenger(messengerid);
                    if (messenger != null) {
                        int position = messenger.getLowestPosition();
                        if ((position > -1) && (position < 4)) {
                            c.getPlayer().setMessenger(messenger);
                            World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                        }
                    }
                }
                break;
            case 2:
                if (messenger == null) {
                    break;
                }
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                c.getPlayer().setMessenger(null);
                break;
            case 3:
                if (messenger == null) {
                    break;
                }
                int position = messenger.getLowestPosition();
                if ((position <= -1) || (position >= 4)) {
                    return;
                }
                String input = slea.readMapleAsciiString();
                MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);
                if (target != null) {
                    if (target.getMessenger() == null) {
                        if ((!target.isIntern()) || (c.getPlayer().isIntern())) {
                            c.getSession().write(MaplePacketCreator.messengerNote(input, 4, 1));
                            target.getClient().getSession().write(MaplePacketCreator.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                        } else {
                            c.getSession().write(MaplePacketCreator.messengerNote(input, 4, 0));
                        }
                    } else {
                        c.getSession().write(MaplePacketCreator.messengerChat(c.getPlayer().getName() + " : " + target.getName() + " 正在做别的事情，暂时无法邀请."));
                    }

                } else if (World.isConnected(input)) {
                    World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isIntern());
                } else {
                    c.getSession().write(MaplePacketCreator.messengerNote(input, 4, 0));
                }

                break;
            case 5:
                String targeted = slea.readMapleAsciiString();
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) {
                    if (target.getMessenger() == null) {
                        break;
                    }
                    target.getClient().getSession().write(MaplePacketCreator.messengerNote(c.getPlayer().getName(), 5, 0));
                } else {
                    if (c.getPlayer().isIntern()) {
                        break;
                    }
                    World.Messenger.declineChat(targeted, c.getPlayer().getName());
                }
                break;
            case 6:
                if (messenger == null) {
                    break;
                }
                String chattext = slea.readMapleAsciiString();
                World.Messenger.messengerChat(messenger.getId(), chattext, c.getPlayer().getName());
                if ((!messenger.isMonitored()) || (chattext.length() <= c.getPlayer().getName().length() + 3)) {
                    break;
                }
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(c.getPlayer().getName()) + "(Messenger: " + messenger.getMemberNamesDEBUG() + ") said: " + chattext));
            case 1:
            case 4:
        }
    }

    public static void Whisper_Find(LittleEndianAccessor slea, MapleClient c) {
        byte mode = slea.readByte();
        slea.readInt();
        switch (mode) {
            case 5:
            case 68:
                String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player != null) {
                    if ((!player.isIntern()) || ((c.getPlayer().isIntern()) && (player.isIntern()))) {
                        c.getSession().write(MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                    } else {
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    }
                } else {
                    int ch = World.Find.findChannel(recipient);
                    if (ch > 0) {
                        player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                        if (player == null) {
                            break;
                        }
                        if (player != null) {
                            if ((!player.isIntern()) || ((c.getPlayer().isIntern()) && (player.isIntern()))) {
                                c.getSession().write(MaplePacketCreator.getFindReply(recipient, (byte) ch, mode == 68));
                            } else {
                                c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                            }
                            return;
                        }
                    }
                    if (ch == -10) {
                        c.getSession().write(MaplePacketCreator.getFindReplyWithCS(recipient, mode == 68));
                    } else if (ch == -20) {
                        c.getPlayer().dropMessage(5, "'" + recipient + "' is at the MTS.");
                    } else {
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    }
                }
                break;
            case 6:
                if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
                    return;
                }
                if (!c.getPlayer().getCanTalk()) {
                    c.getSession().write(MaplePacketCreator.serverNotice(6, "You have been muted and are therefore unable to talk."));
                    return;
                }
                c.getPlayer().getCheatTracker().checkMsg();
                recipient = slea.readMapleAsciiString();
                String text = slea.readMapleAsciiString();
                int ch = World.Find.findChannel(recipient);
                if (ch > 0) {
                    player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player == null) {
                        break;
                    }
                    player.getClient().getSession().write(MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                    if ((!c.getPlayer().isIntern()) && (player.isIntern())) {
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    } else {
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
                    }
                    if (c.isMonitored()) {
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, c.getPlayer().getName() + " whispered " + recipient + " : " + text));
                    } else if (player.getClient().isMonitored()) {
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, c.getPlayer().getName() + " whispered " + recipient + " : " + text));
                    }
                } else {
                    c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                }
        }
    }
}