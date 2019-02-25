package handling.login.handler;

import client.MapleCharacter;
import client.MapleClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class ViewCharHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        Map<Byte, List> worlds = new HashMap<>();
        List<MapleCharacter> chars = c.loadCharacters(0);
        c.getSession().write(LoginPacket.showAllCharacter(chars.size()));
        for (MapleCharacter chr : chars) {
            if (chr != null) {
                ArrayList chrr;
                if (!worlds.containsKey(Byte.valueOf(chr.getWorld()))) {
                    chrr = new ArrayList();
                    worlds.put(Byte.valueOf(chr.getWorld()), chrr);
                } else {
                    chrr = (ArrayList) worlds.get(Byte.valueOf(chr.getWorld()));
                }
                chrr.add(chr);
            }
        }
        for (Entry<Byte, List> w : worlds.entrySet()) {
            c.getSession().write(LoginPacket.showAllCharacterInfo(w.getKey(), w.getValue(), c.getSecondPassword()));
        }
    }
}
