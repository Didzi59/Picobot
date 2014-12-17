open util/boolean
//Use this if you can't import boolean:
//enum Bool {True, False}

/**
  * Define a rule that Picobot applies 
  * state: Picobot's current state
  * env: Surroundings of Picobot
  * next: Action to apply
  */
sig Rule {
	state: Int,
	env: Surroundings,
	next: Action
}

/**
  * Define surroundings of Picobot 
  * True means that Picobot can't move towards that direction, False that it can
  */
sig Surroundings {
	north: Bool,
	east: Bool,
	west: Bool,
	south: Bool
}

/**
  * Define Picobot's action to apply 
  * state: new state once action is performed
  * move: Picobot's move to perform
  */
sig Action {
	state: Int,
	move: Move
}

/**
  * Define a move following a compass direction (North, East, West, South) or no move (X)
  */
abstract sig Move{}
one sig N, E, W, S, X extends Move{}

/************ Facts ************/


/**
  * All state numbers are between 0 and 99
  */
fact validState {
	all r:Rule | r.state >=0 && r.state < 100
	all a:Action | a.state >= 0 && a.state < 100
}

/**
  * At least one rule starts in state 0
  */
fact initialState {
	some r:Rule | r.state=0
}

/**
  * Rules and Actions use consecutive state numbers
  */
fact consecutiveStateNumbers {
	all r1:Rule | some r2:Rule | r1.state !=0 => r1.state.minus[1] = r2.state 
	all a1:Action | some a2:Action | a1.state !=0 => a1.state.minus[1] = a2.state 
}

/**
  * No dead-end state number
  */
fact consistentStateNumbers {
	all a:Action | some r:Rule | a.state = r.state 
	all r:Rule | some a:Action | a.state = r.state 
}

/**
  * No Surroundings where Picobot is stucked
  */
fact neverStucked {
	no s:Surroundings | s.north = True && s.east = True && s.west = True && s.south = True
}

/**
  * No Rule can ask to hold still without changing current state number
  */
fact neverHoldStill {
	all r:Rule | r.next.move = X => r.next.state != r.state
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
	all r1: Rule | all r2:Rule-r1 | r1.state = r2.state => r1.env != r2.env 
}

/**
  * No rule leads into a wall
  */
fact noMoveIntoAWall {
	all r:Rule | r.env.north = True => r.next.move != N
	all r:Rule | r.env.east = True => r.next.move != E
	all r:Rule | r.env.west = True => r.next.move != W
	all r:Rule | r.env.south = True => r.next.move != S
}

/**
  * Every Surroundings are different
  */
fact allDistinctSurroundings {
	all s1:Surroundings | all s2:Surroundings-s1 | (s1.north != s2.north) || (s1.east != s2.east) || (s1.west!= s2.west) || (s1.south!= s2.south)  
}

/**
  * Limit number of inaccesible rules
  */
fact preventInaccessibleRule {
	all r1:Rule | some r2:Rule | r1.state!=0 => r1.state != r2.state && r2.next.state = r1.state
}

/************ Predicates ************/

/**
  * Force to use at least 2 different state numbers
  */
pred cardState2 {
	all r1:Rule | some r2:Rule | r1.state != r2.state 
}

/**
  * Force to use at least 3 different state numbers
  */
pred cardState3 {
	all r1:Rule | some r2,r3:Rule | r1.state != r2.state && r2.state != r3.state && r3.state != r1.state
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
