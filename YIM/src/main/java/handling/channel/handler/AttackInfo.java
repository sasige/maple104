package handling.channel.handler;

import client.MapleCharacter;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import java.awt.Point;
import java.util.List;
import server.MapleStatEffect;
import server.movement.LifeMovementFragment;
import tools.AttackPair;

public class AttackInfo {

    public int skill;
    public int charge;
    public int lastAttackTickCount;
    public List<AttackPair> allDamage;
    public Point position;
    public int display;
    public byte hits;
    public byte targets;
    public byte tbyte;
    public byte speed;
    public byte csstar;
    public byte AOE;
    public byte slot;
    public byte unk;
    public byte move;
    public List<LifeMovementFragment> movei;
    public boolean real = true;

    public MapleStatEffect getAttackEffect(MapleCharacter chr, int skillLevel, Skill skill_) {
        if ((GameConstants.isMulungSkill(this.skill)) || (GameConstants.isPyramidSkill(this.skill)) || (GameConstants.isInflationSkill(this.skill))) {
            skillLevel = 1;
        } else if (skillLevel <= 0) {
            return null;
        }
        int dd = (this.display & 0x8000) != 0 ? this.display - 32768 : this.display;
        if (GameConstants.isLinkedAranSkill(this.skill)) {
            Skill skillLink = SkillFactory.getSkill(this.skill);

            return skillLink.getEffect(skillLevel);
        }
        if (chr.isAdmin()) {
            chr.dropMessage(-5, "技能ID: " + skill_.getId() + " - " + skill_.getName() + " 当前延时: " + dd + " 技能延时: " + skill_.getAnimation() + " 解析延时: " + this.display);
        }

        return skill_.getEffect(skillLevel);
    }

    public static boolean is不检测技能(int skillId) {
        switch (skillId) {
            case 1211002:
            case 3111004:
            case 5301001:
            case 5811006:
            case 5821004:
            case 5821005:
            case 5921009:
            case 13111006:
            case 13111007:
            case 15111012:
            case 22111000:
            case 22150004:
            case 23100004:
            case 23110006:
            case 23120011:
            case 35121005:
                return true;
        }
        return false;
    }
}
