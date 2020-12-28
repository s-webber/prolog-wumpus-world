package org.projog.wumpus.model;

/** The state of the arrow that can be used to kill the wumpus. */
enum ArrowState {
   /** The agent is carrying the arrow. */
   UNUSED,
   /** The arrow has been fired but it missed the wumpus. */
   MISSED,
   /** The arrow has been fired and it hit the wumpus. */
   HIT
}
