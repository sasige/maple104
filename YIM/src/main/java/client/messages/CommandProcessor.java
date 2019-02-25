package client.messages;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.commands.*;
import constants.ServerConstants;
import constants.ServerConstants.CommandType;
import database.DatabaseConnection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import tools.FileoutputUtil;

public class CommandProcessor {

    private static final HashMap<String, CommandObject> commands = new HashMap();
    private static final HashMap<Integer, ArrayList<String>> commandList = new HashMap();

    private static void sendDisplayMessage(MapleClient c, String msg, CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL:
                c.getPlayer().dropMessage(6, msg);
                break;
            case TRADE:
                c.getPlayer().dropMessage(-2, new StringBuilder().append("Error : ").append(msg).toString());
                break;
            case POKEMON:
                c.getPlayer().dropMessage(-3, new StringBuilder().append("(...").append(msg).append("..)").toString());
        }
    }

    public static void dropHelp(MapleClient c) {
        StringBuilder sb = new StringBuilder("Command list: ");
        for (int i = 0; i <= c.getPlayer().getGMLevel(); i++) {
            if (commandList.containsKey(Integer.valueOf(i))) {
                for (String s : commandList.get(i)) {
                    sb.append(s);
                    sb.append(" ");
                }
            }
        }
        c.getPlayer().dropMessage(6, sb.toString());
    }

    public static boolean processCommand(MapleClient c, String line, ServerConstants.CommandType type) {
        if ((line.charAt(0) == ServerConstants.PlayerGMRank.NORMAL.getCommandPrefix()) || ((c.getPlayer().getGMLevel() > ServerConstants.PlayerGMRank.NORMAL.getLevel()) && (line.charAt(0) == ServerConstants.PlayerGMRank.DONATOR.getCommandPrefix()))) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            CommandObject co = (CommandObject) commands.get(splitted[0]);
            if ((co == null) || (co.getType() != type)) {
                sendDisplayMessage(c, "输入的玩家命令不存在.", type);
                return true;
            }
            try {
                int ret = co.execute(c, splitted);
            } catch (Exception e) {
                sendDisplayMessage(c, "There was an error.", type);
                if (c.getPlayer().isGM()) {
                    sendDisplayMessage(c, new StringBuilder().append("Error: ").append(e).toString(), type);
                    FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
                }
            }
            return true;
        }

        if ((c.getPlayer().getGMLevel() > ServerConstants.PlayerGMRank.NORMAL.getLevel()) && ((line.charAt(0) == ServerConstants.PlayerGMRank.SUPERGM.getCommandPrefix()) || (line.charAt(0) == ServerConstants.PlayerGMRank.INTERN.getCommandPrefix()) || (line.charAt(0) == ServerConstants.PlayerGMRank.GM.getCommandPrefix()) || (line.charAt(0) == ServerConstants.PlayerGMRank.ADMIN.getCommandPrefix()))) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            CommandObject co = (CommandObject) commands.get(splitted[0]);
            if (co == null) {
                if (splitted[0].equals(new StringBuilder().append(line.charAt(0)).append("help").toString())) {
                    dropHelp(c);
                    return true;
                }
                sendDisplayMessage(c, "输入的命令不存在.", type);
                return true;
            }
            if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                int ret = 0;
                try {
                    ret = co.execute(c, splitted);
                } catch (ArrayIndexOutOfBoundsException x) {
                    sendDisplayMessage(c, new StringBuilder().append("The command was not used properly: ").append(x).toString(), type);
                } catch (Exception e) {
                    FileoutputUtil.outputFileError("log\\Command_Except.log", e);
                }
                if ((ret > 0) && (c.getPlayer() != null)) {
                    if (c.getPlayer().isGM()) {
                        logCommandToDB(c.getPlayer(), line, "gmlog");
                    } else {
                        logCommandToDB(c.getPlayer(), line, "internlog");
                    }
                }
            } else {
                sendDisplayMessage(c, "您的权限等级不足以使用次命令.", type);
            }
            return true;
        }

        return false;
    }

    private static void logCommandToDB(MapleCharacter player, String command, String table) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement(new StringBuilder().append("INSERT INTO ").append(table).append(" (cid, name, command, mapid) VALUES (?, ?, ?, ?)").toString());
            ps.setInt(1, player.getId());
            ps.setString(2, player.getName());
            ps.setString(3, command);
            ps.setInt(4, player.getMap().getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            FileoutputUtil.outputFileError("log\\Packet_Except.log", ex);
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
            }
        }
    }

    static {
        Class[] CommandFiles = {PlayerCommand.class, InternCommand.class, GMCommand.class, AdminCommand.class, DonatorCommand.class, SuperDonatorCommand.class, SuperGMCommand.class, LvkejianCommand.class};

        for (Class clasz : CommandFiles) {
            try {
                ServerConstants.PlayerGMRank rankNeeded = (ServerConstants.PlayerGMRank) clasz.getMethod("getPlayerLevelRequired", new Class[0]).invoke(null, (Object[]) null);
                Class[] a = clasz.getDeclaredClasses();
                ArrayList<String> cL = new ArrayList<String>();
                for (Class c : a) {
                    try {
                        if ((!Modifier.isAbstract(c.getModifiers())) && (!c.isSynthetic())) {
                            Object o = c.newInstance();
                            boolean enabled;
                            try {
                                enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                            } catch (NoSuchFieldException ex) {
                                enabled = true;
                            }
                            if (((o instanceof CommandExecute)) && (enabled)) {
                                cL.add(new StringBuilder().append(rankNeeded.getCommandPrefix()).append(c.getSimpleName().toLowerCase()).toString());
                                commands.put(new StringBuilder().append(rankNeeded.getCommandPrefix()).append(c.getSimpleName().toLowerCase()).toString(), new CommandObject((CommandExecute) o, rankNeeded.getLevel()));
                                if ((rankNeeded.getCommandPrefix() != ServerConstants.PlayerGMRank.GM.getCommandPrefix()) && (rankNeeded.getCommandPrefix() != ServerConstants.PlayerGMRank.NORMAL.getCommandPrefix())) {
                                    commands.put(new StringBuilder().append("!").append(c.getSimpleName().toLowerCase()).toString(), new CommandObject((CommandExecute) o, ServerConstants.PlayerGMRank.GM.getLevel()));
                                }
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException ex) {
                        FileoutputUtil.outputFileError("log\\Script_Except.log", ex);
                    }
                }
                Collections.sort(cL);
                commandList.put(Integer.valueOf(rankNeeded.getLevel()), cL);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                FileoutputUtil.outputFileError("log\\Script_Except.log", ex);
            }
        }
    }
}