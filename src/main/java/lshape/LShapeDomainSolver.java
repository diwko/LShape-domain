package lshape;

import java.util.ArrayList;
import java.util.List;

public class LShapeDomainSolver {
    private static double fi[][] = {
            {2.0 / 3, -1.0 / 6, -1.0 / 3, -1.0 / 6},
            {-1.0 / 6, 2.0 / 3, -1.0 / 6, -1.0 / 3},
            {-1.0 / 3, -1.0 / 6, 2.0 / 3, -1.0 / 6},
            {-1.0 / 6, -1.0 / 3, -1.0 / 6, 2.0 / 3}};

    public static double[] solve(int cuts) {
        List<Element> elements = generateElements(cuts);
        System.out.println("Elementy: " + elements.size());
        System.out.println("Punkty: " + (3 * cuts * cuts + 10 * cuts + 8));

        double[][] lhs = generateLHS(cuts, elements);

        double[] rhs = generateRHS(cuts);

        return GaussianElimination.lsolve(lhs, rhs);
    }

    private static List<Element> generateElements(int splits) {
        int elementsInRow = 2 * (splits + 1);
        int halfElemetsInRow = splits + 1;
        double length = 2.0 / elementsInRow;

        List<Element> elements = new ArrayList<>();

        double b1 = -1;
        double b2 = 1 - length;
        for (int i = 0; i < elementsInRow; i++) {
            for (int j = 0; j < elementsInRow; j++) {
                if (i < (elementsInRow / 2) || j >= (halfElemetsInRow)) {
                    Element e = new Element(new MyPoint(b1, b2), length, length);
                    elements.add(e);

                    if (i < halfElemetsInRow) {
                        e.corners[0] = (i + 1) * (elementsInRow + 1) + j;
                        e.corners[1] = e.corners[0] + 1;
                        e.corners[3] = i * (elementsInRow + 1) + j;
                        e.corners[2] = e.corners[3] + 1;
                    } else {
                        int lastFull = (splits + 2) * (elementsInRow + 1) - 1;
                        e.corners[0] = lastFull + (halfElemetsInRow + 1) * (i - halfElemetsInRow) + (j - halfElemetsInRow + 1);
                        e.corners[1] = e.corners[0] + 1;
                        if (i == halfElemetsInRow) {
                            e.corners[3] = i * (elementsInRow + 1) + j;
                            e.corners[2] = e.corners[3] + 1;
                        } else {
                            e.corners[3] = lastFull + (halfElemetsInRow + 1) * (i - halfElemetsInRow - 1) + (j - halfElemetsInRow + 1);
                            e.corners[2] = e.corners[3] + 1;
                        }
                    }
                }
                b1 += length;
            }
            b1 = -1;
            b2 -= length;
        }
        return elements;
    }

    private static double[][] generateLHS(int splitNumber, List<Element> elements) {
        int points = 3 * splitNumber * splitNumber + 10 * splitNumber + 8;
        double[][] lhs = new double[points][points];

        for (Element e : elements) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++)
                    lhs[e.corners[i]][e.corners[j]] += fi[i][j];
            }
        }

        //zeroing Dirichlet boundary condition
        int dirichletHorizontalBegin = (splitNumber + 1) * ((splitNumber + 1) * 2 + 1);
        int dirichletHorizontalEnd = dirichletHorizontalBegin + splitNumber + 1;

        for (int i = dirichletHorizontalBegin; i <= dirichletHorizontalEnd; i++) {
            for (int j = 0; j < points; j++) {
                if (i == j)
                    lhs[i][j] = 1;
                else
                    lhs[i][j] = 0;
            }
        }

        int x = splitNumber + 2;
        for (int i = dirichletHorizontalEnd + x; i < points; i += x) {
            for (int j = 0; j < points; j++) {
                if (i == j)
                    lhs[i][j] = 1;
                else
                    lhs[i][j] = 0;
            }
        }

        return lhs;
    }

    private static double g(double x, double y) {
        //double angle = Math.atan(y/x);
        //double r = Math.sqrt(x*x+y*y);
        //return Math.pow(r, 2.0/3.0) * Math.pow(Math.sin(angle + Math.PI / 2.0), 2.0/3.0);

        //Test1
        //return 0;

        //Test 2
        double eps = 1e-6;
        if (Math.abs(x + 1) < eps)
            return -y;
        if (Math.abs(x - 1) < eps)
            return y;
        if (Math.abs(y + 1) < eps)
            return -x;
        if (Math.abs(y - 1) < eps)
            return x;
        return 0;
    }

    private static double[] generateRHS(int splitNumber) {
        int points = 3 * splitNumber * splitNumber + 10 * splitNumber + 8;
        int pointsInRow = 2 * (splitNumber + 1) + 1;
        double[] rhs = new double[points];

        double edge = 1.0 / (splitNumber + 1);

        //upper edge
        int p;
        double x = -1 + 0.5 * edge;
        for (p = 0; p < pointsInRow - 1; p++) {
            x += edge;
            double value = 0.5 * g(x, 1) * edge;
            rhs[p] += value;
            rhs[p + 1] += value;
        }

        //bottom edge
        x = 0;
        p = (pointsInRow / 2 + 1) * pointsInRow + (splitNumber + 2) * splitNumber + 1;
        for (; p < points - 1; p++) {
            x += edge;
            double value = 0.5 * g(x, -1) * edge;
            rhs[p] += value;
            rhs[p + 1] += value;
        }

        //left edge
        int y = 0;
        int pEnd = splitNumber * pointsInRow + 1;
        for (p = 0; p < pEnd; p += pointsInRow) {
            y += edge;
            double value = 0.5 * g(-1, y) * edge;
            rhs[p] += value;
            rhs[p + pointsInRow] += value;
        }

        //right edge
        y = 0;
        pEnd = pointsInRow * (splitNumber + 2) - 1;
        for (p = pointsInRow - 1; p < pEnd; p += pointsInRow) {
            y += edge;
            double value = 0.5 * g(1, y) * edge;
            rhs[p] += value;
            rhs[p + pointsInRow] += value;
        }

        for (; p < points - 1; p += splitNumber + 2) {
            y += edge;
            double value = 0.5 * g(1, y) * edge;
            rhs[p] += value;
            rhs[p + splitNumber + 2] += value;
        }

        //zeros to left down corner
        rhs[(splitNumber + 1) * pointsInRow] = 0;

        return rhs;
    }
}