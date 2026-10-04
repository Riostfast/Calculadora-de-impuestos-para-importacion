package mx.edu.uaeh.importaciones;

import java.awt.*;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Interfaz Swing autocontenida para el ejercicio de cálculo de importación. */
public final class CalculadoraImportacionApp extends JFrame {
    private static final Locale MX = new Locale("es", "MX");
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(MX);

    /** Fecha a la que están referidas las reglas, tasas y aranceles usados en el programa. */
    private static final String FECHA_REGLAS = "28 de septiembre de 2026";

    /** Producto especial para la opción "Otro". */
    private static final Producto OTRO = new Producto(
        "Otro", "0000.00.00", "Otro (definido por el usuario)",
        "Producto no listado. Capture la fracción, descripción y ad valorem manualmente.",
        0.0,
        "Verifique la fracción arancelaria, NOMs y regulaciones aplicables ante la autoridad competente.");

    /** Catálogo agrupado por categoría (orden de inserción preservado). */
    private static final Map<String, List<Producto>> CATALOGO = new LinkedHashMap<>();
    static {
        CATALOGO.put("Electrónicos", List.of(
            new Producto("Electrónicos", "8517.11.01", "Teléfonos con audífonos inalámbricos",
                "Teléfonos de auricular inalámbrico combinado con micrófono.", .15,
                "Ley Aduanera, LIGIE/TIGIE y NOM de información comercial aplicable a productos electrónicos. "
                + "Verificar homologación/telecomunicaciones si corresponde.")
        ));

        CATALOGO.put("Vestimenta", List.of(
            new Producto("Vestimenta", "6114.30.02", "Playeras",
                "Prendas de vestir confeccionadas de fibras sintéticas o artificiales.", .35,
                "Ley Aduanera, LIGIE/TIGIE y NOM-004-SE-2021 (información comercial y etiquetado "
                + "de productos textiles, prendas de vestir y accesorios).")
        ));

        CATALOGO.put("Calzado", List.of(
            new Producto("Calzado", "6402.19.01", "Tenis NIKE",
                "Calzado para hombres, adultos y jóvenes, con corte predominantemente de caucho o plástico.", .35,
                "Ley Aduanera, LIGIE/TIGIE y NOM de información comercial aplicable a calzado. "
                + "Confirmar clasificación, etiquetado y medidas de protección vigentes.")
        ));

        CATALOGO.put("Cosméticos", List.of(
            new Producto("Cosméticos", "3304.10.01", "Labial CYZONE",
                "Preparaciones para el maquillaje de los labios.", .36,
                "Ley Aduanera, LIGIE/TIGIE, Ley General de Salud y NOM-141-SSA1/SCFI-2012 "
                + "(etiquetado de productos cosméticos preenvasados).")
        ));

        CATALOGO.put("Perfumería", List.of(
            new Producto("Perfumería", "3303.00.01", "Perfume DIOR",
                "Perfumes y aguas de tocador.", .25,
                "Ley Aduanera, LIGIE/TIGIE, Ley General de Salud y NOM-141-SSA1/SCFI-2012 "
                + "para etiquetado cosmético."),
            new Producto("Perfumería", "3305.10.01", "Pantene",
                "Champúes.", .25,
                "Ley Aduanera, LIGIE/TIGIE, Ley General de Salud y NOM-141-SSA1/SCFI-2012 "
                + "para etiquetado cosmético.")
        ));

        CATALOGO.put("Muebles", List.of(
            new Producto("Muebles", "9403.50.01", "Mesita de noche",
                "Muebles de madera de los tipos utilizados en dormitorios.", .25,
                "Ley Aduanera, LIGIE/TIGIE y requisitos de información comercial aplicables. "
                + "Revisar además requisitos fitosanitarios cuando la mercancía o embalaje de madera los active.")
        ));

        CATALOGO.put("Autopartes", List.of(
            new Producto("Autopartes", "7009.10.99", "Retrovisor",
                "Espejos retrovisores para vehículos.", .15,
                "Ley Aduanera, LIGIE/TIGIE y, según uso o comercialización, normas de seguridad "
                + "e información comercial aplicables a autopartes.")
        ));

        CATALOGO.put("Juguetes", List.of(
            new Producto("Juguetes", "9503.00.11", "LEGO",
                "Juegos o surtidos de construcción y demás juguetes de construcción.", .30,
                "Ley Aduanera, LIGIE/TIGIE y NOM-252-SSA1-2011 (requisitos sanitarios y de seguridad "
                + "para juguetes); confirmar vigencia y campo de aplicación.")
        ));

        CATALOGO.put("Medicamentos", List.of(
            new Producto("Medicamentos", "3003.10.01", "Antibiótico (Amoxicilina)",
                "Medicamentos con penicilinas, derivados o estreptomicinas.", .15,
                "Ley Aduanera, LIGIE/TIGIE, Ley General de Salud y Reglamento de Insumos para la Salud. "
                + "Requiere revisión sanitaria/COFEPRIS y registro sanitario cuando aplique.")
        ));

        CATALOGO.put("Alcohol y bebidas", List.of(
            // Espacio reservado para futuros productos de esta categoría.
        ));
    }

    private final JComboBox<String> categoria = new JComboBox<>(CATALOGO.keySet().toArray(new String[0]));
    private final JComboBox<Producto> producto = new JComboBox<>();
    private final JTextField valorComercial = campo("0.00");
    private final JTextField incrementables = campo("0.00");
    private final JTextField tipoCambio = campo("19.95");
    private final JTextField cuotaCompensatoria = campo("0.00");
    private final JTextField tasaDta = campo("0.008");
    private final JTextField tasaIva = campo("0.16");
    private final JCheckBox dtaFijo = new JCheckBox("Usar DTA fijo (MXN)");
    private final JTextField importeDtaFijo = campo("0.00");

    private final JTextField fraccionManual = campo("0000.00.00");
    private final JTextField adValoremManual = campo("0.00");
    private final JTextField descripcionManual = campo("");
    private final JPanel panelOtro = new JPanel(new GridBagLayout());
    private final JLabel avisoOtro = new JLabel();

    private final JLabel descripcion = new JLabel();
    private final JTextArea requisitos = area();
    private final JLabel va = resultado();
    private final JLabel igi = resultado();
    private final JLabel dta = resultado();
    private final JLabel iva = resultado();
    private final JLabel total = resultado();
    private final JLabel baseIva = resultado();
    private final JLabel error = new JLabel(" ");

    public CalculadoraImportacionApp() {
        super("Calculadora de Impuestos de Importación 2026");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 780));
        setLocationByPlatform(true);
        crearInterfaz();
        conectarEventos();
        actualizarProductosPorCategoria();
        actualizarProducto();
        calcular();
    }

    private void crearInterfaz() {
        CardLayout tarjetas = new CardLayout();
        JPanel raiz = new JPanel(tarjetas);

        JPanel bienvenida = new JPanel(new GridBagLayout());
        bienvenida.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints b = new GridBagConstraints();
        b.insets = new Insets(12, 12, 12, 12);
        b.gridx = 0; b.gridy = 0; b.anchor = GridBagConstraints.CENTER;

        JLabel nombre = new JLabel("Calculadora de Impuestos de Importación 2026");
        nombre.setFont(nombre.getFont().deriveFont(Font.BOLD, 28f));
        bienvenida.add(nombre, b);

        b.gridy++;
        JLabel queHace = new JLabel("<html><div style='text-align:center;width:650px;'>"
            + "Aplicación académica para estimar las principales contribuciones de una operación de importación "
            + "a partir del valor comercial, incrementables, tipo de cambio, fracción arancelaria y tasas aplicables."
            + "<br><br>Calcula el Valor en Aduana, IGI, DTA, Base de IVA, IVA y el total de contribuciones."
            + "</div></html>");
        queHace.setFont(queHace.getFont().deriveFont(16f));
        bienvenida.add(queHace, b);

        b.gridy++;
        JLabel aviso = new JLabel("<html><div style='text-align:center;width:650px;'>"
            + "</div></html>");
        aviso.setFont(aviso.getFont().deriveFont(14f));
        bienvenida.add(aviso, b);

        b.gridy++;
        JButton entrar = new JButton("Abrir calculadora");
        entrar.setFont(entrar.getFont().deriveFont(Font.BOLD, 15f));
        entrar.addActionListener(e -> tarjetas.show(raiz, "calculadora"));
        bienvenida.add(entrar, b);

        JPanel calculadora = new JPanel(new BorderLayout());
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Calculadora", desplazable(panelCalculadora()));
        pestanas.addTab("Marco legal", desplazable(panelLegal()));
        calculadora.add(pestanas, BorderLayout.CENTER);

        raiz.add(bienvenida, "inicio");
        raiz.add(calculadora, "calculadora");
        setContentPane(raiz);
        tarjetas.show(raiz, "inicio");
    }

    /** Envuelve una vista en desplazamiento vertical y horizontal. */
    private static JScrollPane desplazable(JComponent componente) {
        JScrollPane scroll = new JScrollPane(componente,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel panelCalculadora() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        // Ancho mínimo deliberado para que en laptops aparezca la barra horizontal.
        panel.setPreferredSize(new Dimension(1180, 780));
        JLabel titulo = new JLabel("Cálculo de contribuciones a la importación");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 21f));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 18, 0));
        centro.setPreferredSize(new Dimension(1120, 520));
        JPanel entradas = new JPanel(new GridBagLayout());
        entradas.setBorder(new EmptyBorder(4, 4, 4, 4));
        GridBagConstraints c = restricciones();

        // Fila 0: Categoría
        agregar(entradas, c, 0, "Categoría", categoria);
        // Fila 1: Producto / fracción
        agregar(entradas, c, 1, "Producto / fracción", producto);

        // Fila 2: Panel "Otro" (ocupa las dos columnas)
        panelOtro.setBorder(BorderFactory.createTitledBorder("Datos del producto 'Otro'"));
        GridBagConstraints co = restricciones();
        agregar(panelOtro, co, 0, "Fracción arancelaria", fraccionManual);
        agregar(panelOtro, co, 1, "Ad valorem (ej. 0.15)", adValoremManual);
        agregar(panelOtro, co, 2, "Descripción", descripcionManual);
        avisoOtro.setText("<html><div style='width:380px'><b>Al usar \"Otro\":</b> verifique la fracción y el arancel en la LIGIE/TIGIE, "
            + "la Ley Aduanera, Reglas Generales de Comercio Exterior, NOMs, permisos, regulaciones y restricciones no arancelarias, "
            + "tratados de libre comercio/origen y posibles cuotas compensatorias. Use fuentes oficiales y vigentes.</div></html>");
        avisoOtro.setForeground(new Color(120, 70, 0));
        avisoOtro.setVerticalAlignment(SwingConstants.TOP);
        co.gridx = 0; co.gridy = 3; co.gridwidth = 2; co.weightx = 1;
        co.fill = GridBagConstraints.HORIZONTAL;
        panelOtro.add(avisoOtro, co);
        panelOtro.setVisible(false);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        entradas.add(panelOtro, c);

        // Fila 3 en adelante: resto de los campos
        agregar(entradas, c, 3, "Valor comercial (USD)", valorComercial);
        agregar(entradas, c, 4, "Incrementables (USD)", incrementables);
        agregar(entradas, c, 5, "Tipo de cambio (MXN/USD)", tipoCambio);
        agregar(entradas, c, 6, "Cuota compensatoria (MXN)", cuotaCompensatoria);
        agregar(entradas, c, 7, "Tasa DTA", tasaDta);
        agregar(entradas, c, 8, "Tasa IVA", tasaIva);
        agregar(entradas, c, 9, "", dtaFijo);
        agregar(entradas, c, 10, "DTA fijo (MXN)", importeDtaFijo);

        JButton limpiar = new JButton("Limpiar");
        limpiar.addActionListener(e -> limpiar());
        JButton calcular = new JButton("Calcular");
        calcular.addActionListener(e -> calcular());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(limpiar);
        acciones.add(calcular);

        c.gridx = 0; c.gridy = 11; c.gridwidth = 2;
        c.weightx = 1; c.anchor = GridBagConstraints.EAST; c.fill = GridBagConstraints.NONE;
        entradas.add(acciones, c);

        // Relleno: si sobra espacio, se queda abajo en lugar de comprimir los campos.
        c.gridx = 0; c.gridy = 12; c.gridwidth = 2;
        c.weightx = 1; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        entradas.add(Box.createGlue(), c);

        // Barra de desplazamiento propia (vertical y horizontal) para "Datos de la operación".
        JScrollPane scrollEntradas = desplazable(entradas);
        scrollEntradas.setBorder(BorderFactory.createTitledBorder("Datos de la operación"));
        scrollEntradas.getVerticalScrollBar().setUnitIncrement(16);
        scrollEntradas.getHorizontalScrollBar().setUnitIncrement(16);

        JPanel salidas = new JPanel(new GridBagLayout());
        salidas.setBorder(BorderFactory.createTitledBorder("Resultado en moneda nacional"));
        GridBagConstraints r = restricciones();
        agregar(salidas, r, 0, "Valor en aduana (VA)", va);
        agregar(salidas, r, 1, "IGI", igi);
        agregar(salidas, r, 2, "DTA", dta);
        agregar(salidas, r, 3, "Base de IVA", baseIva);
        agregar(salidas, r, 4, "IVA", iva);
        total.setFont(total.getFont().deriveFont(Font.BOLD, 18f));
        agregar(salidas, r, 5, "Total de contribuciones", total);

        // Fecha a la que están atadas las reglas (mismo aviso de la pantalla de inicio).
        JLabel notaFecha = new JLabel("<html><div style='width:380px'>"
            + "<b>Fecha de referencia de las reglas:</b> " + FECHA_REGLAS + ". "
            + "Las fracciones, aranceles (IGI), tasas de DTA e IVA y demás reglas usadas en este cálculo "
            + "corresponden a esa fecha. </div></html>");
        r.gridx = 0; r.gridy = 6; r.gridwidth = 2; r.weightx = 1; r.weighty = 0;
        r.anchor = GridBagConstraints.NORTHWEST; r.fill = GridBagConstraints.HORIZONTAL;
        salidas.add(notaFecha, r);

        // Botón para sacar este panel a una ventana aparte (y regresarlo).
        JButton separar = new JButton("Separar ventana");
        r.gridx = 0; r.gridy = 7; r.gridwidth = 2; r.weighty = 0;
        r.anchor = GridBagConstraints.EAST; r.fill = GridBagConstraints.NONE;
        salidas.add(separar, r);

        r.gridx = 0; r.gridy = 8; r.gridwidth = 2; r.weighty = 1;
        r.anchor = GridBagConstraints.NORTHWEST; r.fill = GridBagConstraints.HORIZONTAL;
        error.setForeground(new Color(170, 0, 0));
        salidas.add(error, r);

        // Marcador que ocupa el lugar del panel mientras está en ventana aparte.
        JPanel marcador = new JPanel(new GridBagLayout());
        marcador.setBorder(BorderFactory.createTitledBorder("Resultado en moneda nacional"));
        JButton acoplar = new JButton("Acoplar aquí");
        GridBagConstraints m = new GridBagConstraints();
        m.gridx = 0; m.gridy = 0; m.insets = new Insets(6, 8, 6, 8);
        marcador.add(new JLabel("Los resultados están en una ventana separada."), m);
        m.gridy = 1;
        marcador.add(acoplar, m);

        final JFrame[] ventana = new JFrame[1];
        final Runnable[] alternar = new Runnable[1];
        alternar[0] = () -> {
            if (ventana[0] == null) {
                centro.remove(salidas);
                centro.add(marcador);
                centro.revalidate();
                centro.repaint();

                JFrame f = new JFrame("Resultado en moneda nacional");
                f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                f.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override public void windowClosing(java.awt.event.WindowEvent e) {
                        alternar[0].run();
                    }
                });
                JPanel contenido = new JPanel(new BorderLayout());
                contenido.setBorder(new EmptyBorder(8, 8, 8, 8));
                contenido.add(salidas, BorderLayout.CENTER);
                f.setContentPane(contenido);
                separar.setText("Acoplar a la ventana");
                f.setMinimumSize(new Dimension(460, 380));
                f.pack();
                f.setSize(Math.max(f.getWidth(), 520), Math.max(f.getHeight(), 420));
                f.setLocation(getX() + getWidth() - f.getWidth() - 20, getY() + 60);
                ventana[0] = f;
                f.setVisible(true);
            } else {
                JFrame f = ventana[0];
                ventana[0] = null;
                f.getContentPane().remove(salidas);
                f.dispose();
                separar.setText("Separar ventana");
                centro.remove(marcador);
                centro.add(salidas);
                centro.revalidate();
                centro.repaint();
            }
        };
        separar.addActionListener(e -> alternar[0].run());
        acoplar.addActionListener(e -> alternar[0].run());

        centro.add(scrollEntradas);
        centro.add(salidas);
        panel.add(centro, BorderLayout.CENTER);

        JPanel detalle = new JPanel(new BorderLayout(6, 6));
        detalle.setBorder(BorderFactory.createTitledBorder("Producto y requisito orientativo"));
        detalle.add(descripcion, BorderLayout.NORTH);
        detalle.add(new JScrollPane(requisitos), BorderLayout.CENTER);
        panel.add(detalle, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelLegal() {
        JTextArea texto = area();
        texto.setText("MARCO LEGAL DEL EJERCICIO\n\n"
            + "• Ley Aduanera: arts. 52 (sujetos obligados), 64 y 65 (valor en aduana y ajustes/incrementables).\n"
            + "• Ley de los Impuestos Generales de Importación y de Exportación (LIGIE) y su Tarifa (TIGIE): "
            + "fracción arancelaria y arancel aplicable.\n"
            + "• Ley de Comercio Exterior: art. 12 (tipos de arancel) y art. 63 (cuotas compensatorias).\n"
            + "• Ley Federal de Derechos: art. 49 (DTA). Esta práctica usa 8 al millar; la ley contempla "
            + "supuestos y cuotas particulares.\n"
            + "• Ley del IVA: art. 27 (base del IVA en importación). La práctica usa la tasa general de 16%.\n"
            + "• Ley del IEPS y Ley Federal del ISAN: sólo se agregarían si la mercancía y el supuesto legal los causan.\n\n"
            + "CONCEPTOS PRINCIPALES\n\n"
            + "VA — Valor en Aduana: Es el valor que se utiliza como referencia para calcular determinadas contribuciones de la importación. En esta aplicación se obtiene sumando el valor comercial y los incrementables y convirtiéndolos a pesos con el tipo de cambio.\n\n"
            + "IGI — Impuesto General de Importación: Es el impuesto que puede cobrarse por introducir una mercancía a México. Su cálculo depende de la fracción arancelaria y del arancel que corresponda al producto.\n\n"
            + "Ad valorem del producto: Es el porcentaje de arancel que se aplica al valor en aduana para calcular el IGI. Por ejemplo, 0.15 representa un 15%.\n\n"
            + "DTA — Derecho de Trámite Aduanero: Es un derecho relacionado con el trámite aduanero de la operación de importación. En este ejercicio se calcula mediante la tasa indicada o, cuando corresponde, mediante un importe fijo.\n\n"
            + "Base IVA: Es la cantidad sobre la cual se calcula el IVA de la importación. En esta aplicación se obtiene a partir del VA y de las contribuciones consideradas en el ejercicio, como IGI, DTA y cuota compensatoria.\n\n"
            + "Cuota compensatoria: Es un pago adicional que puede aplicarse a determinadas mercancías importadas cuando existe una cuota compensatoria vigente para ese producto y supuesto, por ejemplo como medida de defensa comercial.\n\n"
            + "IVA — Impuesto al Valor Agregado: Es el impuesto que se genera al importar mercancías y que se calcula aplicando la tasa correspondiente sobre la base de IVA.\n\n"
            + "Tasa IVA: Es el porcentaje que se aplica a la Base IVA para obtener el importe de IVA. En este ejercicio se utiliza una tasa de 16%.\n\n"
            + "Total: Cantidad total de contribuciones/impuestos a pagar por la importación\n\n"
            + "FÓRMULAS IMPLEMENTADAS\n\n"
            + "VA = (Valor comercial USD + Incrementables USD) × Tipo de cambio\n"
            + "IGI = VA × ad valorem del producto\n"
            + "DTA = VA × 0.008, o importe fijo si se selecciona\n"
            + "Base IVA = VA + IGI + DTA + Cuota compensatoria\n"
            + "IVA = Base IVA × tasa IVA\n" 
            + "Total = IGI + DTA + Cuota compensatoria + IVA\n\n"
            + "AVISO: Aplicación académica. Las tasas de IGI son las del archivo proporcionado. "
            + "Para una importación real deben confirmarse la fracción, arancel vigente, origen, tratado, "
            + "NOM, permisos, cuotas compensatorias y reglas ante las autoridades competentes."
            );
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.add(new JScrollPane(texto), BorderLayout.CENTER);
        return panel;
    }

    /** Recarga el combo de productos según la categoría seleccionada. */
    private void actualizarProductosPorCategoria() {
        String cat = (String) categoria.getSelectedItem();
        producto.removeAllItems();
        if (cat == null) return;
        for (Producto p : CATALOGO.getOrDefault(cat, List.of())) {
            producto.addItem(p);
        }
        producto.addItem(OTRO); // siempre al final de cada categoría
        if (producto.getItemCount() > 0) producto.setSelectedIndex(0);
    }

    /** Indica si el producto seleccionado es "Otro". */
    private boolean esOtro() {
        return producto.getSelectedItem() == OTRO;
    }

    /** Devuelve el ad valorem efectivo (del catálogo o manual si es "Otro"). */
    private double adValoremEfectivo() {
        if (esOtro()) {
            return numero(adValoremManual);
        }
        Producto p = (Producto) producto.getSelectedItem();
        return p == null ? 0 : p.adValorem();
    }

    /** Devuelve la fracción efectiva (del catálogo o manual si es "Otro"). */
    private String fraccionEfectiva() {
        if (esOtro()) return fraccionManual.getText().trim();
        Producto p = (Producto) producto.getSelectedItem();
        return p == null ? "" : p.fraccion();
    }

    private void actualizarProducto() {
        Producto p = (Producto) producto.getSelectedItem();
        if (p == null) return;

        boolean otro = esOtro();
        panelOtro.setVisible(otro);
        fraccionManual.setEnabled(otro);
        adValoremManual.setEnabled(otro);
        descripcionManual.setEnabled(otro);

        if (otro) {
            descripcion.setText("<html><b>Producto personalizado</b> | "
                + "Capture fracción, ad valorem y descripción en el panel superior.</html>");
            requisitos.setText(OTRO.requisitos());
        } else {
            descripcion.setText("<html><b>" + p.nombre() + "</b> | Fracción: " + p.fraccion()
                + " | Ad valorem: " + String.format("%.0f%%", p.adValorem() * 100)
                + "<br>" + p.descripcion() + "</html>");
            requisitos.setText(p.requisitos());
        }
        revalidate();
        repaint();
    }

    private void calcular() {
        try {
            Producto p = (Producto) producto.getSelectedItem();
            if (p == null) return;

            double vc = numero(valorComercial);
            double inc = numero(incrementables);
            double tc = numero(tipoCambio);
            double cc = numero(cuotaCompensatoria);
            double tasaD = numero(tasaDta);
            double tasaV = numero(tasaIva);

            if (vc < 0 || inc < 0 || tc <= 0 || cc < 0 || tasaD < 0 || tasaV < 0)
                throw new NumberFormatException();

            double adVal = adValoremEfectivo();
            if (esOtro() && (fraccionEfectiva().isBlank() || adVal < 0))
                throw new NumberFormatException();

            double valorAduana = (vc + inc) * tc;
            double impuestoIgi = valorAduana * adVal;
            double derechoDta = dtaFijo.isSelected() ? numero(importeDtaFijo) : valorAduana * tasaD;
            if (derechoDta < 0) throw new NumberFormatException();

            double base = valorAduana + impuestoIgi + derechoDta + cc;
            double impuestoIva = base * tasaV;

            va.setText(MONEDA.format(valorAduana));
            igi.setText(MONEDA.format(impuestoIgi));
            dta.setText(MONEDA.format(derechoDta));
            baseIva.setText(MONEDA.format(base));
            iva.setText(MONEDA.format(impuestoIva));
            total.setText(MONEDA.format(impuestoIgi + derechoDta + cc + impuestoIva));
            error.setText(" ");
        } catch (RuntimeException ex) {
            error.setText("Revise los importes: use números no negativos y tipo de cambio mayor que cero.");
        }
    }

    private void limpiar() {
        valorComercial.setText("0.00");
        incrementables.setText("0.00");
        tipoCambio.setText("19.95");
        cuotaCompensatoria.setText("0.00");
        tasaDta.setText("0.008");
        tasaIva.setText("0.16");
        importeDtaFijo.setText("0.00");
        dtaFijo.setSelected(false);
        fraccionManual.setText("0000.00.00");
        adValoremManual.setText("0.00");
        descripcionManual.setText("");
        calcular();
    }

    private static JTextField campo(String valor) {
        JTextField t = new JTextField(valor, 14);
        t.setHorizontalAlignment(JTextField.RIGHT);
        return t;
    }

    private static JTextArea area() {
        JTextArea a = new JTextArea(5, 30);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBackground(UIManager.getColor("Panel.background"));
        return a;
    }

    private static JLabel resultado() {
        JLabel l = new JLabel(MONEDA.format(0));
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    private static GridBagConstraints restricciones() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    private static void agregar(JPanel panel, GridBagConstraints c, int fila,
                                String etiqueta, JComponent componente) {
        c.gridx = 0; c.gridy = fila; c.gridwidth = 1;
        c.weightx = 0; c.fill = GridBagConstraints.NONE;
        JLabel texto = new JLabel(etiqueta);
        String ayuda = tooltipPara(etiqueta);
        if (ayuda != null) texto.setToolTipText(ayuda);
        panel.add(texto, c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(componente, c);
    }

    private static String tooltipPara(String etiqueta) {
        if (etiqueta == null) return null;
        if (etiqueta.startsWith("Fracción arancelaria"))
            return "Es el código numérico que identifica exactamente la mercancía en la tarifa aduanera. Ayuda a determinar qué impuesto y regulaciones le corresponden.";
        if (etiqueta.startsWith("Ad valorem"))
            return "Es el porcentaje de impuesto que se aplica al valor en aduana de la mercancía. Por ejemplo, 0.15 equivale a 15%.";
        if (etiqueta.startsWith("Valor comercial"))
            return "Es el precio de la mercancía expresado en dólares estadounidenses, según la operación de compra o el valor que corresponda acreditar.";
        if (etiqueta.startsWith("Incrementables"))
            return "Son gastos que, cuando la ley indica que deben sumarse, aumentan el valor en aduana; por ejemplo ciertos fletes, seguros, envases o gastos relacionados con la importación.";
        if (etiqueta.startsWith("Cuota compensatoria"))
            return "Es un pago adicional que puede aplicarse a determinadas mercancías importadas cuando existe una cuota compensatoria vigente para ese producto y supuesto.";
        if (etiqueta.startsWith("Tasa DTA"))
            return "Es la tasa usada en este ejercicio para calcular el Derecho de Trámite Aduanero (DTA), una contribución relacionada con los trámites aduaneros.";
        if (etiqueta.startsWith("Tasa IVA"))
            return "Es el porcentaje del Impuesto al Valor Agregado que se aplica a la base correspondiente de la importación. En este ejercicio se usa 16%.";
        if (etiqueta.startsWith("DTA fijo"))
            return "Es un importe fijo de DTA en pesos que sustituye el cálculo mediante la tasa DTA cuando el supuesto del ejercicio así lo requiere.";
        if (etiqueta.startsWith("Tipo de cambio"))
            return "Es editable para el cambio de valor";
        if (etiqueta.startsWith("Total de contribuciones"))
            return "Total representa la suma de los impuestos y contribuciones de importación que calcula la aplicación, cantidad total de contribuciones/impuestos a pagar por la importación";
        return null;
    }

    private static double numero(JTextField t) {
        return Double.parseDouble(t.getText().trim().replace(",", ""));
    }

    private void conectarEventos() {
        categoria.addActionListener(e -> {
            actualizarProductosPorCategoria();
            actualizarProducto();
            calcular();
        });

        producto.addActionListener(e -> {
            actualizarProducto();
            calcular();
        });

        dtaFijo.addActionListener(e -> {
            importeDtaFijo.setEnabled(dtaFijo.isSelected());
            tasaDta.setEnabled(!dtaFijo.isSelected());
            calcular();
        });

        DocumentListener escucha = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { calcular(); }
            public void removeUpdate(DocumentEvent e)  { calcular(); }
            public void changedUpdate(DocumentEvent e) { calcular(); }
        };

        for (JTextField t : List.of(valorComercial, incrementables, tipoCambio,
                cuotaCompensatoria, tasaDta, tasaIva, importeDtaFijo,
                fraccionManual, adValoremManual, descripcionManual)) {
            t.getDocument().addDocumentListener(escucha);
        }

        importeDtaFijo.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculadoraImportacionApp().setVisible(true));
    }
}