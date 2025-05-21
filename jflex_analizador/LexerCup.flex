package jflex_analizador;

import java_cup.runtime.Symbol;

%%
%class LexerCup
%type java_cup.runtime.Symbol
%cup
%full
%line
%column
%char

%{
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
%}

L = [a-zA-Z_]
D = [0-9]
espacio = [ \t\r\n]
salto = \n

%%

/* Comentarios */
"//".* { /* Ignorar comentarios */ }

/* Tipos de metodo */
("base"|"cuerpo"|"garra") { return symbol(sym.Metodo, yytext()); }

"iniciar" { return symbol(sym.Iniciar, yytext()); }  // Para LexerCup


/* Operador reservada robot */
"Robot" { return symbol(sym.Robot, yytext()); }

/* Operador punto */
" "* "." " "*  { return symbol(sym.Punto, yytext()); }

/* Operador Igual */
"=" { return symbol(sym.Igual, yytext()); }

/* Identificador */
{L}({L}|{D})* { return symbol(sym.Identificador, yytext()); }

/* Numero */
("(-"{D}+")")|{D}+ { return symbol(sym.Numero, yytext()); }

/* Espacios en blanco */
{espacio}+ { /* Ignorar espacios */ }

/* Error de analisis */
. { return symbol(sym.ERROR, yytext()); }