/************** IMapBuilder.java **************/
package picobot.interfaces.core;

import java.io.File;

/** Responsible for creating or modifying maps.
 * 
 * A set of method calls configures the map, and once the map is finished
 * an immutable map is returned by a call to {@link getCurrentMap()}.
 * 
 * Examples:
 * <pre>
 * IMapBuilder mb = new MapBuilder();
 * mb.placeWall(3,4);
 * mb.placeWall(8,7);
 * IMap map = mb.getCurrentMap();
 * </pre>
 * 
 * Note that the size of the map being built is updated on the fly. It 
 * corresponds to the most distant wall towards east and towards north
 * from the origin. In the example above, map.getWidth() = 3, and
 * map.getHeight() = 7.
 * 
 * Also, by convention, unspecified cells are free areas (i.e. contain 
 * no wall).
 * 
 * <pre>
 * IMapBuilder mb = new MapBuilder();
 * mb.createEmptySquareMap(4);
 * IMap map = mb.getCurrentMap();
 * </pre>
 * 
 * <pre>
 * IMapBuilder mb = new MapBuilder();
 * mb.parseMap(new File("/tmp/foo.map"));
 * IMap map = mb.getCurrentMap();
 * </pre>
 * 
 */
public interface IMapBuilder {

  /** Parse an ASCII representation of a map.
   * 
   * This method must be consistent with {@link IMap.toString()}.
   * 
   * @param stringRepresentation a string representing a map
   */
  public void parseMap(String stringRepresentation);

  /**
   * Parses the content of a file as a map representation. Must be semantically
   * equivalent to {@link parseMap(String)}.
   * 
   * Exceptions must be handled as domain-specific exceptions as follows:
   * 
   * <pre>
   * class MyMapFileNotFoundException extends RuntimeException {...}
   * 
   * class MyMapBuilder {
   *   void parseMap(File f) { 
   *     ... catch (IOException e) {
   *     throw new MyMapFileNotFoundException(e); }
   *   }
   * }
   * </pre>
   * 
   * @param mapFile
   *          must be an existing file containing a map that complies with the
   *          specified format.
   */
  public void parseMap(File mapFile);

  /** Creates an empty square map of the given size.
   * The returned map must contain (size-2)*(size-2) free cells 
   * and the borders of the map must be walls.
   * 
   * @param size is the size of the new map
   */
  public void createEmptySquareMap(int size);

  /** Adds a wall in the map at position x,y.
   * 
   * @param x is the x-axis coordinate at which to put the wall
   * @param y is the y-axis coordinate at which to put the wall
   */
  public void placeWall(int x, int y);

  /** Adds a free cell in the map at position x,y .
   * 
   * @param x is the x-axis coordinate at which to put the free cell
   * @param y is the y-axis coordinate at which to put the free cell
   */
  public void placeFreeCell(int x, int y);

  /** Gets the map resulting from the previous calls on this builder object.
   * A reference to this map is kept, such as it is possible to further 
   * modify  the map.
   * @return a map resulting from the previous calls on this builder object.
   */
  public IMap getCurrentMap();

  /** Enables to modify an existing map. The implementation is free to 
   * create a new map object, or to modify the one that is given.
   * 
   *  <pre>
   * IMapBuilder mb = new MapBuilder();
   * mb.modify(aTemplateMap);
   * mb.placeWall(8,7);
   * IMap newMap = mb.getCurrentMap();
   * </pre>
   * 
   * @param map the map to be modified.
   */
  public void modify(IMap map);

} // end interface IMapBuilder
