package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.SummonSkillEntry;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import constants.GameConstants;
import java.awt.Point;

import server.MapleStatEffect;
import tools.MaplePacketCreator;

public final class MapleSummon extends AnimatedMapleMapObject {

    private int ownerid;
    private int skillLevel;
    private int ownerLevel;
    private int skill;
    private MapleMap map;
    private short hp;
    private boolean changedMap = false;
    private SummonMovementType movementType;
    private int lastSummonTickCount;
    private byte Summon_tickResetCount;
    private long Server_ClientSummonTickDiff;
    private long lastAttackTime;

    public MapleSummon(MapleCharacter owner, MapleStatEffect skill, Point pos, SummonMovementType movementType) {
        this(owner, skill.getSourceId(), skill.getLevel(), pos, movementType);
    }

    public MapleSummon(MapleCharacter owner, int sourceid, int level, Point pos, SummonMovementType movementType) {
        this.ownerid = owner.getId();
        this.ownerLevel = owner.getLevel();
        this.skill = sourceid;
        this.map = owner.getMap();
        this.skillLevel = level;
        this.movementType = movementType;
        setPosition(pos);

        if (!isPuppet()) {
            this.lastSummonTickCount = 0;
            this.Summon_tickResetCount = 0;
            this.Server_ClientSummonTickDiff = 0L;
            this.lastAttackTime = 0L;
        }
    }

    @Override
    public void sendSpawnData(MapleClient client) {
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.removeSummon(this, false));
    }

    public void updateMap(MapleMap map) {
        this.map = map;
    }

    public MapleCharacter getOwner() {
        return this.map.getCharacterById(this.ownerid);
    }

    public int getOwnerId() {
        return this.ownerid;
    }

    public int getOwnerLevel() {
        return this.ownerLevel;
    }

    public int getSkill() {
        return this.skill;
    }

    public short getHP() {
        return this.hp;
    }

    public void addHP(short delta) {
        this.hp = (short) (this.hp + delta);
    }

    public SummonMovementType getMovementType() {
        return this.movementType;
    }

    public boolean isPuppet() {
        switch (this.skill) {
            case 3111002:
            case 3120012:
            case 3211002:
            case 3220012:
            case 4341006:
            case 13111004:
            case 33111003:
                return true;
        }
        return isAngel();
    }

    public boolean isAngel() {
        return GameConstants.isAngel(this.skill);
    }

    public boolean isMultiAttack() {
        return (this.skill == 35111002) || (this.skill == 35121003) || ((!isGaviota()) && (this.skill != 33101008) && (this.skill < 35000000)) || (this.skill == 35111001) || (this.skill == 35111009) || (this.skill == 35111010);
    }

    public boolean isGaviota() {
        return this.skill == 5911002;
    }

    public boolean isBeholder() {
        return this.skill == 1321007;
    }

    public boolean isMultiSummon() {
        return (this.skill == 5911002) || (this.skill == 5911001) || (this.skill == 5920002) || (this.skill == 32111006) || (this.skill == 33101008);
    }

    public boolean isSummon() {
        switch (this.skill) {
            case 1321007:
            case 2121005:
            case 2221005:
            case 2321003:
            case 3101007:
            case 3111005:
            case 3201007:
            case 3211005:
            case 4111007:
            case 4211007:
            case 5321003:
            case 5321004:
            case 5711001:
            case 5911001:
            case 5911002:
            case 5920002:
            case 11001004:
            case 12001004:
            case 12111004:
            case 13001004:
            case 13111004:
            case 14001005:
            case 14111010:
            case 15001004:
            case 23111008:
            case 23111009:
            case 23111010:
            case 32111006:
            case 33101008:
            case 33111005:
            case 35111001:
            case 35111002:
            case 35111005:
            case 35111009:
            case 35111010:
            case 35111011:
            case 35121003:
            case 35121009:
            case 35121010:
            case 35121011:
                return true;
        }
        return isAngel();
    }

    public int getSkillLevel() {
        return this.skillLevel;
    }

    public int getSummonType() {
        if (isAngel()) {
            return 2;
        }
        if (((this.skill != 33111003) && (this.skill != 3120012) && (this.skill != 3220012) && (isPuppet())) || (this.skill == 33101008) || (this.skill == 35111002)) {
            return 0;
        }
        switch (this.skill) {
            case 1321007:
                return 2;
            case 35111001:
            case 35111009:
            case 35111010:
                return 3;
            case 35121009:
                return 5;
            case 35121003:
                return 6;
            case 4111007:
            case 4211007:
                return 7;
        }
        return 1;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }

    public void CheckSummonAttackFrequency(MapleCharacter chr, int tickcount) {
        int tickdifference = tickcount - this.lastSummonTickCount;
        if (tickdifference < SkillFactory.getSummonData(this.skill).delay) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        long STime_TC = System.currentTimeMillis() - tickcount;
        long S_C_Difference = this.Server_ClientSummonTickDiff - STime_TC;
        if (S_C_Difference > 500L) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        this.Summon_tickResetCount = (byte) (this.Summon_tickResetCount + 1);
        if (this.Summon_tickResetCount > 4) {
            this.Summon_tickResetCount = 0;
            this.Server_ClientSummonTickDiff = STime_TC;
        }
        this.lastSummonTickCount = tickcount;
    }

    public void CheckPVPSummonAttackFrequency(MapleCharacter chr) {
        long tickdifference = System.currentTimeMillis() - this.lastAttackTime;
        if (tickdifference < SkillFactory.getSummonData(this.skill).delay) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        this.lastAttackTime = System.currentTimeMillis();
    }

    public boolean isChangedMap() {
        return this.changedMap;
    }

    public void setChangedMap(boolean cm) {
        this.changedMap = cm;
    }
}
