package org.projog.wumpus;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.wumpus.model.Action.CLIMB;
import static org.projog.wumpus.model.Action.FIRE;
import static org.projog.wumpus.model.Action.FORWARD;
import static org.projog.wumpus.model.Action.LEFT;
import static org.projog.wumpus.model.Action.RIGHT;
import static org.projog.wumpus.model.Action.TAKE;

import org.junit.Test;
import org.projog.wumpus.model.Action;
import org.projog.wumpus.model.AgentState;
import org.projog.wumpus.model.Maze;
import org.projog.wumpus.model.Maze.MazeBuilder;
import org.projog.wumpus.model.World;

public class WumpusWorldTest {
   private static final GameController AGENT = new PrologGameController();

   @Test
   public void home_contains_gold() {
      MazeBuilder builder = new MazeBuilder();
      builder.home(0, 0);
      builder.gold(0, 0);

      assertActions(builder, TAKE, CLIMB);
   }

   @Test
   public void home_surrounded_by_walls() {
      MazeBuilder builder = new MazeBuilder();
      builder.home(0, 0);
      builder.wall(1, 0).wall(-1, 0).wall(0, 1).wall(0, -1);

      assertActions(builder, FORWARD, LEFT, FORWARD, LEFT, FORWARD, LEFT, FORWARD, CLIMB);
   }

   @Test
   public void home_next_to_pit() {
      MazeBuilder builder = new MazeBuilder();
      builder.home(0, 0);
      builder.pit(1, 0);

      assertActions(builder, CLIMB);
   }

   /** Even if miss wumpus then still move forward as, because there is no stench, we know it will be safe. */
   @Test
   public void home_next_to_wumpus() {
      MazeBuilder builder = new MazeBuilder();
      builder.home(0, 0);
      builder.wumpus(1, 0); // west of agent
      builder.gold(0, -1); // north of agent

      assertActions(builder, FIRE, FORWARD, TAKE, RIGHT, RIGHT, FORWARD, CLIMB);
   }

   /** If miss wumpus then still exit as, because there is a stench, we don't know which adjacent squares are safe. */
   @Test
   public void home_next_to_pit_and_wumpus() {
      MazeBuilder builder = new MazeBuilder();
      builder.home(0, 0);
      builder.pit(-1, 0); // west of agent
      builder.wumpus(1, 0); // east of agent

      assertActions(builder, FIRE, CLIMB);
   }

   @Test
   public void home_facing_wumpus() {
      MazeBuilder builder = new MazeBuilder();
      builder.home(0, 0);
      builder.wumpus(0, -1); // north of agent
      builder.gold(0, -1); // same location as wumpus

      assertActions(builder, FIRE, FORWARD, TAKE, RIGHT, RIGHT, FORWARD, CLIMB);
   }

   @Test
   public void home_next_pit_and_facing_wumpus() {
      MazeBuilder builder = new MazeBuilder();
      builder.home(0, 0);
      builder.wumpus(0, -1); // north of agent
      builder.gold(0, -1); // same location as wumpus
      builder.pit(-1, 0); // west of agent

      assertActions(builder, FIRE, FORWARD, TAKE, RIGHT, RIGHT, FORWARD, CLIMB);
   }

   @Test
   public void no_wumpus() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.home(3, 3);
      builder.pit(0, 1).pit(1, 2);
      builder.wall(2, 1);
      builder.gold(2, 3);
      // NOTE: not adding a wumpus to the maze

      assertActions(builder, FORWARD, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, FORWARD, RIGHT, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD, LEFT, FORWARD, LEFT,
                  FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, FORWARD, LEFT,
                  FORWARD, RIGHT, RIGHT, FORWARD, TAKE, RIGHT, RIGHT, FORWARD, CLIMB);
   }

   @Test
   public void no_wumpus_or_pit() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.home(0, 0);
      builder.gold(2, 2);
      // NOTE: not adding a wumpus, pits or additional walls to maze

      assertActions(builder, FORWARD, LEFT, FORWARD, LEFT, FORWARD, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, FORWARD, FORWARD,
                  LEFT, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, LEFT, FORWARD, TAKE, LEFT, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, CLIMB);
   }

   @Test
   public void empty_maze() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.home(1, 1);
      // NOTE: not adding a wumpus, pits, additional walls or gold to maze

      assertActions(builder, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, FORWARD,
                  FORWARD, FORWARD, LEFT, FORWARD, LEFT, FORWARD, FORWARD, RIGHT, FORWARD, FORWARD, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD,
                  FORWARD, LEFT, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, LEFT,
                  FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, CLIMB);
   }

   /** Confirm agent escapes a randomly generated maze. */
   @Test // TODO run multiple times (x10?) per test run
   public void random_maze() {
      Maze maze = Maze.buildRandom();
      completeMaze(maze);
   }

   // Created below tests by calling Maze.buildRandom() to create 4x4 worlds and then recreating them here, 
   // using MazeBuilder, in order to construct a suite of different scenarios to test.

   @Test
   public void kill_wumpus_but_cannot_take_gold_example1() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.gold(1, 3);
      builder.home(3, 0);
      builder.wumpus(2, 1);
      builder.wall(3, 2);
      builder.pit(1, 0).pit(2, 3);

      assertActions(builder, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, FORWARD, LEFT, FORWARD, LEFT, LEFT, FIRE, FORWARD, FORWARD, RIGHT, RIGHT,
                  FORWARD, RIGHT, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, CLIMB);
   }

   @Test
   public void kill_wumpus_but_cannot_take_gold_example2() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.home(1, 0);
      builder.gold(3, 2);
      builder.wumpus(0, 2);
      builder.wall(0, 3);
      builder.pit(3, 1).pit(2, 3);

      assertActions(builder, FORWARD, LEFT, FORWARD, FORWARD, LEFT, FORWARD, LEFT, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, RIGHT, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD,
                  FORWARD, RIGHT, FORWARD, RIGHT, FORWARD, RIGHT, FORWARD, FORWARD, FORWARD, RIGHT, RIGHT, FORWARD, RIGHT, FORWARD, RIGHT, RIGHT, FORWARD, FIRE, FORWARD, FORWARD,
                  LEFT, FORWARD, LEFT, FORWARD, LEFT, FORWARD, FORWARD, CLIMB);
   }

   @Test
   public void take_gold_without_killing_wumpus() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.gold(3, 3);
      builder.home(0, 0);
      builder.wumpus(1, 2);
      builder.wall(3, 2);
      builder.pit(3, 0).pit(0, 3);

      assertActions(builder, FORWARD, LEFT, FORWARD, LEFT, FORWARD, FORWARD, RIGHT, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD, FORWARD, RIGHT, RIGHT, FORWARD,
                  LEFT, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, TAKE, LEFT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, CLIMB);
   }

   @Test
   public void take_gold_after_killing_wumpus1() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.home(3, 3);
      builder.gold(2, 0);
      builder.wumpus(3, 1);
      builder.wall(1, 2);
      builder.pit(0, 0).pit(1, 1);

      assertActions(builder, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD,
                  FORWARD, RIGHT, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD, LEFT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, LEFT, FORWARD,
                  LEFT, FORWARD, FIRE, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, TAKE, RIGHT, RIGHT, FORWARD, RIGHT, FORWARD, FORWARD, FORWARD, CLIMB);
   }

   @Test
   public void take_gold_after_killing_wumpus2() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.home(0, 2);
      builder.gold(1, 0);
      builder.wumpus(3, 2);
      builder.wall(1, 2);
      builder.pit(1, 1);

      assertActions(builder, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, FORWARD, RIGHT, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD,
                  FORWARD, RIGHT, RIGHT, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, LEFT,
                  FORWARD, LEFT, FORWARD, FORWARD, FORWARD, LEFT, FIRE, FORWARD, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, FORWARD, TAKE, RIGHT, RIGHT, FORWARD, RIGHT, FORWARD,
                  FORWARD, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, CLIMB);
   }

   @Test
   public void take_gold_after_killing_wumpus_located_in_same_square_as_gold() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.home(3, 3);
      builder.gold(3, 1);
      builder.wumpus(3, 1); // same square as gold
      builder.wall(1, 2);
      builder.pit(0, 0).pit(1, 1);

      assertActions(builder, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD, FORWARD, FORWARD, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD,
                  FORWARD, RIGHT, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, RIGHT, FORWARD, LEFT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, LEFT, FORWARD, RIGHT, FORWARD, LEFT, FORWARD,
                  LEFT, FORWARD, FIRE, FORWARD, TAKE, RIGHT, RIGHT, FORWARD, FORWARD, CLIMB);
   }

   private void assertActions(MazeBuilder builder, Action... expectedActions) {
      Maze maze = builder.build();
      World world = new World(maze);
      AGENT.reset();

      for (int i = 0; i < expectedActions.length; i++) {
         Action actualAction = AGENT.process(world.getPercepts());
         assertSame(maze, expectedActions[i], actualAction);

         boolean isFinalMove = i == expectedActions.length - 1;
         AgentState expectedOutcome = isFinalMove ? AgentState.ESCAPED : AgentState.ACTIVE;
         AgentState actualOutcome = world.update(actualAction);
         assertSame(maze, expectedOutcome, actualOutcome);
      }
   }

   private static void assertSame(Maze m, Object expected, Object actual) {
      if (expected != actual) {
         fail("Expected: " + expected + " Actual: " + actual + " Full solution: " + completeMaze(m));
      }
   }

   /** Completes the given maze and returns a comma-separated list of actions in the order they were performed. */
   private static String completeMaze(Maze maze) {
      GameController agent = new PrologGameController();
      World world = new World(maze);
      agent.reset();

      StringBuilder actions = new StringBuilder();

      AgentState outcome = null;
      int ctr = 0;
      while (outcome != AgentState.ESCAPED) {
         Action action = agent.process(world.getPercepts());
         outcome = world.update(action);

         // TODO change to take Producer rather than calling maze.toString each time

         // assert agent has not fallen into a pit or been eaten by the wumpus
         assertNotEquals(maze.toString(), AgentState.DEAD, outcome);
         // assert agent not stuck in a loop
         assertTrue(maze.toString(), ctr++ < 100);

         if (ctr > 1) {
            actions.append(',');
         }
         actions.append(action);
      }

      return actions.toString();
   }
}
