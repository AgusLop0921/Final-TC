package tc.semantics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {

    // Colores ANSI para consola
    public static class ConsoleColors {
        public static final String RESET  = "\u001B[0m";
        public static final String RED    = "\u001B[31m";
        public static final String GREEN  = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
    }

    // Buffers
    private final List<String> syntaxErrors   = new ArrayList<>();
    private final List<String> semanticErrors = new ArrayList<>();
    private final List<String> warnings       = new ArrayList<>();
    private final List<String> infos          = new ArrayList<>();

    // ---- Adders ----
    public void addSyntax(String msg) {
        String line = "[SYNTAX ERROR] " + msg;
        syntaxErrors.add(line);
    }

    public void addSemantic(String msg) {
        String line = "❌ Error: " + msg;
        semanticErrors.add(line);
    }

    public void addWarning(String msg) {
        String line = "⚠️ Warning: " + msg;
        warnings.add(line);
    }

    public void addInfo(String msg) {
        String line = "✅ " + msg;
        infos.add(line);
        System.out.println(ConsoleColors.GREEN + line + ConsoleColors.RESET);
    }

    // ---- Queries ----
    public boolean haySintacticos() { return !syntaxErrors.isEmpty(); }
    public boolean haySemanticos()  { return !semanticErrors.isEmpty(); }
    public boolean hayWarnings()    { return !warnings.isEmpty(); }
    public boolean hayInfos()       { return !infos.isEmpty(); }

    // ---- Save helpers por tipo ----
    public void guardarSyntax(String path)        { write(path, syntaxErrors); }
    public void guardarSemantic(String path)      { write(path, semanticErrors); }
    public void guardarWarnings(String path)      { write(path, warnings); }
    public void guardarInfos(String path)         { write(path, infos); }

    public List<String> getSyntax() { return syntaxErrors; }
    public List<String> getSemantics() { return semanticErrors; }
    public List<String> getWarnings() { return warnings; }


    // ---- Save todo junto (compat) ----
    // Útil si tu App ya llama guardar("reports/syntax.txt")
    // -> mezcla sintácticos + semánticos + warnings + infos (en ese orden)
    public void guardar(String path) {
        List<String> all = new ArrayList<>(syntaxErrors.size()
                                         + semanticErrors.size()
                                         + warnings.size()
                                         + infos.size());
        all.addAll(syntaxErrors);
        all.addAll(semanticErrors);
        all.addAll(warnings);
        all.addAll(infos);
        write(path, all);
    }

    // ---- Save múltiple en archivos separados (si querés) ----
    public void guardarTodo(String syntaxPath, String semanticPath, String warningsPath, String infosPath) {
        if (syntaxPath   != null) write(syntaxPath,   syntaxErrors);
        if (semanticPath != null) write(semanticPath, semanticErrors);
        if (warningsPath != null) write(warningsPath, warnings);
        if (infosPath    != null) write(infosPath,    infos);
    }

    // ---- IO ----
    private void write(String path, List<String> lines) {
        try {
            if (path == null) return;
            Path p = Path.of(path);
            Files.createDirectories(p.getParent());
            Files.write(p, lines);
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "[ERROR] No se pudo escribir " + path + ConsoleColors.RESET);
        }
    }
}