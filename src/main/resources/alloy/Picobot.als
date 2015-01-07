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
	current_state: Int,
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
	next_state: Int,
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
	all r:Rule | r.current_state >=0 && r.current_state < 100
	all a:Action | a.next_state >= 0 && a.next_state < 100
}

/**
  * At least one Rule starts in state 0
  */
fact initialState {
	some r:Rule | r.current_state=0
}

/**
  * At least one Action has an actual Move
  */
fact movingAction {
	some a:Action | a.move!=X
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
  * No Surroundings where Picobot is stucked
  */
fact neverStucked {
	no s:Surroundings | s.north = True && s.east = True && s.west = True && s.south = True
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
  * No rule leads into a wall
  */
fact noMoveIntoAWall {
	all r:Rule | r.env.north = True => r.next.move != N
	all r:Rule | r.env.east = True => r.next.move != E
	all r:Rule | r.env.west = True => r.next.move != W
	all r:Rule | r.env.south = True => r.next.move != S
}

/**
  * Two Rules can't have same state number and Surroundings
  */
fact noDuplicatedRule {
	all r1: Rule | all r2:Rule-r1 | r1.current_state = r2.current_state => r1.env != r2.env 
}

/**
  * Two Actions can't have same state number and Move
  */
fact noDuplicatedAction{
	all a1: Action | all a2:Action-a1 | a1.next_state = a2.next_state => a1.move != a2.move
}

/**
  * Every Surroundings are different
  */
fact noDuplicatedSurroundings {
	all s1:Surroundings | all s2:Surroundings-s1 | (s1.north != s2.north) || (s1.east != s2.east) || (s1.west!= s2.west) || (s1.south!= s2.south)  
}

/**
  * Limit number of inaccesible rules
  */
fact preventInaccessibleRule {
	all r1:Rule | some r2:Rule | r1.current_state!=0 => r1.current_state != r2.current_state && r2.next.next_state = r1.current_state
}

/************ Predicates ************/

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

/**
  * Test that noDuplicatedSurroundings works correctly
  */
pred check_noDuplicatedSurroundings {
	some s1:Surroundings | some s2:Surroundings-s1 | (s1.north = s2.north) && (s1.east = s2.east) && (s1.west= s2.west) && (s1.south= s2.south)
}

/**
  * Force to use each Surroundings for each state number (NOT WORKING)
  *
pred forceAllSurroundings {
	#Surroundings = 15
	all s1:Surroundings | all s2:Surroundings-s1 | some r1:Rule | some r2:Rule | r1.current_state = r2.current_state && r1.env = s1 && r2.env = s2 
}
*/

run {
//#Surroundings < 16
//#Action = 2
//#Rule = 50
//cardState1
} for 10 //but 15 Surroundings, 18 Rule
