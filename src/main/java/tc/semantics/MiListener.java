package tc.semantics;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import tc.grammar.CminiBaseListener;
import tc.grammar.CminiParser;
import tc.symbols.*;

public class MiListener extends CminiBaseListener {

    private final TablaDeSimbolos ts;
    private final ErrorReporter reporter;

    public MiListener(TablaDeSimbolos ts, ErrorReporter reporter) {
        this.ts = ts;
        this.reporter = reporter;
    }

    // --------- Helpers ---------
    private void err(Token t, String m) {
        String at = "L" + t.getLine() + ":" + t.getCharPositionInLine();
        reporter.addSemantic(at + " - " + m);
    }

    private Tipo tipoFrom(CminiParser.TypeContext tctx) {
        return Tipo.fromString(tctx.getText());
    }

    private Tipo inferirLiteral(CminiParser.ExprContext e) {
        if (e.INT() != null) return Tipo.INT;
        if (e.FLOAT() != null) return Tipo.FLOAT;
        return Tipo.DESCONOCIDO;
    }

    // --------- Contextos ---------

    @Override
    public void enterFunctionDecl(CminiParser.FunctionDeclContext ctx) {
        // Nuevo contexto de función
        String nombre = ctx.ID().getText();
        ts.push("fn:" + nombre);

        // Parámetros como variables inicializadas
        if (ctx.paramList() != null) {
            for (var p : ctx.paramList().param()) {
                String pid = p.ID().getText();
                Tipo pt = tipoFrom(p.type());
                Variable v = new Variable(pid, pt);
                v.marcarInicializada(); // convención
                if (!ts.actual().declarar(v)) {
                    err(p.ID().getSymbol(), "Parámetro duplicado: '" + pid + "'");
                }
            }
        }
    }

    @Override
    public void exitFunctionDecl(CminiParser.FunctionDeclContext ctx) {
        // Al salir, marcamos no usados
        for (var e : ts.actual().ids().values()) {
            if (e instanceof Variable v && !v.usada()) {
                err(ctx.ID().getSymbol(), "Identificador declarado pero no usado: '" + v.nombre() + "'");
            }
        }
        ts.pop();
    }

    @Override
    public void enterBlock(CminiParser.BlockContext ctx) {
        ts.push("block");
    }

    @Override
    public void exitBlock(CminiParser.BlockContext ctx) {
        for (var e : ts.actual().ids().values()) {
            if (e instanceof Variable v && !v.usada()) {
                err(ctx.start, "Identificador declarado pero no usado: '" + v.nombre() + "'");
            }
        }
        ts.pop();
    }

    // --------- Declaración de variable ---------

    @Override
    public void enterVarDecl(CminiParser.VarDeclContext ctx) {
        String id = ctx.ID().getText();
        Tipo t = tipoFrom(ctx.type());
        Variable v = new Variable(id, t);

        // Doble declaración en el mismo contexto
        if (!ts.actual().declarar(v)) {
            err(ctx.ID().getSymbol(), "Doble declaración del mismo identificador: '" + id + "'");
            return;
        }

        // Inicialización opcional
        if (ctx.expr() != null) {
            Tipo rhs = inferirExpr(ctx.expr()); // inferencia simple (abajo)
            if (!Tipo.asignable(t, rhs)) {
                err(ctx.ID().getSymbol(), "Tipos de datos incompatibles en inicialización de '" + id +
                        "': " + t + " = " + rhs);
            }
            v.marcarInicializada();
            v.setValor(ctx.expr().getText());
        }
    }

    // --------- Asignación ---------

    @Override
    public void enterAssignStat(CminiParser.AssignStatContext ctx) {
        Token idTok = ctx.ID().getSymbol();
        String id = ctx.ID().getText();

        Id sim = ts.actual().resolver(id);
        if (sim == null) {
            err(idTok, "Uso de un identificador no declarado: '" + id + "'");
            return;
        }
        if (!(sim instanceof Variable v)) return;

        Tipo rhs = inferirExpr(ctx.expr());
        if (!Tipo.asignable(v.tipo(), rhs)) {
            err(idTok, "Tipos de datos incompatibles en asignación a '" + id + "': " +
                    v.tipo() + " = " + rhs);
        }
        v.marcarInicializada();
        v.setValor(ctx.expr().getText());
    }

    // --------- Uso de identificadores en expresiones ---------

    @Override
    public void enterExpr(CminiParser.ExprContext ctx) {
        // Si la expresión es un ID solito (no es llamada a función), marcar uso y chequear inicialización
        if (ctx.ID() != null && ctx.op == null && ctx.funcCall() == null) {
            Token tok = ctx.ID().getSymbol();
            String id = ctx.ID().getText();
            Id sim = ts.actual().resolver(id);
            if (sim == null) {
                err(tok, "Uso de un identificador no declarado: '" + id + "'");
                return;
            }
            if (sim instanceof Variable v) {
                v.marcarUsada();
                if (!v.inicializada()) {
                    err(tok, "Uso de un identificador sin inicializar: '" + id + "'");
                }
            }
        }
    }

    // --------- Inferencia mínima de tipo para expr ---------

    private Tipo inferirExpr(CminiParser.ExprContext e) {
        // Literales
        if (e.INT() != null) return Tipo.INT;
        if (e.FLOAT() != null) return Tipo.FLOAT;

        // ID
        if (e.ID() != null && e.op == null && e.funcCall() == null) {
            Id sim = ts.actual().resolver(e.ID().getText());
            return sim != null ? sim.tipo() : Tipo.DESCONOCIDO;
        }

        // Llamada a función (para TP2 no exigimos verificación de firma; devolvemos UNKNOWN)
        if (e.funcCall() != null) return Tipo.DESCONOCIDO;

        // Operaciones -> si ambos numéricos, promovemos; sino desconocido
        if (e.op != null && e.expr().size() == 2) {
            Tipo a = inferirExpr(e.expr(0));
            Tipo b = inferirExpr(e.expr(1));
            if (a.esNumerico() && b.esNumerico()) {
                // int (+,-,*,/) float -> float
                return (a == Tipo.FLOAT || b == Tipo.FLOAT) ? Tipo.FLOAT : Tipo.INT;
            }
            return Tipo.DESCONOCIDO;
        }

        // Paréntesis
        if (e.getChildCount() == 3 && "(".equals(e.getChild(0).getText())) {
            return inferirExpr(e.expr(0));
        }

        return Tipo.DESCONOCIDO;
    }

    // --------- Utilidad pública ---------

    public static void walk(CminiParser.ProgramContext tree, TablaDeSimbolos ts, ErrorReporter rep) {
        ParseTreeWalker.DEFAULT.walk(new MiListener(ts, rep), tree);
    }
}
