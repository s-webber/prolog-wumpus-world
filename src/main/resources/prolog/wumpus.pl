% This file contains logic to navigate an agent around a Wumpus World.
% The entry points - called from the PrologGameController Java class - are reset_agent_state/0 process/2.

?- arithmetic_function(estimate_cost/2).

% The agents home location.
% This is the location where the agent starts and where they must return in order to climb out of the maze.
home(0:0).

% Resets the agent state.
% The agent starts facing North in the home location.
reset_agent_state :-
  writeln('Reset agent state'),
  retractall(agent_state(_,_)),
  retractall(arrow_state(_)),
  retractall(visited(_)),
  retractall(to_visit(_)),
  retractall(possible_wumpus(_)),
  retractall(stench(_)),
  retractall(wall(_)),
  retractall(have_gold),
  retractall(route(_)),
  home(HomeCoord),
  assert(agent_state(HomeCoord,north)),
  assert(arrow_state(have)).

% direction logic
% ***********************************************************
direction(north,0,-1).
direction(east,1,0).
direction(south,0,1).
direction(west,-1,0).

opposite_direction(north,south).
opposite_direction(south,north).
opposite_direction(east,west).
opposite_direction(west,east).

turn_right(north,east).
turn_right(east,south).
turn_right(south,west).
turn_right(west,north).

turn_left(X,Y) :- turn_right(Y,X).

adjacent_direction(X,Y,right) :- turn_right(X,Y).
adjacent_direction(X,Y,left) :- turn_left(X,Y).

% state handling logic
% ***********************************************************
create_state(state(X:Y,Direction),state(NextX:NextY,Direction)) :-
  number(X),
  !,
  direction(Direction,IncX,IncY),
  NextX is X+IncX,
  NextY is Y+IncY.
create_state(state(PreviousX:PreviousY,Direction),state(X:Y,Direction)) :-
  direction(Direction,IncX,IncY),
  PreviousX is X-IncX,
  PreviousY is Y-IncY.

update_agent_state(Coord,Direction) :-
  retractall(agent_state(_:_,_)),
  assert(agent_state(Coord,Direction)).

update_arrow_state(State) :-
  retractall(arrow_state(_)),
  assert(arrow_state(State)).

adjacent_to_square(X:Y, Output) :-
  direction(_,IncX,IncY),
  NewX is X + IncX,
  NewY is Y + IncY,
  Output = NewX:NewY.

unknown(Coord) :-
  \+ visited(Coord),
  \+ wall(Coord),
  \+ to_visit(Coord).

add_adjacent_to_visit(Coord) :-
  adjacent_to_square(Coord,Adjacent),
  unknown(Adjacent),
  \+ ((wumpus_alive,possible_wumpus(Adjacent))), % TODO should not have to use double brackets
  assert(to_visit(Adjacent)),
  retractall(possible_wumpus(Adjacent)).

add_adjacent_to_stench(Coord) :-
  findall(Adjacent,adjacent_to_square(Coord,Adjacent),Adjacents),
  possible_wumpus(Possible),
  \+ member(Possible, Adjacents),
  retract(possible_wumpus(Possible)).
add_adjacent_to_stench(Coord) :-
  \+ possible_wumpus(_),
  adjacent_to_square(Coord,Adjacent),
  unknown(Adjacent),
  assert(possible_wumpus(Adjacent)).

% Maintain a record of where we have visited and where we have detected a stench percept.
add_visited(Coord,Percepts) :-
  is_stench(Percepts),
  \+ stench(Coord),
  assert(stench(Coord)),
  add_adjacent_to_stench(Coord),
  fail.
add_visited(Coord,Percepts) :-
  \+ visited(Coord),
  assert(visited(Coord)),
  retractall(to_visit(Coord)),
  retractall(possible_wumpus(Coord)),
  \+ is_breeze(Percepts),
  add_adjacent_to_visit(Coord).

add_wall(Coord) :-
  \+ wall(Coord),
  assert(wall(Coord)),
  retractall(to_visit(Coord)),
  retractall(possible_wumpus(Coord)).

is_glitter(Percepts) :- member(glitter, Percepts).

is_breeze(Percepts) :- member(breeze, Percepts).

is_stench(Percepts) :- member(stench, Percepts).

is_bump(Percepts) :- member(bump, Percepts).

is_scream(Percepts) :- member(scream, Percepts).

wumpus_dead :-
  arrow_state(hit).

wumpus_alive :-
  \+ wumpus_dead.

can_hunt :-
  arrow_state(have),
  stench(_),
  !.

should_hunt :-
  can_hunt,
  \+ have_gold,      % if have gold then do not need to kill wumpus, should head home instead
  \+ to_visit(_).  % should visit all safe squares before attempting to kill the wumpus  

not_adjacent_to_every_stench(Coord) :-
  stench(StenchCoord),
  \+ create_state(state(StenchCoord,_),state(Coord,_)).

adjacent_to_every_stench(Coord) :-
  \+ not_adjacent_to_every_stench(Coord).

adjacent_to_no_stench(Coord) :-
  create_state(state(Coord,_),state(AdjacentCoord,_)),
  visited(AdjacentCoord),
  \+ stench(AdjacentCoord).

possibly_wumpus(Coord) :-
  wumpus_alive, % only worry about stench if wumpus alive
  % find first stench and check all cells adjacent to it
  stench(StenchCoord),
  !,
  create_state(state(StenchCoord,_),state(Coord,_)),
  \+ visited(Coord), % if we have already visited the square then it cannot be the location of the wumpus
  \+ wall(Coord),    % if we know square contains a wall then it cannot be the location of the wumpus
  \+ adjacent_to_no_stench(Coord), % if next to any visited square without a stench then cannot be the location of the wumpus
  adjacent_to_every_stench(Coord).

% a square is safe if we have already visited it or know it is safe to visit
safe(state(Coord,_)) :- once((visited(Coord);to_visit(Coord))).

% should exit if have gold or no where left to visit
should_exit :-
  have_gold,
  !.
should_exit :-
  \+ can_hunt, 
  \+ to_visit(_).

should_fire :-
  should_hunt,
  agent_state(Coord,Direction),
  stench(Coord),
  create_state(state(Coord,Direction),state(TargetCoord,Direction)),
  possibly_wumpus(TargetCoord).
  
% path finding code
% ***********************************************************
set_route(Path) :-
  retractall(route(_)),
  assert(route(Path)).

find_path(find_gold,Targets) :-
  \+ have_gold,
  findall(Coord,to_visit(Coord),Targets).
find_path(hunt_wumpus,Targets) :-
  should_hunt,
  findall(Coord,stench(Coord),Targets).
find_path(go_home,Targets) :-
  home(HomeCoord),
  Targets=[HomeCoord].

manhattan_distance(state(FromX:FromY,_),ToX:ToY,Cost) :-
  Cost is abs(FromX-ToX) + abs(FromY-ToY).

estimate_cost(State,Targets,Cost) :- 
  maplist(manhattan_distance(State),Targets,Costs),
  min_list(Costs,Cost).

% dont allow 180 degree turn, would just be returning to an already visited state
next_step(state(X:Y,Direction),Neighbour,1) :-
  create_state(state(X:Y,Direction), Neighbour).
next_step(state(X:Y,Direction),Neighbour,2) :-
  adjacent_direction(Direction,NewDirection,_),
  create_state(state(X:Y,NewDirection), Neighbour).

next_path(route(Path,Cost,_),Child,Targets) :-
  [Head|_] = Path,
  next_step(Head,Neighbour,StepCost),
  safe(Head),
  \+ member(Neighbour,Path), % avoid returning to already visited cell
  NewPath = [Neighbour|Path],
  NewCost is Cost+StepCost,
  Estimate is NewCost+estimate_cost(Neighbour,Targets),
  Child = route([Neighbour|Path],NewCost,Estimate).

insert_into_sorted_list(Element, [], Result) :-
  Result = [Element].
insert_into_sorted_list(Element,[Head|Tail],Result) :-
  Element = route(_,_,ElementCost),
  Head = route(_,_,HeadCost),
  ElementCost =< HeadCost,
  Result = [Element,Head|Tail],
  !.
insert_into_sorted_list(Element,[Head|Tail],Result) :-
  Result = [Head|SubResult],
  insert_into_sorted_list(Element,Tail,SubResult).

add_new_paths([],Paths,Paths).
add_new_paths([Head|Tail],Paths,Sorted) :-
  once(insert_into_sorted_list(Head,Paths,UpdatedPaths)), % shouldnt need to wrap in once
  add_new_paths(Tail,UpdatedPaths,Sorted).

search([BestPath|_],Targets,BestPath) :-
  BestPath=route([state(Coord,_)|_],_,_),
  once(member(Coord,Targets)).
search([Path|Tail],Targets,BestPath) :-
  findall(Child,next_path(Path,Child,Targets),Children),
  add_new_paths(Children,Tail,Sorted),
  search(Sorted,Targets,BestPath).

initial_search_tree(Targets,InitialSearch) :-
  agent_state(Coord,Direction),
  FirstStep = state(Coord,Direction),
  FirstStepEstimate is estimate_cost(FirstStep,Targets),
  FirstRoute=route([FirstStep],0,FirstStepEstimate),
  opposite_direction(Direction,OppositeDirection),
  ReverseStep1 = state(Coord,OppositeDirection),
  create_state(ReverseStep1,ReverseStep2),
  ReverseCost = 3, % 3 = cost of reverse (2*right or 2*left, + 1 forward)
  ReverseStepEstimate is ReverseCost + estimate_cost(ReverseStep2,Targets),
  (safe(ReverseStep2) ->
    ReverseRoute=route([ReverseStep2,ReverseStep1],ReverseCost,ReverseStepEstimate),
    (FirstStepEstimate>ReverseStepEstimate ->
      InitialSearch=[ReverseRoute,FirstRoute];
      InitialSearch=[FirstRoute,ReverseRoute]);
    InitialSearch=[FirstRoute]).

set_path :-
  find_path(Goal,Targets),
  Targets \= [], % need to have at least one target to aim for
  initial_search_tree(Targets,Paths),
  once(search(Paths,Targets,route(Best,_,_))),
  reverse(Best,[_CurrentLocation|Reversed]),
  write('Goal: '), % TODO move to log_state
  writeln(Goal), % TODO move to log_state
  set_route(Reversed),
  !.

% update/2, log_state and process/2 predicates
% ***********************************************************
% If we detect a scream percept then we know the Wumpus has been killed.
update(Percepts, Action) :-
  is_scream(Percepts),
  update_arrow_state(hit),
  agent_state(Coord,Direction),
  % As the wumpus is dead then we know it is safe to move to the square into which we fired the arrow.
  create_state(state(Coord,Direction),NewState),
  set_route([NewState]),
  fail.
% If we detect a buknow that our last move caused us to bump into a wall.
update(Percepts, Action) :-
  is_bump(Percepts),
  % Revert location to previous value prior to the forward action that caused the bump.
  agent_state(Coord,Direction),
  create_state(state(NewCoord,_),state(Coord,Direction)),
  update_agent_state(NewCoord,Direction),
  add_wall(Coord),
  retractall(route(_)), % retract any planned route as has been invalidated by hitting a wall
  fail.
% Record that we have visited the current location.
update(Percepts, Action) :-
  agent_state(Coord,_),
  add_visited(Coord,Percepts),
  fail.
% If we detect a glitter percept then we know we are in the same location as the gold, and so should take it.
update(Percepts, Action) :-
  is_glitter(Percepts), 
  assert(have_gold),
  Action = take,
  !.
% If in the home location and should exit then climb out of the maze.
update(Percepts, Action) :-
  \+ route([_|_]),
  should_exit,
  home(HomeCoord),
  agent_state(HomeCoord,_),
  Action = climb,
  !.
% If we detect a stench percept and a) dont have gold, b) have arrow and c) have nowhere left to visit then fire at the wumpus
update(Percepts, Action) :-
  should_fire,
  update_arrow_state(fired),
  % If not a breeze then after firing the arrow it will be safe to move forward -
  % as the square in front will not contain a pit (as no breeze) and will contain either no wumpus or a dead wumpus.
  (is_breeze(Percepts) -> 
    true; 
    agent_state(Coord,Direction),
    create_state(state(Coord,Direction),NewState),
    set_route([NewState])),
  Action=fire,
  !.
% If detect a stench when hunting the wumpus then turn to face them so they will be killed when we fire the arrow.
update(Percepts,Action) :-
  should_hunt,
  agent_state(Coord,Direction),
  stench(Coord),
  turn_right(Direction,NewDirection),
  create_state(state(Coord,NewDirection),state(NewCoord,_)),
  (possibly_wumpus(NewCoord) ->
    update_agent_state(Coord,NewDirection),Action=right;
    turn_left(Direction,LeftDirection),update_agent_state(Coord,LeftDirection),Action=left),
  !.
% If not already following a path then create one.
update(Percepts, Action) :-
  \+ route([_|_]),
  set_path,
  fail.
% If have path and next step is in front of agent then move forward.
update(Percepts, Action) :-
  route([state(NewCoord,Direction)|T]),
  agent_state(_,Direction),
  retractall(route(_)),
  assert(route(T)),
  update_agent_state(NewCoord,Direction),
  Action=forward,
  !.
% If have path and next step is to side of agent then turn to face it.
update(Percepts, Action) :-
  route([state(_,NewDirection)|_]),
  agent_state(Coord,Direction),
  adjacent_direction(Direction,NewDirection,Action),
  update_agent_state(Coord,NewDirection),
  !.
% If have path and next step is behind agent then start to turn around.
update(Percepts, Action) :- % 180 degree turn required
  route([_|_]),
  agent_state(Coord,Direction),
  turn_right(Direction,NewDirection),
  update_agent_state(Coord,NewDirection),
  Action=right,
  !.
% Should never get here.
update(Percepts, Action) :-
  writeln('Stuck!'),
  Action = stuck.

log_state :- findall(X,visited(X),Result), write('Have visited: '), writeln(Result), fail.
log_state :- findall(X,to_visit(X),Result), write('Safe to visit: '), writeln(Result), fail.
log_state :- findall(X,wall(X),Result), write('Walls: '), writeln(Result), fail.
log_state :- findall(X,possible_wumpus(X),Result), length(Result,Length), Length>0, write('Possible wumpus: '), writeln(Result), fail.
log_state :- route(Result), length(Result,Length), Length>0, write('Follwing path: '), writeln(Result), fail.
log_state.

% This predicate is called from the PrologGameController Java class.
% The first argument will be a list containing atoms representing each of the percepts detected on this move.
% The second argument will be a variable that process/2 will unify with the action the agent should take next.
process(Percepts, Action) :-
  write('Input: '),
  writeln(Percepts),
  update(Percepts, Action),
  log_state,
  write('Output: '),
  writeln(Action).

