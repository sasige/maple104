package handling.login.handler;

import client.MapleClient;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;

public class ClientErrorLogHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        String error = slea.readMapleAsciiString();
        try {
            RandomAccessFile file = new RandomAccessFile("错误信息.txt", "rw");
            int num = (int) file.length();
            file.seek(num);
            file.writeBytes("\r\n------------------------ " + FileoutputUtil.CurrentReadable_Time() + " ------------------------\r\n");
            file.write("错误信息：\r\n".getBytes());
            file.write((error + "\r\n").getBytes());
            file.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientErrorLogHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}