package tc.symbols;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TablaDeSimbolos {
    private final Deque<Contexto> pila = new ArrayDeque<>();
    private final List<Contexto> historial = new ArrayList<>();

    public TablaDeSimbolos() {
        Contexto global = new Contexto("global", null);
        pila.push(global);
        historial.add(global);
    }

    public Contexto actual() { return pila.peek(); }

    public void push(String nombre) {
        Contexto c = new Contexto(nombre, actual());
        pila.push(c);
        historial.add(c);
    }

    public void pop() { pila.pop(); }

    public List<Contexto> historial() { return historial; }
}
