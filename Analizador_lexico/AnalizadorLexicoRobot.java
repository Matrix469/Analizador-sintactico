package Analizador_lexico;

/**
 *
 * @author Diego Quiroga
 */
/**
 * Analizador Léxico y Sintáctico para Control de Brazo Robótico
 *
 * @author Diego Quiroga (Modificado)
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AnalizadorLexicoRobot extends JFrame {

    private JTextArea instruccionesArea;
    private JTextArea errorArea;
    private JTable tokenTable;
    private DefaultTableModel tableModel;
    private JButton btnAnalizar;
    private JButton btnLimpiar;
    private JButton btnArchivo;

    // Lista de tokens reconocidos
    private List<Token> tokens = new ArrayList<>();
    // Mapa para almacenar parámetros y valores
    private Map<String, Integer> parametros = new HashMap<>();
    // Lista para almacenar errores encontrados
    private List<String> errores = new ArrayList<>();
    // Banderas para controlar el estado del análisis
    private boolean robotCreado = false;
    private boolean robotIniciado = false;
    private boolean robotDetenido = false;

    // Colores del tema
    private Color colorPrincipal = new Color(255, 120, 0);  // Naranja institucional
    private Color colorFondo = new Color(240, 240, 240);    // Gris muy claro para el fondo
    private Color colorTexto = new Color(50, 50, 50);       // Gris oscuro para texto
    private Color colorError = new Color(220, 53, 69);      // Rojo para errores
    private Color colorExito = new Color(40, 167, 69);      // Verde para éxito

    public AnalizadorLexicoRobot() {
        setTitle("Analizador Léxico y Sintáctico - Instituto Tecnológico de Oaxaca");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(colorFondo);

        // Panel superior con logo y título
        JPanel headerPanel = crearPanelCabecera();
        add(headerPanel, BorderLayout.NORTH);

        // Panel central con áreas de texto y tabla
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(colorFondo);

        // Panel izquierdo para editor
        JPanel editorPanel = crearPanelEditor();

        // Panel derecho para resultados
        JPanel resultadosPanel = crearPanelResultados();

        centerPanel.add(editorPanel);
        centerPanel.add(resultadosPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel footerPanel = crearPanelBotones();
        add(footerPanel, BorderLayout.SOUTH);

        // Configurar eventos
        configurarEventos();
    }

    private JPanel crearPanelCabecera() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorPrincipal);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel logoLabel = new JLabel("ITO");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel("Instituto Tecnológico de Oaxaca", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Analizador", JLabel.RIGHT);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitleLabel.setForeground(Color.WHITE);

        panel.add(logoLabel, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelEditor() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(colorFondo);

        JPanel instruccionesPanel = new JPanel(new BorderLayout());
        instruccionesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorPrincipal, 2),
                "Código Fuente",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.BLACK));

        instruccionesArea = new JTextArea();
        instruccionesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        instruccionesArea.setForeground(colorTexto);
        instruccionesArea.setMargin(new Insets(5, 5, 5, 5));

        // Numeración de líneas
        TextLineNumber tln = new TextLineNumber(instruccionesArea);

        // Agregar DocumentListener para actualizar en tiempo real
        instruccionesArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tln.repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tln.repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                tln.repaint();
            }
        });

        JScrollPane instruccionesScroll = new JScrollPane(instruccionesArea);
        instruccionesScroll.setRowHeaderView(tln);
        instruccionesPanel.add(instruccionesScroll, BorderLayout.CENTER);

        panel.add(instruccionesPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(colorFondo);

        // Panel para errores
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorError, 2),
                "Errores Encontrados",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                colorError));

        errorArea = new JTextArea();
        errorArea.setEditable(false);
        errorArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        errorArea.setForeground(colorError);
        errorArea.setMargin(new Insets(15, 30, 15, 30));

        JScrollPane errorScroll = new JScrollPane(errorArea);
        errorPanel.add(errorScroll, BorderLayout.CENTER);

        // Tabla de tokens
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorPrincipal, 2),
                "Tabla de Tokens",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                colorPrincipal));

        // Solo dos columnas ahora: TOKEN y TIPO
        String[] columnNames = {"TOKEN", "TIPO"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tokenTable = new JTable(tableModel);
        tokenTable.setFont(new Font("Arial", Font.PLAIN, 14));
        tokenTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tokenTable.setRowHeight(25);

        JScrollPane tableScroll = new JScrollPane(tokenTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // Agregar ambos paneles al principal
        panel.add(errorPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        // Botón Analizar (existente)
        btnAnalizar = new JButton("Analizar");
        btnAnalizar.setFont(new Font("Arial", Font.BOLD, 16));
        btnAnalizar.setBackground(Color.ORANGE);
        btnAnalizar.setForeground(Color.BLACK);
        btnAnalizar.setFocusPainted(false);
        btnAnalizar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Botón Limpiar (existente)
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 16));
        btnLimpiar.setBackground(Color.LIGHT_GRAY);
        btnLimpiar.setForeground(Color.BLACK);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Nuevo botón para abrir archivo
        btnArchivo = new JButton("Abrir Archivo");
        btnArchivo.setFont(new Font("Arial", Font.BOLD, 16));
        btnArchivo.setBackground(new Color(100, 150, 255));  // Azul claro
        btnArchivo.setForeground(Color.BLACK);
        btnArchivo.setFocusPainted(false);
        btnArchivo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panel.add(btnAnalizar);
        panel.add(btnLimpiar);
        panel.add(btnArchivo);

        return panel;
    }
    

    private void configurarEventos() {
    // Eventos existentes
    btnAnalizar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            analizarCodigo();
        }
    });

    btnLimpiar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            instruccionesArea.setText("");
            errorArea.setText("");
            tableModel.setRowCount(0);
        }
    });

    // Nuevo evento para el botón de archivo
    btnArchivo.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            btnArchivoActionPerformed(evt);
        }
    });
}
    private void btnArchivoActionPerformed(java.awt.event.ActionEvent evt) {
    JFileChooser chooser = new JFileChooser();
    int returnVal = chooser.showOpenDialog(this);
    
    if(returnVal == JFileChooser.APPROVE_OPTION) {
        File archivo = chooser.getSelectedFile();
        
        try {
            // Leer el contenido del archivo
            String contenido = new String(Files.readAllBytes(archivo.toPath()));
            // Colocar el contenido en el área de texto
            instruccionesArea.setText(contenido);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, 
                "Archivo no encontrado: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al leer el archivo: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void analizarCodigo() {
        // Limpiar resultados anteriores
        tokens.clear();
        parametros.clear();
        errores.clear();
        tableModel.setRowCount(0);
        errorArea.setText("");
        robotCreado = false;
        robotIniciado = false;
        robotDetenido = false;

        // Obtener líneas de código
        String[] lineas = instruccionesArea.getText().split("\n");

        // Procesar líneas
        procesarLineas(lineas, 0, lineas.length - 1, 1);

        // Mostrar tokens en la tabla
        mostrarTokens();

        // Mostrar errores en el área EOF
        mostrarErrores();
    }

    private int procesarLineas(String[] lineas, int inicio, int fin, int numeroLineaBase) {
        int i = inicio;
        Stack<Integer> buclesActivos = new Stack<>();
        Stack<Integer> repeticionesRestantes = new Stack<>();
        Stack<Integer> iniciosBucles = new Stack<>();

        while (i <= fin) {
            if (i >= lineas.length || lineas[i].trim().isEmpty()) {
                i++;
                continue;
            }

            String codigo = lineas[i].trim();

            if (codigo.matches("^\\d+\\s+.*")) {
                codigo = codigo.replaceFirst("^\\d+\\s+", "");
            }

            int numeroLinea = numeroLineaBase + i;

            if (codigo.startsWith("repetir ")) {
                try {
                    String[] partes = codigo.split("\\s+");
                    if (partes.length < 3 || !partes[2].equals("veces")) {
                        agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: "
                                + codigo.indexOf(' ', 8) + ", Texto: \""
                                + (partes.length > 1 ? partes[1] : "") + "\"");
                        tokens.add(new Token("repetir", "estructura_control"));
                        if (partes.length > 1) {
                            tokens.add(new Token(partes[1], "valor"));
                        }
                        i++;
                        continue;
                    }

                    try {
                        int repeticiones = Integer.parseInt(partes[1]);
                        if (repeticiones <= 0) {
                            agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: "
                                    + codigo.indexOf(partes[1]) + ", Texto: \"" + partes[1]
                                    + "\". El número de repeticiones debe ser positivo.");
                            repeticiones = 1;
                        }

                        tokens.add(new Token("repetir", "estructura_control"));
                        tokens.add(new Token("veces", "palabra_reservada"));

                        buclesActivos.push(1);
                        repeticionesRestantes.push(repeticiones);
                        iniciosBucles.push(i + 1);
                        i++;
                    } catch (NumberFormatException e) {
                        agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: "
                                + codigo.indexOf(partes[1]) + ", Texto: \"" + partes[1]
                                + "\". Se esperaba un valor numérico.");
                        tokens.add(new Token("repetir", "estructura_control"));
                        tokens.add(new Token(partes[1], "valor_invalido"));
                        if (partes.length > 2) {
                            tokens.add(new Token(partes[2], "palabra_reservada"));
                        }
                        i++;
                    }
                } catch (Exception e) {
                    agregarError("Error de sintaxis. Línea: " + numeroLinea + ": Formato incorrecto para repetir");
                    tokens.add(new Token("repetir", "estructura_control"));
                    i++;
                }
            } else if (codigo.equals("fin_repetir")) {
                if (buclesActivos.isEmpty()) {
                    agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: 1, Texto: \"fin_repetir\". 'fin_repetir' sin 'repetir' correspondiente");
                }

                tokens.add(new Token("fin_repetir", "estructura_control"));

                if (!buclesActivos.isEmpty()) {
                    int restantes = repeticionesRestantes.pop() - 1;
                    int inicioBucle = iniciosBucles.pop();
                    buclesActivos.pop();

                    if (restantes > 0) {
                        repeticionesRestantes.push(restantes);
                        iniciosBucles.push(inicioBucle);
                        buclesActivos.push(1);
                        i = inicioBucle;
                    } else {
                        i++;
                    }
                } else {
                    i++;
                }
            } else {
                analizarLinea(codigo, numeroLinea);
                i++;
            }
        }

        if (!buclesActivos.isEmpty()) {
            agregarError("Error de sintaxis. Hay " + buclesActivos.size() + " estructura(s) 'repetir' sin su correspondiente 'fin_repetir'");
        }

        return i;
    }

    private void analizarLinea(String linea, int numeroLinea) {
        if (linea.startsWith("Robot ")) {
            String nombreRobot = linea.substring(6).trim();
            tokens.add(new Token("Robot", "Palabra_r"));
            tokens.add(new Token(nombreRobot, "identificador"));

            if (nombreRobot.equals("r1")) {
                robotCreado = true;
            } else {
                agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: " + (linea.indexOf(nombreRobot) + 1)
                        + ", Texto: \"" + nombreRobot + "\". Nombre de robot no válido. Use 'r1'");
            }
            return;
        }

        Pattern patronEspacios = Pattern.compile("^(r\\d+)\\s+\\.\\s*(\\w+).*$");
        Matcher matcherEspacios = patronEspacios.matcher(linea);

        if (matcherEspacios.find()) {
            String nombreRobot = matcherEspacios.group(1);
            String accion = matcherEspacios.group(2);

            agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: "
                    + (linea.indexOf('.')) + ", Texto: \"" + nombreRobot + " .\". No debe haber espacios entre '"
                    + nombreRobot + "' y '.'");

            tokens.add(new Token(nombreRobot, "identificador"));
            tokens.add(new Token(accion, "accion_con_espacio"));
            return;
        }

        Pattern patronRobot = Pattern.compile("^(r\\d+)\\s*\\.\\s*(\\w+)\\s*=?\\s*(.*)$");
        Matcher matcher = patronRobot.matcher(linea);

        if (matcher.find()) {
            String nombreRobot = matcher.group(1);
            String accion = matcher.group(2);
            String valorStr = matcher.group(3);

            if (!nombreRobot.equals("r1")) {
                agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: " + (linea.indexOf(nombreRobot) + 1)
                        + ", Texto: \"" + nombreRobot + "\". Robot no declarado");
                tokens.add(new Token(nombreRobot, "identificador_invalido"));
                if (!accion.isEmpty()) {
                    tokens.add(new Token(accion, "accion"));
                }
                return;
            }

            if (!robotCreado) {
                agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: 1, Texto: \""
                        + nombreRobot + "\". Robot no creado antes de usar");
            }

            if (linea.contains(" .") || linea.contains(". ")) {
                int columnaPunto = linea.indexOf('.');
                agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: " + columnaPunto
                        + ", Texto: \"" + linea.substring(Math.max(0, columnaPunto - 1),
                                Math.min(linea.length(), columnaPunto + 2))
                        + "\". No debe haber espacios alrededor del punto");
            }

            tokens.add(new Token(nombreRobot, "identificador"));

            if (accion.equals("iniciar")) {
                tokens.add(new Token("iniciar", "accion"));
                robotIniciado = true;
                return;
            } else if (accion.equals("detener")) {
                tokens.add(new Token("detener", "accion"));
                robotDetenido = true;
                return;
            }

            if (!robotIniciado) {
                agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: " + (linea.indexOf(accion) + 1)
                        + ", Texto: \"" + accion + "\". Robot no iniciado antes de controlar");
            }

            if (!esValidaPropiedad(accion)) {
                agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: " + (linea.indexOf(accion) + 1)
                        + ", Texto: \"" + accion + "\". Propiedad no válida");
                tokens.add(new Token(accion, "propiedad_invalida"));
                return;
            }

            if (!valorStr.isEmpty()) {
                int posicionIgual = linea.indexOf('=');

                if (linea.contains(" =") || linea.contains("= ")) {
                    agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: " + posicionIgual
                            + ", Texto: \"" + linea.substring(Math.max(0, posicionIgual - 1),
                                    Math.min(linea.length(), posicionIgual + 2))
                            + "\". No debe haber espacios alrededor del signo igual");
                }

                try {
                    int valor = Integer.parseInt(valorStr);

                    if (!esValorValido(accion, valor)) {
                        agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: " + (linea.indexOf(valorStr) + 1)
                                + ", Texto: \"" + valorStr + "\". Valor fuera de rango para " + accion);
                    }

                    tokens.add(new Token(accion, "Metodo"));

                    if (esValorValido(accion, valor)) {
                        parametros.put(accion, valor);
                    }
                } catch (NumberFormatException e) {
                    agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: " + (linea.indexOf(valorStr) + 1)
                            + ", Texto: \"" + valorStr + "\". Se esperaba un valor numérico");
                    tokens.add(new Token(accion, "Metodo"));
                }
            } else {
                tokens.add(new Token(accion, "Metodo"));
            }
        } else if (linea.startsWith("r") || linea.contains(".")) {
            agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: 1, Texto: \""
                    + linea.split("\\s+")[0] + "\". Formato incorrecto");

            if (linea.startsWith("r")) {
                int dotIndex = linea.indexOf('.');
                int equalIndex = linea.indexOf('=');

                if (dotIndex > 0) {
                    String robotId = linea.substring(0, dotIndex).trim();
                    tokens.add(new Token(robotId, "identificador"));

                    if (equalIndex > dotIndex) {
                        String accion = linea.substring(dotIndex + 1, equalIndex).trim();
                        tokens.add(new Token(accion, "Metodo"));
                    } else if (dotIndex < linea.length() - 1) {
                        String accion = linea.substring(dotIndex + 1).trim();
                        tokens.add(new Token(accion, "accion"));
                    }
                } else {
                    tokens.add(new Token(linea, "token_invalido"));
                }
            } else {
                tokens.add(new Token(linea, "token_invalido"));
            }
        } else if (!linea.startsWith("repetir ") && !linea.equals("fin_repetir")) {
            agregarError("Error de sintaxis. Línea: " + numeroLinea + " Columna: 1, Texto: \""
                    + linea.split("\\s+")[0] + "\". Instrucción no reconocida");
            tokens.add(new Token(linea, "instruccion_invalida"));
        }
    }

    private boolean esValidaPropiedad(String propiedad) {
        return propiedad.equals("base")
                || propiedad.equals("cuerpo")
                || propiedad.equals("garra")
                || propiedad.equals("velocidad");
    }

    private boolean esValorValido(String propiedad, int valor) {
        switch (propiedad) {
            case "base":
                return valor >= 0 && valor <= 180;
            case "cuerpo":
                return valor >= 0 && valor <= 90;
            case "garra":
                return valor >= 0 && valor <= 180;
            case "velocidad":
                return valor >= 1 && valor <= 5;
            default:
                return false;
        }
    }

    private void agregarError(String error) {
        errores.add(error);
    }

    private void mostrarErrores() {
        if (errores.isEmpty()) {  
            errorArea.setText("No se encontraron errores en el análisis.");
            errorArea.setForeground(new Color(25, 111, 61));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String error : errores) {
            sb.append(error).append("\n");
        }
        errorArea.setText(sb.toString());
        errorArea.setForeground(Color.RED);
    }

    private void mostrarTokens() {
        tableModel.setRowCount(0);

        for (Token token : tokens) {
            Object[] row = {
                token.getToken(),
                token.getTipo()
            };
            tableModel.addRow(row);
        }
    }

    private class Token {

        private String token;
        private String tipo;

        public Token(String token, String tipo) {
            this.token = token;
            this.tipo = tipo;
        }

        public String getToken() {
            return token;
        }

        public String getTipo() {
            return tipo;
        }
    }

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

            int startLine = 1;
            int endLine = textComponent.getLineCount();

            for (int i = startLine; i <= endLine; i++) {
                String lineNumber = String.valueOf(i);
                int x = getWidth() - fm.stringWidth(lineNumber) - 5;
                int y = (i - startLine) * lineHeight + baseline;
                g2d.drawString(lineNumber, x, y);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AnalizadorLexicoRobot app = new AnalizadorLexicoRobot();
                app.setLocationRelativeTo(null);
                app.setVisible(true);

                // Añadir texto de ejemplo para demostración
                app.instruccionesArea.setText("Robot r1\n"
                        + "r1.iniciar\n"
                        + "r1.base=90\n"
                        + "r1 .cuerpo=45\n"
                        + "repetir 3 veces\n"
                        + "r1.garra=180\n"
                        + "r1.garra=0\n"
                        + "fin_repetir\n"
                        + "r1.detener");
            }
        });
    }
}
