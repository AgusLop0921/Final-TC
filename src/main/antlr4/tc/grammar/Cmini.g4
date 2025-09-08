grammar Cmini;

program
    : (functionDecl | statement)* EOF
    ;

functionDecl
    : type ID '(' ')' block
    ;

block
    : '{' statement* '}'
    ;

statement
    : 'return' INT ';'
    ;

type: 'int' | 'float' | 'void';

ID  : [a-zA-Z_][a-zA-Z_0-9]* ;
INT : [0-9]+ ;

WS  : [ \t\r\n]+ -> skip ;