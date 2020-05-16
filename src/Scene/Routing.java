package Scene;

import java.lang.Math;
import model.Coordinate;
import model.Street;

public class Routing {
    public static Coordinate intersection(Street street1, Street street2) {
        Coordinate A = street1.getCoordinates().get(0);
        Coordinate B = street1.getCoordinates().get(street1.getCoordinates().size() - 1);
        Coordinate C = street2.getCoordinates().get(0);
        Coordinate D = street2.getCoordinates().get(street2.getCoordinates().size() - 1);

        double a1 = B.getY() - A.getY();
        double b1 = A.getX() - B.getX();
        double c1 = a1*(A.getX()) + b1*(A.getY());

        double a2 = D.getY() - C.getY();
        double b2 = C.getX() - D.getX();
        double c2 = a2*(C.getX())+ b2*(C.getY());

        double determinant = a1*b2 - a2*b1;

        if (determinant == 0) return null;

        double x = (b2*c1 - b1*c2)/determinant;
        double y = (a1*c2 - a2*c1)/determinant;
        return new Coordinate((int)x, (int)y);
    }

    public static double distance(Coordinate c1, Coordinate c2) {
        double x1 = c1.getX();
        double y1 = c1.getY();
        double x2 = c2.getX();
        double y2 = c2.getY();

        double aSquared = Math.pow(Math.abs(x1 - x2), 2);
        double bSquared = Math.pow(Math.abs(y1 - y2), 2);

        return Math.sqrt(aSquared + bSquared);
    }

    public static Coordinate move(Coordinate from, Coordinate to, double distance) {
        double fullDistance = Routing.distance(from, to);
        Coordinate vector = new Coordinate(to.getX() - from.getX(), to.getY() - from.getY());
        vector.setX((int)(vector.getX() * (distance/fullDistance)));
        vector.setY((int)(vector.getY() * (distance/fullDistance)));
        return new Coordinate(from.getX() + vector.getX(), from.getY() + vector.getY());
    }

    public static boolean isOnLine(Coordinate from, Coordinate to, Coordinate point) {
        return distance(from, point) + distance(point, to) == distance(from, to);
    }
}
