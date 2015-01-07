/**
  * Defines a rule that Picobot applies
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
  * Defines surroundings of Picobot
  * True means that Picobot can't move towards that direction, False that it can
  */
sig Surroundings {
	north: EnvironmentInfo,
	east: EnvironmentInfo,
	west: EnvironmentInfo,
	south: EnvironmentInfo
}

/**
  * Defines Picobot's action to apply
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
abstract sig Move{}
one sig N, E, W, S, X extends Move{}

/**
  * Define Picobot's environment information where :
  *		+ dN refers to north direction blocked
  *		+ dE refers to east direction blocked
  *		+ dW refers to west direction blocked
  *		+ dS refers to south direction blocked
  *		+ dX refers to a direction where Picobot is stuck or not doesn't matter
  */
abstract sig EnvironmentInfo{}
one sig dX, dN, dE, dW, dS extends EnvironmentInfo{}


/************ Facts ************/
/**
  * The north direction for every surroundings can only contain values dN and dX
  */
fact validNorthDirectionSurroundings {
	all s : Surroundings | s.north = dN || s.north = dX
}

/**
  * The east direction for every surroundings can only contain values dE and dX
  */
fact validEastDirectionSurroundings {
	all s : Surroundings | s.east = dE || s.east = dX
}

/**
  * The west direction for every surroundings can only contain values dW and dX
  */
fact validWestDirectionSurroundings {
	all s : Surroundings | s.west = dW || s.west = dX
}

/**
  * The south direction for every surroundings can only contain values dS and dX
  */
fact validSouthDirectionSurroundings {
	all s : Surroundings | s.south = dS || s.south = dX
}

/**
  * All state numbers are between 0 and 99
  */
fact validState {
	all r:Rule | r.current_state >=0 && r.current_state < 100
	all a:Action | a.next_state >= 0 && a.next_state < 100
}

/**
  * At least one rule starts in state 0
  */
fact initialState {
	some r:Rule | r.current_state=0
}

/**
  * Rules and Actions use consecutive state numbers
  */
fact consecutiveStateNumbers {
	all r1:Rule | some r2:Rule | r1.current_state !=0 => r1.current_state.minus[1] = r2.current_state 
	all a1:Action | some a2:Action | a1.next_state !=0 => a1.next_state.minus[1] = a2.next_state 
}

/**
  * No dead-end state number
  */
fact consistentStateNumbers {
	all a:Action | some r:Rule | a.next_state = r.current_state 
	all r:Rule | some a:Action | a.next_state = r.current_state 
}

/**
  * No Surroundings where Picobot is stuck
  */
fact neverStuck {
	no s:Surroundings | s.north = dN && s.east = dE && s.west = dW && s.south = dS
}

/**
  * No Rule can ask to hold still without changing current state number
  */
fact neverHoldStill {
	all r:Rule | r.next.move = X => r.next.next_state != r.current_state
}

/**
  * Every Action is linked to a Rule
  */
fact allActionsHaveRule {
	all a:Action | some r:Rule | r.next = a
}

/**
  * Every Surroundings are linked to a Rule
  */
fact allSurroundingsHaveRule {
	all s:Surroundings | some r:Rule | r.env = s
}

/**
  * Two Rules can't have same state number and Surroundings
  */
fact incompatibleRule {
	all r1: Rule | all r2:Rule-r1 | r1.current_state = r2.current_state => r1.env != r2.env 
}

/**
  * No rule leads into a wall
  */
fact noMoveIntoAWall {
	all r:Rule | r.env.north = dN => r.next.move != N
	all r:Rule | r.env.east = dE => r.next.move != E
	all r:Rule | r.env.west = dW => r.next.move != W
	all r:Rule | r.env.south = dS => r.next.move != S
}

/**
  * Every Surroundings are different
  */
fact allDistinctSurroundings {
	all s1:Surroundings | all s2:Surroundings-s1 | (s1.north != s2.north) || (s1.east != s2.east) || (s1.west!= s2.west) || (s1.south!= s2.south)  
}

/**
  * Limit number of inaccessible rules
  */
fact preventInaccessibleRule {
	all r1:Rule | some r2:Rule | r1.current_state!=0 => r1.current_state != r2.current_state && r2.next.next_state = r1.current_state
}


/************ Predicates ************/

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

/**
  * Test that allDistinctSurroundings works correctly
  */
pred check_allDistinctSurroundings {
	some s1:Surroundings | some s2:Surroundings-s1 | (s1.north = s2.north) && (s1.east = s2.east) && (s1.west= s2.west) && (s1.south= s2.south)
}


run {
#Rule = 3
cardState3
//check_allDistinctSurroundings
} for 3
