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

    public boolean isOpposite(SimpleDirection other) {
        switch (this) {
            default:
                return false;
            case UP:
                return other == DOWN;
            case DOWN:
                return other == UP;
            case LEFT:
                return other == RIGHT;
            case RIGHT:
                return other == LEFT;
        }
    }
}
