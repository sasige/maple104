package handling.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import server.maps.AnimatedMapleMapObject;
import server.movement.AbsoluteLifeMovement;
import server.movement.AranMovement;
import server.movement.BounceMovement;
import server.movement.ChairMovement;
import server.movement.ChangeEquipSpecialAwesome;
import server.movement.JumpDownMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.RelativeLifeMovement;
import server.movement.TeleportMovement;
import server.movement.UnknownMovement;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.data.LittleEndianAccessor;

public class MovementParse {

    private static Logger log = Logger.getLogger(MovementParse.class);

    public static List<LifeMovementFragment> parseMovement(LittleEndianAccessor lea, int kind) {
        List res = new ArrayList();
        byte numCommands = lea.readByte();
        for (byte i = 0; i < numCommands; i = (byte) (i + 1)) {
            byte command = lea.readByte();
            switch (command) {
                case -1:
                case 20: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short unk = lea.readShort();
                    short fh = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    BounceMovement bm = new BounceMovement(command, new Point(xpos, ypos), duration, newstate);
                    bm.setFH(fh);
                    bm.setUnk(unk);
                    res.add(bm);
                    break;
                }
                case 0:
                case 7:
                case 16:
                case 40:
                case 41:
                case 42:
                case 45:
                case 46: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short xwobble = lea.readShort();
                    short ywobble = lea.readShort();
                    short unk = lea.readShort();
                    short xoffset = lea.readShort();
                    short yoffset = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate);
                    alm.setUnk(unk);
                    alm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    alm.setOffset(new Point(xoffset, yoffset));
                    res.add(alm);

                    break;
                }
                case 1:
                case 2:
                case 15:
                case 21:
                case 33:
                case 34: {
                    short xmod = lea.readShort();
                    short ymod = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xmod, ymod), duration, newstate);
                    res.add(rlm);

                    break;
                }
                case 4:
                case 5:
                case 8:
                case 100:
                case 101: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short xwobble = lea.readShort();
                    short ywobble = lea.readShort();
                    byte newstate = lea.readByte();
                    TeleportMovement tm = new TeleportMovement(command, new Point(xpos, ypos), newstate);
                    tm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    res.add(tm);
                    break;
                }
                case 11: {
                    res.add(new ChangeEquipSpecialAwesome(command, lea.readByte()));
                    break;
                }
                case 3:
                case 6:
                case 9:
                case 10:
                case 12:
                case 13:
                case 18:
                case 19: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short unk = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos), duration, newstate);
                    cm.setUnk(unk);
                    res.add(cm);
                    break;
                }
                case 17:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39: {
                    byte newstate = lea.readByte();
                    short unk = lea.readShort();
                    AranMovement am = new AranMovement(command, new Point(0, 0), unk, newstate);
                    res.add(am);
                    break;
                }
                case 14: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short xwobble = lea.readShort();
                    short ywobble = lea.readShort();
                    short unk = lea.readShort();
                    short fh = lea.readShort();
                    short xoffset = lea.readShort();
                    short yoffset = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    JumpDownMovement jdm = new JumpDownMovement(command, new Point(xpos, ypos), duration, newstate);
                    jdm.setUnk(unk);
                    jdm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    jdm.setOffset(new Point(xoffset, yoffset));
                    jdm.setFH(fh);
                    res.add(jdm);
                    break;
                }
                case 99: {
                    short unk = lea.readShort();
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short xwobble = lea.readShort();
                    short ywobble = lea.readShort();
                    short fh = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    UnknownMovement um = new UnknownMovement(command, new Point(xpos, ypos), duration, newstate);
                    um.setUnk(unk);
                    um.setPixelsPerSecond(new Point(xwobble, ywobble));
                    um.setFH(fh);
                    res.add(um);
                    break;
                }
                case 43:
                case 44:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                default:
                    log.warn("未知的移动类型: " + HexTool.toString(command) + " - ( " + command + " )");
                    String moveMsg = "";
                    if (kind == 1) {
                        moveMsg = "玩家";
                    } else if (kind == 2) {
                        moveMsg = "怪物";
                    } else if (kind == 3) {
                        moveMsg = "宠物";
                    } else if (kind == 4) {
                        moveMsg = "召唤兽";
                    } else if (kind == 5) {
                        moveMsg = "龙龙";
                    } else if (kind == 6) {
                        moveMsg = "攻击怪物";
                    }
                    FileoutputUtil.log("log\\Movement.log", moveMsg + "未知移动封包 剩余次数: " + (numCommands - res.size()) + " 移动类型: " + HexTool.toString(command) + ", 封包: " + lea.toString(true));
                    return null;
            }
        }
        if (numCommands != res.size()) {
            return null;
        }
        return res;
    }

    public static void updatePosition(List<LifeMovementFragment> movement, AnimatedMapleMapObject target, int yoffset) {
        if (movement == null) {
            return;
        }
        for (LifeMovementFragment move : movement) {
            if ((move instanceof LifeMovement)) {
                if ((move instanceof AbsoluteLifeMovement)) {
                    Point position = ((LifeMovement) move).getPosition();
                    position.y += yoffset;
                    target.setPosition(position);
                }
                target.setStance(((LifeMovement) move).getNewstate());
            }
        }
    }
}