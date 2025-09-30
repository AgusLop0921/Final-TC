package tc.symbols;

public class Variable extends Id {
    private final int dimension;
    private boolean inicializada = false;
    private boolean usada = false;
    private String valor;

    public Variable(String nombre, Tipo tipo, int linea, int columna) {
        super(nombre, tipo, linea, columna);
        this.dimension = 0;
    }

    public Variable(String nombre, Tipo tipo, int dimension, int linea, int columna) {
        super(nombre, tipo, linea, columna);
        this.dimension = dimension;
    }

    public boolean esArray() { return dimension > 0; }

    public void marcarInicializada() { this.inicializada = true; }
    public void marcarUsada() { this.usada = true; }

    public boolean inicializada() { return inicializada; }
    public boolean usada() { return usada; }

    public int dimension() { return dimension; }

    public void setValor(String v) { this.valor = v; }
    public String valor() { return valor; }
}