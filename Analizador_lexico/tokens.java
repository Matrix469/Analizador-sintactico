/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Analizador_lexico;

/**
 *
 * @author Diego Quiroga
 */
public class tokens {

    // Tenemos que crear dos variables dos variables de tipo string 
    private String valor;
    private Tipo tipo;

    // Creamos nuestro getter y setters
    enum Tipo {
        /* numero("[0-9]+"),
        operador("[*|/|+|-]"),
        palabra ("[a-zA-Z]+");*/
        numeroEntero("[0-9]+"),
        numeroDecimal("[0-9]+\\.[0-9]+"), // Para decimales
        operador("[+\\-*/]"),
        identificador("[a-zA-Z_][a-zA-Z0-9_]*"), // Permite var1, _temp
        simboloIgual("="),
        parentesisIzq("\\("),
        parentesisDer("\\)"),
        revervada("r1-robot- base - velocidad- giro");
        public final String patron; // va contener nuestro patron de busqueda

        Tipo(String s) {
            this.patron = s; // vamos a asinar un patros que vamos asignando arriba
        }// cierra contructor tipo
    }

    public String getValor() {return valor;
    }

    public void setValor(String valor) { this.valor = valor;
    }

    public Tipo getTipo() {return tipo;
    }

    public void setTipo(Tipo tipo) {this.tipo = tipo;
    }

}
