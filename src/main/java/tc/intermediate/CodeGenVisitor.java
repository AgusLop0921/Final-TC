package tc.intermediate;

import tc.grammar.CminiBaseVisitor;
import tc.grammar.CminiParser;

public class CodeGenVisitor extends CminiBaseVisitor<String> {

    private final CodeGenerator gen;

    public CodeGenVisitor(CodeGenerator gen) {
        this.gen = gen;
    }

    // Programa
    @Override
    public String visitProgram(CminiParser.ProgramContext ctx) {
        gen.emit("Código de tres direcciones generado");
        gen.emit("PROGRAMA_INICIO:");

        for (var child : ctx.children) {
            visit(child);
        }

        gen.emit("PROGRAMA_FIN:");
        return null;
    }

    // Funciones 
    @Override
    public String visitFunctionDecl(CminiParser.FunctionDeclContext ctx) {
        String fname = ctx.ID().getText();
        gen.emit("func_" + fname + ":");

        // parámetros
        if (ctx.paramList() != null) {
            for (CminiParser.ParamContext p : ctx.paramList().param()) {
                String pname = p.ID().getText();
                gen.emit("PARAM " + pname + " " + p.type().getText());
            }
        }

        // si es main, inicializar activo
        if (fname.equals("main")) {
            gen.emit("activo = false");
        }

        visit(ctx.block());
        return null;
    }

    //  Declaración 
    @Override
    public String visitVarDecl(CminiParser.VarDeclContext ctx) {
        String id = ctx.ID().getText();
        String tipo = ctx.type().getText();

        if (ctx.INT() != null) {
            gen.emit("DECLARE " + id + "[" + ctx.INT().getText() + "] " + tipo);
        } else {
            gen.emit("DECLARE " + id + " " + tipo);
            // inicialización de bool a false
            if (tipo.equals("bool")) {
                gen.emit(id + " = false");
            }
        }
        return null;
    }

    //  Asignación 
    @Override
    public String visitAssignStat(CminiParser.AssignStatContext ctx) {
        String id = ctx.ID().getText();

        if (ctx.expr().size() == 2) { // array
            String index = visit(ctx.expr(0));
            String rhs = visit(ctx.expr(1));
            gen.emit(id + "[" + index + "] = " + rhs);
        } else { // variable normal
            String rhs = visit(ctx.expr(0));
            gen.emit(id + " = " + rhs);
        }
        return null;
    }

    //  Return 
    @Override
    public String visitReturnStat(CminiParser.ReturnStatContext ctx) {
        if (ctx.expr() != null) {
            String v = visit(ctx.expr());
            gen.emit("return " + v);
        } else {
            gen.emit("return");
        }
        return null;
    }

    //  Expresiones 
    @Override
    public String visitExpr(CminiParser.ExprContext ctx) {
        // literales
        if (ctx.literal() != null) return ctx.getText();

        // array directo en operación binaria
        if (ctx.expr().size() == 2) {
            String a = visit(ctx.expr(0));
            String b = visit(ctx.expr(1));
            String op = ctx.getChild(1).getText();
            String res = gen.newTemp().getName();
            gen.emit(res + " = " + a + " " + op + " " + b);
            return res;
        }

        // acceso a array aislado
        if (ctx.ID() != null && ctx.getChildCount() == 4) {
            String index = visit(ctx.expr(0));
            Temp t = gen.newTemp();
            gen.emit(t.getName() + " = " + ctx.ID().getText() + "[" + index + "]");
            return t.getName();
        }

        // identificador solo
        if (ctx.ID() != null && ctx.getChildCount() == 1) {
            return ctx.ID().getText();
        }

        // paréntesis
        if (ctx.getChildCount() == 3 && "(".equals(ctx.getChild(0).getText())) {
            return visit(ctx.expr(0));
        }

        // llamada a función
        if (ctx.funcCall() != null) {
            String fname = ctx.funcCall().ID().getText();
            StringBuilder args = new StringBuilder();
            if (ctx.funcCall().argList() != null) {
                for (CminiParser.ExprContext arg : ctx.funcCall().argList().expr()) {
                    String val = visit(arg);
                    if (args.length() > 0) args.append(", ");
                    args.append(val);
                }
            }
            gen.emit("CALL func_" + fname + (args.length() > 0 ? ", " + args : ""));
            return "RETURN_VALUE";
        }

        return null;
    }
    //  If 
    @Override
    public String visitIfStat(CminiParser.IfStatContext ctx) {
        String cond = visit(ctx.expr());
        Label Lthen = gen.newLabel("THEN");
        Label Lend = gen.newLabel("END_IF");

        gen.emit("if " + cond + " goto " + Lthen.getName());
        gen.emit("goto " + Lend.getName());

        gen.emit(Lthen.toString() + ":");
        visit(ctx.statement(0));

        gen.emit(Lend.toString() + ":");
        return null;
    }

    //  While 
    @Override
    public String visitWhileStat(CminiParser.WhileStatContext ctx) {
        Label Lbegin = gen.newLabel("WHILE");
        Label Lend = gen.newLabel("END_WHILE");

        gen.emit(Lbegin.toString() + ":");
        String cond = visit(ctx.expr());
        gen.emit(gen.nextInstr() + ": if " + cond + " goto " + Lend.getName());

        visit(ctx.statement());

        gen.emit(gen.nextInstr() + ": goto " + Lbegin.getName());
        gen.emit(Lend.toString() + ":");
        return null;
    }

    //  For 
    @Override
    public String visitForStat(CminiParser.ForStatContext ctx) {
        if (ctx.varDecl() != null) visit(ctx.varDecl());
        if (ctx.assignStat(0) != null) visit(ctx.assignStat(0));

        Label Lbegin = gen.newLabel("FOR");
        Label Lend = gen.newLabel("END_FOR");

        gen.emit(Lbegin.toString() + ":");
        if (ctx.expr() != null) {
            String cond = visit(ctx.expr());
            gen.emit(gen.nextInstr() + ": if " + cond + " goto " + Lend.getName());
        }

        visit(ctx.statement());
        if (ctx.assignStat(1) != null) visit(ctx.assignStat(1));

        gen.emit(gen.nextInstr() + ": goto " + Lbegin.getName());
        gen.emit(Lend.toString() + ":");
        return null;
    }
}