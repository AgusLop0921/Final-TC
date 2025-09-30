package tc.semantics;

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

    private void err(Token t, String m) {
        String at = "L" + t.getLine() + ":" + t.getCharPositionInLine();
        reporter.addSemantic(at + " - " + m);
    }

    private Tipo tipoFrom(CminiParser.TypeContext tctx) {
        return Tipo.fromString(tctx.getText());
    }

    //  Contextos 
    @Override
    public void enterFunctionDecl(CminiParser.FunctionDeclContext ctx) {
        String nombre = ctx.ID().getText();
        Tipo ret = tipoFrom(ctx.type());

        // Registrar la función como símbolo
        Funcion f = new Funcion(nombre, ret);
        if (!ts.global().declarar(f)) {
            err(ctx.ID().getSymbol(), "Función duplicada: '" + nombre + "'");
        }

        // Nuevo contexto para la función
        ts.push("fn:" + nombre);

        // Parámetros
        if (ctx.paramList() != null) {
            for (var p : ctx.paramList().param()) {
                String pid = p.ID().getText();
                Tipo pt = tipoFrom(p.type());
                Variable v = new Variable(pid, pt);
                v.marcarInicializada();
                if (!ts.actual().declarar(v)) {
                    err(p.ID().getSymbol(), "Parámetro duplicado: '" + pid + "'");
                }
                f.addParametro(v);
            }
        }
    }

    @Override
    public void exitFunctionDecl(CminiParser.FunctionDeclContext ctx) {
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

    //  Declaración de variable 
    @Override
    public void enterVarDecl(CminiParser.VarDeclContext ctx) {
        String id = ctx.ID().getText();
        Tipo t = tipoFrom(ctx.type());

        int dimension = 0;
        if (ctx.INT() != null) {
            dimension = Integer.parseInt(ctx.INT().getText());
        }

        Variable v = new Variable(id, t, dimension);

        if (!ts.actual().declarar(v)) {
            err(ctx.ID().getSymbol(), "Doble declaración del mismo identificador: '" + id + "'");
            return;
        }

        // Si es array, lo marcamos como inicializado al declararlo
        if (v.esArray()) {
            v.marcarInicializada();
        }
    }

    //  Asignación 
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

        if (ctx.expr().size() == 2) {
            Tipo indexT = inferirExpr(ctx.expr(0));
            if (indexT != Tipo.INT) {
                err(idTok, "Índice de array debe ser int en '" + id + "'");
            }
            Tipo rhs = inferirExpr(ctx.expr(1));
            if (!Tipo.asignable(v.tipo(), rhs)) {
                err(idTok, "Tipos incompatibles en asignación a array '" + id + "'");
            }
        } else { // var = expr
            Tipo rhs = inferirExpr(ctx.expr(0));
            if (!Tipo.asignable(v.tipo(), rhs)) {
                err(idTok, "Tipos incompatibles en asignación a '" + id + "'");
            }
            v.marcarInicializada();
            v.setValor(ctx.expr(0).getText());
        }
    }

    //  Uso de expresiones 
    @Override
    public void enterExpr(CminiParser.ExprContext ctx) {
        // Caso variable normal
        if (ctx.ID() != null && ctx.getChildCount() == 1) {
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

        // Caso acceso array: ID '[' expr ']'
        if (ctx.ID() != null && ctx.getChildCount() == 4) {
            String id = ctx.ID().getText();
            Id sim = ts.actual().resolver(id);
            if (sim instanceof Variable v && v.esArray()) {
                v.marcarUsada();
                v.marcarInicializada(); // consideramos el array declarado como "usable"
            }
        }
    }

    //  Inferencia de tipos 
    private Tipo inferirExpr(CminiParser.ExprContext e) {
        if (e == null) return Tipo.DESCONOCIDO;

        // Literales
        if (e.literal() != null) {
            if (e.literal().INT() != null) return Tipo.INT;
            if (e.literal().FLOAT() != null) return Tipo.FLOAT;
            if (e.literal().CHAR_LITERAL() != null) return Tipo.CHAR;
            if (e.literal().TRUE() != null || e.literal().FALSE() != null) return Tipo.BOOL;
        }

        // Identificador simple
        if (e.ID() != null && e.getChildCount() == 1) {
            Id sim = ts.actual().resolver(e.ID().getText());
            return sim != null ? sim.tipo() : Tipo.DESCONOCIDO;
        }

        // Acceso a array
        if (e.ID() != null && e.getChildCount() == 4) {
            Id sim = ts.actual().resolver(e.ID().getText());
            if (sim instanceof Variable v && v.esArray()) {
                return v.tipo(); // el tipo base
            }
        }

        // Llamada a función
        if (e.funcCall() != null) {
            Id sim = ts.actual().resolver(e.funcCall().ID().getText());
            if (sim instanceof Funcion f) {
                return f.retorno();
            }
            return Tipo.DESCONOCIDO;
        }

        // Operación binaria
        if (e.expr().size() == 2) {
            Tipo a = inferirExpr(e.expr(0));
            Tipo b = inferirExpr(e.expr(1));
            if (a.esNumerico() && b.esNumerico()) {
                return (a == Tipo.DOUBLE || b == Tipo.DOUBLE) ? Tipo.DOUBLE : Tipo.INT;
            }
            if (a == Tipo.BOOL && b == Tipo.BOOL) {
                return Tipo.BOOL;
            }
            return Tipo.DESCONOCIDO;
        }

        // Paréntesis
        if (e.getChildCount() == 3 && "(".equals(e.getChild(0).getText())) {
            return inferirExpr(e.expr(0));
        }

        return Tipo.DESCONOCIDO;
    }

    //  Utilidad pública 
    public static void walk(CminiParser.ProgramContext tree, TablaDeSimbolos ts, ErrorReporter rep) {
        ParseTreeWalker.DEFAULT.walk(new MiListener(ts, rep), tree);
    }
}