package org.projog.wumpus.view;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Provides a graphical representation of a single location in a wumpus world.
 * 
 * @see WorldPanel
 */
class LocationPanel extends JPanel {
   private static final long serialVersionUID = 1L;

   private final JLabel label;

   LocationPanel() {
      setBorder(BorderFactory.createLineBorder(Color.BLACK));

      label = new JLabel();
      label.setVerticalTextPosition(JLabel.TOP);
      label.setHorizontalTextPosition(JLabel.CENTER);

      add(label);
   }

   void setImage(ImageIcon icon) {
      label.setIcon(icon);
   }

   @Override
   public void setBackground(Color bg) {
      super.setBackground(bg);
      // although final, label will be null when this method is called from super constructor
      if (label != null) {
         label.setForeground(bg == Color.BLACK ? Color.WHITE : Color.BLACK);
      }
   }

   void setText(String text) {
      label.setText(text);
   }
}