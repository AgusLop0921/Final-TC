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

  // Funciones
  @Override
  public void enterFunctionDecl(CminiParser.FunctionDeclContext ctx) {
    String nombre = ctx.ID().getText();
    Tipo ret = tipoFrom(ctx.type());
    int linea = ctx.ID().getSymbol().getLine();
    int columna = ctx.ID().getSymbol().getCharPositionInLine() + 1;

    // Registrar la funcion como simbolo
    Funcion f = new Funcion(nombre, ret, linea, columna);
    if (!ts.global().declarar(f)) {
      err(ctx.ID().getSymbol(), "funcion duplicada: '" + nombre + "'");
    }

    // Nuevo contexto para la funcion
    ts.push("fn:" + nombre);

    // Parámetros
    if (ctx.paramList() != null) {
      for (var p : ctx.paramList().param()) {
        String pid = p.ID().getText();
        Tipo pt = tipoFrom(p.type());
        int l = p.ID().getSymbol().getLine();
        int c = p.ID().getSymbol().getCharPositionInLine() + 1;
        Variable v = new Variable(pid, pt, l, c);
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
        reporter.addWarning(
            "Variable '"
                + v.nombre()
                + "' declarada pero nunca utilizada en el ámbito '"
                + ctx.ID().getText()
                + "'");
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
        reporter.addWarning(
            "Variable '" + v.nombre() + "' declarada pero nunca utilizada en un bloque");
      }
    }
    ts.pop();
  }

  //  Declaración de variable
  @Override
  public void enterVarDecl(CminiParser.VarDeclContext ctx) {
    String id = ctx.ID().getText();
    Tipo t = tipoFrom(ctx.type());
    int linea = ctx.ID().getSymbol().getLine();
    int columna = ctx.ID().getSymbol().getCharPositionInLine() + 1;

    int dimension = 0;
    if (ctx.INT() != null) {
      dimension = Integer.parseInt(ctx.INT().getText());
    }

    Variable v = new Variable(id, t, dimension, linea, columna);

    if (ctx.expr() != null) {
        Tipo rhs = inferirExpr(ctx.expr());
        if (!Tipo.asignable(v.tipo(), rhs)) {
            err(ctx.ID().getSymbol(),
                "Tipos incompatibles en inicialización de '" + id + "': " 
                + v.tipo() + " <- " + rhs);
        } else {
            v.marcarInicializada();
        }
    }

    if (!ts.actual().declarar(v)) {
      err(ctx.ID().getSymbol(), "Doble declaración del mismo identificador: '" + id + "'");
    } else {
      if (ctx.expr() != null || v.esArray()) {
        v.marcarInicializada();
      }
    }
  }

  //  Asignacion
  @Override
  public void enterAssignStat(CminiParser.AssignStatContext ctx) {
    Token idTok = ctx.ID().getSymbol();
    String id = ctx.ID().getText();
    Id sim = ts.actual().resolver(id);

    if (sim == null) {
      err(idTok, "Uso de un identificador no declarado: '" + id + "'");
    } else if (sim instanceof Funcion) {
      err(idTok, "No se puede asignar valor a '" + id + "' porque no es una variable");
    } else if (sim instanceof Variable v) {
      if (ctx.expr().size() == 2) { // array[index] = expr
        Tipo indexT = inferirExpr(ctx.expr(0));
        if (indexT != Tipo.INT) {
          err(idTok, "Índice de array debe ser int en '" + id + "'");
        }
        Tipo rhs = inferirExpr(ctx.expr(1));
        if (!Tipo.asignable(v.tipo(), rhs)) {
          err(idTok, "Tipos incompatibles en Asignacion a array '" + id + "'");
        } else {
          v.marcarInicializada();
        }
      } else { // var = expr
        Tipo rhs = inferirExpr(ctx.expr(0));

        if (!Tipo.asignable(v.tipo(), rhs)) {
            err(idTok, "Tipos incompatibles en asignacion a '" + id + "': " 
                + v.tipo() + " <- " + rhs);
        } else {
            v.marcarInicializada();
            v.setValor(ctx.expr(0).getText());
        }
      }
    }
  }

  //  Uso de expresiones
  @Override
  public void enterExpr(CminiParser.ExprContext ctx) {
    // ID simple
    if (ctx.ID() != null && ctx.getChildCount() == 1) {
      Token tok = ctx.ID().getSymbol();
      String id = ctx.ID().getText();
      Id sim = ts.actual().resolver(id);
      if (sim == null) {
        err(tok, "Uso de un identificador no declarado: '" + id + "'");
      } else if (sim instanceof Variable v) {
        v.marcarUsada();
        if (!v.inicializada()) {
          err(tok, "Uso de un identificador sin inicializar: '" + id + "'");
        }
      }
    }

    // Acceso: ID '[' expr ']'
    if (ctx.ID() != null && ctx.getChildCount() == 4) {
      Token tok = ctx.ID().getSymbol();
      String id = ctx.ID().getText();
      Id sim = ts.actual().resolver(id);
      if (sim == null) {
        err(tok, "Uso de un identificador no declarado: '" + id + "'");
      } else if (sim instanceof Variable v) {
        if (!v.esArray()) {
          err(tok, "El identificador '" + id + "' no es un array");
        } else {
          v.marcarUsada();
          v.marcarInicializada();
          // validar índice INT
          Tipo idxT = inferirExpr(ctx.expr(0));
          if (idxT != Tipo.INT) {
            err(tok, "Índice de array debe ser int en '" + id + "'");
          }
        }
      } else if (sim instanceof Funcion) {
        err(tok, "El identificador '" + id + "' es una funcion, no un array");
      }
    }

    // Llamada a funcion: funcCall()
    if (ctx.funcCall() != null) {
      Token tok = ctx.funcCall().ID().getSymbol();
      String fname = ctx.funcCall().ID().getText();
      Id sim = ts.actual().resolver(fname);
      if (sim == null) {
        err(tok, "Llamada a funcion no declarada: '" + fname + "'");
      } else if (!(sim instanceof Funcion)) {
        err(tok, "'" + fname + "' no es una funcion");
      }
    }
  }

  //  Inferencia de tipos
  private Tipo inferirExpr(CminiParser.ExprContext e) {
    if (e == null) return Tipo.DESCONOCIDO;

    // Casting explícito: (tipo) expr
    if (e.type() != null && e.expr().size() == 1) {
      return tipoFrom(e.type()); // ⚡ devolvemos el tipo de destino del cast
    }

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

    // Acceso a array: ID[expr]
    if (e.ID() != null && e.getChildCount() == 4) {
      Id sim = ts.actual().resolver(e.ID().getText());
      if (sim instanceof Variable v && v.esArray()) {
        return v.tipo();
      }
    }

    // Post-incremento / Post-decremento: ID++ / ID--
    if (e.ID() != null && e.getChildCount() == 2) {
      Id sim = ts.actual().resolver(e.ID().getText());
      if (sim != null && sim.tipo() == Tipo.INT) {
        return Tipo.INT;
      }
      return Tipo.DESCONOCIDO;
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
        return (a == Tipo.DOUBLE || b == Tipo.DOUBLE || a == Tipo.FLOAT || b == Tipo.FLOAT)
            ? Tipo.FLOAT
            : Tipo.INT;
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
