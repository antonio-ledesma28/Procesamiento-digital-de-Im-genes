//Antonio Yamir Ledesma Briones

package com.antonio.programa8;

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

public class Programa8 extends JFrame {

    // Declaración de componentes de la interfaz gráfica
    private JButton openButton;
    private JLabel imageLabel;
    private JFrame colorFrame;
    private JPanel colorPanel;
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

    private HistogramFrame histogramFrame;

    // Declaración de elementos del menú
    private JMenuItem adjustBrightnessMenuItem;
    private JMenuItem negativeImageMenuItem;
    private JMenuItem borderFilterMenuItem;
    private JMenuItem sobelHorizontalMenuItem;
    private JMenuItem sobelVerticalMenuItem;
    private JMenuItem prewittHorizontalMenuItem;
    private JMenuItem prewittVerticalMenuItem;
    private JMenuItem laplacianFilterMenuItem;
    private JMenuItem customFilterMenuItem;
    private JDialog customFilterDialog;
    
    

    public Programa8() {
        // Configuración de la ventana principal
        setTitle("Aplicación 8 PDI");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuración del botón "Abrir"
        openButton = new JButton("Abrir");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Selector de archivos para abrir imágenes
                JFileChooser fileChooser = new JFileChooser();
                FileFilter imageFilter = new FileNameExtensionFilter("Archivos de imagen", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(imageFilter);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());
                    displayImage(selectedFile);
                    updateHistogram(selectedFile);
                }
            }
        });

        imageLabel = new JLabel();

        createColorWindow();

        histogramFrame = new HistogramFrame("Histograma RGB");

        // Manejador de eventos para el movimiento del ratón sobre la imagen
        imageLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Color pixelColor = getColorAtPixel(x, y);
                System.out.println("Posición: (" + x + ", " + y + ")");
                System.out.println("Color: " + pixelColor);
                colorPanel.setBackground(pixelColor);
                colorFrame.repaint();
                updateHistogramData(pixelColor);
            }
        });

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu operationsMenu = new JMenu("Configuraciones de Imagen");
        menuBar.add(operationsMenu);

        adjustBrightnessMenuItem = new JMenuItem("Ajustar Brillo");
        negativeImageMenuItem = new JMenuItem("Negativo");
        borderFilterMenuItem = new JMenuItem("Filtro de Borde");
        sobelHorizontalMenuItem = new JMenuItem("Sobel Horizontal");
        sobelVerticalMenuItem = new JMenuItem("Sobel Vertical");
        prewittHorizontalMenuItem = new JMenuItem("Prewitt Horizontal");
        prewittVerticalMenuItem = new JMenuItem("Prewitt Vertical");
        laplacianFilterMenuItem = new JMenuItem("Filtro Laplaciano");
        customFilterMenuItem  =new JMenuItem("Filtro Personalizado");

        // Asignación de manejadores de eventos para los elementos del menú
        adjustBrightnessMenuItem.addActionListener(e -> showBrightnessDialog());
        negativeImageMenuItem.addActionListener(e -> performOperation(2)); // 2 representa negativo
        borderFilterMenuItem.addActionListener(e -> performFilterOperation(1)); // 1 representa Filtro de Borde
        sobelHorizontalMenuItem.addActionListener(e -> performFilterOperation(2)); // 2 representa Sobel Horizontal
        sobelVerticalMenuItem.addActionListener(e -> performFilterOperation(3)); // 3 representa Sobel Vertical
        prewittHorizontalMenuItem.addActionListener(e -> performFilterOperation(4)); // 4 representa Prewitt Horizontal
        prewittVerticalMenuItem.addActionListener(e -> performFilterOperation(5)); // 5 representa Prewitt Vertical
        laplacianFilterMenuItem.addActionListener(e -> performFilterOperation(6)); // 6 representa Filtro Laplaciano
        customFilterMenuItem.addActionListener(e -> showCustomFilterDialog());

        // Agregación de elementos al menú
        operationsMenu.add(adjustBrightnessMenuItem);
        operationsMenu.add(negativeImageMenuItem);
        operationsMenu.add(borderFilterMenuItem);
        operationsMenu.add(sobelHorizontalMenuItem);
        operationsMenu.add(sobelVerticalMenuItem);
        operationsMenu.add(prewittHorizontalMenuItem);
        operationsMenu.add(prewittVerticalMenuItem);
        operationsMenu.add(laplacianFilterMenuItem);
        operationsMenu.add(customFilterMenuItem);

        // Adición de componentes a la interfaz principal
        getContentPane().add(openButton, "North");
        getContentPane().add(imageLabel, "Center");

        setVisible(true);
    }
    // Método para mostrar el cuadro de diálogo de ajuste de brillo
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
    // Método para mostrar la imagen con ajuste de brillo
    private void showImageWithBrightnessAdjustment(double factor) {
        ImageIcon currentImageIcon = (ImageIcon) imageLabel.getIcon();
        BufferedImage adjustedImage = adjustBrightness(imageIconToBufferedImage(currentImageIcon), factor);

        JFrame resultFrame = new JFrame("Resultado - Brillo Ajustado");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setSize(adjustedImage.getWidth(), adjustedImage.getHeight());
        resultFrame.setLocationRelativeTo(this);

        JLabel resultImageLabel = new JLabel();
        resultImageLabel.setIcon(new ImageIcon(adjustedImage));

        resultFrame.getContentPane().add(resultImageLabel);
        resultFrame.setVisible(true);
        resultFrame.toFront();
    }
    // Método para crear la ventana de color RGB
    private void createColorWindow() {
        colorFrame = new JFrame("Color RGB");
        colorFrame.setSize(400, 400);
        int mainFrameX = getLocation().x;
        int mainFrameY = getLocation().y;
        colorFrame.setLocation(mainFrameX - colorFrame.getWidth(), mainFrameY);
        colorFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        colorPanel = new JPanel();
        colorFrame.getContentPane().add(colorPanel);
        colorFrame.setVisible(true);
    }

    // Método para realizar operaciones como negativo, etc.
    private void performOperation(int operationType) {
        ImageIcon currentImageIcon = (ImageIcon) imageLabel.getIcon();
        JFrame resultFrame = null;

        switch (operationType) {
            case 2: // Negativo
                resultFrame = new JFrame("Resultado - Negativo");
                break;
        }

        if (resultFrame != null) {
            resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            resultFrame.setSize(currentImageIcon.getIconWidth(), currentImageIcon.getIconHeight());
            resultFrame.setLocationRelativeTo(this);

            JLabel resultImageLabel = new JLabel();
            BufferedImage resultImage = null;

            switch (operationType) {
                case 2: // Negativo
                    resultImage = getNegativeImage(currentImageIcon);
                    break;
            }

            if (resultImage != null) {
                resultImageLabel.setIcon(new ImageIcon(resultImage));
                resultFrame.getContentPane().add(resultImageLabel);
                resultFrame.setVisible(true);
                resultFrame.toFront();
            }
        }
    }

    private BufferedImage adjustBrightness(BufferedImage originalImage, double factor) {
        RescaleOp op = new RescaleOp((float) factor, 0, null);
        return op.filter(originalImage, null);
    }

    private BufferedImage getNegativeImage(ImageIcon originalIcon) {
        BufferedImage originalImage = imageIconToBufferedImage(originalIcon);
        RescaleOp op = new RescaleOp(-1.0f, 255f, null);
        return op.filter(originalImage, null);
    }

    private void performFilterOperation(int filterType) {
        ImageIcon currentImageIcon = (ImageIcon) imageLabel.getIcon();
        JFrame resultFrame = new JFrame();
        String frameTitle = "Resultado"; // Título por defecto
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setSize(currentImageIcon.getIconWidth(), currentImageIcon.getIconHeight());
        resultFrame.setLocationRelativeTo(this);
        JLabel resultImageLabel = new JLabel();
        BufferedImage resultImage = null;
        switch (filterType) {
            case 1: // Filtro de Borde
                frameTitle = "Resultado - Filtro de Borde";
                resultImage = applyBorderFilter(imageIconToBufferedImage(currentImageIcon));
                break;
            case 2: // Sobel Horizontal
                frameTitle = "Resultado - Sobel Horizontal";
                resultImage = applySobelHorizontalFilter(imageIconToBufferedImage(currentImageIcon));
                break;
            case 3: // Sobel Vertical
                frameTitle = "Resultado - Sobel Vertical";
                resultImage = applySobelVerticalFilter(imageIconToBufferedImage(currentImageIcon));
                break;
            case 4: // Prewitt Horizontal
                frameTitle = "Resultado - Prewitt Horizontal";
                resultImage = applyPrewittHorizontalFilter(imageIconToBufferedImage(currentImageIcon));
                break;
            case 5: // Prewitt Vertical
                frameTitle = "Resultado - Prewitt Vertical";
                resultImage = applyPrewittVerticalFilter(imageIconToBufferedImage(currentImageIcon));
                break;

            case 6: // Filtro Laplaciano
                frameTitle = "Resultado - Filtro Laplaciano";
                resultImage = applyLaplacianFilter(imageIconToBufferedImage(currentImageIcon));
                break;
            case 7:
                break;
        }
        resultFrame.setTitle(frameTitle); // Establecer el título del JFrame
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setSize(currentImageIcon.getIconWidth(), currentImageIcon.getIconHeight());
        resultFrame.setLocationRelativeTo(this);

        if (resultImage != null) {
            resultImageLabel.setIcon(new ImageIcon(resultImage));
            resultFrame.getContentPane().add(resultImageLabel);
            resultFrame.setVisible(true);
        }
    }

    private BufferedImage applyBorderFilter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int[][] neighbors = new int[3][3];
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        neighbors[i + 1][j + 1] = originalImage.getRGB(x + i, y + j) & 0xFF; // Get red channel
                    }
                }

                int gx = (neighbors[0][0] + 2 * neighbors[0][1] + neighbors[0][2])
                        - (neighbors[2][0] + 2 * neighbors[2][1] + neighbors[2][2]);

                int gy = (neighbors[0][0] + 2 * neighbors[1][0] + neighbors[2][0])
                        - (neighbors[0][2] + 2 * neighbors[1][2] + neighbors[2][2]);

                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
                int grayValue = Math.min(255, magnitude); // Ensure the value does not exceed 255

                int newPixel = (0xFF << 24) | (grayValue << 16) | (grayValue << 8) | grayValue; // Create new pixel value

                resultImage.setRGB(x, y, newPixel);
            }
        }

        return resultImage;
    }

    private BufferedImage applySobelHorizontalFilter(BufferedImage originalImage) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();

    BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int gx = (originalImage.getRGB(x - 1, y - 1) & 0xFF)
                    + 2 * (originalImage.getRGB(x - 1, y) & 0xFF)
                    + (originalImage.getRGB(x - 1, y + 1) & 0xFF)
                    - (originalImage.getRGB(x + 1, y - 1) & 0xFF)
                    - 2 * (originalImage.getRGB(x + 1, y) & 0xFF)
                    - (originalImage.getRGB(x + 1, y + 1) & 0xFF);

            int grayValue = Math.min(255, Math.abs(gx)); // Ensure the value does not exceed 255

            int newPixel = (0xFF << 24) | (grayValue << 16) | (grayValue << 8) | grayValue; // Create new pixel value

            resultImage.setRGB(x, y, newPixel);
        }
    }

    return resultImage;
}

private BufferedImage applySobelVerticalFilter(BufferedImage originalImage) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();

    BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int gy = (originalImage.getRGB(x - 1, y - 1) & 0xFF)
                    + 2 * (originalImage.getRGB(x, y - 1) & 0xFF)
                    + (originalImage.getRGB(x + 1, y - 1) & 0xFF)
                    - (originalImage.getRGB(x - 1, y + 1) & 0xFF)
                    - 2 * (originalImage.getRGB(x, y + 1) & 0xFF)
                    - (originalImage.getRGB(x + 1, y + 1) & 0xFF);

            int grayValue = Math.min(255, Math.abs(gy)); // Ensure the value does not exceed 255

            int newPixel = (0xFF << 24) | (grayValue << 16) | (grayValue << 8) | grayValue; // Create new pixel value

            resultImage.setRGB(x, y, newPixel);
        }
    }

    return resultImage;
}
    
    private BufferedImage applyPrewittHorizontalFilter(BufferedImage originalImage) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();

    BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int[][] neighbors = new int[3][3];
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    neighbors[i + 1][j + 1] = originalImage.getRGB(x + i, y + j) & 0xFF; // Get red channel
                }
            }

            int gx = (-1 * neighbors[0][0] + 0 * neighbors[0][1] + 1 * neighbors[0][2]) +
                     (-1 * neighbors[1][0] + 0 * neighbors[1][1] + 1 * neighbors[1][2]) +
                     (-1 * neighbors[2][0] + 0 * neighbors[2][1] + 1 * neighbors[2][2]);

            int grayValue = Math.min(255, Math.abs(gx)); // Ensure the value does not exceed 255

            int newPixel = (0xFF << 24) | (grayValue << 16) | (grayValue << 8) | grayValue; // Create new pixel value

            resultImage.setRGB(x, y, newPixel);
        }
    }

    return resultImage;
}

private BufferedImage applyPrewittVerticalFilter(BufferedImage originalImage) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();

    BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int[][] neighbors = new int[3][3];
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    neighbors[i + 1][j + 1] = originalImage.getRGB(x + i, y + j) & 0xFF; // Get red channel
                }
            }

            int gy = (-1 * neighbors[0][0] - 1 * neighbors[0][1] - 1 * neighbors[0][2]) +
                     (0 * neighbors[1][0] + 0 * neighbors[1][1] + 0 * neighbors[1][2]) +
                     (1 * neighbors[2][0] + 1 * neighbors[2][1] + 1 * neighbors[2][2]);

            int grayValue = Math.min(255, Math.abs(gy)); // Ensure the value does not exceed 255

            int newPixel = (0xFF << 24) | (grayValue << 16) | (grayValue << 8) | grayValue; // Create new pixel value

            resultImage.setRGB(x, y, newPixel);
        }
    }

    return resultImage;
}

    private BufferedImage applyLaplacianFilter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int[][] neighbors = new int[3][3];
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        neighbors[i + 1][j + 1] = originalImage.getRGB(x + i, y + j) & 0xFF; // Get red channel
                    }
                }

                int laplacian = 4 * neighbors[1][1] - (neighbors[0][1] + neighbors[1][0] + neighbors[1][2] + neighbors[2][1]);

                int grayValue = Math.min(255, Math.abs(laplacian)); // Ensure the value does not exceed 255

                int newPixel = (0xFF << 24) | (grayValue << 16) | (grayValue << 8) | grayValue; // Create new pixel value

                resultImage.setRGB(x, y, newPixel);
            }
        }

        return resultImage;
    }
    
private void showCustomFilterDialog() {
    String[] filterSizes = {"3x3", "5x5", "7x7"};
    JComboBox<String> sizeComboBox = new JComboBox<>(filterSizes);
    JTextField[][] filterValues = new JTextField[5][5];

    // Panel de cuadrícula que se actualizará según el tamaño seleccionado
    JPanel panel = new JPanel();

    // Manejador de eventos para actualizar la cuadrícula cuando se cambia el tamaño
    sizeComboBox.addActionListener(e -> {
        int sizeIndex = sizeComboBox.getSelectedIndex() + 3;
        updateFilterGrid(panel, filterValues, sizeIndex);
    });

    // Inicialmente, se muestra la cuadrícula para 3x3
    updateFilterGrid(panel, filterValues, 3);

    JButton applyButton = new JButton("Aplicar");
    applyButton.addActionListener(e -> {
        int sizeIndex = sizeComboBox.getSelectedIndex() + 3;
        int[][] customFilter = new int[sizeIndex][sizeIndex];

        for (int i = 0; i < sizeIndex; i++) {
            for (int j = 0; j < sizeIndex; j++) {
                try {
                    customFilter[i][j] = Integer.parseInt(filterValues[i][j].getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ingrese valores numéricos para el filtro.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        showImageWithCustomFilter(customFilter);
        customFilterDialog.dispose();
    });

    JPanel controlPanel = new JPanel(new FlowLayout());
    controlPanel.add(new JLabel("Tamaño del filtro:"));
    controlPanel.add(sizeComboBox);
    controlPanel.add(applyButton);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(controlPanel, BorderLayout.NORTH);
    mainPanel.add(panel, BorderLayout.CENTER);

    customFilterDialog = new JDialog(this, "Filtro Personalizado", true);
    customFilterDialog.setSize(400, 300);
    customFilterDialog.setLocationRelativeTo(this);
    customFilterDialog.add(mainPanel);

    customFilterDialog.setVisible(true);
}

private void updateFilterGrid(JPanel panel, JTextField[][] filterValues, int sizeIndex) {
    panel.removeAll(); // Limpiar el panel actual

    int gridSize = sizeIndex; // El tamaño de la cuadrícula ahora debe ser igual al tamaño del filtro

    // Configurar el nuevo diseño de la cuadrícula según el tamaño seleccionado
    panel.setLayout(new GridLayout(gridSize, gridSize));

    // Inicializar filterValues solo si aún no ha sido creado
    if (filterValues == null || filterValues.length != sizeIndex || filterValues[0].length != sizeIndex) {
        filterValues = new JTextField[sizeIndex][sizeIndex];
    }

    // Agregar las celdas al panel
    for (int i = 0; i < sizeIndex; i++) {
        for (int j = 0; j < sizeIndex; j++) {
            filterValues[i][j] = new JTextField();
            panel.add(filterValues[i][j]);
        }
    }

    // Repintar el panel
    panel.revalidate();
    panel.repaint();
}


// Añade el siguiente método para mostrar la imagen con el filtro personalizado:
private void showImageWithCustomFilter(int[][] customFilter) {
    ImageIcon currentImageIcon = (ImageIcon) imageLabel.getIcon();
    BufferedImage originalImage = imageIconToBufferedImage(currentImageIcon);

    BufferedImage resultImage = applyCustomFilter(originalImage, customFilter);

    JFrame resultFrame = new JFrame("Resultado - Filtro Personalizado");
    resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Obtener la ubicación actual de la ventana principal
    int mainFrameX = getLocation().x;
    int mainFrameY = getLocation().y;

    // Establecer la nueva ubicación más abajo
    int newFrameX = mainFrameX;
    int newFrameY = mainFrameY + getHeight();

    resultFrame.setLocation(newFrameX, newFrameY);
    resultFrame.setSize(resultImage.getWidth(), resultImage.getHeight());

    JLabel resultImageLabel = new JLabel();
    resultImageLabel.setIcon(new ImageIcon(resultImage));

    resultFrame.getContentPane().add(resultImageLabel);
    resultFrame.setVisible(true);
}

private BufferedImage applyCustomFilter(BufferedImage originalImage, int[][] customFilter) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    int filterSize = customFilter.length;
    int halfFilterSize = filterSize / 2;

    BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int newValueR = applyCustomFilterSingleChannel(originalImage, x, y, customFilter, 0);
            int newValueG = applyCustomFilterSingleChannel(originalImage, x, y, customFilter, 1);
            int newValueB = applyCustomFilterSingleChannel(originalImage, x, y, customFilter, 2);

            int newPixel = (0xFF << 24) | (newValueR << 16) | (newValueG << 8) | newValueB;
            resultImage.setRGB(x, y, newPixel);
        }
    }

    return resultImage;
}

private int applyCustomFilterSingleChannel(BufferedImage originalImage, int x, int y, int[][] customFilter, int channel) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    int filterSize = customFilter.length;
    int halfFilterSize = filterSize / 2;

    float result = 0.0f;

    for (int i = -halfFilterSize; i <= halfFilterSize; i++) {
        for (int j = -halfFilterSize; j <= halfFilterSize; j++) {
            int pixelX = Math.min(Math.max(x + j, 0), width - 1);
            int pixelY = Math.min(Math.max(y + i, 0), height - 1);

            int colorValue = (originalImage.getRGB(pixelX, pixelY) >> (channel * 8)) & 0xFF;
            result += colorValue * customFilter[i + halfFilterSize][j + halfFilterSize];
        }
    }

    return Math.min(255, Math.max(0, Math.round(result)));
}
    
    private void updateHistogramData(Color color) {
        histogramFrame.updateHistogramData(color);
        if (!histogramFrame.isVisible()) {
            histogramFrame.setVisible(true);
        }
    }

    private Color getColorAtPixel(int x, int y) {
        Image img = ((ImageIcon) imageLabel.getIcon()).getImage();
        PixelGrabber pg = new PixelGrabber(img, x, y, 1, 1, new int[1], 0, 1);
        try {
            if (pg.grabPixels()) {
                int rgb = (int) ((int[]) pg.getPixels())[0];
                return new Color(rgb);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void displayImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            double scale = Math.min((double) MAX_WIDTH / image.getWidth(), (double) MAX_HEIGHT / image.getHeight());
            int newWidth = (int) (image.getWidth() * scale);
            int newHeight = (int) (image.getHeight() * scale);
            int buttonHeight = openButton.getPreferredSize().height;
            int windowWidth = Math.min(newWidth, MAX_WIDTH);
            int extraHeight = 40;
            int windowHeight = Math.min(newHeight + buttonHeight + extraHeight, MAX_HEIGHT);
            setSize(windowWidth, windowHeight);
            ImageIcon icon = new ImageIcon(image.getScaledInstance(windowWidth, newHeight, BufferedImage.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHistogram(File selectedFile) {
        try {
            BufferedImage image = ImageIO.read(selectedFile);
            Color initialColor = new Color(image.getRGB(0, 0));
            updateHistogramData(initialColor);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen para el histograma.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BufferedImage imageIconToBufferedImage(ImageIcon icon) {
        BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(icon.getImage(), 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Programa8());
    }
}
