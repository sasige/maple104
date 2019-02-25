package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class GetMACAddress {

    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static String getMACAddress() {
        String os = getOSName();
        String mac = null;
        if (os.startsWith("windows")) {
            mac = getWindowsMACAddress();
        } else if (os.startsWith("linux")) {
            mac = getLinuxMACAddress();
        } else {
            mac = getUnixMACAddress();
        }
        return mac;
    }

    public static String getUnixMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index = line.toLowerCase().indexOf("hwaddr");

                if (index == -1) {
                    continue;
                }
                mac = line.substring(index + "hwaddr".length() + 1).trim();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }

    public static String getLinuxMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index = line.toLowerCase().indexOf("硬件地址");

                if (index == -1) {
                    continue;
                }
                mac = line.substring(index + 4).trim();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }

    public static String getWindowsMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ipconfig /all");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index = line.toLowerCase().indexOf("physical address");
                if (index != -1) {
                    index = line.indexOf(":");
                    if (index == -1) {
                        break;
                    }
                    mac = line.substring(index + 1).trim();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }

    public static void main(String[] argc) {
        String os = getOSName();
        System.out.println(os);
        System.out.println("今天是星期: " + Calendar.getInstance().get(7));
        if (os.startsWith("windows")) {
            String mac = getWindowsMACAddress();
            System.out.println("本地是windows:" + mac);
        } else if (os.startsWith("linux")) {
            String mac = getLinuxMACAddress();
            System.out.println("本地是Linux系统,MAC地址是:" + mac);
        } else {
            String mac = getUnixMACAddress();
            System.out.println("本地是Unix系统 MAC地址是:" + mac);
        }
    }
}