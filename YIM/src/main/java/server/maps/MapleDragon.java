package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import tools.MaplePacketCreator;

public class MapleDragon extends AnimatedMapleMapObject {

    private int owner;
    private int jobid;

    public MapleDragon(MapleCharacter owner) {
        this.owner = owner.getId();
        this.jobid = owner.getJob();
        if ((this.jobid < 2200) || (this.jobid > 2218)) {
            throw new RuntimeException("Trying to create a dragon for a non-Evan");
        }
        setPosition(owner.getTruePosition());
        setStance(4);
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.spawnDragon(this));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.removeDragon(this.owner));
    }

    public int getOwner() {
        return this.owner;
    }

    public int getJobId() {
        return this.jobid;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }
}
