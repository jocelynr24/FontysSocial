package routin.fontyssocial.modelGoogle;

import java.util.Arrays;

public class Row {

    public Element[] elements;

    public Row() {
    }

    public Element[] getElements() {
        return elements;
    }

    public void setElements(Element[] elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "Row{" +
                "elements=" + Arrays.toString(elements) +
                '}';
    }
}
