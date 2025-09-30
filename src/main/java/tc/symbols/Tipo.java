package tc.symbols;

public enum Tipo {
    INT, FLOAT, DOUBLE, BOOL, CHAR, VOID, DESCONOCIDO;

    public static Tipo fromString(String t) {
        if (t == null) return DESCONOCIDO;
        return switch (t) {
            case "int"    -> INT;
            case "float"  -> FLOAT;
            case "double" -> DOUBLE;
            case "bool"   -> BOOL;
            case "char"   -> CHAR;
            case "void"   -> VOID;
            default       -> DESCONOCIDO;
        };
    }

    public boolean esNumerico() {
        return this == INT || this == FLOAT || this == DOUBLE;
    }

    // Compatibilidad y promoción simple
    public static boolean asignable(Tipo destino, Tipo origen) {
        if (destino == DESCONOCIDO || origen == DESCONOCIDO) return false;
        if (destino == origen) return true;

        // Promociones permitidas
        if (destino == FLOAT && origen == INT) return true;
        if (destino == DOUBLE && (origen == INT || origen == FLOAT)) return true;

        return false;
    }
}