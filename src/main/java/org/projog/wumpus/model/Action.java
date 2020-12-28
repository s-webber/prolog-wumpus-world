package org.projog.wumpus.model;

/** An action that the agent can perform. */
public enum Action {
   /** Move forward in the direction the agent is facing. */
   FORWARD,
   /** Turn to the right. */
   RIGHT,
   /** Turn to the left. */
   LEFT,
   /** Fire the arrow. */
   FIRE,
   /** Take the gold. */
   TAKE,
   /** Climb out of the maze. */
   CLIMB
}
