package client;

public class CharacterNameAndId {

    private int id;
    private String name;
    private String group;

    public CharacterNameAndId(int id, String name, String group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }
}