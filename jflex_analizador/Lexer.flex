package jflex_analizador;
import static jflex_analizador.Tokens.*;

%%
%class Lexer
%type Tokens
%{
    public String lexeme;
%}

L = [a-zA-Z_]
D = [0-9]
espacio = [ ,\t,\r,\n]

%%

/* Espacios en blanco */
{espacio}+ { /* Ignorar */ }

/* Comentarios */
"//".* { /* Ignorar */ }

/* Salto de línea */
"\n" { return Linea; }

/* Operador Igual */
"=" { lexeme = yytext(); return Igual; }

/* Metodos */
("base"|"cuerpo"|"garra") { lexeme = yytext(); return Metodo; }

"iniciar" { lexeme = yytext(); return Iniciar; }  


/* Números */
("(-"{D}+")")|{D}+ { lexeme = yytext(); return Numero; }

/* Palabra reservada Robot */
"Robot" { lexeme = yytext(); return Robot; }

/* Operador punto */
" "* "." " "* { lexeme = yytext().trim(); return Punto; }

/* Identificadores */
{L}({L}|{D})* { lexeme = yytext(); return Identificador; }

/* Error de análisis */
. { lexeme = yytext(); return ERROR; }