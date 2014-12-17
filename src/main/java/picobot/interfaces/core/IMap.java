/************** IMap.java **************/
package picobot.interfaces.core;

/** Responsible for representing a map where picobots move around. 
 * {@link isCompletelySurrounded()} and {@link isAConnectedMaze()} 
 * encapsulate two algorithms which assess certain formal characteristics
 * of the map. 
 */
public interface IMap {
    
  /** @return the width of the map. The unit is a number of cells. */
  public int getWidth();

  /** @return  the height of the map. The unit is a number of cells. */
  public int getHeight();
   
  /** @return  the cell kind at a given position (x,y)
   * Must be in {"WALL", "FREE"}
   */
  public String getCellKind(int x, int y);
  
  /** Returns an ASCII representation of the map.
   * The ASCII presentation must be correctly displayed on a terminal 
   * (i.e. it must contain the correct spaces and line-breaks). Also,
   * it must be unambiguous so as to be parsed by 
   * {@link IMapBuilder.parseMap(String)}.
   *  
   * An example of the output is:
   * <pre>
   *  ######
   *  #    # 
   *  #  # # 
   *  #  # # 
   *  ######
   * </pre>
   * @return an ASCII representation of the map.
   */
  public String toString();

  /** Returns whether the border of the map is only composed of walls,
   * i.e. the free areas are 
   * completely surrounded by walls.
   * Examples:
   * 
   * The following map is completely surrounded.
   * <pre>
   *  ####
   *  #  # 
   *  ####
   * </pre>
   *  
   * On the contrary, the following map is **not** completely surrounded.
   * <pre>
   *  ## #
   *  #  # 
   *  ## #
   * </pre>
   * @return true if the map is "completely surrounded", false otherwise
   */
  public boolean isCompletelySurrounded();  
  
  /** Returns whether the map is a connected maze, 
   * i.e. whether it is possible to go from each free area
   * to all other free areas (without jumping over walls).
   * Furthermore, corridors must be one cell wide and the maze must not 
   * contain cycles.
   * 
   * You are free to design the algorithm or reuse an existing one. 
   * @return true if the map is "a connected maze" , false otherwise
   */
  public boolean isAConnectedMaze();

  /** @return the number of walls of the map. */
  public int getNumberOfWalls();

} // end interface IMap
