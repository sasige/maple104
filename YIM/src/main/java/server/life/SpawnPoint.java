package server.life;

import client.Skill;
import client.SkillFactory;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import java.awt.Point;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import server.MapleCarnivalFactory;
import server.MapleCarnivalFactory.MCSkill;
import server.MapleStatEffect;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import server.maps.MapleSummon;
import tools.MaplePacketCreator;

public class SpawnPoint extends Spawns {

    private MapleMonsterStats monster;
    private Point pos;
    private long nextPossibleSpawn;
    private int mobTime;
    private int carnival = -1;
    private int fh;
    private int f;
    private int id;
    private int level = -1;
    private AtomicInteger spawnedMonsters = new AtomicInteger(0);
    private String msg;
    private byte carnivalTeam;

    public SpawnPoint(MapleMonster monster, Point pos, int mobTime, byte carnivalTeam, String msg) {
        this.monster = monster.getStats();
        this.pos = pos;
        this.id = monster.getId();
        this.fh = monster.getFh();
        this.f = monster.getF();
        this.mobTime = (mobTime < 0 ? -1 : mobTime * 1000);
        this.carnivalTeam = carnivalTeam;
        this.msg = msg;
        this.nextPossibleSpawn = System.currentTimeMillis();
    }

    public void setCarnival(int c) {
        carnival = c;
    }

    public void setLevel(int c) {
        level = c;
    }

    @Override
    public int getF() {
        return f;
    }

    @Override
    public int getFh() {
        return fh;
    }

    @Override
    public Point getPosition() {
        return pos;
    }

    @Override
    public MapleMonsterStats getMonster() {
        return monster;
    }

    @Override
    public byte getCarnivalTeam() {
        return carnivalTeam;
    }

    @Override
    public int getCarnivalId() {
        return carnival;
    }

    @Override
    public boolean shouldSpawn(long time) {
        if (mobTime < 0) {
            return false;
        }

        if (((mobTime != 0 || !monster.getMobile()) && spawnedMonsters.get() > 0) || spawnedMonsters.get() > 1) {
    
            return false;
        }
        return nextPossibleSpawn <= time;
    }

    @Override
    public MapleMonster spawnMonster(MapleMap map) {
        MapleMonster mob = new MapleMonster(id, monster);
        mob.setPosition(pos);
        mob.setCy(pos.y);
        mob.setRx0(pos.x - 50);
        mob.setRx1(pos.x + 50);
        mob.setFh(fh);
        mob.setF(f);
        mob.setCarnivalTeam(carnivalTeam);
        if (level > -1) {
            mob.changeLevel(level);
        }
        spawnedMonsters.incrementAndGet();
        mob.addListener(new MonsterListener() {

            @Override
            public void monsterKilled() {
                nextPossibleSpawn = System.currentTimeMillis();

                if (mobTime > 0) {
                    nextPossibleSpawn += mobTime;
                }
                spawnedMonsters.decrementAndGet();
            }
        });
        map.spawnMonster(mob, -2);
        if (carnivalTeam > -1) {
            for (MapleReactor r : map.getAllReactorsThreadsafe()) {
                if ((r.getName().startsWith(String.valueOf(carnivalTeam))) && (r.getReactorId() == 9980000 + carnivalTeam) && (r.getState() < 5)) {
                    int num = Integer.parseInt(r.getName().substring(1, 2));
                    MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
                    if (skil != null) {
                        skil.getSkill().applyEffect(null, mob, false);
                    }
                }
            }
        }
        for (MapleSummon s : map.getAllSummonsThreadsafe()) {
            if (s.getSkill() == 35111005) {
                MapleStatEffect effect = SkillFactory.getSkill(s.getSkill()).getEffect(s.getSkillLevel());
                for (Map.Entry stat : effect.getMonsterStati().entrySet()) {
                    mob.applyStatus(s.getOwner(), new MonsterStatusEffect((MonsterStatus) stat.getKey(), (Integer) stat.getValue(), s.getSkill(), null, false), false, effect.getDuration(), true, effect);
                }
                break;
            }
        }
        if (msg != null) {
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, msg));
        }
        return mob;
    }

    @Override
    public int getMobTime() {
        return mobTime;
    }
}
