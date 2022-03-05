package Enums;

public enum SimpleDirection {
    NONE (0),
    UP (1),
    DOWN (3),
    LEFT (2),
    RIGHT (0);

    public final int degrees;

    SimpleDirection(int degrees) {
        this.degrees = degrees;
    }
}
