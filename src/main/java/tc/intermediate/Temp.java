package tc.intermediate;

public class Temp {
    private static int count = 0;
    private final String name;

    public Temp() {
        this.name = "t" + (++count);
    }

    public String getName() {
        return name;
    }
}