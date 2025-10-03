package tc.symbols;

import java.util.LinkedHashMap;
import java.util.Map;

public class Contexto {
  private final String nombre;
  private final Contexto padre;
  private final Map<String, Id> ids = new LinkedHashMap<>();

  public Contexto(String nombre, Contexto padre) {
    this.nombre = nombre;
    this.padre = padre;
  }

  public String nombre() {
    return nombre;
  }

  public Contexto padre() {
    return padre;
  }

  public boolean declarar(Id id) {
    if (ids.containsKey(id.nombre())) return false;
    ids.put(id.nombre(), id);
    return true;
  }

  public Id resolverLocal(String nombre) {
    return ids.get(nombre);
  }

  public Id resolver(String nombre) {
    Id x = ids.get(nombre);
    if (x != null) return x;
    return padre != null ? padre.resolver(nombre) : null;
  }

  public Map<String, Id> ids() {
    return ids;
  }
}
