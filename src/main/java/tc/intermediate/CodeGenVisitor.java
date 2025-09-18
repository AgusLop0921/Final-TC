package tc.intermediate;

import tc.grammar.CminiBaseVisitor;
import tc.grammar.CminiParser;

/**
 * Genera código de tres direcciones (TAC) para:
 * - Expresiones aritméticas y comparativas
 * - Asignaciones
 * - VarDecl con inicialización
 *
 * Retorna el "nombre" del valor generado (temp, id o literal).
 */
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

    // functionDecl: por ahora solo marcamos la etiqueta (etapa 3 haremos params/return)
    @Override
    public String visitFunctionDecl(CminiParser.FunctionDeclContext ctx) {
        String fname = ctx.ID().getText();
        gen.emit(fname + ":");
        // Bloque de la función
        visit(ctx.block());
        // Nota: return se maneja en visitReturnStat (más adelante lo refinamos)
        return null;
    }

    // block: solo recorrer
    @Override
    public String visitBlock(CminiParser.BlockContext ctx) {
        return super.visitBlock(ctx);
    }

    // varDecl: con inicialización emitimos asignación
    @Override
    public String visitVarDecl(CminiParser.VarDeclContext ctx) {
        if (ctx.expr() != null) {
            String rhs = visit(ctx.expr());
            String id = ctx.ID().getText();
            gen.emit(id + " = " + rhs);
        }
        return null;
    }

    // assignStat: ID '=' expr
    @Override
    public String visitAssignStat(CminiParser.AssignStatContext ctx) {
        String id = ctx.ID().getText();
        String rhs = visit(ctx.expr());
        gen.emit(id + " = " + rhs);
        return null;
    }

    // returnStat: (lo refinaremos en la Parte 3)
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

    // expr:
    @Override
    public String visitExpr(CminiParser.ExprContext ctx) {
        // Literales
        if (ctx.INT() != null)  return ctx.INT().getText();
        if (ctx.FLOAT() != null) return ctx.FLOAT().getText();

        // Identificador solo (no operador ni llamada)
        if (ctx.ID() != null && ctx.op == null && ctx.funcCall() == null) {
            return ctx.ID().getText();
        }

        // Paréntesis
        if (ctx.getChildCount() == 3 && "(".equals(ctx.getChild(0).getText())) {
            return visit(ctx.expr(0));
        }

        // Operación binaria ( + - * / y comparaciones )
        if (ctx.op != null && ctx.expr().size() == 2) {
            String a = visit(ctx.expr(0));
            String b = visit(ctx.expr(1));
            String op = ctx.op.getText();
            String res = gen.newTemp().getName();
            gen.emit(res + " = " + a + " " + op + " " + b);
            return res;
        }

        // Llamada a función (se implementará en Parte 3). Por ahora, devolvemos un temp con comentario.
        if (ctx.funcCall() != null) {
            String res = gen.newTemp().getName();
            String fname = ctx.funcCall().ID().getText();
            gen.emit("// TODO call " + fname + " (...)");
            gen.emit(res + " = " + fname); // placeholder
            return res;
        }

        return null;
    }
}
