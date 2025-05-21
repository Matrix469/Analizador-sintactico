/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Analizador_lexico;

/**
 *
 * @author Diego Quiroga
 */
import java.awt.*;
import javax.swing.*;

public class TextLineNumber extends JPanel {
    private final JTextArea textComponent;
    private final Font font;
    private final Color lineNumberColor = Color.GRAY;
    private final Color backgroundColor = new Color(240, 240, 240);

    public TextLineNumber(JTextArea textComponent) {
        this.textComponent = textComponent;
        this.font = new Font("monospaced", Font.PLAIN, textComponent.getFont().getSize());
        setPreferredSize(new Dimension(40, 1));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        // Hacer que el panel no sea focusable ni editable
        setFocusable(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(font);
        g2d.setColor(lineNumberColor);

        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        int baseline = fm.getAscent();

        // Obtener el número de líneas visibles
        int startLine = 1;
        int endLine = textComponent.getLineCount();

        // Dibujar números de línea
        for (int i = startLine; i <= endLine; i++) {
            String lineNumber = String.valueOf(i);
            int x = getWidth() - fm.stringWidth(lineNumber) - 5;
            int y = (i - startLine) * lineHeight + baseline;
            g2d.drawString(lineNumber, x, y);
        }
    }
}
