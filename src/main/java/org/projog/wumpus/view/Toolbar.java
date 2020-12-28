package org.projog.wumpus.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JPanel;

class Toolbar extends JPanel {
   private static final long serialVersionUID = 1L;

   private static final int TIMER_DELAY = 1000;

   private final JButton newMaze = new JButton("New");
   private final JButton reset = new JButton("Reset");
   private final JButton step = new JButton("Step");
   private final JButton play = new JButton("Play");
   private final JButton stop = new JButton("Stop");

   private Timer timer = new Timer();

   Toolbar(UserInterface userInterface) {
      newMaze.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            userInterface.createNew();
            setEnabled(false, true, true, false);
         }
      });
      reset.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            userInterface.reset();
            setEnabled(false, true, true, false);
         }
      });
      step.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            boolean isFinished = userInterface.play();
            setEnabled(true, !isFinished, !isFinished, false);
         }
      });
      play.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            setEnabled(false, false, false, true);
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
               @Override
               public void run() {
                  boolean isFinished = userInterface.play();
                  if (isFinished) {
                     cancel();
                     setEnabled(true, false, false, false);
                  }
               }
            }, 0, TIMER_DELAY);
         }
      });
      stop.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            timer.cancel();
            setEnabled(true, true, true, false);
         }
      });

      setEnabled(false, true, true, false);

      add(newMaze);
      add(reset);
      add(step);
      add(play);
      add(stop);
   }

   private void setEnabled(boolean resetEnabled, boolean playEnabled, boolean stepEnabled, boolean stopEnabled) {
      reset.setEnabled(resetEnabled);
      play.setEnabled(playEnabled);
      step.setEnabled(stepEnabled);
      stop.setEnabled(stopEnabled);
   }
}
