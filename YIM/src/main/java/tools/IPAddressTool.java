package tools;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IPAddressTool {

    public static long dottedQuadToLong(String dottedQuad)
            throws RuntimeException {
        String[] quads = dottedQuad.split("\\.");
        if (quads.length != 4) {
            throw new RuntimeException("Invalid IP Address format.");
        }
        long ipAddress = 0L;
        for (int i = 0; i < 4; i++) {
            ipAddress += (long) (Integer.parseInt(quads[i]) % 256) * (long) Math.pow(256, (double) (4 - i));
        }
        return ipAddress;
    }

    public static String longToDottedQuad(long longIP)
            throws RuntimeException {
        StringBuilder ipAddress = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int quad = (int) (longIP / (long) Math.pow(256, (double) (4 - i)));
            longIP -= (long) quad * (long) Math.pow(256, (double) (4 - i));
            if (i > 0) {
                ipAddress.append(".");
            }
            if (quad > 255) {
                throw new RuntimeException("Invalid long IP address.");
            }
            ipAddress.append(quad);
        }
        return ipAddress.toString();
    }

    public static String getLocalIP() {
        String ipAddrStr = "";
        byte[] ipAddr;
        try {
            ipAddr = InetAddress.getLocalHost().getAddress();
        } catch (UnknownHostException e) {
            return null;
        }
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr = new StringBuilder().append(ipAddrStr).append(".").toString();
            }
            ipAddrStr = new StringBuilder().append(ipAddrStr).append(ipAddr[i] & 0xFF).toString();
        }
        return ipAddrStr;
    }

    public static void getLocalIPs() {
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface nif = (NetworkInterface) netInterfaces.nextElement();
                Enumeration iparray = nif.getInetAddresses();
                while (iparray.hasMoreElements()) {
                    System.out.println(new StringBuilder().append("IP:").append(((InetAddress) iparray.nextElement()).getHostAddress()).toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getMaster() {
        String netip = null;
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();

            boolean finded = false;
            while ((netInterfaces.hasMoreElements()) && (!finded)) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress ip = (InetAddress) address.nextElement();
                    if ((ip.getHostAddress().equalsIgnoreCase("221.231.130.70")) || (ip.getHostAddress().equalsIgnoreCase("221.231.130.71")) || (ip.getHostAddress().equalsIgnoreCase("127.0.0.1"))) {
                        continue;
                    }
                    if ((!ip.isSiteLocalAddress()) && (!ip.isLoopbackAddress()) && (ip.getHostAddress().indexOf(":") == -1)) {
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    }
                    if ((ip.isSiteLocalAddress()) && (!ip.isLoopbackAddress()) && (ip.getHostAddress().indexOf(":") == -1)) {
                        netip = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
        }
        if ((netip != null) && (!"".equals(netip))) {
            if (netip.equalsIgnoreCase("115.238.250.86")) {
                return "c12ee5577281615b6156152d30bee40248c551d0";
            }
            if ((netip.equalsIgnoreCase("115.238.252.230")) || (netip.equalsIgnoreCase("115.238.250.86"))) {
                return "c60c64c2a4312564a71bd912d05ac43cfa0dc003";
            }
            if (netip.equalsIgnoreCase("192.168.1.11")) {
                return "a67e1b53de4de67e3a490f25632ef011bcd2c42b";
            }
            return "48239defb943bde63d65d02201262b8cc638b377";
        }

        return "48239defb943bde63d65d02201262b8cc638b377";
    }
}