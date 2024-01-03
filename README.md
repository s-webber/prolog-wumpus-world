# prolog-wumpus-world
[![Build Status](https://github.com/s-webber/prolog-wumpus-world/actions/workflows/github-actions.yml/badge.svg)](https://github.com/s-webber/prolog-wumpus-world/actions/)

## About

This project contains a simulator and agent for a Wumpus World. Wumpus World is a problem discussed in [Artificial Intelligence: A Modern Approach](https://en.wikipedia.org/wiki/Artificial_Intelligence:_A_Modern_Approach), a university textbook on artificial intelligence.

The simulator and user interface is written in Java. The logic for controlling the agent is implemented in [Prolog](https://en.wikipedia.org/wiki/Prolog). The open source [Projog](http://projog.org "Prolog interpreter for Java") library is used to integrate Java with Prolog.

The images used by the application are taken from [Bootstrap Icons](https://icons.getbootstrap.com/), which is licensed under the MIT license.

## How to run the application

You can build the application using the command:

```
./mvnw package
```

You can then run the application using the command:

```
java -jar target/prolog-wumpus-world-0.1.0-SNAPSHOT.jar
```

## Rules


### Components
The world consists of a 4x4 grid of squares. The squares can contain the following items:

1. Agent. The agent moves around the 4x4 grid. The agent has an arrow that they can fire once.
2. Gold. If the agent enters a square that contains the gold then the agent can take it.
3. A wumpus. If the agent enters the square that contains the wumpus, and the wumpus is alive, then the agent is eaten and cannot continue. If the agent fires the arrow and the square immediately in front of the agent contains the wumpus then the wumpus dies.
4. Pits. If the agent enters a square that contains a pit then the agent falls into it and cannot continue.
5. Walls. If the agent attempts to enter a square that contains a wall then they will not be able to.

The wumpus, pits and walls are static - they remain in the same square they were allocated to when the world was created.

### Goal
The aim of the problem is to navigate the agent around the world to:

1. Find the square that contains the gold.
2. Take the gold.
3. Return to the square that the agent started from.
4. Climb out of the world.

### Actions
To interact with the world the agent can perform the following actions:

1. `FORWARD` Move into the square directly in front of where the agent is facing.
2. `RIGHT` Turn right.
3. `LEFT` Turn left.
4. `TAKE` Take the gold. Can only do this if in the same square as the gold.
5. `FIRE` Fire the arrow. Can only do this if have not already fired the arrow.
6. `CLIMB` Climb out of the world. Can only do this if located in the square the agent started from.

### Percepts
To help the agent reason about the world it receives percepts. The percepts the agent can receive are:

1. `STENCH` Indicates that the agent is next to a square that contains the wumpus or in the square that contains the wumpus.
2. `BREEZE` Indicates that the agent is next to a square that contains a pit.
3. `GLITTER` Indicates that the agent is in the same square as the gold.
4. `BUMP` Indicates that the agent's previous action (to move forward) caused them to bump into a wall.
5. `BREEZE` Indicates that the agent's previous action (to fire the arrow) killed the wumpus. 

## Reusing the simulator

If someone would like to implement their own logic to navigate the agent around the world then they can do so by:

### Implementing the agent logic in Prolog
If implementing the logic using Prolog then they can replace the logic in `src/main/resources/prolog/wumpus.pl`. They will need to provide a `reset_agent_state/0` predicate which resets the agent's game state and a `process/2` predicate. The first argument of `process/2` will be a list of percepts. The second argument will be a variable that should be unified with an atom that indicates which action the agent should perform next.

### Implementing the agent logic in Java (or another JVM language)
If implementing the logic in a different language than Prolog then they will need to implement their own version of `org.projog.wumpus.GameController`. They will then need to alter `org.projog.wumpus.WumpusWorld` to create an instance of their implementation instead of `PrologGameController`. 

## Resources

- [Calling Prolog from Java](http://projog.org/calling-prolog-from-java.html)
- [Bootstrap Icons GitHub](https://github.com/twbs/icons)
