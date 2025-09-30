package tc.utils;

import org.antlr.v4.runtime.CommonTokenStream;
import tc.semantics.ErrorReporter;
import tc.symbols.TablaDeSimbolos;

public class ReportUtils {

    public static void printLexical(CommonTokenStream tokens) {
        System.out.println("\n=== 1. ANÁLISIS LÉXICO ===");
        System.out.println("✅ Análisis léxico completado sin errores.");
        System.out.println("📊 Tokens procesados: " + tokens.getTokens().size());
    }

    public static void printSyntax(ErrorReporter syntaxReporter) {
        System.out.println("\n=== 2. ANÁLISIS SINTÁCTICO ===");
        if (syntaxReporter.getSyntax().isEmpty()) {
            System.out.println("✅ Análisis sintáctico completado sin errores.");
        } else {
            syntaxReporter.getSyntax().forEach(e -> System.err.println("❌ " + e));
        }
    }

    public static void printAST() {
        System.out.println("\n=== 3. VISUALIZACIÓN DEL AST ===");
    }

    public static void printSemantic(TablaDeSimbolos ts, ErrorReporter reporter) throws Exception {
        System.out.println("\n=== 4. ANÁLISIS SEMÁNTICO ===");

        // tabla bonita
        System.out.printf("%-20s %-10s %-12s %-8s %-8s %-12s %-10s%n",
                "NOMBRE", "TIPO", "CATEGORÍA", "LÍNEA", "COLUMNA", "ÁMBITO", "DETALLES");
        System.out.println("---------------------------------------------------------------------------------");

        ts.historial().forEach(ctx -> {
            ctx.ids().forEach((k, v) -> {
                System.out.printf("%-20s %-10s %-12s %-8d %-8d %-12s %-10s%n",
                        v.nombre(),
                        v.tipo(),
                        v.esFuncion() ? "funcion" : "variable",
                        v.linea(),
                        v.columna(),
                        ctx.nombre(),
                        "[private]");
            });
        });

        // errores
        System.out.println("\n x ERRORES SEMÁNTICOS:");
        if (reporter.getSemantics().isEmpty()) {
            System.out.println("  (ninguno)");
        } else {
            reporter.getSemantics().forEach(s -> System.out.println("  " + s));
        }

        // warnings
        System.out.println("\n ! WARNINGS SEMÁNTICOS:");
        if (reporter.getWarnings().isEmpty()) {
            System.out.println("  (ninguno)");
        } else {
            reporter.getWarnings().forEach(w -> System.out.println("  " + w));
        }

        if (reporter.haySemanticos()) {
            System.err.println("\n❌ Compilación detenida debido a errores semánticos.");
        } else {
            System.out.println("\n✅ Compilación completada con éxito.");
        }
    }
}