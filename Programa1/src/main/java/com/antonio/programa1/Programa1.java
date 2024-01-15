
package com.antonio.programa1;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import javax.swing.JFrame;

/**
 *
 * @author anton
 */
public class Programa1 extends JFrame{
    public Image imagen;
    int ancho, largo;
    String cAncho, cLargo;

    public Programa1() {
        this.setTitle("Aplicación 1 PDI");
        this.setSize(800, 600);
        this.setVisible(true);

        imagen = Toolkit.getDefaultToolkit().getImage("C:/imagen1.png");

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        new Programa1();
    }

    public void paint(Graphics g) {
        NumberFormat convertir = NumberFormat.getCurrencyInstance();
        largo = imagen.getHeight(this);
        ancho = imagen.getWidth(this);
        g.drawImage(imagen, 10, 10, ancho, largo, this);
    }
}