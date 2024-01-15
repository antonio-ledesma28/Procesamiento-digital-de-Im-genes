// Antonio Yamir Ledesma Briones
package com.antonio.programa5;

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
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Programa5 extends JFrame {

    private JButton openButton;
    private JLabel imageLabel;
    private JFrame colorFrame;
    private JPanel colorPanel;
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

    private HistogramFrame histogramFrame;  // Agregada instancia de HistogramFrame

    public Programa5() {
        // Configurar la ventana principal con tamaño inicial de 400x400
        setTitle("Aplicación 5 PDI");
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

        // Agregar el botón y el JLabel a la ventana principal
        getContentPane().add(openButton, "North");
        getContentPane().add(imageLabel, "Center");

        // Mostrar la ventana principal
        setVisible(true);
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
        SwingUtilities.invokeLater(() -> new Programa5());
    }
}
