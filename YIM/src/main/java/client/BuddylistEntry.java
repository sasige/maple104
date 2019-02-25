package client;

public class BuddylistEntry {

    private String name;
    private String group;
    private int cid;
    private int channel;
    private boolean visible;

    public BuddylistEntry(String name, int characterId, String group, int channel, boolean visible) {
        this.name = name;
        this.cid = characterId;
        this.group = group;
        this.channel = channel;
        this.visible = visible;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isOnline() {
        return channel >= 0;
    }

    public void setOffline() {
        channel = -1;
    }

    public String getName() {
        return name;
    }

    public int getCharacterId() {
        return cid;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String g) {
        this.group = g;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + cid;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BuddylistEntry other = (BuddylistEntry) obj;

        return cid == other.cid;
    }
}