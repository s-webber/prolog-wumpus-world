package org.projog.wumpus.model;

/** The direction the agent is facing. */
public enum Direction {
   NORTH(0, -1),
   EAST(1, 0),
   SOUTH(0, 1),
   WEST(-1, 0);

   private final int x;
   private final int y;

   Direction(int x, int y) {
      this.x = x;
      this.y = y;
   }

   Direction right() {
      int i = ordinal() + 1;
      if (i == values().length)
         i = 0;
      return values()[i];
   }

   Direction left() {
      int i = ordinal();
      if (i == 0)
         i = values().length;
      return values()[i - 1];
   }

   int getX() {
      return x;
   }

   int getY() {
      return y;
   }
}
