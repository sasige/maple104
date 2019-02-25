package handling.login.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import org.apache.log4j.Logger;
import server.MapleItemInformationProvider;
import server.ServerProperties;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class CreateCharHandler {

    private static final Logger log = Logger.getLogger(CreateCharHandler.class);

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        String 名字 = slea.readMapleAsciiString();

        int 职业类型 = slea.readInt();
        JobType jobType = JobType.getByType(职业类型);
        short db = slea.readShort();
        byte 性别 = slea.readByte();
        byte 皮肤颜色 = slea.readByte();
        byte 头发颜色 = slea.readByte();
        int 脸型 = slea.readInt();
        int 发型 = slea.readInt();
        int 脸饰 = 职业类型 == 6 ? slea.readInt() : 0;
        int 上衣 = slea.readInt();
        int 披风 = 职业类型 == 7 ? slea.readInt() : 0;
        int 裤子 = (职业类型 == 0) || (职业类型 == 5) || (职业类型 == 6) || (职业类型 == 7) ? 0 : slea.readInt();
        int 鞋子 = slea.readInt();
        int 武器 = slea.readInt();
        int 盾牌 = 职业类型 == 6 ? slea.readInt() : 0;

        if (ServerProperties.ShowPacket()) {
            log.info("\r\n名字: " + 名字 + "\r\n职业: " + 职业类型 + "\r\n性别: " + 性别 + "\r\n皮肤: " + 皮肤颜色 + "\r\n头发: " + 头发颜色 + "\r\n脸型: " + 脸型 + "\r\n发型: " + 发型 + "\r\n脸饰: " + 脸饰 + "\r\n上衣: " + 上衣 + "\r\n裤子: " + 裤子 + "\r\n鞋子: " + 鞋子 + "\r\n武器: " + 武器 + "\r\n盾牌: " + 盾牌 + "\r\n");
        }

//        if (!LoginInformationProvider.getInstance().isEligibleItem(性别, (LoginInformationProvider.getInstance().isEligibleItem(性别, 0, jobType.type, 脸型)) || ((LoginInformationProvider.getInstance().isEligibleItem(性别, 1, jobType.type, 发型))
//                || (职业类型 == 6))
//                ? 3 : 2, jobType.type, 上衣)) {
//            log.info("[作弊] 新建角色上衣检测失败 名字: " + 名字 + " 职业: " + 职业类型 + " 上衣: " + 上衣);
//            c.getSession().write(LoginPacket.charNameResponse(名字, (byte) 3));
//            return;
//        }
//
//        if ((披风 != 1102347) && (职业类型 == 7)) {
//            log.info("[作弊] 新建角色披风检测失败 名字: " + 名字 + " 职业: " + 职业类型 + " 披风: " + 披风);
//            c.getSession().write(LoginPacket.charNameResponse(名字, (byte) 3));
//            return;
//        }
//
//        if ((!LoginInformationProvider.getInstance().isEligibleItem(性别, 3, jobType.type, 裤子)) && (职业类型 != 0) && (职业类型 != 5) && (职业类型 != 6) && (职业类型 != 7)) {
//            log.info("[作弊] 新建角色裤子检测失败 名字: " + 名字 + " 职业: " + 职业类型 + " 裤子: " + 裤子);
//            c.getSession().write(LoginPacket.charNameResponse(名字, (byte) 3));
//            return;
//        }
//
//        if (!LoginInformationProvider.getInstance().isEligibleItem(性别, (职业类型 == 0) || (职业类型 == 5) ? 3 : 4, jobType.type, 鞋子)) {
//            log.info("[作弊] 新建角色鞋子检测失败 名字: " + 名字 + " 职业: " + 职业类型 + " 鞋子: " + 鞋子);
//            c.getSession().write(LoginPacket.charNameResponse(名字, (byte) 3));
//            return;
//        }
//        if (!LoginInformationProvider.getInstance().isEligibleItem(性别, (职业类型 == 0) || (职业类型 == 5) ? 4 : 5, jobType.type, 武器)) {
//            log.info("[作弊] 新建角色武器检测失败 名字: " + 名字 + " 职业: " + 职业类型 + " 武器: " + 武器);
//            c.getSession().write(LoginPacket.charNameResponse(名字, (byte) 3));
//            return;
//        }
//        if ((!LoginInformationProvider.getInstance().isEligibleItem(性别, 6, jobType.type, 盾牌)) && (职业类型 == 6)) {
//            log.info("[作弊] 新建角色盾牌检测失败 名字: " + 名字 + " 职业: " + 职业类型 + " 盾牌: " + 盾牌);
//            c.getSession().write(LoginPacket.charNameResponse(名字, (byte) 3));
//            return;
//        }
        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setWorld((byte) c.getWorld());
        newchar.setFace(脸型);
        newchar.setHair(发型);
        newchar.setGender(性别);
        newchar.setName(名字);
        newchar.setSkinColor(皮肤颜色);
        newchar.setDecorate(脸饰);

        MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        
        Item item = li.getEquipById(上衣);
        item.setPosition((short) -5);
        equip.addFromDB(item);

        if (裤子 > 0) {
            item = li.getEquipById(裤子);
            item.setPosition((short) -6);
            equip.addFromDB(item);
        }

        item = li.getEquipById(鞋子);
        item.setPosition((short) -7);
        equip.addFromDB(item);

        if ((职业类型 == 7) && (披风 > 0)) {
            item = li.getEquipById(披风);
            item.setPosition((short) -9);
            equip.addFromDB(item);
        }

        if (盾牌 > 0) {
            item = li.getEquipById(盾牌);
            item.setPosition((short) -10);
            equip.addFromDB(item);
        }

        item = li.getEquipById(武器);
        item.setPosition((short) -11);
        equip.addFromDB(item);
        
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000013, (short) 0, (short) 100, (short) 0));
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000014, (short) 0, (short) 100, (short) 0));

        switch (jobType) {
            case 反抗者:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (short) 0, (short) 1, (short) 0));
                break;
            case 冒险家:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (short) 0, (short) 1, (short) 0));
                break;
            case 骑士团:
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null);
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (short) 0, (short) 1, (short) 0));
                break;
            case 战神:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (short) 0, (short) 1, (short) 0));
                break;
            case 龙神:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (short) 0, (short) 1, (short) 0));
                break;
            case 恶魔猎手:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161079, (short) 0, (short) 1, (short) 0));
                break;
            case 双弩精灵:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161079, (short) 0, (short) 1, (short) 0));
        }

        if ((MapleCharacterUtil.canCreateChar(名字, c.isGm())) && ((!LoginInformationProvider.getInstance().isForbiddenName(名字)) || (c.isGm())) && ((c.isGm()) || (c.canMakeCharacter(c.getWorld())))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, db);
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
        }
    }
}