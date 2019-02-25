package handling.login.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.PlayerStats;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import handling.world.World.Broadcast;

import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;

public class CreateUltimateHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        if ((!c.isLoggedIn()) || (c.getPlayer() == null) || (c.getPlayer().getLevel() < 120) || (c.getPlayer().getQuestStatus(20734) != 0) || (c.getPlayer().getQuestStatus(20616) != 2) || (!GameConstants.is骑士团(c.getPlayer().getJob()))) {
            c.getPlayer().dropMessage(1, "建立终极冒险家失败：\r\n1.等级不够120级\r\n2.任务未完成\r\n3.职业不是骑士团职业\r\n4.已创建过终极冒险家的角色");
            c.getSession().write(MaplePacketCreator.createUltimate(1));
            return;
        }
        if (!c.canMakeCharacter(c.getPlayer().getWorld())) {
            c.getSession().write(MaplePacketCreator.createUltimate(2));
            return;
        }
        String name = slea.readMapleAsciiString();
        int job = slea.readInt();
        if ((job < 110) || (job > 520) || (job % 10 > 0) || ((job % 100 != 10) && (job % 100 != 20) && (job % 100 != 30)) || (job == 430)) {
            c.getPlayer().dropMessage(1, "建立终极冒险家失败，职业ID不正确。");
            c.getSession().write(MaplePacketCreator.createUltimate(1));
            return;
        }
        int face = slea.readInt();
        int hair = slea.readInt();

        int hat = slea.readInt();
        int top = slea.readInt();
        int glove = slea.readInt();
        int shoes = slea.readInt();
        int weapon = slea.readInt();
        if ((job == 110) || (job == 120)) {
            weapon = 1402092;
        } else if (job == 130) {
            weapon = 1432082;
        } else if ((job == 210) || (job == 220) || (job == 230)) {
            weapon = 1372081;
        } else if (job == 310) {
            weapon = 1452108;
        } else if (job == 320) {
            weapon = 1462095;
        } else if (job == 410) {
            weapon = 1472119;
        } else if (job == 420) {
            weapon = 1332127;
        } else if (job == 510) {
            weapon = 1482081;
        } else if (job == 520) {
            weapon = 1492082;
        } else {
            weapon = 1402092;
        }

        byte gender = c.getPlayer().getGender();

        LoginInformationProvider.JobType jobType = LoginInformationProvider.JobType.终极冒险家;
        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld(c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3);
        newchar.setLevel((short) 51);
        newchar.getStat().str = 4;
        newchar.getStat().dex = 4;
        newchar.getStat().int_ = 4;
        newchar.getStat().luk = 4;
        newchar.setRemainingAp((short) 254);
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122);
        newchar.getStat().maxhp += 150;
        newchar.getStat().maxmp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600;
                newchar.getStat().maxhp += 2000;
                newchar.getStat().maxmp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900;
                newchar.getStat().maxmp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450;
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800;
                newchar.getStat().maxmp += 400;
                break;
            default:
                return;
        }
        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(MapleQuest.getInstance(i), (byte) 2, null);
        }
        newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte) 2, null);
        newchar.setQuestAdd(MapleQuest.getInstance(111111), (byte) 0, c.getPlayer().getName());
        newchar.changeSkillLevel_Skip(SkillFactory.getSkill(1074 + job / 100), 5, (byte) 5);
        newchar.changeSkillLevel_Skip(SkillFactory.getSkill(80), 1, (byte) 1);
        MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        int[] items = {1142257, 1003159, 1052304, 1082290, 1072476, weapon};
        for (byte i = 0; i < items.length; i = (byte) (i + 1)) {
            Item item = li.getEquipById(items[i]);
            item.setPosition((short) (byte) (i + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (short) 0, (short) 100, (short) 0));
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (short) 0, (short) 100, (short) 0));
        c.getPlayer().fakeRelog();
        if ((MapleCharacterUtil.canCreateChar(name, c.isGm())) && ((!LoginInformationProvider.getInstance().isForbiddenName(name)) || (c.isGm()))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.getSession().write(MaplePacketCreator.createUltimate(0));
            Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[系统公告] 恭喜玩家 " + c.getPlayer().getName() + " 创建了终极冒险家。"));
        } else {
            c.getSession().write(MaplePacketCreator.createUltimate(3));
        }
    }
}