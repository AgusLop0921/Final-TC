package tc.errors;

import org.antlr.v4.runtime.*;
import tc.semantics.ErrorReporter;

public class BaseErrorListener extends org.antlr.v4.runtime.BaseErrorListener {

  private final ErrorReporter reporter;

  public BaseErrorListener(ErrorReporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public void syntaxError(
      Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line,
      int charPositionInLine,
      String msg,
      RecognitionException e) {
    String error = "L" + line + ":" + charPositionInLine + " - " + msg;
    if (reporter != null) reporter.addSyntax(error);
  }
}
