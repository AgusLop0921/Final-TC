package tc.intermediate;

public class Instruction {
    private final String code;

    public Instruction(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}