package org.projog.wumpus.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class CoordinateTest {
   @Test
   public void testMove() {
      int x = 3;
      int y = 5;
      Coordinate c = new Coordinate(x, y);

      assertEquals(new Coordinate(x, y - 1), c.move(Direction.NORTH));

      // assert calling move doesn't alter the state of the Coordinate
      assertEquals(new Coordinate(x, y), c);

      assertEquals(new Coordinate(x + 1, y), c.move(Direction.EAST));

      assertEquals(new Coordinate(x, y + 1), c.move(Direction.SOUTH));

      assertEquals(new Coordinate(x - 1, y), c.move(Direction.WEST));
   }

   @Test
   public void testMinus() {
      Coordinate c1 = new Coordinate(3, 5);
      Coordinate c2 = new Coordinate(-9, 12);

      assertEquals(c1, c1.minus(new Coordinate(0, 0)));
      assertEquals(new Coordinate(-3, -5), new Coordinate(0, 0).minus(c1));

      assertEquals(new Coordinate(12, -7), c1.minus(c2));

      // assert calling minus doesn't alter the state of either Coordinate
      assertEquals(new Coordinate(3, 5), c1);
      assertEquals(new Coordinate(-9, 12), c2);

      assertEquals(new Coordinate(-12, 7), c2.minus(c1));
   }

   @Test
   public void testEquals() {
      int x = 3;
      int y = 5;
      Coordinate c = new Coordinate(x, y);

      assertEquals(c, c);
      assertEquals(c, new Coordinate(x, y));

      assertNotEquals(c, new Coordinate(x - 1, y)); // different x
      assertNotEquals(c, new Coordinate(x + 1, y)); // different x
      assertNotEquals(c, new Coordinate(x, y - 1)); // different y
      assertNotEquals(c, new Coordinate(x, y + 1)); // different y
      assertNotEquals(c, new Coordinate(y, x)); // x and y switched
      assertNotEquals(c, new Coordinate(-x, -y));// x and y negated
   }

   @Test
   public void testHashCode() {
      int x = 3;
      int y = 5;
      Coordinate c = new Coordinate(x, y);

      assertEquals(c.hashCode(), new Coordinate(x, y).hashCode());

      assertNotEquals(c.hashCode(), new Coordinate(y, x).hashCode());// x and y switched
   }

   @Test
   public void testToString() {
      assertEquals("3,5", new Coordinate(3, 5).toString());
      assertEquals("-3,7", new Coordinate(-3, 7).toString());
   }
}
