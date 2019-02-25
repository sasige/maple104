package server.maps;

public enum SummonMovementType {

    STATIONARY(0),
    FOLLOW(1),
    WALK_STATIONARY(2),
    CIRCLE_FOLLOW(4),
    CIRCLE_STATIONARY(5);
    private int val;

    private SummonMovementType(int val) {
        this.val = val;
    }

    public int getValue() {
        return this.val;
    }
}
