package modelGoogle;

public class Distance {

    public int value;
    public String text;


    public Distance() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                ", text='" + text + '\'' +
                '}';
    }
}
