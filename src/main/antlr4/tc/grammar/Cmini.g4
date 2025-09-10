grammar Cmini;

// --- Parser rules ---
program
    : (functionDecl | statement)* EOF
    ;

// Funciones
functionDecl
    : type ID '(' paramList? ')' block
    ;

paramList
    : param (',' param)*
    ;

param
    : type ID
    ;

// Bloques
block
    : '{' statement* '}'
    ;

// Sentencias
statement
    : varDecl ';'
    | assignStat ';'
    | ifStat
    | whileStat
    | forStat
    | returnStat ';'
    | funcCall ';'
    | block
    ;

// Declaraciones y asignaciones
varDecl
    : type ID ('=' expr)?
    ;

assignStat
    : ID '=' expr
    ;

// Control de flujo
ifStat
    : 'if' '(' expr ')' statement ('else' statement)?
    ;

whileStat
    : 'while' '(' expr ')' statement
    ;

forStat
    : 'for' '(' (varDecl | assignStat)? ';' expr? ';' assignStat? ')' statement
    ;

// Return
returnStat
    : 'return' expr?
    ;

// Llamada a función
funcCall
    : ID '(' argList? ')'
    ;

argList
    : expr (',' expr)*
    ;

// Expresiones
expr
    : expr op=('*'|'/') expr
    | expr op=('+'|'-') expr
    | expr op=('=='|'!='|'<'|'>'|'<='|'>=') expr
    | ID
    | INT
    | FLOAT
    | funcCall
    | '(' expr ')'
    ;

// --- Lexer rules ---
type  : 'int' | 'float' | 'void' ;

ID    : [a-zA-Z_][a-zA-Z_0-9]* ;
INT   : [0-9]+ ;
FLOAT : [0-9]+'.'[0-9]+ ;

WS    : [ \t\r\n]+ -> skip ;    

// Comentarios
COMMENT
      : '//' ~[\r\n]* -> skip
      ;
MULTILINE_COMMENT
      : '/*' .*? '*/' -> skip
      ;
