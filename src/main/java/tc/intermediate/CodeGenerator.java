package tc.intermediate;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    private final List<String> code = new ArrayList<>();
    private int tempCount = 0;
    private int labelCount = 0;
    private int instrCount = 0;

    public void emit(String instr) {
        code.add(instr);
    }

    public Temp newTemp() {
        return new Temp("t" + (++tempCount));
    }

    public Label newLabel(String prefix) {
        return new Label(prefix + "_" + (++labelCount));
    }

    public int nextInstr() {
        return instrCount++;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("// Código de tres direcciones generado\n");
        for (int i = 0; i < code.size(); i++) {
            sb.append(code.get(i)).append("\n");
        }
        return sb.toString();
    }
}