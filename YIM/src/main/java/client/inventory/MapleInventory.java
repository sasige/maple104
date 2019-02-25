package client.inventory;

import constants.GameConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapleInventory implements Iterable<Item>, Serializable {

    private Map<Short, Item> inventory;
    private byte slotLimit = 0;
    private MapleInventoryType type;
    private byte exSlotLimit = 0;

    public MapleInventory(MapleInventoryType type) {
        this.inventory = new LinkedHashMap<Short, Item>();
        this.type = type;
    }

    public void addSlot(byte slot) {
        this.slotLimit+= slot;
        if (this.slotLimit > 96) {
            this.slotLimit = 96;
        }
    }

    public byte getSlotLimit() {
        return slotLimit;
    }

    public void setSlotLimit(byte slot) {
        if (slot > 96) {
            slot = 96;
        }
       slotLimit = slot;
    }

    public byte getExSlotLimit() {
        return this.exSlotLimit;
    }

    public void setExSlotLimit(byte slot) {
        if (slot > 96) {
            slot = 96;
        }
        this.exSlotLimit = slot;
    }

    public Item findById(int itemId) {
        for (Item item : this.inventory.values()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public Item findByUniqueId(int itemId) {
        for (Item item : this.inventory.values()) {
            if (item.getUniqueId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public Item findByInventoryId(long itemId, int itemI) {
        for (Item item : inventory.values()) {
            if (item.getInventoryId() == itemId && item.getItemId() == itemI) {
                return item;
            }
        }
        return findById(itemI);
    }

    public Item findByInventoryIdOnly(long itemId, int itemI) {
        for (Item item : inventory.values()) {
            if (item.getInventoryId() == itemId && item.getItemId() == itemI) {
                return item;
            }
        }
        return null;
    }

    public int countById(int itemId) {
        int possesed = 0;
        for (Item item : inventory.values()) {
            if (item.getItemId() == itemId) {
                possesed += item.getQuantity();
            }
        }
        return possesed;
    }

    public List<Item> listById(int itemId) {
        List<Item> ret = new ArrayList<Item>();
        for (Item item : inventory.values()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }

        }

        if (ret.size() > 1) {
            Collections.sort(ret);
        }
        return ret;
    }

    public Collection<Item> list() {
        return inventory.values();
    }

    public List<Item> newList() {
        if (this.inventory.size() <= 0) {
            return Collections.emptyList();
        }
        return new LinkedList<Item>(inventory.values());
    }

    public List<Integer> listIds() {
        List<Integer> ret = new ArrayList<Integer>();
        for (Item item : this.inventory.values()) {
            if (!ret.contains(item.getItemId())) {
                ret.add(item.getItemId());
            }
        }
        if (ret.size() > 1) {
            Collections.sort(ret);
        }
        return ret;
    }

    public short addItem(Item item) {
        short slotId = getNextFreeSlot();
        if (slotId < 0) {
            return -1;
        }
        inventory.put(slotId, item);
        item.setPosition(slotId);
        return slotId;
    }

    public void addFromDB(Item item) {
        if (item.getPosition() < 0 && !type.equals(MapleInventoryType.EQUIPPED)) {
            return;
        }
        if (item.getPosition() > 0 && type.equals(MapleInventoryType.EQUIPPED)) {
            return;
        }
        inventory.put(item.getPosition(), item);
    }

    public void move(short sSlot, short dSlot, short slotMax) {
        Item source = (Item) this.inventory.get(sSlot);
        Item target = (Item) this.inventory.get(dSlot);
        if (source == null) {
            throw new InventoryException("Trying to move empty slot");
        }
        if (target == null) {
            if (dSlot < 0 && type.equals(MapleInventoryType.EQUIPPED)) {
                return;
            }
            if (dSlot > 0 && type.equals(MapleInventoryType.EQUIPPED)) {
                return;
            }
            source.setPosition(dSlot);
           inventory.put(dSlot, source);
            inventory.remove(sSlot);
        } else if (target.getItemId() == source.getItemId() && !GameConstants.isThrowingStar(source.getItemId()) && !GameConstants.isBullet(source.getItemId()) && target.getOwner().equals(source.getOwner()) && target.getExpiration() == source.getExpiration()) {
            if (type.getType() == MapleInventoryType.EQUIP.getType() || this.type.getType() == MapleInventoryType.CASH.getType()) {
                swap(target, source);
            } else if (source.getQuantity() + target.getQuantity() > slotMax) {
                source.setQuantity((short) (source.getQuantity() + target.getQuantity() - slotMax));
                target.setQuantity(slotMax);
            } else {
                target.setQuantity((short) (source.getQuantity() + target.getQuantity()));
                this.inventory.remove(sSlot);
            }
        } else {
            swap(target, source);
        }
    }

    private void swap(Item source, Item target) {
        inventory.remove(source.getPosition());
        inventory.remove(target.getPosition());
        short swapPos = source.getPosition();
        source.setPosition(target.getPosition());
        target.setPosition(swapPos);
        inventory.put(source.getPosition(), source);
        inventory.put(target.getPosition(), target);
    }

    public Item getItem(short slot) {
        return inventory.get(slot);
    }

    public void removeItem(short slot) {
        removeItem(slot, (short) 1, false);
    }

    public void removeItem(short slot, short quantity, boolean allowZero) {
        Item item = inventory.get(slot);
        if (item == null) {
            return;
        }
        item.setQuantity((short) (item.getQuantity() - quantity));
        if (item.getQuantity() < 0) {
            item.setQuantity((short) 0);
        }
        if (item.getQuantity() == 0 && !allowZero) {
            removeSlot(slot);
        }
    }

    public void removeSlot(short slot) {
        this.inventory.remove(slot);
    }

    public boolean isFull() {
        return inventory.size() >= slotLimit;
    }

    public boolean isFull(int margin) {
        return inventory.size() + margin >=slotLimit;
    }

    public short getNextFreeSlot() {
        if (isFull()) {
            return -1;
        }
        for (short i = 1; i <= slotLimit; i = (short) (i + 1)) {
            if (!this.inventory.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }

    public short getNumFreeSlot() {
        if (isFull()) {
            return 0;
        }
        byte free = 0;
        for (short i = 1; i <=slotLimit; i++) {
            if (!this.inventory.containsKey(i)) {
                free ++;
            }
        }
        return free;
    }

    public MapleInventoryType getType() {
        return type;
    }

    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableCollection(inventory.values()).iterator();
    }
}