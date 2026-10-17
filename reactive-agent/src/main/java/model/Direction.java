package model;

public enum Direction {
    NORTH(-1, 0),
    EAST(0, 1),
    SOUTH(1, 0),
    WEST(0, -1);

    public final int dr, dc;
    public Direction left;
    public Direction right;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }

    static {
        NORTH.left = WEST;  NORTH.right = EAST;
        EAST.left  = NORTH; EAST.right  = SOUTH;
        SOUTH.left = EAST;  SOUTH.right = WEST;
        WEST.left  = SOUTH; WEST.right  = NORTH;
    }

    public Direction turnLeft() {
        return left;
    }

    public Direction turnRight() {
        return right;
    }
}