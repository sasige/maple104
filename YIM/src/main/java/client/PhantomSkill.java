package client;

import java.io.Serializable;

public class PhantomSkill implements Serializable {

    public int skillid;
    public int skilllevel;
    public int type;
    public int position;

    public PhantomSkill(int skillid, int skilllevel, int type, int position) {
        this.skillid = skillid;
        this.skilllevel = skilllevel;
        this.type = type;
        this.position = position;
    }

    public int getSkillId() {
        return this.skillid;
    }

    public int getSkillLevel() {
        return this.skilllevel;
    }

    public int getType() {
        return this.type;
    }

    public int getPosition() {
        return this.position;
    }

    public void setSkillId(int skillId) {
        this.skillid = skillId;
    }

    public void setSkillLevel(int level) {
        this.skilllevel = level;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    @Override
    public String toString() {
        return this.skillid + " : " + this.skilllevel + " : " + this.type + " : " + this.position;
    }
}