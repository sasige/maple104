package client.status;

import client.MapleDisease;
import constants.GameConstants;
import handling.Buffstat;
import java.io.Serializable;

public enum MonsterStatus implements Serializable, Buffstat {

    Îï¹¥(0x1, 1),
    Îï·À(0x2, 1),
    Ä§¹¥(0x4, 1),
    Ä§·À(0x8, 1),
    ÃüÖĞ(0x10, 1),
    »Ø±Ü(0x20, 1),
    ËÙ¶È(0x40, 1),
    Ñ£ÔÎ(0x80, 1),
    ½á±ù(0x100, 1),
    ÖĞ¶¾(0x200, 1),
    ·âÓ¡(0x400, 1),
    ÌôĞÆ(0x800, 1),
    Îï¹¥ÌáÉı(0x1000, 1),
    Îï·ÀÌáÉı(0x2000, 1),
    Ä§¹¥ÌáÉı(0x4000, 1),
    Ä§·ÀÌáÉı(0x8000, 1),
    Î×¶¾(0x10000, 1),
    Ó°Íø(0x20000, 1),
    ÃâÒßÎï¹¥(0x40000, 1),
    ÃâÒßÄ§¹¥(0x40000, 1),
    ÃâÒßÉËº¦(0x80000, 1),
    ÈÌÕß·ü»÷(0x200000, 1),
    ÁÒÑæÅçÉä(0x400000, 1),
    ¿Ö»Å(0x1000000, 1),
    ĞÄÁé¿ØÖÆ(0x2000000, 1),
    ·´ÉäÎï¹¥(0x20000000, 1),
    ·´ÉäÄ§¹¥(0x40000000, 1),
    ¿¹Ñ¹(0x2, 2),
    ¹í¿Ì·û(0x4, 2),
    ¹ÖÎïÕ¨µ¯(0x8, 2),
    Ä§»÷ÎŞĞ§(0x10, 2),
    ¿Õ°×BUFF(0x8000000, 1, true),
    ÕÙ»½¹ÖÎï(0x80000000, 1, true),
    EMPTY_1(0x20, 2, !GameConstants.GMS),
    EMPTY_2(0x40, 2, true),
    EMPTY_3(0x80, 2, true),
    EMPTY_4(0x100, 2, GameConstants.GMS),
    EMPTY_5(0x200, 2, GameConstants.GMS),
    EMPTY_6(0x04000, 2, GameConstants.GMS);
    static final long serialVersionUID = 0L;
    private final int i;
    private final int first;
    private final boolean end;

    private MonsterStatus(int i, int first) {
        this.i = i;
        this.first = first;
        this.end = false;
    }

    private MonsterStatus(int i, int first, boolean end) {
        this.i = i;
        this.first = first;
        this.end = end;
    }

    public int getPosition() {
        return this.first;
    }

    public boolean isEmpty() {
        return this.end;
    }

    public int getValue() {
        return this.i;
    }

    public static MonsterStatus getBySkill_Pokemon(int skill) {
        switch (skill) {
            case 120:
                return ·âÓ¡;
            case 121:
                return ¿Ö»Å;
            case 123:
                return Ñ£ÔÎ;
            case 125:
                return ÖĞ¶¾;
            case 126:
                return ËÙ¶È;
            case 137:
                return ½á±ù;
            case 122:
            case 124:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
        }
        return null;
    }

    public static MapleDisease getLinkedDisease(MonsterStatus skill) {
        switch (skill) {
            case Ñ£ÔÎ:
            case Ó°Íø:
                return MapleDisease.STUN;
            case ÖĞ¶¾:
            case ĞÄÁé¿ØÖÆ:
                return MapleDisease.POISON;
            case ·âÓ¡:
            case Ä§»÷ÎŞĞ§:
                return MapleDisease.SEAL;
            case ½á±ù:
                return MapleDisease.FREEZE;
            case ·´ÉäÎï¹¥:
                return MapleDisease.DARKNESS;
            case ËÙ¶È:
                return MapleDisease.SLOW;
        }
        return null;
    }
}