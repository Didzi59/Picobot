package picobot.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IPicobot;
import picobot.interfaces.core.IRule;

public class Picobot implements IPicobot {

  protected int _xCoordinate = 0;

  public int getXCoordinate() {
    return _xCoordinate;
  }

  public void setInitialPosition(int xCoordinate, int yCoordinate) {
    this._xCoordinate = xCoordinate;    
    this._yCoordinate = yCoordinate;
  }
  
  protected int _yCoordinate = 0; 

  public int getYCoordinate() {
    return _yCoordinate;
  }

  public int[] getPosition() {
    int[] result = {getXCoordinate(), getYCoordinate()};
    return result;
  }


  protected Cell cellAtSouth(IMap _map) {
	//System.out.println("trace="+trace()+" map="+_map);
    return (Cell)((Map) _map).getCell(getXCoordinate(), getYCoordinate()-1);
  }

  protected Cell cellAtNorth(IMap _map) {
    return (Cell)((Map) _map).getCell(getXCoordinate(), getYCoordinate()+1);
  }
  protected Cell cellAtWest(IMap _map) {
    return (Cell)((Map) _map).getCell(getXCoordinate()-1, getYCoordinate());
  }
  protected Cell cellAtEast(IMap _map) {
    return (Cell)((Map) _map).getCell(getXCoordinate()+1, getYCoordinate());
  }
  
  
  private int _state = 0;
  /** Returns the internal state of the picobot.
   * Used by the graphical debugger.
   */
  public int getState() {
    return _state;
  }
  
  public void loadRules(List<IRule> rules) {
    _rules = rules;
  }
  
  public void apply(IRule rule) {
    Rule r = (Rule)rule;
    if (getState() == rule.getRequiredState()) {
      if (r.getDestinationKind() == RuleDirection.EAST) {
        _xCoordinate ++;
      }
      if (r.getDestinationKind() == RuleDirection.WEST) {
        _xCoordinate --;
      }
      if (r.getDestinationKind() == RuleDirection.SOUTH) {
        _yCoordinate --;
      }
      if (r.getDestinationKind() == RuleDirection.NORTH) {
        _yCoordinate ++;
      }
      _state = r.getTargetState();
    }
    //System.out.println("moved to "+_xCoordinate+","+_yCoordinate+ "("+_state+")");
  }


  private List<IRule> _rules = new ArrayList<IRule>();
  public List<IRule> getRules() {
    return _rules;
  }

  public IRule getApplicableRule(IMap _map) {
    if (((Map)_map).getCellKind(getXCoordinate(), getYCoordinate()).equals(CellKind.WALL.toString())) {
      throw new SimulationException("Sorry, I can not be at a place where there is a wall. Revise the matrix.");
    }
    for (IRule r : _rules) {
      Rule rule = (Rule)r;
      // first checking the condition 
      boolean stateCondition = (rule.getRequiredState() == this.getState());
      
      // second, checking that the environment is correct
      boolean mapConditions =       
        cellAtSouth(_map).isCompliantWith(rule.getConditionAt(RuleDirection.SOUTH))
        && cellAtNorth(_map).isCompliantWith(rule.getConditionAt(RuleDirection.NORTH))
        && cellAtEast(_map).isCompliantWith(rule.getConditionAt(RuleDirection.EAST))
        && cellAtWest(_map).isCompliantWith(rule.getConditionAt(RuleDirection.WEST));
              
       if (stateCondition && mapConditions) {
         int nextX = _xCoordinate;
         int nextY = _yCoordinate;
         // sanitycheck
         if (rule.getDestinationKind() == (RuleDirection.EAST)) {
           nextX ++;
         }
         if (rule.getDestinationKind() ==(RuleDirection.WEST)) {
           nextX --;
         }
         if (rule.getDestinationKind() ==(RuleDirection.SOUTH)) {
           nextY --;
         }
         if (rule.getDestinationKind() ==(RuleDirection.NORTH)) {
           nextY ++;
         }
         
         if (nextX < 0 || nextY < 0 || nextX >= _map.getWidth() || nextY >= _map.getHeight() ) {
           throw new SimulationException("Sorry, you can not exit the map! Revise your rules!");           
         }

         if (_map.getCellKind(nextX, nextY).equals(CellKind.WALL.toString())) {
           throw new SimulationException("Sorry, are you sure you want to go directly into the wall? Revise your rules!");           
         }
         return rule;
       }
    }
    throw new SimulationException("No applicable rules in this state at this position!");
    }

  public String toString() {
    return "Picobot at position "+Arrays.toString(getPosition())+" in state "+getState()+" with "+this.getRules().size()+" rules.";    
  }

  public void resetInternalState() {
    _state = 0;
  }


}
