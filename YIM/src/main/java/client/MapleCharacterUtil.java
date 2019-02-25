package client;

import database.DatabaseConnection;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import tools.Pair;
import tools.Triple;

public class MapleCharacterUtil {

    private static final Logger log = Logger.getLogger(MapleCharacterUtil.class);
    private static Pattern namePattern = Pattern.compile("^(?!_)(?!.*?_$)[a-zA-Z0-9_一-龥]+$");
    private static Pattern petPattern = Pattern.compile("^(?!_)(?!.*?_$)[a-zA-Z0-9_一-龥]+$");

    public static boolean canCreateChar(String name, boolean gm) {
        return (getIdByName(name) == -1) && (isEligibleCharName(name, gm));
    }

    public static boolean canChangePetName(String name) {
        if ((name.getBytes().length < 4) || (name.getBytes().length > 12)) {
            return false;
        }
        return petPattern.matcher(name).matches();
    }

    public static boolean isEligibleCharName(String name, boolean gm) {
        if (name.getBytes().length > 12) {
            return false;
        }
        if (gm) {
            return true;
        }
        if (name.getBytes().length < 4) {
            return false;
        }
        return namePattern.matcher(name).matches();
    }

    public static String makeMapleReadable(String in) {
        String wui = in.replace('I', 'i');
        wui = wui.replace('l', 'L');
        wui = wui.replace("rn", "Rn");
        wui = wui.replace("vv", "Vv");
        wui = wui.replace("VV", "Vv");
        return wui;
    }

    public static int getIdByName(String name) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            int id = rs.getInt("id");
            rs.close();
            ps.close();

            return id;
        } catch (SQLException e) {
            log.error("error 'getIdByName' " + e);
        }
        return -1;
    }

    public static Pair<String, Integer> getNameById(int chrId, int world) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ? AND world = ?");
            ps.setInt(1, chrId);
            ps.setInt(2, world);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            Pair id = new Pair(rs.getString("name"), Integer.valueOf(rs.getInt("accountid")));
            rs.close();
            ps.close();
            return id;
        } catch (Exception e) {
            log.error("error 'getInfoByName' " + e);
        }
        return null;
    }

    public static boolean PromptPoll(int accountid) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean prompt = false;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("SELECT * from game_poll_reply where AccountId = ?");
            ps.setInt(1, accountid);
            rs = ps.executeQuery();
            prompt = !rs.next();
        } catch (SQLException e) {
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return prompt;
    }

    public static boolean SetPoll(int accountid, int selection) {
        if (!PromptPoll(accountid)) {
            return false;
        }
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO game_poll_reply (AccountId, SelectAns) VALUES (?, ?)");
            ps.setInt(1, accountid);
            ps.setInt(2, selection);

            ps.execute();
        } catch (SQLException e) {
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
            }
        }
        return true;
    }

    public static int Change_SecondPassword(int accid, String password, String newpassword) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from accounts where id = ?");
            ps.setInt(1, accid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            String secondPassword = rs.getString("2ndpassword");
            String salt2 = rs.getString("salt2");
            if ((secondPassword != null) && (salt2 != null)) {
                secondPassword = LoginCrypto.rand_r(secondPassword);
            } else if ((secondPassword == null) && (salt2 == null)) {
                rs.close();
                ps.close();
                return 0;
            }
            if (!check_ifPasswordEquals(secondPassword, password, salt2)) {
                rs.close();
                ps.close();
                return 1;
            }
            rs.close();
            ps.close();
            String SHA1hashedsecond;
            try {
                SHA1hashedsecond = LoginCryptoLegacy.encodeSHA1(newpassword);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                return -2;
            }
            ps = con.prepareStatement("UPDATE accounts set 2ndpassword = ?, salt2 = ? where id = ?");
            ps.setString(1, SHA1hashedsecond);
            ps.setString(2, null);
            ps.setInt(3, accid);
            if (!ps.execute()) {
                ps.close();
                return 2;
            }
            ps.close();
            return -2;
        } catch (SQLException e) {
            log.error("修改二级密码发生错误" + e);
        }
        return -2;
    }

    private static boolean check_ifPasswordEquals(String passhash, String pwd, String salt) {
        if ((LoginCryptoLegacy.isLegacyPassword(passhash)) && (LoginCryptoLegacy.checkPassword(pwd, passhash))) {
            return true;
        }
        if ((salt == null) && (LoginCrypto.checkSha1Hash(passhash, pwd))) {
            return true;
        }
        return LoginCrypto.checkSaltedSha512Hash(passhash, pwd, salt);
    }

    public static Triple<Integer, Integer, Integer> getInfoByName(String name, int world) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name = ? AND world = ?");
            ps.setString(1, name);
            ps.setInt(2, world);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            Triple id = new Triple(Integer.valueOf(rs.getInt("id")), Integer.valueOf(rs.getInt("accountid")), Integer.valueOf(rs.getInt("gender")));
            rs.close();
            ps.close();
            return id;
        } catch (Exception e) {
            log.error("error 'getInfoByName' " + e);
        }
        return null;
    }

    public static void setNXCodeUsed(String name, String code) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE nxcode SET `user` = ?, `valid` = 0 WHERE code = ?");
        ps.setString(1, name);
        ps.setString(2, code);
        ps.execute();
        ps.close();
    }

    public static void sendNote(String to, String name, String msg, int fame) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO notes (`to`, `from`, `message`, `timestamp`, `gift`) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, to);
            ps.setString(2, name);
            ps.setString(3, msg);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, fame);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("Unable to send note" + e);
        }
    }

    public static Triple<Boolean, Integer, Integer> getNXCodeInfo(String code) throws SQLException {
        Triple ret = null;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `valid`, `type`, `item` FROM nxcode WHERE code LIKE ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            ret = new Triple(Boolean.valueOf(rs.getInt("valid") > 0), Integer.valueOf(rs.getInt("type")), Integer.valueOf(rs.getInt("item")));
        }
        rs.close();
        ps.close();
        return ret;
    }
}