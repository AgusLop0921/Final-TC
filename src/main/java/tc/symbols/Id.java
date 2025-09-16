package tc.symbols;

public abstract class Id {
    protected final String nombre;
    protected final Tipo tipo;
    protected String valor;         // texto crudo (opcional para TP2)
    protected boolean usada = false;
    protected boolean inicializada = false;

    protected Id(String nombre, Tipo tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String nombre() { return nombre; }
    public Tipo tipo() { return tipo; }

    public boolean usada() { return usada; }
    public boolean inicializada() { return inicializada; }

    public void marcarUsada() { this.usada = true; }
    public void marcarInicializada() { this.inicializada = true; }
    public void setValor(String v) { this.valor = v; }
    public String valor() { return valor; }
}
