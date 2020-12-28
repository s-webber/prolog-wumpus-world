package org.projog.wumpus.model;

/** An input that the agent is perceiving at any given moment. */
public enum Percept {
   /** Indicates that the agent is next to a pit. */
   BREEZE,
   /** Indicates that the agent is next to the wumpus. */
   STENCH,
   /** Indicates that the agent is in the same location as the gold. */
   GLITTER,
   /** Indicates that the wumpus has been killed. */
   SCREAM,
   /** Indicates that the agent has hit a wall. */
   BUMP
}
