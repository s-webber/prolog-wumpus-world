package org.projog.wumpus.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.projog.wumpus.model.Coordinate;
import org.projog.wumpus.model.World;

/**
 * Provides a graphical representation of a wumpus world.
 * <p>
 * The world is represented as a 4x4 grid.
 */
class WorldPanel extends JPanel {
   private static final long serialVersionUID = 1L;

   private static final int WIDTH = 4;

   private final LocationPanel[][] cells;

   private final ImageIcon agentNorthImage;
   private final ImageIcon agentEastImage;
   private final ImageIcon agentSouthImage;
   private final ImageIcon agentWestImage;
   private final ImageIcon wumpusAliveImage;
   private final ImageIcon wumpusDeadImage;
   private final ImageIcon homeImage;
   private final ImageIcon goldImage;
   private final ImageIcon pitImage;

   WorldPanel() {
      setLayout(new GridLayout(WIDTH, WIDTH));

      cells = new LocationPanel[WIDTH][WIDTH];
      for (int y = 0; y < WIDTH; y++) {
         for (int x = 0; x < WIDTH; x++) {
            cells[x][y] = new LocationPanel();
            add(cells[x][y]);
         }
      }

      agentNorthImage = loadImage("images/box-arrow-up.png");
      agentEastImage = loadImage("images/box-arrow-right.png");
      agentSouthImage = loadImage("images/box-arrow-down.png");
      agentWestImage = loadImage("images/box-arrow-left.png");
      wumpusAliveImage = loadImage("images/emoji-angry.png");
      wumpusDeadImage = loadImage("images/emoji-dizzy.png");
      homeImage = loadImage("images/house.png");
      goldImage = loadImage("images/trophy.png");
      pitImage = loadImage("images/exclamation-octagon.png");
   }

   void update(World world) {
      for (int y = 0; y < WIDTH; y++) {
         for (int x = 0; x < WIDTH; x++) {
            Coordinate coordinate = new Coordinate(x, y);
            LocationPanel cell = cells[x][y];

            // set colours
            Color background;
            String description;
            if (world.isWall(coordinate)) {
               background = Color.BLACK;
               description = "wall";
            } else if (world.isPit(coordinate)) {
               background = Color.RED;
               description = "pit";
            } else if (world.isWumpus(coordinate) && world.isWumpusAlive()) {
               background = Color.RED;
               description = "wumpus";
            } else if (world.isGold(coordinate)) {
               background = Color.YELLOW;
               description = "gold";
            } else if (world.isHome(coordinate)) {
               background = Color.GREEN;
               description = "home";
            } else {
               background = Color.WHITE;
               description = "";
            }

            ImageIcon image;
            if (world.isAgentLocated(coordinate)) {
               switch (world.getAgentDirection()) {
                  case NORTH:
                     image = agentNorthImage;
                     break;
                  case EAST:
                     image = agentEastImage;
                     break;
                  case SOUTH:
                     image = agentSouthImage;
                     break;
                  case WEST:
                     image = agentWestImage;
                     break;
                  default:
                     throw new IllegalArgumentException();
               }
            } else if (world.isWumpus(coordinate) && world.isWumpusAlive()) {
               image = wumpusAliveImage;
            } else if (world.isGold(coordinate)) {
               image = goldImage;
            } else if (world.isHome(coordinate)) {
               image = homeImage;
            } else if (world.isPit(coordinate)) {
               image = pitImage;
            } else if (world.isWumpus(coordinate)) {
               image = wumpusDeadImage;
            } else {
               image = null;
            }

            cell.setBackground(background);
            cell.setImage(image);
            cell.setText(coordinate.minus(world.getHome()) + " " + description);
         }
      }
   }

   private static ImageIcon loadImage(String imageName) {
      URL url = ClassLoader.getSystemClassLoader().getResource(imageName);
      if (url != null) {
         return new ImageIcon(url);
      } else {
         throw new IllegalArgumentException("cannot find resource: " + imageName);
      }
   }
}
