open util/boolean

sig Surroundings {
	north: Bool,
	east: Bool,
	west: Bool,
	south: Bool
}

sig Rule {
	state: Int,
	sur: Surroundings,
	next: Action
}

abstract sig Move{}
one sig N, E, W, S, X extends Move{}

sig Action {
	state: Int,
	move: Move
}

fact validState {
	all r:Rule | r.state >=0 && r.state < 100
	all a:Action | a.state >= 0 && a.state < 100
}

fact initialState {
	some r:Rule | r.state=0
}

fact neverStucked {
	not some s:Surroundings | s.north = True && s.east = True && s.west = True && s.south = True
}

fact allActionsHaveRule {
	all a:Action | some r:Rule | r.next = a
}

fact stateNumber {
	all r:Rule | some r2:Rule | r.state !=0 => r.state.minus[1] = r2.state 
	all a:Action | some a2:Action | a.state !=0 => a.state.minus[1] = a2.state 
	all a:Action | some r:Rule | a.state = r.state 
	all r:Rule | some a:Action | a.state = r.state 
}

pred sameAction {
	some r1,r2:Rule | r1.next = r2.next && r1 != r2
}

fact neverHoldStill {
	all r:Rule | r.next.move = X => r.next.state != r.state
}

/*
fact cardState2 {
	all r:Rule | some r2:Rule | r.state != r2.state 
}
*/

run {
//#Surroundings > 3
//sameAction
} for 5
