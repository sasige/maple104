package server;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class AutoRegister {

    private static final Logger log = Logger.getLogger(AutoRegister.class);
    private static int ACCOUNTS_PER_IP = 6;
    public static boolean success;

    public static boolean getAccountExists(String login) {
        boolean accountExists = false;
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
        } catch (Exception ex) {
            log.error("AutoRegister Error!", ex);
        }
        return accountExists;
    }

    public static void createAccount(String login, String pwd, String eip) {
        String sockAddr = eip;
        Connection con;
        try {
            con = DatabaseConnection.getConnection();
        } catch (Exception ex) {
            log.error("AutoRegister Error!", ex);
            return;
        }
        try {
            PreparedStatement ipc = con.prepareStatement("SELECT lastknownip FROM accounts WHERE lastknownip = ?");
            ipc.setString(1, sockAddr.substring(1, sockAddr.lastIndexOf(':')));
            ResultSet rs = ipc.executeQuery();
            if ((!rs.first()) || ((rs.last() == true) && (rs.getRow() < ACCOUNTS_PER_IP))) {
                try {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password, birthday, macs, lastknownip) VALUES (?, ?, ?, ?, ?)");
                    ps.setString(1, login);
                    ps.setString(2, pwd);
                    ps.setString(3, "0000-00-00");
                    ps.setString(4, "00-00-00-00-00-00");
                    ps.setString(5, sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                    ps.executeUpdate();
                    ps.close();
                    success = true;
                } catch (SQLException ex) {
                    log.error("AutoRegister Error!", ex);
                    return;
                }
            }
            ipc.close();
            rs.close();
        } catch (SQLException ex) {
            log.error("Something bad with Autoregister.", ex);
        }
    }
}
