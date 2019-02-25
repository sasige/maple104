package constants;

import client.LoginCrypto;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import tools.IPAddressTool;

public class ServerConstants  implements ServerConstantsMBean {
    private static final Logger log = Logger.getLogger(ServerConstants.class);
    public static boolean TESPIA = false;
    public static byte[] Gateway_IP = {-35, -25, -126, 70};
    public static boolean PollEnabled = false;
    public static String Poll_Question = "Are you mudkiz?";
    public static String[] Poll_Answers = {"test1", "test2", "test3"};
    public static short MAPLE_VERSION = 104;//版本号
    public static String MAPLE_PATCH = "1";//客服端 版本 小号
    public static byte MAPLE_TYPE = 4;
    public static boolean Use_Fixed_IV = true;
    //Use_Localhost 单机模式！开启 管理员登录 模式
    public static boolean Use_Localhost = false;
    public static final int MIN_MTS = 100;
    public static final int MTS_BASE = 0;
    public static final int MTS_TAX = 5;
    public static final int MTS_MESO = 10000;
    public static final String MASTER_LOGIN = "playdk001";
    public static final String MASTER = "48239defb943bde63d65d02201262b8cc638b377";
    public static final String MASTER2 = "900702";
    public static final String SQL_USER = "root";
    public static final String SQL_PASSWORD = "";
    public static final long number1 = 611816276193195499L;
    public static final long number2 = 1877319832L;
    public static final long number3 = 202227478981090217L;
    public static final List<String> eligibleIP = new LinkedList();
    public static final List<String> localhostIP = new LinkedList();
    public static final List<String> vpnIp = new LinkedList();
    public static String master;
    public static ServerConstants instance;
    public static String resourcePath = System.getProperty("resourcePath");


    public static byte Class_Bonus_EXP(int job) {
        switch (job) {
            case 501:
            case 530:
            case 531:
            case 532:
            case 800:
            case 900:
            case 910:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
                return 10;
        }
        return 0;
    }

    public static boolean isEligibleMaster(String pwd, String sessionIP) {
        return (LoginCrypto.checkSha1Hash(getMaster(), pwd)) && (isEligible(sessionIP));
    }

    public static String getMaster() {
        if (master == null) {
            return "48239defb943bde63d65d02201262b8cc638b377";
        }
        return master;
    }

    public static boolean isEligible(String sessionIP) {
        return true;
    }

    public static boolean isEligibleMaster2(String pwd, String sessionIP) {
        return (pwd.equals("900702")) && (isEligible(sessionIP));
    }

    public static boolean isIPLocalhost(String sessionIP) {
        return (!Use_Fixed_IV) && (localhostIP.contains(sessionIP.replace("/", "")));
    }

    public static boolean isVpn(String sessionIP) {
        return vpnIp.contains(sessionIP.replace("/", ""));
    }

    @Override
    public void run() {
        updateIP();
    }

    @Override
    public void updateIP() {
        master = IPAddressTool.getMaster();
        eligibleIP.clear();
        String[] eligibleIPs = {"www.baidu.com"};
        for (String eIP : eligibleIPs) {
            try {
                eligibleIP.add(InetAddress.getByName(eIP).getHostAddress().replace("/", ""));
            } catch (Exception e) {
                log.error("更新ip出错：",e);
            }
        }
    }

    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            instance = new ServerConstants();
            instance.updateIP();
            mBeanServer.registerMBean(instance, new ObjectName("constants:type=ServerConstants"));
        } catch (Exception e) {
            System.out.println("Error registering Shutdown MBean");
        }
    }

    static {
        localhostIP.add("221.231.130.70");
        for (int i = 0; i < 256; i++) {
            vpnIp.add("221.231.130." + i);
        }
        for (int i = 0; i < 256; i++) {
            vpnIp.add("17.1.1." + i);
        }
        for (int i = 0; i < 256; i++) {
            vpnIp.add("17.1.2." + i);
        }
    }

    public static enum CommandType {

        NORMAL(0),
        TRADE(1),
        POKEMON(2);
        private int level;

        private CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return this.level;
        }
    }

    public static enum PlayerGMRank {

        NORMAL('@', 0),
        DONATOR('#', 1),
        SUPERDONATOR('$', 2),
        INTERN('%', 3),
        GM('!', 4),
        SUPERGM('!', 5),
       // LVKEJIAN('!', 55 ),
        ADMIN('!', 6),
        LVKEJIAN('!', 55 );

        private char commandPrefix;
        private int level;

        private PlayerGMRank(char ch, int level) {
            this.commandPrefix = ch;
            this.level = level;
        }

        public char getCommandPrefix() {
            return this.commandPrefix;
        }

        public int getLevel() {
            return this.level;
        }
    }
}