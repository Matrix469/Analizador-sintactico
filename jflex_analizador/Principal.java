/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jflex_analizador;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java_cup.Main;

public class Principal {
    public static void main(String[] args) throws Exception {
        String ruta1 = "C:/Users/Diego Quiroga/OneDrive/Documentos/NetBeansProjects/AUTOMATAS/src/jflex_analizador/Lexer.flex";
        String ruta2 = "C:/Users/Diego Quiroga/OneDrive/Documentos/NetBeansProjects/AUTOMATAS/src/jflex_analizador/LexerCup.flex";
        String[] rutaS = {"-parser", "Sintax", "-symbols", "sym", 
                         "C:/Users/Diego Quiroga/OneDrive/Documentos/NetBeansProjects/AUTOMATAS/src/jflex_analizador/Sintax.cup"};
        generarLexer(ruta1, ruta2, rutaS);
    }

    public static void generarLexer(String ruta1, String ruta2, String[] rutaS) throws IOException, Exception {
        // Generar analizadores léxicos
        File archivo = new File(ruta1);
        JFlex.Main.generate(archivo);
        
        archivo = new File(ruta2);
        JFlex.Main.generate(archivo);
        
        // Generar analizador sintáctico
        Main.main(rutaS);
        
        // Mover archivos generados por CUP
        Path destinoSym = Paths.get("src/jflex_analizador/sym.java");
        Path destinoSin = Paths.get("src/jflex_analizador/Sintax.java");
        
        // Esperar a que los archivos se generen
        Thread.sleep(1000);
        
        // Mover sym.java
        if(Files.exists(Paths.get("sym.java"))) {
            Files.move(
                Paths.get("sym.java"),
                destinoSym,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
        }
        
        // Mover Sintax.java
        if(Files.exists(Paths.get("Sintax.java"))) {
            Files.move(
                Paths.get("Sintax.java"),
                destinoSin,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}

