package picobot.implementation;

/** Represents a cell in the map.
 * This is the only domain class which is not enforced by the public
 * interfaces given to the students.
 * 
 */
public class Cell {

  /** Returns a Cell which is free area by default.
   * <p>
   * This method must remain package protected, because outside of the package,
   * the factory must be used.
   */
  Cell(int x, int y) {
    _x = x;
    _y = y;
  }


  private int _x = 0;
  private int _y = 0;
  public int getXCoordinate() {
    return _x;
  }

  public int getYCoordinate() {
    return _y;
  }

  
  private CellKind _cellKind = CellKind.FREE;
  
  public void setKind(String s) {
    _cellKind = CellKind.valueOf(s);
    //if (_cellKind == CellKind.ANY) throw new SimulationException("oops");
  }
  
  public boolean isWall() {
    return _cellKind == CellKind.WALL;
  }

  public boolean isFreeArea() {
    return _cellKind == CellKind.FREE;
  }


  public boolean isCompliantWith(CellKind kind) {
    if (kind == CellKind.ANY) return true;
    return _cellKind == kind;
  }

  // this is not testable because Cell is not in the interface
  public String toString() {
    return "("+_x+","+_y+","+_cellKind.toString()+")";
  }

  public CellKind getKind() {
    return _cellKind;
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Cell) {
      Cell c = (Cell)o;

      if(this._cellKind == c.getKind()) {
        return this._x == c.getXCoordinate() && this._y == c.getYCoordinate();
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this._x + this._y + this._cellKind.hashCode();
  }
}
