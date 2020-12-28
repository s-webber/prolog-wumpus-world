package org.projog.wumpus;

import org.projog.wumpus.model.World;
import org.projog.wumpus.view.UserInterface;

public class WumpusWorld {
   public static void main(String[] args) {
      GameController controller = new PrologGameController();
      World state = new World();
      new UserInterface(controller, state);
   }
}
