// Antonio Yamir Ledesma Briones
package com.antonio.programa5;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HistogramFrame extends JFrame {
    private JFreeChart histogramChart;
    private DefaultCategoryDataset dataset;

    private boolean isVisibleRed = true;
    private boolean isVisibleGreen = true;
    private boolean isVisibleBlue = true;

    // Nuevos botones de alternancia
    private JToggleButton redToggleButton;
    private JToggleButton greenToggleButton;
    private JToggleButton blueToggleButton;

    public HistogramFrame(String title) {
        super(title);
        setSize(400, 400);

        // Configurar el dataset para el histograma
        dataset = new DefaultCategoryDataset();
        histogramChart = ChartFactory.createBarChart(
                "Histograma RGB",
                "Canal de Color",
                "Frecuencia",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Crear el panel del histograma y agregarlo a la ventana
        ChartPanel chartPanel = new ChartPanel(histogramChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        add(chartPanel, BorderLayout.CENTER);

        // Cambiar el color de las barras
        CategoryPlot plot = histogramChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.RED);   // Rojo
        renderer.setSeriesPaint(1, Color.GREEN); // Verde
        renderer.setSeriesPaint(2, Color.BLUE);  // Azul

        // Ajustar el ancho de las barras
        double[] seriesWidths = {0.5, 0.5, 0.5}; // Ancho de cada barra, puedes ajustar estos valores
        renderer.setMaximumBarWidth(0.5); // Máximo ancho de las barras
        renderer.setItemMargin(0.1); // Margen entre las barras

        // Inicializar y agregar los botones de alternancia
        redToggleButton = new JToggleButton("Rojo");
        greenToggleButton = new JToggleButton("Verde");
        blueToggleButton = new JToggleButton("Azul");

        JPanel togglePanel = new JPanel();
        togglePanel.add(redToggleButton);
        togglePanel.add(greenToggleButton);
        togglePanel.add(blueToggleButton);

        // Agregar los paneles de botones de alternancia y de información
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(togglePanel, BorderLayout.NORTH);
        //southPanel.add(infoPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Agregar ActionListener a los botones de alternancia
        redToggleButton.addActionListener(e -> toggleHistogramChannel(0)); // 0 representa el canal rojo
        greenToggleButton.addActionListener(e -> toggleHistogramChannel(1)); // 1 representa el canal verde
        blueToggleButton.addActionListener(e -> toggleHistogramChannel(2)); // 2 representa el canal azul
    }

    private void toggleHistogramChannel(int channelIndex) {
        // Alternar la visibilidad del canal seleccionado
        switch (channelIndex) {
            case 0:
                isVisibleRed = !isVisibleRed;
                break;
            case 1:
                isVisibleGreen = !isVisibleGreen;
                break;
            case 2:
                isVisibleBlue = !isVisibleBlue;
                break;
        }

        // Actualizar el gráfico del histograma
        updateHistogramVisibility();
    }

    private void updateHistogramVisibility() {
        BarRenderer renderer = (BarRenderer) histogramChart.getCategoryPlot().getRenderer();
        renderer.setSeriesVisible(0, isVisibleRed);
        renderer.setSeriesVisible(1, isVisibleGreen);
        renderer.setSeriesVisible(2, isVisibleBlue);
    }

    public void updateHistogramData(Color color) {
        // Actualizar el dataset del histograma con los valores de r, g y b
        dataset.addValue(isVisibleRed ? color.getRed() : 0, "Rojo", "Rojo");
        dataset.addValue(isVisibleGreen ? color.getGreen() : 0, "Verde", "Verde");
        dataset.addValue(isVisibleBlue ? color.getBlue() : 0, "Azul", "Azul");

        // Actualizar el gráfico del histograma
        histogramChart.fireChartChanged();
    }
}
