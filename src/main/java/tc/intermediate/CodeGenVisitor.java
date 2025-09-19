package tc.intermediate;

import tc.grammar.CminiBaseVisitor;
import tc.grammar.CminiParser;

public class CodeGenVisitor extends CminiBaseVisitor<String> {

    private final CodeGenerator gen;

    public CodeGenVisitor(CodeGenerator gen) {
        this.gen = gen;
    }

    // program: visit hijos
    @Override
    public String visitProgram(CminiParser.ProgramContext ctx) {
        return super.visitProgram(ctx);
    }

    @Override
    public String visitFunctionDecl(CminiParser.FunctionDeclContext ctx) {
        String fname = ctx.ID().getText();
        gen.emit(fname + ":");

        if (ctx.paramList() != null) {
            for (CminiParser.ParamContext p : ctx.paramList().param()) {
                String pname = p.ID().getText();
                gen.emit("param " + pname);
            }
        }

        visit(ctx.block());
        return null;
    }

    @Override
    public String visitBlock(CminiParser.BlockContext ctx) {
        return super.visitBlock(ctx);
    }

    @Override
    public String visitVarDecl(CminiParser.VarDeclContext ctx) {
        if (ctx.expr() != null) {
            String rhs = visit(ctx.expr());
            String id = ctx.ID().getText();
            gen.emit(id + " = " + rhs);
        }
        return null;
    }

    @Override
    public String visitAssignStat(CminiParser.AssignStatContext ctx) {
        String id = ctx.ID().getText();
        String rhs = visit(ctx.expr());
        gen.emit(id + " = " + rhs);
        return null;
    }

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

    @Override
    public String visitExpr(CminiParser.ExprContext ctx) {
        if (ctx.INT() != null)  return ctx.INT().getText();
        if (ctx.FLOAT() != null) return ctx.FLOAT().getText();

        if (ctx.ID() != null && ctx.op == null && ctx.funcCall() == null) {
            return ctx.ID().getText();
        }

        if (ctx.getChildCount() == 3 && "(".equals(ctx.getChild(0).getText())) {
            return visit(ctx.expr(0));
        }

        if (ctx.op != null && ctx.expr().size() == 2) {
            String a = visit(ctx.expr(0));
            String b = visit(ctx.expr(1));
            String op = ctx.op.getText();
            String res = gen.newTemp().getName();
            gen.emit(res + " = " + a + " " + op + " " + b);
            return res;
        }

        if (ctx.funcCall() != null) {
            String fname = ctx.funcCall().ID().getText();

            if (ctx.funcCall().argList() != null) {
                for (CminiParser.ExprContext arg : ctx.funcCall().argList().expr()) {
                    String val = visit(arg);
                    gen.emit("param " + val);
                }
            }

            Temp res = gen.newTemp();
            gen.emit(res.getName() + " = call " + fname);
            return res.getName();
        }

        return null;
    }

    @Override
    public String visitIfStat(CminiParser.IfStatContext ctx) {
        String cond = visit(ctx.expr());
        Label Ltrue = gen.newLabel();
        Label Lend = gen.newLabel();

        gen.emit("if " + cond + " goto " + Ltrue.getName());
        gen.emit("goto " + Lend.getName());

        gen.emit(Ltrue.toString());
        visit(ctx.statement(0));

        if (ctx.statement().size() > 1) {
            Label Lelse = gen.newLabel();
            Label Lafter = gen.newLabel();
            gen.emit("goto " + Lafter.getName());

            gen.emit(Lelse.toString());
            visit(ctx.statement(1));

            gen.emit(Lafter.toString());
        } else {
            gen.emit(Lend.toString());
        }
        return null;
    }

    @Override
    public String visitWhileStat(CminiParser.WhileStatContext ctx) {
        Label Lbegin = gen.newLabel();
        Label Lend = gen.newLabel();

        gen.emit(Lbegin.toString());
        String cond = visit(ctx.expr());
        gen.emit("if " + cond + " goto " + Lend.getName());

        visit(ctx.statement());

        gen.emit("goto " + Lbegin.getName());
        gen.emit(Lend.toString());

        return null;
    }

    @Override
    public String visitForStat(CminiParser.ForStatContext ctx) {

        if (ctx.varDecl() != null) visit(ctx.varDecl());
        if (ctx.assignStat(0) != null) visit(ctx.assignStat(0));

        Label Lbegin = gen.newLabel();
        Label Lend = gen.newLabel();

        gen.emit(Lbegin.toString());

        if (ctx.expr() != null) {
            String cond = visit(ctx.expr());
            gen.emit("if " + cond + " goto " + Lend.getName());
        }

        visit(ctx.statement());

        if (ctx.assignStat(1) != null) visit(ctx.assignStat(1));

        gen.emit("goto " + Lbegin.getName());
        gen.emit(Lend.toString());

        return null;
    }
}
