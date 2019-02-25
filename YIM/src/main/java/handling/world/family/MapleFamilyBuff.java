package handling.world.family;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.MapleStatEffect.CancelEffectAction;
import server.Timer.BuffTimer;
import tools.MaplePacketCreator;
import tools.Pair;

public enum MapleFamilyBuff {

    瞬移("瞬移", "[对象] 我自己\n[效果] 可以马上瞬移到自己想去见的学院成员所在的身边.", 0, 0, 0, 300, 190000),
    召唤("召唤", "[对象] 学院成员1名\n[效果] 可以召唤自己想召唤的学院成员到自己的身边.", 1, 0, 0, 500, 190001),
    爆率15分钟("我的爆率 1.2倍(15分钟)", "[对象] 我自己\n[持续时间] 15分钟\n[效果] 打猎怪物时提升怪物爆率 #c1.2倍#.\n※ 与爆率活动重叠时效果被无视.", 2, 15, 120, 700, 190002),
    经验15分钟("我的经验值 1.2倍(15分钟)", "[对象] 我自己\n[持续时间] 15分钟\n[效果] 打猎怪物时提升怪物经验值#c1.2倍#.\n※ 与经验值活动重叠时效果被无视.", 3, 15, 120, 900, 190003),
    爆率30分钟("我的爆率 1.2倍(30分钟)", "[对象] 我自己\n[持续时间] 30分钟\n[效果] 打猎怪物时提升怪物爆率#c1.2倍#.\n※ 与爆率活动重叠时效果被无视.", 2, 30, 120, 1500, 190004),
    经验30分钟("我的经验值 1.2倍(30分钟)", "[对象] 我自己\n[持续时间] 30分钟\n[效果] 打猎怪物时提升怪物经验值 #c1.2倍#.\n※ 与经验值活动重叠时效果被无视.", 3, 30, 120, 2000, 190005),
    团结("团结(30分钟)", "[发动条件]学院关系图中下端上学院成员有6名以上在线时\n[持续时间] 30分钟\n[效果] 提升爆率,经验值#c1.5倍#. ※ 与爆率经验值活动重叠时,效果被无视.", 4, 30, 150, 3000, 190006);
    public String name;
    public String desc;
    public int rep;
    public int type;
    public int questID;
    public int duration;
    public int effect;
    public List<Pair<MapleBuffStat, Integer>> effects;

    private MapleFamilyBuff(String name, String desc, int type, int duration, int effect, int rep, int questID) {
        this.name = name;
        this.desc = desc;
        this.rep = rep;
        this.type = type;
        this.questID = questID;
        this.duration = duration;
        this.effect = effect;
        setEffects();
    }

    public int getEffectId() {
        switch (this.type) {
            case 2:
                return 2022694;
            case 3:
                return 2450018;
        }
        return 2022332;
    }

    public void setEffects() {
        this.effects = new ArrayList();
        switch (this.type) {
            case 2:
                this.effects.add(new Pair(MapleBuffStat.DROP_RATE, Integer.valueOf(this.effect)));
                this.effects.add(new Pair(MapleBuffStat.MESO_RATE, Integer.valueOf(this.effect)));
                break;
            case 3:
                this.effects.add(new Pair(MapleBuffStat.EXPRATE, Integer.valueOf(this.effect)));
                break;
            case 4:
                this.effects.add(new Pair(MapleBuffStat.EXPRATE, Integer.valueOf(this.effect)));
                this.effects.add(new Pair(MapleBuffStat.DROP_RATE, Integer.valueOf(this.effect)));
                this.effects.add(new Pair(MapleBuffStat.MESO_RATE, Integer.valueOf(this.effect)));
        }
    }

    public void applyTo(MapleCharacter chr) {
        chr.getClient().getSession().write(MaplePacketCreator.giveBuff(-getEffectId(), this.duration * 60000, this.effects, null));
        MapleStatEffect eff = MapleItemInformationProvider.getInstance().getItemEffect(getEffectId());
        chr.cancelEffect(eff, true, -1L, this.effects);
        long starttime = System.currentTimeMillis();
        MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(chr, eff, starttime, this.effects);
        ScheduledFuture schedule = BuffTimer.getInstance().schedule(cancelAction, this.duration * 60000);
        chr.registerEffect(eff, starttime, schedule, this.effects, false, this.duration, chr.getId());
    }
}