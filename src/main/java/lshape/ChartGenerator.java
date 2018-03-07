package lshape;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.util.ArrayList;
import java.util.List;

public class ChartGenerator {

    public static AWTChart draw(int cuts, double[] result, JavaFXChartFactory factory, String toolkit) {
        Shape surface = new Shape(generatePolygons(cuts, result));
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new org.jzy3d.colors.Color(1, 1, 1, 1f)));
        surface.setWireframeDisplayed(true);
        surface.setWireframeColor(org.jzy3d.colors.Color.BLACK);

        AWTChart chart = (AWTChart) factory.newChart(Quality.Advanced, toolkit);
        chart.getScene().getGraph().add(surface);
        return chart;
    }

    private static List<Polygon> generatePolygons(int splits, double[] result) {
        int pointsInRow = 2 * (splits + 1) + 1;

        double length = 2.0 / (pointsInRow - 1);

        List<Polygon> polygons = new ArrayList<>();

        double[][] resultToPlot = new double[pointsInRow][pointsInRow];
        MyPoint[][] pointToPlot = new MyPoint[pointsInRow][pointsInRow];

        double b1 = -1;
        double b2 = 1;
        int el = 0;
        for (int i = 0; i < pointsInRow; i++) {
            for (int j = 0; j < pointsInRow; j++) {
                if (i <= (pointsInRow / 2) || j >= (pointsInRow / 2)) {
                    MyPoint p = new MyPoint(b1, b2);
                    resultToPlot[i][j] = result[el++];
                    pointToPlot[i][j] = p;
                } else {
                    MyPoint p = new MyPoint(b1, b2);
                    resultToPlot[i][j] = 0;
                    pointToPlot[i][j] = p;
                }
                b1 += length;
            }
            b1 = -1;
            b2 -= length;
        }

        for (int i = 0; i < pointsInRow - 1; i++) {
            for (int j = 0; j < pointsInRow - 1; j++) {
                Polygon polygon = new Polygon();
                polygon.add(new Point(new Coord3d(pointToPlot[i][j].x, pointToPlot[i][j].y, resultToPlot[i][j])));
                polygon.add(new Point(new Coord3d(pointToPlot[i][j + 1].x, pointToPlot[i][j + 1].y, resultToPlot[i][j + 1])));
                polygon.add(new Point(new Coord3d(pointToPlot[i + 1][j + 1].x, pointToPlot[i + 1][j + 1].y, resultToPlot[i + 1][j + 1])));
                polygon.add(new Point(new Coord3d(pointToPlot[i + 1][j].x, pointToPlot[i + 1][j].y, resultToPlot[i + 1][j])));
                polygons.add(polygon);
            }
        }
        return polygons;
    }
}

