package tc.intermediate;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    private final List<Instruction> code = new ArrayList<>();

    public Temp newTemp() { return new Temp(); }
    public Label newLabel() { return new Label(); }

    public void emit(String codeLine) {
        code.add(new Instruction(codeLine));
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        for (Instruction i : code) sb.append(i).append("\n");
        return sb.toString();
    }
}
