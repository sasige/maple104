package handling.login.handler;

import client.MapleClient;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import java.util.Calendar;
import server.AutoRegister;
import tools.DateUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class LoginPasswordHandler {

    private static boolean loginFailCount(MapleClient c) {
        c.loginAttempt = (short) (c.loginAttempt + 1);

        return c.loginAttempt > 5;
    }

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        int loginok = 0;
        slea.skip(21);//一大堆假数据 他妹的!!!   ----- 104
        String login = slea.readMapleAsciiString();
        String pwd = slea.readMapleAsciiString();

        boolean isBanned = (c.hasBannedIP()) || (c.hasBannedMac());
        if (!LoginServer.isAutoReg()) {
            loginok = c.login(login, pwd, isBanned);
        } else if (AutoRegister.getAccountExists(login)) {
            loginok = c.login(login, pwd, isBanned);
        } else if ((AutoRegister.success = !isBanned ? true : false) != false) {
            AutoRegister.createAccount(login, pwd, c.getSession().getRemoteAddress().toString());
            if (AutoRegister.success) {
                loginok = c.login(login, pwd, isBanned);
            }
        } else {
            loginok = c.login(login, pwd, isBanned);
        }

        Calendar tempbannedTill = c.getTempBanCalendar();

        if ((tempbannedTill != null) && (tempbannedTill.getTimeInMillis() > System.currentTimeMillis())) {
            c.clearInformation();
            long tempban = DateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis());
            c.getSession().write(LoginPacket.getTempBan(tempban, c.getBanReason()));

            return;
        }

        if ((loginok == 3) && (!isBanned)) {
            c.clearInformation();
            c.getSession().write(LoginPacket.getTempBan(2147483647L, c.getBanReason()));
        } else if ((loginok == 0) && (isBanned) && (!c.isGm())) {
            loginok = 3;
            c.getSession().write(LoginPacket.getPermBan((byte) 1));
        } else if (loginok != 0) {
            c.clearInformation();
            c.getSession().write(LoginPacket.getLoginFailed(loginok));
        } else if (c.getGender() == 10) {
            c.updateLoginState(4);
            c.getSession().write(LoginPacket.genderNeeded(c));
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }
}