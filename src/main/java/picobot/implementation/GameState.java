package picobot.implementation;

import java.util.HashSet;
import java.util.Set;

public class GameState {

  /** The already traversed cells */
  private final Set<Cell> _cells = new HashSet<Cell>();
  
  public Set<Cell> getTraversedCells() { return new HashSet<Cell>(_cells); }

  public void reset() { _cells.clear(); }

  public GameState addCell(Cell c) { _cells.add(c); return this; }

}
