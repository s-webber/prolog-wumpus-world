package org.projog.wumpus.model;

/** Mutable object representing the current location and state of the agent. */
class Agent {
   private Coordinate location;
   private Direction direction = Direction.NORTH;
   private AgentState state = AgentState.ACTIVE;
   private ArrowState arrow = ArrowState.UNUSED;
   private boolean hasGold;

   Agent(Coordinate home) {
      this.location = home;
   }

   AgentState getState() {
      return state;
   }

   void setState(AgentState state) {
      this.state = state;
   }

   Coordinate getLocation() {
      return location;
   }

   void setLocation(Coordinate location) {
      this.location = location;
   }

   Direction getDirection() {
      return direction;
   }

   void setDirection(Direction direction) {
      this.direction = direction;
   }

   boolean isHasGold() {
      return hasGold;
   }

   void setHasGold(boolean hasGold) {
      this.hasGold = hasGold;
   }

   void turnLeft() {
      this.direction = direction.left();
   }

   void turnRight() {
      this.direction = direction.right();
   }

   void setHasMissed() {
      this.arrow = ArrowState.MISSED;
   }

   void setHasKilledWumpus() {
      this.arrow = ArrowState.HIT;
   }

   boolean haveArrow() {
      return arrow == ArrowState.UNUSED;
   }

   boolean haveNotKilledWumpus() {
      return !haveKilledWumpus();
   }

   boolean haveKilledWumpus() {
      return arrow == ArrowState.HIT;
   }
}
