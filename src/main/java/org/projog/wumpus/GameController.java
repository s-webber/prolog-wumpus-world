package org.projog.wumpus;

import java.io.PrintStream;
import java.util.Set;

import org.projog.wumpus.model.Action;
import org.projog.wumpus.model.Percept;

/** Logic used to control the agent. */
public interface GameController {
   /** Set output stream so debug from controller can be displayed in UI. */
   void setOut(PrintStream out);

   /** Reset agent state back to the starting state. */
   void reset();

   /**
    * Determines the next action that the agent should perform.
    * 
    * @param percepts the inputs the agent can perceive
    * @return the action the agent should perform
    */
   Action process(Set<Percept> percepts);
}
