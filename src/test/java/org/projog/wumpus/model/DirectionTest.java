package org.projog.wumpus.model;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class DirectionTest {
   @Test
   public void right() {
      assertSame(Direction.NORTH.right(), Direction.EAST);
      assertSame(Direction.EAST.right(), Direction.SOUTH);
      assertSame(Direction.SOUTH.right(), Direction.WEST);
      assertSame(Direction.WEST.right(), Direction.NORTH);
   }

   @Test
   public void left() {
      assertSame(Direction.NORTH.left(), Direction.WEST);
      assertSame(Direction.WEST.left(), Direction.SOUTH);
      assertSame(Direction.SOUTH.left(), Direction.EAST);
      assertSame(Direction.EAST.left(), Direction.NORTH);
   }
}
