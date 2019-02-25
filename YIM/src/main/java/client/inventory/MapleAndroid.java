package client.inventory;

import database.DatabaseConnection;
import java.awt.Point;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import tools.Pair;

public class MapleAndroid
        implements Serializable {

    private static final Logger log = Logger.getLogger(MapleAndroid.class);
    private static final long serialVersionUID = 9179541993413738569L;
    private int stance = 0;
    private int uniqueid;
    private int itemid;
    private int skin;
    private int hair;
    private int face;
    private String name;
    private Point pos = new Point(0, 0);
    private boolean changed = false;

    private MapleAndroid(int itemid, int uniqueid) {
        this.itemid = itemid;
        this.uniqueid = uniqueid;
    }

    public static MapleAndroid loadFromDb(int itemid, int uid) {
        try {
            MapleAndroid ret = new MapleAndroid(itemid, uid);
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM androids WHERE uniqueid = ?");
            ps.setInt(1, uid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            ret.setSkin(rs.getInt("skin"));
            ret.setHair(rs.getInt("hair"));
            ret.setFace(rs.getInt("face"));
            ret.setName(rs.getString("name"));
            ret.changed = false;
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            log.error("加载安卓信息出错", ex);
        }
        return null;
    }

    public void saveToDb() {
        if (!this.changed) {
            return;
        }
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE androids SET skin = ?, hair = ?, face = ?, name = ? WHERE uniqueid = ?");
            ps.setInt(1, this.skin);
            ps.setInt(2, this.hair);
            ps.setInt(3, this.face);
            ps.setString(4, this.name);
            ps.setInt(5, this.uniqueid);
            ps.executeUpdate();
            ps.close();
            this.changed = false;
        } catch (SQLException ex) {
            log.error("保存安卓信息出错", ex);
        }
    }

    public static MapleAndroid create(int itemid, int uniqueid) {
        int type;
        switch (itemid) {
            case 1662000://普及型智能机器人(男)
            case 1662007:
                type = 1;
                break;
            case 1662001:
            case 1662008:
                type = 2;
                break;
            case 1662002:
            case 1662004:
            case 1662009:
                type = 3;
                break;
            case 1662003:
            case 1662005:
            case 1662010:
                type = 4;
                break;
            case 1662006:
                type = 5;
                break;
            default:
                type = 4;
        }

        Pair aInfo = MapleItemInformationProvider.getInstance().getAndroidInfo(type);
        if (aInfo == null) {
            return null;
        }
        return create(itemid, uniqueid, 0, ((Integer) ((List) aInfo.left).get(Randomizer.nextInt(((List) aInfo.left).size()))).intValue(), ((Integer) ((List) aInfo.right).get(Randomizer.nextInt(((List) aInfo.right).size()))).intValue());
    }

    public static MapleAndroid create(int itemid, int uniqueid, int skin, int hair, int face) {
        if (uniqueid <= -1) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        try {
            PreparedStatement pse = DatabaseConnection.getConnection().prepareStatement("INSERT INTO androids (uniqueid, skin, hair, face, name) VALUES (?, ?, ?, ?, ?)");
            pse.setInt(1, uniqueid);
            pse.setInt(2, skin);
            pse.setInt(3, hair);
            pse.setInt(4, face);
            pse.setString(5, "智能机器人");
            pse.executeUpdate();
            pse.close();
        } catch (SQLException ex) {
            log.error("创建安卓信息出错", ex);
            return null;
        }
        MapleAndroid and = new MapleAndroid(itemid, uniqueid);
        and.setSkin(skin);
        and.setHair(hair);
        and.setFace(face);
        and.setName("智能机器人");

        return and;
    }

    public int getUniqueId() {
        return this.uniqueid;
    }

    public void setHair(int closeness) {
        this.hair = closeness;
        this.changed = true;
    }

    public int getHair() {
        return this.hair;
    }

    public void setFace(int closeness) {
        this.face = closeness;
        this.changed = true;
    }

    public int getFace() {
        return this.face;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
        this.changed = true;
    }

    public void setSkin(int closeness) {
        this.skin = closeness;
        this.changed = true;
    }

    public int getSkin() {
        return this.skin;
    }

    public Point getPos() {
        return this.pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public int getStance() {
        return this.stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public int getItemId() {
        return this.itemid;
    }

    public int getType() {
        switch (this.itemid) {
            case 1662000://普及型智能机器人(男)
            case 1662007://普及型智能机器人（男）
                return 1;
            case 1662001://普及型智能机器人(女)
            case 1662008://普及型智能机器人（女）
                return 2;
            case 1662002://高级智能机器人(男)
            case 1662004://雪花智能机器人(男)
            case 1662009://正义智能机器人(男)
                return 3;
            case 1662003://高级智能机器人(女)
            case 1662005://雪花智能机器人(女)
            case 1662010://正义智能机器人(女)
                return 4;
            case 1662006://我的公主
                return 5;
        }
        return 4;
    }

    public int getGender() {
        switch (getItemId()) {
            case 1662000:
            case 1662002:
            case 1662004:
            case 1662007:
            case 1662009:
                return 0;
            case 1662001:
            case 1662003:
            case 1662005:
            case 1662006:
            case 1662008:
            case 1662010:
                return 1;
        }
        return -1;
    }

    public void updatePosition(List<LifeMovementFragment> movement) {
        for (LifeMovementFragment move : movement) {
            if ((move instanceof LifeMovement)) {
                if ((move instanceof AbsoluteLifeMovement)) {
                    setPos(((LifeMovement) move).getPosition());
                }
                setStance(((LifeMovement) move).getNewstate());
            }
        }
    }
}
