package tc.symbols;

public class Variable extends Id {
    private final int dimension; // 0 = no es array

    public Variable(String nombre, Tipo tipo) {
        super(nombre, tipo);
        this.dimension = 0;
    }

    public Variable(String nombre, Tipo tipo, int dimension) {
        super(nombre, tipo);
        this.dimension = dimension;
    }

    public boolean esArray() {
        return dimension > 0;
    }

    public int dimension() {
        return dimension;
    }
}