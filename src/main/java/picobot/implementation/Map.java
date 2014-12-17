package picobot.implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;



public class Map implements picobot.interfaces.core.IMap {

  public boolean isAConnectedMaze() {
        
    // getting a free cell
    Cell firstFreeCell = null;
    Set<Cell> freeCells = new HashSet<Cell>();
    for (int i=0; i<_width;i++) {
      for (int j=0; j<_height;j++) {
        Cell tmpCell = getCell(i, j);
        if (tmpCell.isFreeArea() && firstFreeCell == null) firstFreeCell =  tmpCell;
        if (tmpCell.isFreeArea()) freeCells.add(tmpCell);
      }
    }

    // the cells to explore
    Stack<Cell> cellstoBeExplored = new Stack<Cell>();
    cellstoBeExplored.push(firstFreeCell);
    // the cells already explored
    Set<Cell> alreadyExplored= new HashSet<Cell>();
    // the algorithm to find all cells accessible from the first one
    while (!cellstoBeExplored.isEmpty()) {
      Cell currentCell = cellstoBeExplored.pop();
      alreadyExplored.add(currentCell);
      Cell northCell = getCell(currentCell.getXCoordinate(), currentCell.getYCoordinate()+1);
      Cell southCell = getCell(currentCell.getXCoordinate(), currentCell.getYCoordinate()-1);
      Cell eastCell = getCell(currentCell.getXCoordinate()+1, currentCell.getYCoordinate());
      Cell westCell = getCell(currentCell.getXCoordinate()-1, currentCell.getYCoordinate());
      
      // adding the next cells to visit
      if (northCell.isFreeArea() && !alreadyExplored.contains(northCell)) { cellstoBeExplored.push(northCell); }
      if (southCell.isFreeArea() && !alreadyExplored.contains(southCell)) { cellstoBeExplored.push(southCell); }
      if (eastCell.isFreeArea() && !alreadyExplored.contains(eastCell)) { cellstoBeExplored.push(eastCell); }
      if (westCell.isFreeArea() && !alreadyExplored.contains(westCell)) { cellstoBeExplored.push(westCell); }  
      
      // now detecting a wrong corridor
      // a square signals both horizontal and vertical corridor
      // needs a proof
      Cell northEastCell = getCell(currentCell.getXCoordinate()+1, currentCell.getYCoordinate()+1);
      if ( currentCell.isFreeArea() && // useless test but better for understandability
          northCell.isFreeArea() &&
          eastCell.isFreeArea() &&
          northEastCell.isFreeArea()) return false;      
    }
        
    boolean result = alreadyExplored.containsAll(freeCells);
    
    
    return result;
  }

  public boolean isCompletelySurrounded() {
    boolean result = true;
    // the south wall
    for (int i=0; i<getWidth(); i++) {
      if (getCell(i, 0).isFreeArea()) result = false;
    }
    
    // the north wall
    for (int i=0; i<getWidth(); i++) {
      if (getCell(i, getHeight()-1).isFreeArea()) result = false;
    }

    // the west wall
    for (int j=0; j<getHeight(); j++) {
      if (getCell(0, j).isFreeArea()) result = false;
    }

    // the east wall
    for (int j=0; j<getHeight(); j++) {
      if (getCell(getWidth()-1, j).isFreeArea()) result = false;
    }

    return result;
  }

  public int getWidth() {
    return _width;
  }

  public int getHeight() {
    return _height;
  }

  public void setWidth(int width) {
    _width = width;
  }

  public void setHeight(int height) {
    _height = height;
  }

  
  public void addWall(int i, int j) {
    Cell c = new Cell(i,j);
    c.setKind("WALL");    
    addCell(c);
  }
  
 
  /** This Map implementation simply uses a list of cells */
  private List<Cell> _cells = new ArrayList<Cell>();
  
  /** Returns the cell at position (x,y).
   *  If we don't have a cell information (x,y) is not in the map, throws a new SimulationException;*/
  public Cell getCell(int x, int y) {
    
    /*if (x<0 || y<0 || x>=getWidth() || y>=getHeight()) {
      throw new SimulationException("those coordinates are not possible!");
    }*/
    
    for (Cell c: _cells) {
      if (c.getXCoordinate() == x && c.getYCoordinate() == y) {
        return c;
      }
    }
    
    // adding a free area
    Cell c = new Cell(x,y);
    if (x>0 && y>0 && x<getWidth() && y<getHeight()) 
      {addCell(c);}
    return c;
  }

  /** the width of the map */
  private int _width = 0;

  /** the height of the map */
  private int _height = 0;

  @Override
  public String toString() {
    String s ="";
    
    for (int y=_height-1; y>=0;y--) {
      for (int j=0; j<_width;j++) {
          Cell c = getCell(j,y);
          if (c.isWall()) {s+="#";}
          else {s+=" ";}      
      }
      s+="\n";
    }
    return s;
  }

  public void addCell(Cell c) {
    for (Cell c2: new ArrayList<Cell>(_cells)) {
      if (c2.getXCoordinate() == c.getXCoordinate() && c2.getYCoordinate() == c.getYCoordinate()) {
        _cells.remove(c2);
      }
    }
    // updating the maximum size
    if (c.getXCoordinate()>=_width) {
      _width = c.getXCoordinate()+1;
    }
    if (c.getYCoordinate()>=_height) {
      _height = c.getYCoordinate()+1;
    }

    // adding the cell
    _cells.add(c);

  }

  public String getCellKind(int x, int y) { 
    return getCell(x, y).getKind().toString();
  }
  
  public int getNumberOfWalls() {
	int numOfWalls = 0;
	for (int y=_height-1; y>=0;y--) {
	      for (int j=0; j<_width;j++) {
	          Cell c = getCell(j,y);
	          if (c.isWall()) numOfWalls++;
	      }
	}
	return numOfWalls;
  }
}
