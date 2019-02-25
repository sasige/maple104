package scripting;

import client.MapleClient;
import constants.ServerConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import server.maps.MapleReactor;
import server.maps.ReactorDropEntry;
import tools.FileoutputUtil;

public class ReactorScriptManager extends AbstractScriptManager {

    private static final ReactorScriptManager instance = new ReactorScriptManager();
    private final Map<Integer, List<ReactorDropEntry>> drops = new HashMap();

    public static ReactorScriptManager getInstance() {
        return instance;
    }

    public void act(MapleClient c, MapleReactor reactor) {
        try {
            Invocable iv = getInvocable(ServerConstants.resourcePath+"reactor/" + reactor.getReactorId() + ".js", c);
            if (iv == null) {
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(5, "未找到 Reactor 文件中的 " + reactor.getReactorId() + ".js 文件.");
                }
                return;
            }
            ScriptEngine scriptengine = (ScriptEngine) iv;
            ReactorActionManager rm = new ReactorActionManager(c, reactor);

            scriptengine.put("rm", rm);
            iv.invokeFunction("act");
        } catch (Exception e) {
            System.err.println("执行Reactor文件出错 ReactorID: " + reactor.getReactorId() + ", ReactorName: " + reactor.getName() + " 错误信息: " + e);
            FileoutputUtil.log("log\\Script_Except.log", "执行Reactor文件出错 ReactorID: " + reactor.getReactorId() + ", ReactorName: " + reactor.getName() + " 错误信息: " + e);
        }
    }

    public List<ReactorDropEntry> getDrops(int rid) {
        List ret = (List) this.drops.get(Integer.valueOf(rid));
        if (ret != null) {
            return ret;
        }
        ret = new LinkedList();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM reactordrops WHERE reactorid = ?");
            ps.setInt(1, rid);
            rs = ps.executeQuery();

            while (rs.next()) {
                ret.add(new ReactorDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("questid")));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Could not retrieve drops for reactor " + rid + e);
            List localList1 = ret;
            return localList1;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ignore) {
                return ret;
            }
        }
        this.drops.put(Integer.valueOf(rid), ret);
        return ret;
    }

    public void clearDrops() {
        this.drops.clear();
    }
}
