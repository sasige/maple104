package client.inventory;

import constants.GameConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import server.MapleItemInformationProvider;
import tools.Pair;

public enum ItemLoader {

    装备道具(0, false),
    仓库道具(1, true),
    现金道具(2, true),
    雇佣道具(5, false),
    送货道具(6, false),
    拍卖道具(8, false),
    MTS_TRANSFER(9, false);
    private int value;
    private boolean account;

    private ItemLoader(int value, boolean account) {
        this.value = value;
        this.account = account;
    }

    public int getValue() {
        return value;
    }

    public Map<Long, Pair<Item, MapleInventoryType>> loadItems(boolean login, int id) throws SQLException {
        Map items = new LinkedHashMap();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) WHERE `type` = ? AND `");
            query.append(this.account ? "accountid" : "characterid");
            query.append("` = ?");

            if (login) {
                query.append(" AND `inventorytype` = ");
                query.append(MapleInventoryType.EQUIPPED.getType());
            }

            ps = DatabaseConnection.getConnection().prepareStatement(query.toString());
            ps.setInt(1, value);
            ps.setInt(2, id);
            rs = ps.executeQuery();

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            while (rs.next()) {
                if (!ii.itemExists(rs.getInt("itemid"))) {
                    continue;
                }
                MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));

                if ((mit.equals(MapleInventoryType.EQUIP)) || (mit.equals(MapleInventoryType.EQUIPPED))) {
                    Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getInt("uniqueid"), rs.getShort("flag"));
                    if ((!login) && (equip.getPosition() != -55)) {
                        equip.setQuantity((byte) 1);
                        equip.setInventoryId(rs.getLong("inventoryitemid"));
                        equip.setOwner(rs.getString("owner"));
                        equip.setExpiration(rs.getLong("expiredate"));
                        equip.setUpgradeSlots(rs.getByte("upgradeslots"));
                        equip.setLevel(rs.getByte("level"));
                        equip.setStr(rs.getShort("str"));
                        equip.setDex(rs.getShort("dex"));
                        equip.setInt(rs.getShort("int"));
                        equip.setLuk(rs.getShort("luk"));
                        equip.setHp(rs.getShort("hp"));
                        equip.setMp(rs.getShort("mp"));
                        equip.setWatk(rs.getShort("watk"));
                        equip.setMatk(rs.getShort("matk"));
                        equip.setWdef(rs.getShort("wdef"));
                        equip.setMdef(rs.getShort("mdef"));
                        equip.setAcc(rs.getShort("acc"));
                        equip.setAvoid(rs.getShort("avoid"));
                        equip.setHands(rs.getShort("hands"));
                        equip.setSpeed(rs.getShort("speed"));
                        equip.setJump(rs.getShort("jump"));
                        equip.setViciousHammer(rs.getByte("ViciousHammer"));
                        equip.setItemEXP(rs.getInt("itemEXP"));
                        equip.setGMLog(rs.getString("GM_Log"));
                        equip.setDurability(rs.getInt("durability"));
                        equip.setState(rs.getByte("state"));
                        equip.setEnhance(rs.getByte("enhance"));
                        equip.setPotential1(rs.getInt("potential1"));
                        equip.setPotential2(rs.getInt("potential2"));
                        equip.setPotential3(rs.getInt("potential3"));
                        equip.setPotential4(rs.getInt("potential4"));
                        equip.setPotential5(rs.getInt("potential5"));
                        equip.setGiftFrom(rs.getString("sender"));
                        equip.setIncSkill(rs.getInt("incSkill"));
                        equip.setPVPDamage(rs.getShort("pvpDamage"));
                        equip.setCharmEXP(rs.getShort("charmEXP"));
                        equip.setStateMsg(rs.getInt("statemsg"));
                        equip.setSocket1(rs.getInt("itemSlot1"));
                        equip.setSocket2(rs.getInt("itemSlot2"));
                        equip.setSocket3(rs.getInt("itemSlot3"));
                        if (((equip.getPotential1() == 60001) || (equip.getPotential1() == 60003))
                                && (!GameConstants.optionTypeFitsX(equip.getPotential1(), equip.getItemId()))) {
                            equip.setPotential1(60002);
                        }

                        if (((equip.getPotential2() == 60001) || (equip.getPotential2() == 60003))
                                && (!GameConstants.optionTypeFitsX(equip.getPotential2(), equip.getItemId()))) {
                            equip.setPotential2(60002);
                        }

                        if (((equip.getPotential3() == 60001) || (equip.getPotential3() == 60003))
                                && (!GameConstants.optionTypeFitsX(equip.getPotential3(), equip.getItemId()))) {
                            equip.setPotential3(60002);
                        }

                        if (equip.getCharmEXP() < 0) {
                            equip.setCharmEXP(((Equip) ii.getEquipById(equip.getItemId())).getCharmEXP());
                        }
                        if (equip.getUniqueId() > -1) {
                            if (GameConstants.isEffectRing(rs.getInt("itemid"))) {
                                MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals(MapleInventoryType.EQUIPPED));
                                if (ring != null) {
                                    equip.setRing(ring);
                                }
                            } else if (equip.getItemId() / 10000 == 166) {
                                MapleAndroid ring = MapleAndroid.loadFromDb(equip.getItemId(), equip.getUniqueId());
                                if (ring != null) {
                                    equip.setAndroid(ring);
                                }
                            }
                        }
                    }
                    items.put(Long.valueOf(rs.getLong("inventoryitemid")), new Pair(equip.copy(), mit));
                } else {
                    Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getShort("flag"), rs.getInt("uniqueid"));
                    item.setOwner(rs.getString("owner"));
                    item.setInventoryId(rs.getLong("inventoryitemid"));
                    item.setExpiration(rs.getLong("expiredate"));
                    item.setGMLog(rs.getString("GM_Log"));
                    item.setGiftFrom(rs.getString("sender"));
                    if (GameConstants.isPet(item.getItemId())) {
                        if (item.getUniqueId() > -1) {
                            MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                            if (pet != null) {
                                item.setPet(pet);
                            }
                        } else {
                            item.setPet(MaplePet.createPet(item.getItemId(), MapleInventoryIdentifier.getInstance()));
                        }
                    }
                    items.put(Long.valueOf(rs.getLong("inventoryitemid")), new Pair(item.copy(), mit));
                }
            }
            rs.close();
            ps.close();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return items;
    }

    public void saveItems(List<Pair<Item, MapleInventoryType>> items, int id) throws SQLException {
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        int autokey = -1;
        try {
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM `inventoryitems` WHERE `type` = ? AND `");
            query.append(this.account ? "accountid" : "characterid");
            query.append("` = ?");

            Connection con = DatabaseConnection.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setInt(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
            if (items == null) {
                return;
            }
            ps = con.prepareStatement("INSERT INTO `inventoryitems` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            pse = con.prepareStatement("INSERT INTO `inventoryequipment` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (Pair<Item, MapleInventoryType> pair : items) {
                Item item = pair.getLeft();
                MapleInventoryType mit = pair.getRight();
                ps.setInt(1, value);
                ps.setString(2, account ? null : String.valueOf(id));
                ps.setString(3, account ? String.valueOf(id) : null);
                ps.setInt(4, item.getItemId());
                ps.setInt(5, mit.getType());
                ps.setInt(6, item.getPosition());
                ps.setInt(7, item.getQuantity());
                ps.setString(8, item.getOwner());
                ps.setString(9, item.getGMLog());
                if (item.getPet() != null) {
                    ps.setInt(10, Math.max(item.getUniqueId(), item.getPet().getUniqueId()));
                } else {
                    ps.setInt(10, item.getUniqueId());
                }

                ps.setShort(11, item.getFlag());
                ps.setLong(12, item.getExpiration());
                ps.setString(13, item.getGiftFrom());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    rs.close();
                    continue;
                } else {
                    autokey = rs.getInt(1);
                }
                if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                    Equip equip = (Equip) item;
                    pse.setInt(1, autokey);
                    rs.close();
                    pse.setInt(2, equip.getUpgradeSlots());
                    pse.setInt(3, equip.getLevel());
                    pse.setInt(4, equip.getStr());
                    pse.setInt(5, equip.getDex());
                    pse.setInt(6, equip.getInt());
                    pse.setInt(7, equip.getLuk());
                    pse.setInt(8, equip.getHp());
                    pse.setInt(9, equip.getMp());
                    pse.setInt(10, equip.getWatk());
                    pse.setInt(11, equip.getMatk());
                    pse.setInt(12, equip.getWdef());
                    pse.setInt(13, equip.getMdef());
                    pse.setInt(14, equip.getAcc());
                    pse.setInt(15, equip.getAvoid());
                    pse.setInt(16, equip.getHands());
                    pse.setInt(17, equip.getSpeed());
                    pse.setInt(18, equip.getJump());
                    pse.setInt(19, equip.getViciousHammer());
                    pse.setInt(20, equip.getItemEXP());
                    pse.setInt(21, equip.getDurability());
                    pse.setByte(22, equip.getState());
                    pse.setByte(23, equip.getEnhance());
                    pse.setInt(24, equip.getPotential1());
                    pse.setInt(25, equip.getPotential2());
                    pse.setInt(26, equip.getPotential3());
                    pse.setInt(27, equip.getPotential4());
                    pse.setInt(28, equip.getPotential5());
                    pse.setInt(29, equip.getIncSkill());
                    pse.setShort(30, equip.getCharmEXP());
                    pse.setShort(31, equip.getPVPDamage());
                    pse.setInt(32, equip.getStateMsg());
                    pse.setInt(33, equip.getSocket1());
                    pse.setInt(34, equip.getSocket2());
                    pse.setInt(35, equip.getSocket3());
                    pse.executeUpdate();
                }
            }
            pse.close();
            ps.close();
        } catch (SQLException ex) {
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (pse != null) {
                pse.close();
            }
        }
    }
}