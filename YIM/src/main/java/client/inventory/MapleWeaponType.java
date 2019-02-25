package client.inventory;

public enum MapleWeaponType {

    NOT_A_WEAPON(1.43F, 20),
    BOW(1.2F, 15),
    CLAW(1.75F, 15),
    DAGGER(1.3F, 20),
    CROSSBOW(1.35F, 15),
    AXE1H(1.2F, 20),
    SWORD1H(1.2F, 20),
    BLUNT1H(1.2F, 20),
    AXE2H(1.32F, 20),
    SWORD2H(1.32F, 20),
    BLUNT2H(1.32F, 20),
    POLE_ARM(1.49F, 20),
    SPEAR(1.49F, 20),
    STAFF(1.0F, 25),
    WAND(1.0F, 25),
    KNUCKLE(1.7F, 20),
    GUN(1.5F, 15),
    CANNON(1.35F, 15),
    DUAL_BOW(2.0F, 15),
    MAGIC_ARROW(2.0F, 15),
    KATARA(1.3F, 20);
    private float damageMultiplier;
    private int baseMastery;

    private MapleWeaponType(float maxDamageMultiplier, int baseMastery) {
        this.damageMultiplier = maxDamageMultiplier;
        this.baseMastery = baseMastery;
    }

    public float getMaxDamageMultiplier() {
        return this.damageMultiplier;
    }

    public int getBaseMastery() {
        return this.baseMastery;
    }
}