package tc;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import tc.errors.BaseErrorListener;
import tc.grammar.CminiLexer;
import tc.grammar.CminiParser;
import tc.semantics.ErrorReporter;
import tc.semantics.MiListener;
import tc.symbols.TablaDeSimbolos;

import tc.intermediate.CodeGenerator;
import tc.intermediate.CodeGenVisitor;

import java.nio.file.*;

public class App {
    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "samples/ok_semantic.c";
        String code = Files.readString(Path.of(inputFile));

        // Lexer/Parser
        CharStream cs = CharStreams.fromString(code);
        CminiLexer lexer = new CminiLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CminiParser parser = new CminiParser(tokens);

        // Error sintactico personalizado
        ErrorReporter reporterSyntax = new ErrorReporter();
        BaseErrorListener synErr = new BaseErrorListener(reporterSyntax);
        parser.removeErrorListeners();
        lexer.removeErrorListeners();
        parser.addErrorListener(synErr);
        lexer.addErrorListener(synErr);

        // Árbol sintactico
        ParseTree tree = parser.program();
        System.out.println(tree.toStringTree(parser));

        // Tabla de simbolos + Listener semantico
        ErrorReporter reporter = new ErrorReporter();
        TablaDeSimbolos ts = new TablaDeSimbolos();
        MiListener.walk((CminiParser.ProgramContext) tree, ts, reporter);

        // Guardar reportes
        Files.createDirectories(Path.of("reports"));
        reporter.guardar("reports/syntax.txt", "reports/semantic.txt");

        // Dump de la Tabla de simbolos (simple)
        StringBuilder sb = new StringBuilder();
        ts.historial().forEach(ctx -> {
            sb.append("Contexto: ").append(ctx.nombre()).append("\n");
            ctx.ids().forEach((k, v) -> sb.append("  ")
                    .append(v.tipo()).append(" ").append(v.nombre())
                    .append(v.inicializada() ? " [init]" : " [no init]")
                    .append(v.usada() ? " [used]" : " [no used]")
                    .append(v.valor() != null ? " = " + v.valor() : "")
                    .append("\n"));
            sb.append("\n");
        });
        Files.writeString(Path.of("reports/symbols.txt"), sb.toString());

        if (reporter.haySemanticos()) {
            System.err.println("Se detectaron errores semánticos. Revisar reports/semantic.txt");
        } else {
            System.out.println("TP2: Árbol + TS generados sin errores semánticos.");
        }
        reporterSyntax.guardar("reports/syntax.txt", null);

        CodeGenerator cg = new CodeGenerator();
        CodeGenVisitor codegen = new CodeGenVisitor(cg);
        codegen.visit(tree);

        java.nio.file.Files.createDirectories(java.nio.file.Path.of("reports"));
        java.nio.file.Files.writeString(java.nio.file.Path.of("reports/intermediate.txt"), cg.dump());
        System.out.println("TAC generado en reports/intermediate.txt");
    }
}
