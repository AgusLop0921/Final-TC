package tc.intermediate;

public class Label {
    private static int count = 0;
    private final String name;

    public Label() {
        this.name = "L" + (++count);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
