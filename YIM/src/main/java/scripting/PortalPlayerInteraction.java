package scripting;

import client.MapleClient;
import server.MaplePortal;

/**
 *
 * @author Mary
 */
public class PortalPlayerInteraction extends AbstractPlayerInteraction {

    private MaplePortal portal;

    public PortalPlayerInteraction(MapleClient c, MaplePortal portal) {
        super(c, portal.getId(), c.getPlayer().getMapId(), -1);
        this.portal = portal;
    }

    public MaplePortal getPortal() {
        return this.portal;
    }

    public void inFreeMarket() {
        if (getMapId() != 910000000) {
            if (getPlayer().getLevel() > 10) {
                saveLocation("FREE_MARKET");
                playPortalSE();
                warp(910000000, "st00");
            } else {
                playerMessage(5, "你必须10级以上才能进入自由市场。");
            }
        }
    }

    public void inArdentmill() {
        if (getMapId() != 910001000) {
            if (getPlayer().getLevel() >= 10) {
                saveLocation("ARDENTMILL");
                playPortalSE();
                warp(910001000, "st00");
            } else {
                playerMessage(5, "你必须10级以上才能进入匠人街。");
            }
        }
    }

    @Override
    public void spawnMonster(int id) {
        spawnMonster(id, 1, this.portal.getPosition());
    }

    @Override
    public void spawnMonster(int id, int qty) {
        spawnMonster(id, qty, this.portal.getPosition());
    }
}
