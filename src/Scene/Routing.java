package Scene;

import model.Coordinate;
import model.Street;

public class Routing {
    static Coordinate intersection(Street street1, Street street2) {
        Coordinate A = street1.getCoordinates().get(0);
        Coordinate B = street1.getCoordinates().get(street1.getCoordinates().size() - 1);
        Coordinate C = street2.getCoordinates().get(0);
        Coordinate D = street2.getCoordinates().get(street1.getCoordinates().size() - 1);

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
}
