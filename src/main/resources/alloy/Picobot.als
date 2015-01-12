/**
  * Define a rule that Picobot applies 
  * state: Picobot's current state
  * env: Surroundings of Picobot
  * next: Action to apply
  */
sig Rule {
	current_state: Int,
	env: Surroundings,
	next: Action
}

/**
  * Define surroundings of Picobot 
  * True means that Picobot can't move towards that direction, False that it can
  */
sig Surroundings {
	north: Wall,
	east: Wall,
	west: Wall,
	south: Wall
}

/**
  * Define Picobot's action to apply 
  * state: new state once action is performed
  * move: Picobot's move to perform
  */
sig Action {
	next_state: Int,
	move: Move
}

/**
  * Define a move following a compass direction (North, East, West, South) or no move (X)
  */
enum Move {N, E, W, S, X}

/**
  * Define whether there is a wall or not
  */
enum Wall {True, False}

/************ Facts ************/

/**
  * All state numbers are between 0 and 99
  */
fact validState {
	all r:Rule | r.current_state >= 0 && r.current_state < 100
	all a:Action | a.next_state >= 0 && a.next_state < 100
}

/**
  * At least one Rule starts in state 0 with an actual Move
  */
fact initialState {
	some r:Rule | r.current_state = 0 && r.next.move != X
}

/**
  * Two Rules can't have same state number and Surroundings
  */
fact noDuplicatedRule {
	all r1: Rule | all r2:Rule-r1 | r1.current_state = r2.current_state => r1.env != r2.env
}

/**
  * Every Surroundings are different
  */
fact noDuplicatedSurroundings {
	all s1:Surroundings | no s2:Surroundings-s1 | s1.north = s2.north && s1.east = s2.east && s1.west = s2.west && s1.south = s2.south  
}

/**
  * No dead-end state number
  */
fact consistentStateNumbers {
	all a:Action | some r:Rule | a.next_state = r.current_state 
}

/**
  * Every Action is linked to a Rule
  */
fact allActionsHaveRule {
	all a:Action | some r:Rule | r.next = a
}


/************ Predicates for Optimization ************/

/**
  * No Rule can ask to hold still without changing current state number
  */
pred neverHoldStill {
	all r:Rule | r.next.move = X => r.next.next_state != r.current_state
}

/**
  * No Surroundings where Picobot is stucked
  */
pred neverStucked {
	no s:Surroundings | s.north = True && s.east = True && s.west = True && s.south = True
}

/**
  * No rule leads into a wall
  */
pred noMoveIntoAWall {
	all r:Rule | r.env.north = True => r.next.move != N
	all r:Rule | r.env.east = True => r.next.move != E
	all r:Rule | r.env.west = True => r.next.move != W
	all r:Rule | r.env.south = True => r.next.move != S
}

/**
  * Limit number of inaccesible rules
  */
pred preventInaccessibleRule {
	all r1:Rule | some r2:Rule | r1.current_state!=0 => r1.current_state != r2.current_state && r2.next.next_state = r1.current_state
}

/************ Cosmectic Predicates ************/

/**
  * Every Surroundings are linked to a Rule
  */
pred allSurroundingsHaveRule {
	all s:Surroundings | some r:Rule | r.env = s
}

/**
  * Two Actions can't have same state number and Move
  */
pred noDuplicatedAction {
	all a1: Action | all a2:Action-a1 | a1.next_state = a2.next_state => a1.move != a2.move
}

/**
  * Rules use consecutive state numbers
  */
pred consecutiveStateNumbers {
	all r1:Rule | some r2:Rule | r1.current_state != 0 => r1.current_state.minus[1] = r2.current_state 
}

/************ 'Number of states' Predicates ************/

/**
  * Force to use only one state number
  */
pred cardState1 {
	all r1:Rule | r1.current_state = 0
}

/**
  * Force to use at least 2 different state numbers
  */
pred cardState2 {
	all r1:Rule | some r2:Rule | r1.current_state != r2.current_state 
}

/**
  * Force to use at least 3 different state numbers
  */
pred cardState3 {
	all r1:Rule | some r2,r3:Rule | r1.current_state != r2.current_state && r2.current_state != r3.current_state && r3.current_state != r1.current_state
}

/************ Run ************/

run {
//#Rule = 50
//cardState2

preventInaccessibleRule
neverHoldStill
neverStucked
noMoveIntoAWall

/*
// Non important predicates
allSurroundingsHaveRule
noDuplicatedAction
consecutiveStateNumbers
*/
} for 25 //but 15 Surroundings//, 18 Rule
