//Antonio Yamir Ledesma Briones
package com.antonio.programa3;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Programa3 extends JFrame {
    private JButton openButton;
    private JLabel imageLabel;
    private JLabel sizeLabel; // Etiqueta para mostrar el tamaño
    private JLabel statusBar; // Barra de estado para mostrar información adicional
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

    public Programa3() {
        // Configurar la ventana principal con tamaño inicial de 400x400
        setTitle("Aplicación 3 PDI");
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

                    // Mostrar la imagen en la ventana
                    displayImage(selectedFile);
                }
            }
        });

        // Crear el JLabel para mostrar la imagen
        imageLabel = new JLabel();

        // Crear el JLabel para mostrar el tamaño
        sizeLabel = new JLabel("Tamaño: ");

        // Crear la barra de estado
        statusBar = new JLabel(" ");
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());

        // Agregar el botón y los JLabel a la ventana
        getContentPane().add(openButton, "North");
        getContentPane().add(imageLabel, "Center");
        getContentPane().add(sizeLabel, "South");

        // Agregar la barra de estado al sur de la ventana
        getContentPane().add(statusBar, "South");

        // Mostrar la ventana
        setVisible(true);
    }

    //Método para mostrar la imagen en la ventana.
    private void displayImage(File file) {
        try {
            // Leer la imagen desde el archivo seleccionado
            BufferedImage image = ImageIO.read(file);

            // Calcular el factor de escala
            double scale = Math.min((double) MAX_WIDTH / image.getWidth(), (double) MAX_HEIGHT / image.getHeight());

            // Redimensionar la imagen proporcionalmente
            int newWidth = (int) (image.getWidth() * scale);
            int newHeight = (int) (image.getHeight() * scale);

            // Crear un ImageIcon redimensionando la imagen
            ImageIcon icon = new ImageIcon(image.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH));

            // Establecer la imagen en el JLabel
            imageLabel.setIcon(icon);

            // Mostrar el tamaño de la imagen en la etiqueta
            sizeLabel.setText("Tamaño: " + newWidth + " x " + newHeight);

            // Ajustar el tamaño de la ventana según el tamaño de la imagen, con límite de 800x800
            int windowWidth = Math.min(newWidth, MAX_WIDTH);
            int windowHeight = Math.min(newHeight, MAX_HEIGHT);
            setSize(windowWidth, windowHeight);

            // Actualizar la barra de estado con información adicional
            updateStatusBar(image.getWidth(), image.getHeight(), windowWidth, windowHeight, file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatusBar(int originalWidth, int originalHeight, int newWidth, int newHeight, String filePath) {
        int totalPixels = newWidth * newHeight;
        String statusText = "Original: " + originalWidth + " x " + originalHeight +
                            " | Redimensionado: " + newWidth + " x " + newHeight +
                            " | Total de píxeles: " + totalPixels +
                            " | Ruta: " + filePath;
        statusBar.setText(statusText);
    }


    //Método principal para iniciar la aplicación.
    public static void main(String[] args) {
        // Crear y mostrar la ventana principal
        SwingUtilities.invokeLater(() -> new Programa3());
    }
}
