// Generated from Cmini.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced by {@link
 * CminiParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return
 *     type.
 */
public interface CminiVisitor<T> extends ParseTreeVisitor<T> {
  /**
   * Visit a parse tree produced by {@link CminiParser#program}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitProgram(CminiParser.ProgramContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#functionDecl}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunctionDecl(CminiParser.FunctionDeclContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#paramList}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitParamList(CminiParser.ParamListContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#param}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitParam(CminiParser.ParamContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#block}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBlock(CminiParser.BlockContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#statement}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitStatement(CminiParser.StatementContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#varDecl}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitVarDecl(CminiParser.VarDeclContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#assignStat}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAssignStat(CminiParser.AssignStatContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#ifStat}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIfStat(CminiParser.IfStatContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#whileStat}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitWhileStat(CminiParser.WhileStatContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#forStat}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitForStat(CminiParser.ForStatContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#returnStat}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitReturnStat(CminiParser.ReturnStatContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#funcCall}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFuncCall(CminiParser.FuncCallContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#argList}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArgList(CminiParser.ArgListContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExpr(CminiParser.ExprContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#literal}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLiteral(CminiParser.LiteralContext ctx);

  /**
   * Visit a parse tree produced by {@link CminiParser#type}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitType(CminiParser.TypeContext ctx);
}
