package org.projog.wumpus.model;

import static org.projog.wumpus.model.Percept.BREEZE;
import static org.projog.wumpus.model.Percept.BUMP;
import static org.projog.wumpus.model.Percept.GLITTER;
import static org.projog.wumpus.model.Percept.SCREAM;
import static org.projog.wumpus.model.Percept.STENCH;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Mutable object representing the state of a wumpus world game. */
public class World {
   private final Maze maze;
   private Agent agent;
   /** Did the last action cause the agent to bump into a wall? */
   private boolean isBumpedIntoWall;
   /** Did the last action cause the wumpus to die? */
   private boolean isScream;

   public World() {
      this(Maze.buildRandom());
   }

   public World(Maze maze) {
      this.maze = maze;
      this.agent = new Agent(maze.getHome());
   }

   public void reset() {
      agent = new Agent(maze.getHome());
      isBumpedIntoWall = false;
      isScream = false;
   }

   /**
    * Perform the given action to update the world.
    * 
    * @param action the action to perform
    * @return the state after the action has been performed
    * @throws IllegalStateException if the action is not appropriate for the current state
    */
   public AgentState update(Action action) {
      if (isFinished()) {
         throw new IllegalStateException("agent state: " + agent.getState());
      }

      isBumpedIntoWall = false;
      isScream = false;

      Coordinate location = getAgentLocation();
      Direction direction = getAgentDirection();
      switch (action) {
         case FORWARD:
            Coordinate nextLocation = location.move(getAgentDirection());
            if (isWall(nextLocation)) {
               isBumpedIntoWall = true;
            } else {
               agent.setLocation(nextLocation);
               if (isWumpus(nextLocation) && isWumpusAlive()) {
                  // agent has been eaten by the wumpus
                  agent.setState(AgentState.DEAD);
               } else if (isPit(nextLocation)) {
                  // agent has fallen into a pit
                  agent.setState(AgentState.DEAD);
               }
            }
            break;
         case RIGHT:
            agent.turnRight();
            break;
         case LEFT:
            agent.turnLeft();
            break;
         case TAKE:
            if (isGold(location)) {
               agent.setHasGold(true);
            } else {
               throw new IllegalStateException("no gold to take");
            }
            break;
         case FIRE:
            if (haveArrow()) {
               Coordinate targetLocation = location.move(direction);
               if (isWumpus(targetLocation)) {
                  agent.setHasKilledWumpus();
                  isScream = true;
               } else {
                  agent.setHasMissed();
               }
            } else {
               throw new IllegalStateException("no arrow to fire");
            }
            break;
         case CLIMB:
            if (isHome(location)) {
               agent.setState(AgentState.ESCAPED);
            } else {
               throw new IllegalStateException("cannot climb as not home");
            }
            break;
         default:
            throw new IllegalArgumentException("unknown action: " + action);
      }

      return agent.getState();
   }

   /**
    * Returns the percepts that are currently available to the agent.
    * <p>
    * The percepts available to the agent will depend on the agent's current location (e.g. STENCH, GLITTER or BREEZE)
    * and the result of the agent's previous action (e.g. SCREAM or BUMP).
    * 
    * @return percepts available to the agent
    */
   public Set<Percept> getPercepts() {
      if (isFinished()) {
         return Collections.emptySet();
      }

      Coordinate location = agent.getLocation();
      Set<Percept> percepts = new HashSet<>();
      if (isBumpedIntoWall) {
         percepts.add(BUMP);
      }
      if (isScream) {
         percepts.add(SCREAM);
      }
      if (isGold(location)) {
         percepts.add(GLITTER);
      }
      if (isWumpus(location)) {
         percepts.add(STENCH);
      }
      for (Direction d : Direction.values()) {
         Coordinate adjacent = location.move(d);
         if (isPit(adjacent)) {
            percepts.add(BREEZE);
         }
         if (isWumpus(adjacent)) {
            percepts.add(STENCH);
         }
      }
      return percepts;
   }

   public boolean isFinished() {
      return agent.getState() != AgentState.ACTIVE;
   }

   public Direction getAgentDirection() {
      return agent.getDirection();
   }

   public Coordinate getHome() {
      return maze.getHome();
   }

   public boolean isHome(Coordinate coordinate) {
      return getHome().equals(coordinate);
   }

   public Coordinate getAgentLocation() {
      return agent.getLocation();
   }

   public boolean isAgentLocated(Coordinate coordinate) {
      return agent.getLocation().equals(coordinate);
   }

   public boolean haveGold() {
      return agent.isHasGold();
   }

   public boolean haveArrow() {
      return agent.haveArrow();
   }

   public boolean isWumpusAlive() {
      return !isWumpusKilled();
   }

   public boolean isWumpusKilled() {
      return agent.haveKilledWumpus();
   }

   public boolean isWall(Coordinate c) {
      return maze.isWall(c);
   }

   public boolean isGold(Coordinate c) {
      return !agent.isHasGold() && maze.isGold(c);
   }

   public boolean isWumpus(Coordinate c) {
      return maze.isWumpus(c);
   }

   public boolean isPit(Coordinate c) {
      return maze.isPit(c);
   }
}
