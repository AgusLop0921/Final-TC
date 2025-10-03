grammar Cmini;

// ---------------- Parser rules ----------------

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
    : simpleStatement ';'
    | ifStat
    | whileStat
    | forStat
    | returnStat ';'
    | block
    ;

// Sentencias simples (las que requieren ;)
simpleStatement
    : varDecl
    | assignStat
    | funcCall
    ;

// Declaraciones (array opcional + inicialización opcional)
varDecl
    : type ID ('[' INT ']')? ('=' expr)?
    ;

// Asignación (soporta array[i] = expr)
assignStat
    : (ID ('[' expr ']')?) '=' expr
    ;

// Control de flujo
ifStat
    : 'if' '(' expr ')' statement ('else' statement)?
    ;

whileStat
    : 'while' '(' expr ')' statement
    ;

forStat
    : 'for' '(' (varDecl | assignStat)? ';' expr? ';' (assignStat | expr)? ')' statement
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
    : expr ('*' | '/' | MOD) expr
    | expr ('+' | '-') expr
    | expr ('==' | '!=' | '<' | '>' | '<=' | '>=') expr
    | ID ('[' expr ']')?
    | literal
    | funcCall
    | '(' expr ')'
    | '(' type ')' expr       // casting (ej: (int)pr)
    | ID '++' 
    | ID '--'          
    ;

// Literales
literal
    : INT
    | FLOAT
    | CHAR_LITERAL
    | TRUE
    | FALSE
    ;

// ---------------- Lexer rules ----------------

// Tipos
type
    : 'int'
    | 'float'
    | 'double'
    | 'char'
    | 'bool'
    | 'string'
    | 'void'
    ;

// Identificadores y números
ID     : [a-zA-Z_][a-zA-Z_0-9]* ;
INT    : [0-9]+ ;
FLOAT  : [0-9]+ '.' [0-9]+ ;

// Bool literals
TRUE   : 'true' ;
FALSE  : 'false' ;

// Char literal
CHAR_LITERAL : '\'' . '\'' ;

// Operador módulo
MOD    : '%' ;

// Espacios y comentarios
WS    : [ \t\r\n]+ -> skip ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;

MULTILINE_COMMENT
    : '/*' .*? '*/' -> skip
    ;