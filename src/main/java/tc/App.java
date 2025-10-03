package tc;

import java.nio.file.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import tc.errors.BaseErrorListener;
import tc.grammar.CminiLexer;
import tc.grammar.CminiParser;
import tc.intermediate.CodeGenVisitor;
import tc.intermediate.CodeGenerator;
import tc.semantics.ErrorReporter;
import tc.semantics.MiListener;
import tc.symbols.TablaDeSimbolos;
import tc.utils.ReportUtils;

public class App {
  public static void main(String[] args) throws Exception {
    String inputFile = args.length > 0 ? args[0] : "samples/tp1/ok_semantic.c";
    String code = Files.readString(Path.of(inputFile));

    // Lexer + Parser
    CharStream cs = CharStreams.fromString(code);
    CminiLexer lexer = new CminiLexer(cs);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CminiParser parser = new CminiParser(tokens);

    // Error sintáctico
    ErrorReporter reporterSyntax = new ErrorReporter();
    BaseErrorListener synErr = new BaseErrorListener(reporterSyntax);
    parser.removeErrorListeners();
    lexer.removeErrorListeners();
    parser.addErrorListener(synErr);
    lexer.addErrorListener(synErr);

    // Árbol
    ParseTree tree = parser.program();

    // Semántico
    ErrorReporter reporter = new ErrorReporter();
    TablaDeSimbolos ts = new TablaDeSimbolos();
    MiListener.walk((CminiParser.ProgramContext) tree, ts, reporter);

    // REPORTES
    ReportUtils.printLexical(tokens);
    ReportUtils.printSyntax(reporterSyntax);
    ReportUtils.printAST(tree, parser, inputFile);
    ReportUtils.printSemantic(ts, reporter);

    // TAC
    if (!reporter.haySemanticos()) {
      CodeGenerator cg = new CodeGenerator();
      CodeGenVisitor codegen = new CodeGenVisitor(cg);
      codegen.visit(tree);

      Files.createDirectories(Path.of("reports"));
      Files.writeString(Path.of("reports/intermediate.txt"), cg.dump());
      System.out.println("TAC generado en reports/intermediate.txt");
    }
  }
}
