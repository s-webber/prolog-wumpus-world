package org.projog.wumpus.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.projog.wumpus.GameController;
import org.projog.wumpus.model.Action;
import org.projog.wumpus.model.AgentState;
import org.projog.wumpus.model.Percept;
import org.projog.wumpus.model.World;

public class UserInterface {
   private static final int MAZE_SIZE = 400;
   private static final int STATUS_WIDTH = 150;
   private static final int FEEDBACK_HEIGHT = 300;

   private final JFrame frame;
   private final WorldPanel mazePanel;
   private final JTextArea statusTextArea;
   private final JTextArea feedback;
   private World world;
   private final GameController controller;
   private int moveCtr;

   public UserInterface(GameController controller, World world) {
      this.controller = controller;
      this.world = world;

      mazePanel = new WorldPanel();

      statusTextArea = new JTextArea();
      statusTextArea.setPreferredSize(new Dimension(STATUS_WIDTH - 20, MAZE_SIZE));
      statusTextArea.setEditable(false);

      JPanel statusPanel = new JPanel();
      statusPanel.setPreferredSize(new Dimension(STATUS_WIDTH, MAZE_SIZE));
      statusPanel.add(statusTextArea);

      // create scroll pane where debug from the controller will be directed
      feedback = new JTextArea();
      feedback.setEditable(false);
      JScrollPane feedbackPane = new JScrollPane(feedback);
      feedbackPane.setPreferredSize(new Dimension(MAZE_SIZE + STATUS_WIDTH, FEEDBACK_HEIGHT));
      OutputStream os = new OutputStream() {
         @Override
         public void write(int b) throws IOException {
            feedback.append(String.valueOf((char) b));
         }
      };
      controller.setOut(new PrintStream(os));

      update(world, null);

      frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.add(new Toolbar(this), BorderLayout.NORTH);
      frame.add(mazePanel, BorderLayout.CENTER);
      frame.add(statusPanel, BorderLayout.EAST);
      frame.add(feedbackPane, BorderLayout.SOUTH);
      frame.setSize(MAZE_SIZE + STATUS_WIDTH, MAZE_SIZE + FEEDBACK_HEIGHT);
      frame.setVisible(true);
   }

   synchronized void createNew() {
      resetAgentState();
      world = new World();
      update(world, null);
   }

   synchronized void reset() {
      resetAgentState();
      world.reset();
      update(world, null);
   }

   private void resetAgentState() {
      moveCtr = 0;
      clearFeedback();
      controller.reset();
   }

   synchronized boolean play() {
      clearFeedback();
      Action action = controller.process(world.getPercepts());
      AgentState agentState = world.update(action);
      moveCtr++;
      update(world, action);
      if (agentState == AgentState.ACTIVE) {
         return false;
      } else {
         JOptionPane.showMessageDialog(frame, "game over", "bye", JOptionPane.INFORMATION_MESSAGE);
         return true;
      }
   }

   private void clearFeedback() {
      feedback.setText("");
   }

   private void update(World world, Action action) {
      mazePanel.update(world);

      StringBuilder status = new StringBuilder();

      status.append("Move: ");
      status.append(moveCtr);

      status.append("\n\nLocation: ");
      status.append(world.getAgentLocation().minus(world.getHome()));

      status.append("\n\nFacing: ");
      status.append(world.getAgentDirection());

      if (world.haveArrow()) {
         status.append("\n\nHave arrow.");
      } else {
         status.append("\n\nHave no arrow.");
      }

      if (world.haveGold()) {
         status.append("\n\nHave gold.");
      } else {
         status.append("\n\nHave no gold.");
      }

      if (world.isWumpusAlive()) {
         status.append("\n\nWumpus alive.");
      } else {
         status.append("\n\nWumpus dead.");
      }

      if (action != null) {
         status.append("\n\nLast action:\n ");
         status.append(action);
      }

      Set<Percept> percepts = world.getPercepts();
      if (percepts.isEmpty()) {
         status.append("\n\nNo percepts.");
      } else {
         status.append("\n\nPercepts:");
         for (Percept percept : percepts) {
            status.append("\n ");
            status.append(percept);
         }
      }

      statusTextArea.setText(status.toString());
   }
}
