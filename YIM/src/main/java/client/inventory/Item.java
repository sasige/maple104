package client.inventory;

import java.io.Serializable;

public class Item implements Comparable<Item>, Serializable {

    private int id;
    private short position;
    private short quantity;
    private short flag;
    private long expiration = -1L;
    private long inventoryitemid = 0L;
    private MaplePet pet = null;
    private int uniqueid;
    private int sn;
    private String owner = "";
    private String GameMaster_log = "";
    private String giftFrom = "";

    public Item(int id, short position, short quantity, short flag, int uniqueid) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = uniqueid;
    }

    public Item(int id, short position, short quantity, short flag) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = -1;
    }

    public Item(int id, byte position, short quantity) {
        this.id = id;
        this.position = (short) position;
        this.quantity = quantity;
        this.uniqueid = -1;
    }

    public Item copyWithQuantity(short quantitys) {
        Item ret = new Item(this.id, this.position, quantitys, this.flag, this.uniqueid);
        ret.pet = this.pet;
        ret.owner = this.owner;
        ret.sn = this.sn;
        ret.GameMaster_log = this.GameMaster_log;
        ret.expiration = this.expiration;
        ret.giftFrom = this.giftFrom;
        return ret;
    }

    public Item copy() {
        Item ret = new Item(this.id, this.position, this.quantity, this.flag, this.uniqueid);
        ret.pet = this.pet;
        ret.owner = this.owner;
        ret.sn = this.sn;
        ret.GameMaster_log = this.GameMaster_log;
        ret.expiration = this.expiration;
        ret.giftFrom = this.giftFrom;
        return ret;
    }

    public void setPosition(short position) {
        this.position = position;
        if (this.pet != null) {
            this.pet.setInventoryPosition(position);
        }
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public int getItemId() {
        return this.id;
    }

    public short getPosition() {
        return this.position;
    }

    public short getFlag() {
        return this.flag;
    }

    public short getQuantity() {
        return this.quantity;
    }

    public byte getType() {
        return 2;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public long getExpiration() {
        return this.expiration;
    }

    public void setExpiration(long expire) {
        this.expiration = expire;
    }

    public String getGMLog() {
        return this.GameMaster_log;
    }

    public void setGMLog(String GameMaster_log) {
        this.GameMaster_log = GameMaster_log;
    }

    public int getUniqueId() {
        return this.uniqueid;
    }

    public void setUniqueId(int ui) {
        this.uniqueid = ui;
    }

    public int getSN() {
        return this.sn;
    }

    public void setSN(int sn) {
        this.sn = sn;
    }

    public long getInventoryId() {
        return this.inventoryitemid;
    }

    public void setInventoryId(long ui) {
        this.inventoryitemid = ui;
    }

    public MaplePet getPet() {
        return this.pet;
    }

    public void setPet(MaplePet pet) {
        this.pet = pet;
        if (pet != null) {
            this.uniqueid = pet.getUniqueId();
        }
    }

    public void setGiftFrom(String gf) {
        this.giftFrom = gf;
    }

    public String getGiftFrom() {
        return this.giftFrom;
    }

    @Override
    public int compareTo(Item other) {
        if (Math.abs(this.position) < Math.abs(other.getPosition())) {
            return -1;
        }
        if (Math.abs(this.position) == Math.abs(other.getPosition())) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        Item ite = (Item) obj;
        return (this.uniqueid == ite.getUniqueId()) && (this.id == ite.getItemId()) && (this.quantity == ite.getQuantity()) && (Math.abs(this.position) == Math.abs(ite.getPosition()));
    }

    @Override
    public String toString() {
        return "物品: " + this.id + " 数量: " + this.quantity;
    }
}