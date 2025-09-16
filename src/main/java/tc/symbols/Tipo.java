package tc.symbols;

public enum Tipo {
    INT, FLOAT, VOID, DESCONOCIDO;

    public static Tipo fromString(String t) {
        if (t == null) return DESCONOCIDO;
        return switch (t) {
            case "int" -> INT;
            case "float" -> FLOAT;
            case "void" -> VOID;
            default -> DESCONOCIDO;
        };
    }

    public boolean esNumerico() { return this == INT || this == FLOAT; }

    // Promoción simple: int -> float
    public static boolean asignable(Tipo destino, Tipo origen) {
        if (destino == DESCONOCIDO || origen == DESCONOCIDO) return false;
        if (destino == origen) return true;
        return destino == FLOAT && origen == INT;
    }
}
