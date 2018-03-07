package lshape;

public class Element {
    MyPoint b;
    double width;
    double height;
    int[] corners = new int[4];

    public Element(MyPoint b, double width, double height) {
        this.b = b;
        this.width = width;
        this.height = height;
    }
}
