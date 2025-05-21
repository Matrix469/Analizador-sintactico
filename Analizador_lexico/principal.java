/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Analizador_lexico;

import Analizador_lexico.tokens.Tipo;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Diego Quiroga
 */
public class principal {

    //vamos a devolver un valor de tipo token
    private static ArrayList<tokens> lex(String entrada) {
        //una variable final 
        final ArrayList<tokens> token = new ArrayList();
        /*definimos un variable final de una clase StringTokenizer
        hace el trabajo de dividir los token*/

        // nos solicita un parametro la cual será la entrada de texto
        /*Quetemos divir por token la entrada*/
        final StringTokenizer st = new StringTokenizer(entrada);

        while (st.hasMoreTokens()) { // si contiene tokens
            String palabra = st.nextToken();
            // para determianr el matcha, la podemos considerar banderas
            boolean banderas = false;

            for (Tipo tokenTipo : Tipo.values()) {// vamos interar los tipos, para numeros y operadores

                Pattern patron = Pattern.compile(tokenTipo.patron);
                Matcher busqueda = patron.matcher(palabra); //desglozar nuestra palabra

                /*Creamos una estructura condicional*/
                if (busqueda.find()) {
                    tokens Token = new tokens();

                    Token.setTipo(tokenTipo);
                    Token.setValor(palabra);
                    token.add(Token);
                    banderas = true;

                } //if 
            } // for 
            if (!banderas) {
                //mostrar un error sobre que el token es invalido
                throw new RuntimeException("El token es invalido " + palabra);
            }//if 
        }//while 
        return token;
    }//private

    public static void main(String[] agrs) {
        /*Expresión matematica donde no evaluamos, solo analiza numeros y operadores*/
        String entrada = "11 + 22 - 10 - aA";
        ArrayList<tokens> Token = lex(entrada);

        for (tokens Tokens : Token) {
            System.err.println(Tokens.getTipo() + " : " + Tokens.getValor());
        }
    }
}
