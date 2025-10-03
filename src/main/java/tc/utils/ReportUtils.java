package tc.utils;

import java.nio.file.Files;
import java.nio.file.Path;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import tc.grammar.CminiParser;
import tc.semantics.ErrorReporter;
import tc.symbols.TablaDeSimbolos;

public class ReportUtils {

  public static void printLexical(CommonTokenStream tokens) {
    System.out.println("\n=== 1. ANÁLISIS LÉXICO ===");
    System.out.println("Análisis léxico completado sin errores.");
    System.out.println(" Tokens procesados: " + tokens.getTokens().size());
    // System.out.println("Tokens: " + tokens.getTokens());
  }

  public static void printSyntax(ErrorReporter syntaxReporter) {
    System.out.println("\n=== 2. ANÁLISIS SINTÁCTICO ===");
    if (syntaxReporter.getSyntax().isEmpty()) {
      System.out.println("Análisis sintáctico completado sin errores.");
    } else {
      syntaxReporter.getSyntax().forEach(e -> System.err.println("❌ " + e));
    }
  }

  public static void printAST(ParseTree tree, CminiParser parser, String inputFile) throws Exception {
      System.out.println("\n=== 3. VISUALIZACIÓN DEL AST ===");

      System.out.println(tree.toStringTree(parser));
      System.out.println(tree.toStringTree(parser)); 
      String dot = toDot(tree, parser);

      Files.createDirectories(Path.of("reports"));

      String baseName = Path.of(inputFile).getFileName().toString().replace(".c", "");
      Path dotFile = Path.of("reports/" + baseName + "_ast.dot");

      Files.writeString(dotFile, dot);

      System.out.println("AST exportado en: " + dotFile.toAbsolutePath());
  }

  private static String toDot(ParseTree tree, CminiParser parser) {
    StringBuilder sb = new StringBuilder();
    sb.append("digraph AST {\n");
    walk(tree, parser, sb, 0, new int[]{0});
    sb.append("}\n");
    return sb.toString();
  }

  private static int walk(ParseTree node, CminiParser parser, StringBuilder sb, int parent, int[] counter) {
    int myId = counter[0]++;
    String label = node.getClass().getSimpleName().replace("Context", "");
    if (node.getChildCount() == 0) {
      label = node.getText().replace("\"", "\\\"");
    }
    sb.append("  node").append(myId).append(" [label=\"").append(label).append("\"];\n");

    if (parent != myId) {
      sb.append("  node").append(parent).append(" -> node").append(myId).append(";\n");
    }

    for (int i = 0; i < node.getChildCount(); i++) {
      walk(node.getChild(i), parser, sb, myId, counter);
    }

    return myId;
  }

  public static void printSemantic(TablaDeSimbolos ts, ErrorReporter reporter) throws Exception {
    System.out.println("\n=== 4. ANÁLISIS SEMÁNTICO ===");

    System.out.printf(
        "%-20s %-10s %-12s %-8s %-8s %-12s%n",
        "NOMBRE", "TIPO", "CATEGORÍA", "LÍNEA", "COLUMNA", "ÁMBITO");
    System.out.println(
        "---------------------------------------------------------------------------------");

    ts.historial()
        .forEach(
            ctx -> {
              ctx.ids()
                  .forEach(
                      (k, v) -> {
                        System.out.printf(
                            "%-20s %-10s %-12s %-8d %-8d %-12s%n",
                            v.nombre(),
                            v.tipo(),
                            v.esFuncion() ? "funcion" : "variable",
                            v.linea(),
                            v.columna(),
                            ctx.nombre());
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
