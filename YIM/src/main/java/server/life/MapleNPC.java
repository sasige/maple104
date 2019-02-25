package server.life;

import client.MapleClient;
import server.MapleShopFactory;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;

public class MapleNPC extends AbstractLoadedMapleLife {

    private String name = "MISSINGNO";
    private boolean custom = false;

    public MapleNPC(int id, String name) {
        super(id);
        this.name = name;
    }

    public boolean hasShop() {
        return MapleShopFactory.getInstance().getShopForNPC(getId()) != null;
    }

    public void sendShop(MapleClient c) {
        MapleShopFactory.getInstance().getShopForNPC(getId()).sendShop(c);
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (getId() >= 9901000) {
            return;
        }
        client.getSession().write(MaplePacketCreator.spawnNPC(this, true));
        client.getSession().write(MaplePacketCreator.spawnNPCRequestController(this, true));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.removeNPCController(getObjectId()));
        client.getSession().write(MaplePacketCreator.removeNPC(getObjectId()));
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.NPC;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public boolean isCustom() {
        return this.custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
