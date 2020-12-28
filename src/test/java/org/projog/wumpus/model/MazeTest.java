package org.projog.wumpus.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;
import org.projog.wumpus.model.Maze.MazeBuilder;

public class MazeTest {
   @Test
   public void buildRandom() {
      Maze maze = Maze.buildRandom();

      Coordinate home = maze.getHome();
      List<Coordinate> gold = find(maze, maze::isGold);
      List<Coordinate> wumpus = find(maze, maze::isWumpus);
      List<Coordinate> pits = find(maze, maze::isPit);
      List<Coordinate> walls = find(maze, maze::isWall);

      // maze should contain exactly 1 gold, 1 wumpus, 1 wall (excluding the boundaries) and 2 pits
      assertEquals(1, gold.size());
      assertEquals(1, wumpus.size());
      assertEquals(2, pits.size());
      assertEquals(1, walls.size());

      // assert pit not in same square as home, gold, wumpus or wall
      assertFalse(pits.contains(home));
      assertFalse(pits.contains(gold.get(0)));
      assertFalse(pits.contains(wumpus.get(0)));
      assertFalse(pits.contains(walls.get(0)));

      // assert wall not in same square as home, gold or wumpus
      assertFalse(walls.contains(home));
      assertFalse(walls.contains(gold.get(0)));
      assertFalse(walls.contains(wumpus.get(0)));

      // assert wumpus not in same square as home
      assertNotEquals(home, wumpus.get(0));

      // assert walls surround the 4x4 grid
      assertTrue(maze.isWall(new Coordinate(0, -1)));
      assertTrue(maze.isWall(new Coordinate(1, -1)));
      assertTrue(maze.isWall(new Coordinate(2, -1)));
      assertTrue(maze.isWall(new Coordinate(3, -1)));
      assertTrue(maze.isWall(new Coordinate(-1, 0)));
      assertTrue(maze.isWall(new Coordinate(-1, 1)));
      assertTrue(maze.isWall(new Coordinate(-1, 2)));
      assertTrue(maze.isWall(new Coordinate(-1, 3)));
      assertTrue(maze.isWall(new Coordinate(0, 4)));
      assertTrue(maze.isWall(new Coordinate(1, 4)));
      assertTrue(maze.isWall(new Coordinate(2, 4)));
      assertTrue(maze.isWall(new Coordinate(3, 4)));
      assertTrue(maze.isWall(new Coordinate(4, 0)));
      assertTrue(maze.isWall(new Coordinate(4, 1)));
      assertTrue(maze.isWall(new Coordinate(4, 2)));
      assertTrue(maze.isWall(new Coordinate(4, 3)));
   }

   /** assert multiple calls to buildRandom() return different results. */
   @Test
   public void buildRandom_is_random() {
      Maze m1 = Maze.buildRandom();
      Maze m2 = Maze.buildRandom();

      boolean homeEquals = m1.getHome().equals(m2.getHome());
      boolean goldEquals = find(m1, m1::isGold).equals(find(m2, m2::isGold));
      boolean wumpusEquals = find(m1, m1::isWumpus).equals(find(m2, m2::isWumpus));
      boolean pitsEquals = find(m1, m1::isPit).equals(find(m2, m2::isPit));
      boolean wallsEquals = find(m1, m1::isWall).equals(find(m2, m2::isWall));

      assertFalse(homeEquals && goldEquals && wumpusEquals && pitsEquals && wallsEquals);
   }

   private List<Coordinate> find(Maze maze, Function<Coordinate, Boolean> function) {
      List<Coordinate> result = new ArrayList<>();

      for (int x = 0; x < 4; x++) {
         for (int y = 0; y < 4; y++) {
            Coordinate c = new Coordinate(x, y);
            if (function.apply(c)) {
               result.add(c);
            }
         }
      }
      return result;
   }

   @Test
   public void simple_example_all_components_same_location() {
      Coordinate c = new Coordinate(3, 5);

      MazeBuilder builder = new MazeBuilder();
      builder.home(c);
      builder.gold(c);
      builder.wumpus(c);
      builder.pit(c);
      builder.wall(c);

      Maze maze = builder.build();

      assertEquals(c, maze.getHome());
      assertTrue(maze.isGold(c));
      assertTrue(maze.isWumpus(c));
      assertTrue(maze.isPit(c));
      assertTrue(maze.isWall(c));
   }

   @Test
   public void simple_example_all_components_different_locations() {
      Coordinate home = new Coordinate(0, 0);
      Coordinate gold = new Coordinate(0, 1);
      Coordinate wumpus = new Coordinate(0, 2);
      Coordinate pit = new Coordinate(0, 3);
      Coordinate wall = new Coordinate(0, 4);
      Coordinate empty = new Coordinate(0, 5);

      MazeBuilder builder = new MazeBuilder();
      builder.home(home);
      builder.gold(gold);
      builder.wumpus(wumpus);
      builder.pit(pit);
      builder.wall(wall);

      Maze maze = builder.build();

      assertEquals(home, maze.getHome());

      // assert isGold behavior
      assertTrue(maze.isGold(gold));
      assertFalse(maze.isGold(home));
      assertFalse(maze.isGold(wumpus));
      assertFalse(maze.isGold(pit));
      assertFalse(maze.isGold(wall));
      assertFalse(maze.isGold(empty));

      // assert isWumpus behavior
      assertTrue(maze.isWumpus(wumpus));
      assertFalse(maze.isWumpus(home));
      assertFalse(maze.isWumpus(gold));
      assertFalse(maze.isWumpus(pit));
      assertFalse(maze.isWumpus(wall));
      assertFalse(maze.isWumpus(empty));

      // assert isPit behavior
      assertTrue(maze.isPit(pit));
      assertFalse(maze.isPit(home));
      assertFalse(maze.isPit(gold));
      assertFalse(maze.isPit(wumpus));
      assertFalse(maze.isPit(wall));
      assertFalse(maze.isPit(empty));

      // assert isWall behavior
      assertTrue(maze.isWall(wall));
      assertFalse(maze.isWall(home));
      assertFalse(maze.isWall(gold));
      assertFalse(maze.isWall(wumpus));
      assertFalse(maze.isWall(pit));
      assertFalse(maze.isWall(empty));
   }

   @Test
   public void four_by_four_grid_example() {
      MazeBuilder builder = new MazeBuilder();
      builder.boundary(0, 3, 0, 3);
      builder.home(1, 0);
      builder.gold(3, 2);
      builder.wumpus(0, 2);
      builder.pit(3, 1).pit(2, 3);
      builder.wall(0, 3);

      Maze maze = builder.build();

      assertEquals(new Coordinate(1, 0), maze.getHome());

      assertTrue(maze.isGold(new Coordinate(3, 2)));

      assertTrue(maze.isWumpus(new Coordinate(0, 2)));

      assertTrue(maze.isPit(new Coordinate(3, 1)));
      assertTrue(maze.isPit(new Coordinate(2, 3)));

      assertTrue(maze.isWall(new Coordinate(0, 3)));
      assertTrue(maze.isWall(new Coordinate(0, -1)));
      assertTrue(maze.isWall(new Coordinate(1, -1)));
      assertTrue(maze.isWall(new Coordinate(2, -1)));
      assertTrue(maze.isWall(new Coordinate(3, -1)));
      assertTrue(maze.isWall(new Coordinate(-1, 0)));
      assertTrue(maze.isWall(new Coordinate(-1, 1)));
      assertTrue(maze.isWall(new Coordinate(-1, 2)));
      assertTrue(maze.isWall(new Coordinate(-1, 3)));
      assertTrue(maze.isWall(new Coordinate(0, 4)));
      assertTrue(maze.isWall(new Coordinate(1, 4)));
      assertTrue(maze.isWall(new Coordinate(2, 4)));
      assertTrue(maze.isWall(new Coordinate(3, 4)));
      assertTrue(maze.isWall(new Coordinate(4, 0)));
      assertTrue(maze.isWall(new Coordinate(4, 1)));
      assertTrue(maze.isWall(new Coordinate(4, 2)));
      assertTrue(maze.isWall(new Coordinate(4, 3)));

      assertEquals("Maze [home=1,0, gold=3,2, wumpus=0,2, pits=[3,1, 2,3], walls=[-1,0, 4,3, 4,2, 4,1, 4,0, 3,-1, 0,4, 2,-1, -1,3, 1,-1, 1,4, -1,2, 0,3, 0,-1, 2,4, -1,1, 3,4]]",
                  maze.toString());
   }
}
