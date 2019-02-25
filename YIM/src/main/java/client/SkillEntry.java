package client;

import java.io.Serializable;

public class SkillEntry implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    public int skillevel;
    public int teachId;
    public byte masterlevel;
    public long expiration;

    public SkillEntry(int skillevel, byte masterlevel, long expiration, int teachId) {
        this.skillevel = skillevel;
        this.masterlevel = masterlevel;
        this.expiration = expiration;
        this.teachId = teachId;
    }


    @Override
    public String toString() {
        return this.skillevel + ":" + this.masterlevel;
    }
}