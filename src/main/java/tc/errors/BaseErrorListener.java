package tc.errors;

import org.antlr.v4.runtime.*;

public class BaseErrorListener extends org.antlr.v4.runtime.BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e)
    {
        String error = String.format("Error en línea %d:%d - %s",
                                     line, charPositionInLine, msg);
        System.err.println(error);
    }
}
