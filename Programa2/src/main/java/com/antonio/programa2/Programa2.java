//Antonio Yamir Ledesma Briones

package com.antonio.programa2;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Programa2 extends JFrame {
    private JButton openButton;
    private JLabel imageLabel;
    private JLabel sizeLabel; // Nueva etiqueta para mostrar el tamaño

    public Programa2() {
        // Configurar la ventana principal
        setTitle("Aplicación 2 PDI");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

                    // Mostrar la imagen en la ventana y actualizar el tamaño
                    displayImage(selectedFile);
                }
            }
        });

        // Crear el JLabel para mostrar la imagen
        imageLabel = new JLabel();

        // Crear el JLabel para mostrar el tamaño
        sizeLabel = new JLabel("Tamaño: ");

        // Agregar el botón y los JLabel a la ventana
        getContentPane().add(openButton, "North");
        getContentPane().add(imageLabel, "Center");
        getContentPane().add(sizeLabel, "South");

        // Mostrar la ventana
        setVisible(true);
    }

    private void displayImage(File file) {
        try {
            // Leer la imagen desde el archivo seleccionado
            BufferedImage image = ImageIO.read(file);

            // Obtener las dimensiones de la imagen
            int width = image.getWidth();
            int height = image.getHeight();

            // Mostrar el tamaño de la imagen en la etiqueta
            sizeLabel.setText("Tamaño: " + width + " x " + height);

            // Redimensionar la imagen si es necesario
            ImageIcon icon = new ImageIcon(image.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), BufferedImage.SCALE_SMOOTH));

            // Mostrar la imagen en el JLabel
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Crear y mostrar la ventana principal
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Programa2();
            }
        });
    }
}
