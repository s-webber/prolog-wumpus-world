package org.projog.wumpus.model;

/** A specific location within the maze. */
public class Coordinate {
   private final int x;
   private final int y;

   public Coordinate(int x, int y) {
      this.x = x;
      this.y = y;
   }

   Coordinate move(Direction d) {
      return new Coordinate(x + d.getX(), y + d.getY());
   }

   public Coordinate minus(Coordinate c) {
      return new Coordinate(x - c.x, y - c.y);
   }

   @Override
   public String toString() {
      return x + "," + y;
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof Coordinate) {
         Coordinate other = (Coordinate) o;
         return x == other.x && y == other.y;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return x + (31 * y);
   }
}
