package model;

public class Coordinate {

    public static Coordinate create(int x, int y) {
        if (x < 0 || y < 0) {
            return null;
        }
        return new Coordinate(x, y);
    }

    private int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return this.x == that.x &&
                this.y == that.y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int diffX(Coordinate c) {
        return this.x - c.x;
    }

    public int diffY(Coordinate c) {
        return this.y - c.y;
    }
}