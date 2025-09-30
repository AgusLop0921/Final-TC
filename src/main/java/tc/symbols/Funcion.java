package tc.symbols;

import java.util.ArrayList;
import java.util.List;

public class Funcion extends Id {
    private final List<Variable> parametros = new ArrayList<>();

    public Funcion(String nombre, Tipo retorno, int linea, int columna) {
        super(nombre, retorno, linea, columna);
    }

    public void addParametro(Variable v) { parametros.add(v); }
    public List<Variable> parametros() { return parametros; }

    public Tipo retorno() { return this.tipo(); }

    @Override
    public boolean esFuncion() { return true; }
}