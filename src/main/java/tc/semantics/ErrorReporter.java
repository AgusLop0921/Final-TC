package tc.semantics;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {
    private final List<String> sintacticos = new ArrayList<>();
    private final List<String> semanticos = new ArrayList<>();

    public void addSyntax(String msg) { sintacticos.add(msg); }
    public void addSemantic(String msg) { semanticos.add(msg); }

    public boolean haySemanticos() { return !semanticos.isEmpty(); }

    public void guardar(String sintaxPath, String semanticPath) {
        try {
            if (sintaxPath != null) {
                Path p1 = Path.of(sintaxPath);
                Files.createDirectories(p1.getParent());
                Files.write(p1, sintacticos);
            }
            if (semanticPath != null) {
                Path p2 = Path.of(semanticPath);
                Files.createDirectories(p2.getParent());
                Files.write(p2, semanticos);
            }
        } catch (IOException e) {
            System.err.println("No se pudo escribir reportes: " + e.getMessage());
        }
    }
}
