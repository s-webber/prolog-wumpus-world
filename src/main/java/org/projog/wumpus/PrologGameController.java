package org.projog.wumpus;

import static java.util.stream.Collectors.toList;

import java.io.PrintStream;
import java.util.Set;

import org.projog.api.Projog;
import org.projog.api.QueryPlan;
import org.projog.api.QueryStatement;
import org.projog.wumpus.model.Action;
import org.projog.wumpus.model.Percept;

/** Facade to prolog code that contains the logic used to control the agent. */
class PrologGameController implements GameController {
   private final Projog projog;
   private final QueryPlan initQuery;
   private final QueryPlan updateQuery;

   PrologGameController() {
      projog = new Projog();
      projog.consultResource("prolog/wumpus.pl");
      initQuery = projog.createPlan("init.");
      updateQuery = projog.createPlan("process(Percepts,Action).");

      reset();
   }

   /** Set output stream so debug from controller can be displayed in UI. */
   @Override
   public void setOut(PrintStream out) {
      projog.setUserOutput(out);
   }

   /** Reset agent state back to the starting state. */
   @Override
   public synchronized void reset() {
      initQuery.executeOnce();
   }

   /**
    * Determines the next action that the agent should perform.
    * 
    * @param percepts the inputs the agent can perceive
    * @return the action the agent should perform
    */
   @Override
   public synchronized Action process(Set<Percept> percepts) {
      try {
         QueryStatement updateStatement = updateQuery.createStatement();
         // Percept names of enum are upper-case but wumpus.pl expects lower-case versions, so convert here.
         updateStatement.setListOfAtomNames("Percepts", percepts.stream().map(p -> p.toString().toLowerCase()).collect(toList()));
         String action = updateStatement.findFirstAsAtomName();
         return Action.valueOf(action.toUpperCase());
      } catch (RuntimeException e) {
         projog.printProjogStackTrace(e);
         throw e;
      }
   }
}
