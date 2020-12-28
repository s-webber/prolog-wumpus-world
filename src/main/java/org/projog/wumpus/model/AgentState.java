package org.projog.wumpus.model;

/** State of the agent. */
public enum AgentState {
   /** The agent is alive and navigating the maze. */
   ACTIVE,
   /** The agent is dead because they have fallen into a pit or been eaten by the wumpus. */
   DEAD,
   /** The agent has climbed out of the maze. */
   ESCAPED
}
