package tc.symbols;

import java.util.ArrayList;
import java.util.List;

public class Funcion extends Id {
    private final Tipo retorno;
    private final List<Variable> parametros = new ArrayList<>();

    public Funcion(String nombre, Tipo retorno) {
        super(nombre, retorno);
        this.retorno = retorno;
    }

    public Tipo retorno() {
        return retorno;
    }

    public void addParametro(Variable v) {
        parametros.add(v);
    }

    public List<Variable> parametros() {
        return parametros;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(retorno).append(" ").append(nombre()).append("(");
        for (int i = 0; i < parametros.size(); i++) {
            sb.append(parametros.get(i).tipo()).append(" ").append(parametros.get(i).nombre());
            if (i < parametros.size() - 1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}