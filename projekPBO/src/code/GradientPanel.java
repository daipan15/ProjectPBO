/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package code;

/**
 *
 * @author DHAIFAN
 */

import java.awt.*;
import javax.swing.*;

public class GradientPanel extends JPanel {    // Panel gradient
        private final Color color1;
        private final Color color2;
        
        public GradientPanel(Color c1, Color c2) {
            this.color1 = c1;
            this.color2 = c2;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }