package tc.symbols;

public abstract class Id {
  protected final String nombre;
  protected final Tipo tipo;
  protected final int linea;
  protected final int columna;

  public Id(String nombre, Tipo tipo, int linea, int columna) {
    this.nombre = nombre;
    this.tipo = tipo;
    this.linea = linea;
    this.columna = columna;
  }

  public String nombre() {
    return nombre;
  }

  public Tipo tipo() {
    return tipo;
  }

  public int linea() {
    return linea;
  }

  public int columna() {
    return columna;
  }

  public boolean esFuncion() {
    return false;
  }
}
