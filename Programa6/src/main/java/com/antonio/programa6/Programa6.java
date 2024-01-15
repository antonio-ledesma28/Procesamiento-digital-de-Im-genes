// Antonio Yamir Ledesma Briones
package com.antonio.programa6;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.io.File;
import javax.imageio.ImageIO;

public class Programa6 extends JFrame {

    private JButton openButton;
    private JLabel imageLabel;
    private JFrame colorFrame;
    private JPanel colorPanel;
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

    private HistogramFrame histogramFrame;  // Agregada instancia de HistogramFrame

    private JMenuItem adjustBrightnessMenuItem;
    private JMenuItem negativeImageMenuItem;

    public Programa6() {
        // Configurar la ventana principal con tamaño inicial de 400x400
        setTitle("Aplicación 6 PDI");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);

        // Crear el botón "Abrir"
        openButton = new JButton("Abrir");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Crear un objeto JFileChooser
                JFileChooser fileChooser = new JFileChooser();

                // Agregar un filtro para solo permitir archivos de imagen
                FileFilter imageFilter = new FileNameExtensionFilter("Archivos de imagen", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(imageFilter);

                // Mostrar el cuadro de diálogo "Abrir archivo"
                int returnValue = fileChooser.showOpenDialog(null);

                // Comprobar si se seleccionó un archivo
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());

                    // Mostrar la imagen en la ventana y actualizar el histograma
                    displayImage(selectedFile);
                    updateHistogram(selectedFile);
                }
            }
        });

        // Crear el JLabel para mostrar la imagen
        imageLabel = new JLabel();

        // Crear la ventana de color
        createColorWindow();

        // Crear la instancia de HistogramFrame
        histogramFrame = new HistogramFrame("Histograma RGB");

        // Agregar un MouseListener al JLabel para capturar eventos del mouse
        imageLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Obtener la posición del cursor en relación con el JLabel
                int x = e.getX();
                int y = e.getY();

                // Obtener el color y el valor RGB del píxel en esa posición
                Color pixelColor = getColorAtPixel(x, y);

                // Mostrar la información en la consola
                System.out.println("Posición: (" + x + ", " + y + ")");
                System.out.println("Color: " + pixelColor);

                // Actualizar el color del JPanel en la ventana de color
                colorPanel.setBackground(pixelColor);

                // Actualizar la ventana de color
                colorFrame.repaint();

                // Actualizar el histograma
                updateHistogramData(pixelColor);
            }
        });

        // Crear la barra de menú
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Crear el menú "Operaciones"
        JMenu operationsMenu = new JMenu("Operaciones");
        menuBar.add(operationsMenu);

        // Crear las opciones del menú
        adjustBrightnessMenuItem = new JMenuItem("Ajustar Brillo");
        negativeImageMenuItem = new JMenuItem("Negativo");

        // Agregar ActionListener a las opciones del menú
        adjustBrightnessMenuItem.addActionListener(e -> showBrightnessDialog());
        negativeImageMenuItem.addActionListener(e -> performOperation(2)); // 2 representa negativo

        // Agregar las opciones al menú
        operationsMenu.add(adjustBrightnessMenuItem);
        operationsMenu.add(negativeImageMenuItem);

        // Agregar el botón y el JLabel a la ventana principal
        getContentPane().add(openButton, "North");
        getContentPane().add(imageLabel, "Center");

        // Mostrar la ventana principal
        setVisible(true);
    }

    private void showBrightnessDialog() {
        JDialog brightnessDialog = new JDialog(this, "Ajustar Brillo", true);
        brightnessDialog.setSize(300, 200);
        brightnessDialog.setLocationRelativeTo(this);

        JSlider brightnessSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 100);
        brightnessSlider.setMajorTickSpacing(25);
        brightnessSlider.setMinorTickSpacing(5);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);

        JLabel valueLabel = new JLabel("Valor: " + brightnessSlider.getValue() + "%");

        brightnessSlider.addChangeListener(e -> valueLabel.setText("Valor: " + brightnessSlider.getValue() + "%"));

        JButton applyButton = new JButton("Aplicar");
        applyButton.addActionListener(e -> {
            double factor = brightnessSlider.getValue() / 100.0;
            showImageWithBrightnessAdjustment(factor);

            brightnessDialog.dispose();
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        panel.add(new JLabel("Ajustar Brillo:"));
        panel.add(brightnessSlider);
        panel.add(valueLabel);
        panel.add(applyButton);

        brightnessDialog.add(panel);

        brightnessDialog.setVisible(true);
    }

    private void showImageWithBrightnessAdjustment(double factor) {
    ImageIcon currentImageIcon = (ImageIcon) imageLabel.getIcon();
    BufferedImage adjustedImage = adjustBrightness(currentImageIcon, factor);

    JFrame resultFrame = new JFrame("Resultado");
    resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    resultFrame.setSize(adjustedImage.getWidth(), adjustedImage.getHeight());

    // Obtener la posición de la ventana principal
    Point mainFrameLocation = getLocation();

    // Establecer la posición de la ventana de resultado un poco más abajo que la ventana principal
    resultFrame.setLocation(mainFrameLocation.x, mainFrameLocation.y + 300);

    JLabel resultImageLabel = new JLabel();
    resultImageLabel.setIcon(new ImageIcon(adjustedImage));

    resultFrame.getContentPane().add(resultImageLabel);

    // Asegurarse de que la ventana no esté minimizada
    resultFrame.setExtendedState(JFrame.NORMAL);
    resultFrame.setVisible(true);

    // Hacer que la ventana de resultado esté en primer plano
    resultFrame.toFront();
}

    private void createColorWindow() {
        // Crear la ventana de color
        colorFrame = new JFrame("Color RGB");
        colorFrame.setSize(400, 400);

        // Obtener la posición de la ventana principal
        int mainFrameX = getLocation().x;
        int mainFrameY = getLocation().y;

        // Establecer la posición de la ventana de color al lado izquierdo de la ventana principal
        colorFrame.setLocation(mainFrameX - colorFrame.getWidth(), mainFrameY);

        colorFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Crear un JPanel para mostrar el color RGB
        colorPanel = new JPanel();
        colorFrame.getContentPane().add(colorPanel);

        // Mostrar la ventana de color
        colorFrame.setVisible(true);
    }

    private void performOperation(int operationType) {
        // Obtener la imagen actual
        ImageIcon currentImageIcon = (ImageIcon) imageLabel.getIcon();

        // Crear una nueva ventana con la imagen resultante de la operación seleccionada
        JFrame resultFrame = new JFrame("Resultado");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // No cierra la aplicación completa
        resultFrame.setSize(currentImageIcon.getIconWidth(), currentImageIcon.getIconHeight());
        resultFrame.setLocationRelativeTo(this); // Centrar la nueva ventana respecto a la ventana principal

        JLabel resultImageLabel = new JLabel();

        // Realizar la operación seleccionada
        BufferedImage resultImage = null;
        switch (operationType) {
            case 2: // Negativo
                resultImage = getNegativeImage(currentImageIcon);
                break;
        }

        // Mostrar la imagen resultante en la nueva ventana
        if (resultImage != null) {
            resultImageLabel.setIcon(new ImageIcon(resultImage));
            resultFrame.getContentPane().add(resultImageLabel);
            resultFrame.setVisible(true);
        }
    }

    private BufferedImage adjustBrightness(ImageIcon originalIcon, double factor) {
        // Obtener la imagen del ImageIcon original
        Image originalImage = originalIcon.getImage();

        // Crear un BufferedImage a partir de la imagen original
        BufferedImage bufferedImage = new BufferedImage(
                originalImage.getWidth(null),
                originalImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Crear un objeto Graphics para dibujar la imagen en el BufferedImage
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        // Aplicar la operación de ajuste de brillo
        RescaleOp op = new RescaleOp((float) factor, 0, null);
        bufferedImage = op.filter(bufferedImage, null);

        return bufferedImage;
    }

    private BufferedImage getNegativeImage(ImageIcon originalIcon) {
        // Obtener la imagen del ImageIcon original
        Image originalImage = originalIcon.getImage();

        // Crear un BufferedImage a partir de la imagen original
        BufferedImage bufferedImage = new BufferedImage(
                originalImage.getWidth(null),
                originalImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Crear un objeto Graphics para dibujar la imagen en el BufferedImage
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        // Aplicar la operación de obtener el negativo
        RescaleOp op = new RescaleOp(-1.0f, 255f, null);
        bufferedImage = op.filter(bufferedImage, null);

        return bufferedImage;
    }

    private void updateHistogramData(Color color) {
        // Actualizar el dataset del histograma con los valores de r, g y b
        histogramFrame.updateHistogramData(color);

        // Mostrar la ventana del histograma si no está visible
        if (!histogramFrame.isVisible()) {
            histogramFrame.setVisible(true);
        }
    }

    private Color getColorAtPixel(int x, int y) {
        // Obtener la imagen del ImageIcon
        Image img = ((ImageIcon) imageLabel.getIcon()).getImage();

        // Crear un PixelGrabber para obtener el valor RGB del píxel
        PixelGrabber pg = new PixelGrabber(img, x, y, 1, 1, new int[1], 0, 1);
        try {
            if (pg.grabPixels()) {
                // Obtener el valor RGB del píxel
                int rgb = (int) ((int[]) pg.getPixels())[0];

                // Crear un objeto Color a partir del valor RGB
                return new Color(rgb);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void displayImage(File file) {
        try {
            // Leer la imagen desde el archivo seleccionado
            BufferedImage image = ImageIO.read(file);

            // Calcular el factor de escala
            double scale = Math.min((double) MAX_WIDTH / image.getWidth(), (double) MAX_HEIGHT / image.getHeight());

            // Redimensionar la imagen proporcionalmente
            int newWidth = (int) (image.getWidth() * scale);
            int newHeight = (int) (image.getHeight() * scale);

            // Obtener la altura del botón
            int buttonHeight = openButton.getPreferredSize().height;

            // Ajustar el tamaño de la ventana según el tamaño de la imagen y la altura del botón
            int windowWidth = Math.min(newWidth, MAX_WIDTH);
            int extraHeight = 40;  // Valor adicional para aumentar la altura
            int windowHeight = Math.min(newHeight + buttonHeight + extraHeight, MAX_HEIGHT);
            setSize(windowWidth, windowHeight);

            // Crear un ImageIcon redimensionando la imagen
            ImageIcon icon = new ImageIcon(image.getScaledInstance(windowWidth, newHeight, BufferedImage.SCALE_SMOOTH));

            // Establecer la imagen en el JLabel
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHistogram(File selectedFile) {
        try {
            // Leer la imagen desde el archivo seleccionado
            BufferedImage image = ImageIO.read(selectedFile);

            // Obtener el color del píxel en la esquina superior izquierda
            Color initialColor = new Color(image.getRGB(0, 0));

            // Actualizar el histograma
            updateHistogramData(initialColor);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen para el histograma.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Crear y mostrar la ventana principal
        SwingUtilities.invokeLater(() -> new Programa6());
    }
}
