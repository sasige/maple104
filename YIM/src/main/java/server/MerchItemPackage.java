package server;

import client.inventory.Item;
import java.util.ArrayList;
import java.util.List;

public class MerchItemPackage {

    private long sentTime;
    private int mesos = 0;
    private List<Item> items = new ArrayList();

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public long getSentTime() {
        return this.sentTime;
    }

    public int getMesos() {
        return this.mesos;
    }

    public void setMesos(int set) {
        this.mesos = set;
    }
}