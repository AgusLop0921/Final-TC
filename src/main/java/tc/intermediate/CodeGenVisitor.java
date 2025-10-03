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
    gen.emit("PROGRAMA_INICIO:");
    gen.emit("// Declaración de variables globales");

    // 1) Declaraciones globales: program -> statement -> simpleStatement -> varDecl
    for (CminiParser.StatementContext st : ctx.statement()) {
      CminiParser.SimpleStatementContext ss = st.simpleStatement();
      if (ss != null && ss.varDecl() != null) {
        CminiParser.VarDeclContext g = ss.varDecl();

        String id = g.ID().getText();
        String tipo = g.type().getText();

        if (g.INT() != null) {
          // Array global: type ID '[' INT ']'
          gen.emit("DECLARE " + id + "[" + g.INT().getText() + "] " + tipo);
        } else {
          // Variable global simple
          gen.emit("DECLARE " + id + " " + tipo);
        }
        // IMPORTANTE: NO emitir inicializaciones aquí (solo DECLARE)
      }
    }

    // 2) Funciones (en el orden en que aparecen)
    for (var child : ctx.children) {
      if (child instanceof CminiParser.FunctionDeclContext f) {
        visit(f);
      }
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
      if (tipo.equals("bool")) {
        gen.emit(id + " = false");
      }
      // ⚡ inicialización explícita: int x = 5;  o int z = suma(x,y);
      if (ctx.expr() != null) {
        String rhs = visit(ctx.expr());
        gen.emit(id + " = " + rhs);
      }
    }
    return null;
  }

  //  Asignación
  @Override
  public String visitAssignStat(CminiParser.AssignStatContext ctx) {
    String id = ctx.ID().getText();

    // Caso array[i] = expr
    if (ctx.expr().size() == 2) {
      String index = visit(ctx.expr(0));
      String rhs = visit(ctx.expr(1));
      gen.emit(id + "[" + index + "] = " + rhs);
    }
    // Caso variable normal
    else {
      // Si es llamada a función -> CALL directo + asignación
      if (ctx.expr(0).funcCall() != null) {
        String fname = ctx.expr(0).funcCall().ID().getText();
        StringBuilder args = new StringBuilder();
        if (ctx.expr(0).funcCall().argList() != null) {
          for (CminiParser.ExprContext arg : ctx.expr(0).funcCall().argList().expr()) {
            String val = visit(arg);
            if (args.length() > 0) args.append(", ");
            args.append(val);
          }
        }
        gen.emit("CALL func_" + fname + (args.length() > 0 ? ", " + args : ""));
        gen.emit(id + " = RETURN_VALUE");
      } else {
        String rhs = visit(ctx.expr(0));
        gen.emit(id + " = " + rhs);
      }
    }

    return null;
  }

  // Return
  @Override
  public String visitReturnStat(CminiParser.ReturnStatContext ctx) {
    if (ctx.expr() != null) {
      String v = visit(ctx.expr());
      gen.emit("RETURN " + v);
    } else {
      gen.emit("RETURN");
    }
    return null;
  }

  //  Expresiones
  @Override
  public String visitExpr(CminiParser.ExprContext ctx) {
    // 1. Literales
    if (ctx.literal() != null) return ctx.getText();

    // 2. Casting
    if (ctx.type() != null && ctx.expr().size() == 1) {
      String valor = visit(ctx.expr(0));
      String tipo = ctx.type().getText();
      Temp t = gen.newTemp();
      gen.emit(t.getName() + " = CAST(" + valor + ", " + tipo + ")");
      return t.getName();
    }

    // 3. Operación binaria
    if (ctx.expr().size() == 2) {
      String a = visit(ctx.expr(0));
      String b = visit(ctx.expr(1));
      String op = ctx.getChild(1).getText();
      String res = gen.newTemp().getName();
      gen.emit(res + " = " + a + " " + op + " " + b);
      return res;
    }

    // 4. Acceso a array aislado
    if (ctx.ID() != null && ctx.getChildCount() == 4) {
      String index = visit(ctx.expr(0));
      // ⚡ En vez de crear un temp, devolvemos la referencia directa
      return ctx.ID().getText() + "[" + index + "]";
    }

    // 5. Identificador solo
    if (ctx.ID() != null && ctx.getChildCount() == 1) {
      return ctx.ID().getText();
    }

    // 6. Paréntesis (expr)
    if (ctx.getChildCount() == 3 && "(".equals(ctx.getChild(0).getText())) {
      return visit(ctx.expr(0));
    }

    // 7. Post-incremento / Post-decremento
    if (ctx.ID() != null && ctx.getChildCount() == 2) {
      String id = ctx.ID().getText();
      String op = ctx.getChild(1).getText();
      Temp t = gen.newTemp();
      gen.emit(t.getName() + " = " + id);
      gen.emit(id + " = " + id + " " + (op.equals("++") ? "+" : "-") + " 1");
      return t.getName();
    }

    // 8. Llamada a función en expresiones (solo si no es asignación directa)
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
      Temp t = gen.newTemp();
      gen.emit(t.getName() + " = RETURN_VALUE");
      return t.getName();
    }

    return null;
  }

  //  If
  @Override
  public String visitIfStat(CminiParser.IfStatContext ctx) {
    String cond = visit(ctx.expr());
    // mismo número para THEN y END_IF
    Label Lthen = gen.newLabel("THEN");
    String num = Lthen.getName().split("_")[1];
    Label Lend = new Label("END_IF_" + num);

    gen.emit("if " + cond + " goto " + Lthen.getName());
    gen.emit("goto " + Lend.getName());

    gen.emit(Lthen.getName() + ":");
    visit(ctx.statement(0));
    gen.emit(Lend.getName() + ":");
    return null;
  }

  //  While
  @Override
  public String visitWhileStat(CminiParser.WhileStatContext ctx) {
    Label Lbegin = gen.newLabel("WHILE");
    Label Lend = gen.newLabel("END_WHILE");

    // inicio del bucle
    gen.emit(Lbegin.toString() + ":");

    // condición
    String cond = visit(ctx.expr());
    gen.emit("ifFalse " + cond + " goto " + Lend.getName());

    // cuerpo
    visit(ctx.statement());

    // volver al inicio
    gen.emit("goto " + Lbegin.getName());

    // fin
    gen.emit(Lend.toString() + ":");
    return null;
  }

  //  For
  @Override
  public String visitForStat(CminiParser.ForStatContext ctx) {
    // inicialización
    if (ctx.varDecl() != null) {
      visit(ctx.varDecl());
    } else if (!ctx.assignStat().isEmpty()) {
      visit(ctx.assignStat(0));
    }

    Label Lbegin = gen.newLabel("FOR");
    Label Lend = gen.newLabel("END_FOR");

    gen.emit(Lbegin.toString() + ":");

    // condición
    if (!ctx.expr().isEmpty()) {
      String cond = visit(ctx.expr(0));
      gen.emit("ifFalse " + cond + " goto " + Lend.getName());
    }

    // cuerpo
    visit(ctx.statement());

    // actualización (la última parte del for)
    if (ctx.assignStat().size() > 1) {
      visit(ctx.assignStat(1));
    } else if (ctx.expr().size() > 1) {
      visit(ctx.expr(1));
    }

    gen.emit("goto " + Lbegin.getName());
    gen.emit(Lend.toString() + ":");
    return null;
  }
}
