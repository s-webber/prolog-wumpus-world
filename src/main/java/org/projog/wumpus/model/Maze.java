package org.projog.wumpus.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Immutable object representing the initial state of a maze to be explored by the agent. */
public class Maze {
   private final Coordinate home;
   private final Coordinate gold;
   private final Coordinate wumpus;
   private final Set<Coordinate> pits;
   private final Set<Coordinate> walls;

   private Maze(MazeBuilder builder) {
      this.home = requireNonNull(builder.home);
      this.gold = builder.gold;
      this.wumpus = builder.wumpus;
      this.pits = requireNonNull(builder.pits);
      this.walls = requireNonNull(builder.walls);
   }

   Coordinate getHome() {
      return home;
   }

   boolean isGold(Coordinate c) {
      return c.equals(gold);
   }

   boolean isWumpus(Coordinate c) {
      return c.equals(wumpus);
   }

   boolean isPit(Coordinate c) {
      return pits.contains(c);
   }

   boolean isWall(Coordinate c) {
      return walls.contains(c);
   }

   @Override
   public String toString() {
      return "Maze [home=" + home + ", gold=" + gold + ", wumpus=" + wumpus + ", pits=" + pits + ", walls=" + walls + "]";
   }

   public static Maze buildRandom() {
      MazeBuilder builder = new MazeBuilder();

      int width = 4;
      builder.boundary(0, width - 1, 0, width - 1);
      List<Coordinate> cells = new ArrayList<>();
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < width; y++) {
            cells.add(new Coordinate(x, y));
         }
      }

      // gold can be in same cell as wumpus or home but not pit or wall
      // wumpus can be in same cell as gold but not home, pit or wall
      // home can be in same cell as gold but not wumpus, pit or wall
      Collections.shuffle(cells);
      Coordinate gold = cells.get(0);
      builder.gold(gold);
      Collections.shuffle(cells);
      builder.home(cells.remove(0));
      builder.wumpus(cells.remove(0));
      cells.remove(gold);

      for (int i = 0; i < 2; i++) {
         builder.pit(cells.remove(0));
      }

      builder.wall(cells.remove(0));

      return new Maze(builder);
   }

   public static class MazeBuilder {
      private final Set<Coordinate> pits = new HashSet<>();
      private final Set<Coordinate> walls = new HashSet<>();
      private Coordinate home;
      private Coordinate gold;
      private Coordinate wumpus;

      public MazeBuilder home(int x, int y) {
         return home(new Coordinate(x, y));
      }

      public MazeBuilder home(Coordinate home) {
         this.home = home;
         return this;
      }

      public MazeBuilder gold(int x, int y) {
         return gold(new Coordinate(x, y));
      }

      public MazeBuilder gold(Coordinate gold) {
         this.gold = gold;
         return this;
      }

      public MazeBuilder wumpus(int x, int y) {
         return wumpus(new Coordinate(x, y));
      }

      public MazeBuilder wumpus(Coordinate wumpus) {
         this.wumpus = wumpus;
         return this;
      }

      public MazeBuilder pit(int x, int y) {
         return pit(new Coordinate(x, y));
      }

      public MazeBuilder pit(Coordinate pit) {
         this.pits.add(pit);
         return this;
      }

      public MazeBuilder wall(int x, int y) {
         return wall(new Coordinate(x, y));
      }

      public MazeBuilder wall(Coordinate wall) {
         this.walls.add(wall);
         return this;
      }

      /** Adds walls around the given boundary. */
      public void boundary(int minX, int maxX, int minY, int maxY) {
         for (int x = minX; x <= maxX; x++) {
            wall(x, minY - 1);
            wall(x, maxY + 1);
         }
         for (int y = minY; y <= maxY; y++) {
            wall(minX - 1, y);
            wall(maxX + 1, y);
         }
      }

      public Maze build() {
         return new Maze(this);
      }
   }
}
