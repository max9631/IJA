package common;

import java.lang.Math;

import model.Coordinate;
import model.Street;

public class Routing {
    public static Coordinate intersection(Street street1, Street street2) {
        for (int i = 1; i < street1.getCoordinates().size(); i++) {
            Coordinate A = street1.getCoordinates().get(i-1).getKey();
            Coordinate B = street1.getCoordinates().get(i).getKey();
            for (int j = 1; j < street2.getCoordinates().size(); j++) {
                Coordinate C = street2.getCoordinates().get(j-1).getKey();
                Coordinate D = street2.getCoordinates().get(j).getKey();
                Coordinate intersection = intersection(A, B, C, D);
                if (intersection != null) {
                    return intersection;
                } else if (A.equals(C)) { // bit of a heck, when both streets are the same
                    return A;
                }
            }

        }
        return null;
    }

    public static Coordinate intersection(Coordinate A, Coordinate B, Coordinate C, Coordinate D) {
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
