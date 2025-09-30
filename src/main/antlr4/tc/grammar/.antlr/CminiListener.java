// Generated from Cmini.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CminiParser}.
 */
public interface CminiListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CminiParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CminiParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CminiParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(CminiParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(CminiParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(CminiParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(CminiParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(CminiParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(CminiParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(CminiParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(CminiParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(CminiParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(CminiParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(CminiParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(CminiParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#assignStat}.
	 * @param ctx the parse tree
	 */
	void enterAssignStat(CminiParser.AssignStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#assignStat}.
	 * @param ctx the parse tree
	 */
	void exitAssignStat(CminiParser.AssignStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#ifStat}.
	 * @param ctx the parse tree
	 */
	void enterIfStat(CminiParser.IfStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#ifStat}.
	 * @param ctx the parse tree
	 */
	void exitIfStat(CminiParser.IfStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#whileStat}.
	 * @param ctx the parse tree
	 */
	void enterWhileStat(CminiParser.WhileStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#whileStat}.
	 * @param ctx the parse tree
	 */
	void exitWhileStat(CminiParser.WhileStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#forStat}.
	 * @param ctx the parse tree
	 */
	void enterForStat(CminiParser.ForStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#forStat}.
	 * @param ctx the parse tree
	 */
	void exitForStat(CminiParser.ForStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#returnStat}.
	 * @param ctx the parse tree
	 */
	void enterReturnStat(CminiParser.ReturnStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#returnStat}.
	 * @param ctx the parse tree
	 */
	void exitReturnStat(CminiParser.ReturnStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#funcCall}.
	 * @param ctx the parse tree
	 */
	void enterFuncCall(CminiParser.FuncCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#funcCall}.
	 * @param ctx the parse tree
	 */
	void exitFuncCall(CminiParser.FuncCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#argList}.
	 * @param ctx the parse tree
	 */
	void enterArgList(CminiParser.ArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#argList}.
	 * @param ctx the parse tree
	 */
	void exitArgList(CminiParser.ArgListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(CminiParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(CminiParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(CminiParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(CminiParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link CminiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(CminiParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CminiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(CminiParser.TypeContext ctx);
}