package tc;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import tc.grammar.CminiLexer;
import tc.grammar.CminiParser;

import java.nio.file.*;

public class App {
    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "samples/ok_hello.c";
        String code = Files.readString(Path.of(inputFile));

        // Crear lexer y parser
        CharStream cs = CharStreams.fromString(code);
        CminiLexer lexer = new CminiLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CminiParser parser = new CminiParser(tokens);

        // Parsear y mostrar árbol
        ParseTree tree = parser.program();
        System.out.println(tree.toStringTree(parser));
    }
}