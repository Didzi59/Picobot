package picobot.implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import opl.iagl.alloy.picobot.SimulationPicobotException;
import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IPicobot;
import picobot.interfaces.core.IRule;
import picobot.interfaces.core.ISimulator;

public class Simulator implements ISimulator {

  /** The current picobot of the simulator  */
  private Picobot _picobot;

  /** The current map of the simulator  */
  private Map _map;
  
  /** Note that the specification does not specify what to do when the there
   * is no free area in the map;
   */
  public void loadPicobot(IPicobot bot) {
    _picobot = (Picobot)bot;
    if (_map != null) {
      positionThePicobot();   
      // different assumptions are possible
      //addTraversedCell(_picobot.getXCoordinate(), _picobot.getYCoordinate());
    }    
  }

  /** Randomly position the picobot on a free cell */
  public void positionThePicobot() {
    List<Cell> l = new ArrayList<Cell>();
    for (int x=0; x<_map.getWidth(); x++) {
      for (int y=0; y<_map.getHeight(); y++) {
        Cell cell = ((Map)_map).getCell(x,y);
        if (cell.isFreeArea()) {
          l.add(cell);
        }
      }        
    }

    if (l.isEmpty()) throw new SimulationPicobotException("can not position the picobot, no free cell on the current map", this.getNbTraversedCells());

    Cell initialPosition = l.get(new Random().nextInt(l.size()));
    _picobot.setInitialPosition(initialPosition.getXCoordinate(), initialPosition.getYCoordinate());

  }

  public void loadMap(IMap map) {
    _map = (Map)map;
    if (_picobot!=null) {
      positionThePicobot();
    }
  }

  public IMap getMap() {
    return _map;
  }

  public IPicobot getPicobot() {
    return _picobot;
  }

  /** Used by the debugger UI */
  IRule lastRule = null;
    
  public void step() {
    try {
      IRule rule = _picobot.getApplicableRule(_map);
      lastRule = rule;
      _picobot.apply(rule);
      // convention: a cell is traversed as soon as a picobot enters it.
      addTraversedCell(_picobot.getXCoordinate(), _picobot.getYCoordinate());
    }
    catch(SimulationException se) {
      throw new SimulationPicobotException(se.getMessage(), this.getNbTraversedCells());
    }
  }

  /** adds the cell at (x,y) in the set of traversed cells */
  public void addTraversedCell(int x, int y) {
    _state.addCell(((Map) _map).getCell(x, y));
  }
  
  /** used in the GUI, not testable from the outside */
  public IRule getLastRule() {
    return lastRule;
  }
  
  /** used in the GUI, not testable from the outside  */
  public IRule getNextRule() {
    try {
    return _picobot.getApplicableRule(_map);
    }
    catch (RuntimeException e) { 
      // the ui expects null if no rule exists
      return null;
    }
  }

  /** Wraps up the content of the game state so as satisfy with the interface. 
    */
  public Set<Cell> getTraversedCells() {
    return _state.getTraversedCells();
  }

  /** Store the state of the game (the mission), i.e. the traversed cells */
  private final GameState _state = new GameState();
  
  /** @return the current state of the game 
   * resulting from a long discussion with Tom */
  public GameState getGameState() {
    return _state;
  }
  
  public void run(int nSteps) {
    // the simulation starts, we add the position of the picobot
    addTraversedCell(_picobot.getXCoordinate(), _picobot.getYCoordinate());

    for (int i=0; i<nSteps; i++) {
      step();
    }
  }

  /** If maxSteps is achieved, throws an exception */
  public int runUntilCompletion(int maySteps) {
    // the simulation starts, we add the position of the picobot
    addTraversedCell(_picobot.getXCoordinate(), _picobot.getYCoordinate());
    int i=-1;
    try {
      for (i=0; i<maySteps; i++) {
        //System.err.println(i);
        step();
        if (i%(maySteps/10)==0) {
          if (isMissionCompleted()) { return i; }
        }
      }
    }
    catch (SimulationPicobotException spe) {
      // sometimes, the mission is completed 
      if (isMissionCompleted()) return i;
    }
    //return i;
    throw new SimulationPicobotException("sorry I am done", this.getNbTraversedCells());
    
  }

  public boolean isMissionCompleted() {
    IMap map = getMap();
    Set<Cell> traversed = getGameState().getTraversedCells();
    for (int i=0; i< map.getWidth(); i++) {
      for (int j=0; j< map.getHeight(); j++) {
        Cell cell = ((Map)map).getCell(i, j);
        if (cell.isFreeArea() && !traversed.contains(cell)) { 
          //System.err.println(cell);
          return false; 
        }
      }
    }
    return true;
  }

  public int getNbTraversedCells() {
    //return this.getGameState().getTraversedCells().size();
    return this.getTraversedCells().size();
  }

  public void reset() {
    //reset game state
    _state.reset();
    
    //set picobot to a new random position
    positionThePicobot();

    // after a reset a cell is traversed
    addTraversedCell(_picobot.getXCoordinate(), _picobot.getYCoordinate());
    
    // reset the internal state 
    ((Picobot)getPicobot()).resetInternalState();
  }
}
