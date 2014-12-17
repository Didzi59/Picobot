package picobot.implementation;

import java.util.ArrayList;
import java.util.List;

public class Rule implements picobot.interfaces.core.IRule {

 
  private RuleDirection _direction = RuleDirection.NORTH;

  public void setDestination(String direction) {
    _direction = RuleDirection.valueOf(direction);
  }  
  
  private class Condition {
    CellKind _kind;
    RuleDirection _location;
    Condition(CellKind kind, RuleDirection location) {
      _kind = kind; _location = location;
    }
  }
  List<Condition> _conditions = new ArrayList<Condition>();
  public void addCondition(String kind, String where) {
    // pre condition
    try {
    CellKind.valueOf(kind);
    RuleDirection.valueOf(where);
    }
    catch (IllegalArgumentException e) { throw new SimulationException(e);}
    
    _conditions.add(new Condition(CellKind.valueOf(kind), RuleDirection.valueOf(where)));
    
  }
  
  
  
  public String getConditionAt(String place) {
    for (Condition c:_conditions) {
      if (c._location.toString().equals(place)) { return c._kind.toString();}
    }
    throw new SimulationException("No condition at this place");
  }

  public CellKind getConditionAt(RuleDirection place) {
    for (Condition c:_conditions) {
      if (c._location == place) { return c._kind;}
    }
    throw new SimulationException("No condition at this place");
  }


  private int _state = 0;
  public void setInputState(int i) {
    _state = i;
  }

  public int getRequiredState() {
    return _state;
  }

  public String getDestination() {
    return _direction.toString();
  }
  
  public RuleDirection getDestinationKind() {
    return _direction;
 }


  @Override
  public String toString() {
    return getRequiredState()+" "
      +toStringRepresentation("NORTH")
      +toStringRepresentation("EAST")
      +toStringRepresentation("WEST")
      +toStringRepresentation("SOUTH")
      +" -> "
      +getDestination().substring(0,1)
      +" "
      +getTargetState();
  }

  private int _targetState = 0;

  public int getTargetState() {
    return _targetState;
  }
  
  
  public void setTargetState(int i) {
    _targetState = i;
    
  }

  private String toStringRepresentation(String conditionAt) {
    String condition = getConditionAt(conditionAt);
    if (condition.equals("WALL")) return conditionAt.substring(0,1);
    
    if (condition.equals("ANY")) return "*";
    
    if (condition.equals("FREE")) return "x";
    throw new SimulationException("can not understand "+conditionAt+" as a condition of a sensor");
  }



}
