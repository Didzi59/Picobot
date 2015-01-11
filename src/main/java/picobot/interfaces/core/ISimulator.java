/************** ISimulator.java **************/
package picobot.interfaces.core;

import picobot.implementation.Cell;

import java.util.Set;

/** Responsible for a correct collaboration between a map and a picobot.
 * 
 * Especially, the method {@link step} defines what is a simulation step.
 */
public interface ISimulator {
  
  /** Loads a picobot that is already programmed (that contains 
   * at least one rule). Removes the previously loaded picobot if 
   * there was one.
   * 
   * If a map is already loaded, the picobot is placed at a random 
   * location (free area) in the IMap. 
   * @param bot is the picobot to be loaded
   */
  public void loadPicobot(IPicobot bot);
  
  /** Loads a map into this ISimulator. Displays it on the screen.
   * If the ISimulator is already configured with a picobot, 
   * the picobot must be put at a random location (free area) in the 
   * IMap and be displayed.
   * @param map is the map to be loaded
   */
  public void loadMap(IMap map);  
  
  
  /** @return the current map. */
  public IMap getMap();
  
  /** @return  the picobot that is loaded in the simulator. */
  public IPicobot getPicobot();
  
  /** Executes one step in the simulation. */
  public void step();

  /** Gets a set of already traversed cells. Each item of the set must be a 
   * array of integers of size 2 (i.e. for all elements of the result set
   * element.length = 2). 
   * 
   * By convention, a cell is traversed as soon as the picobot enters it.
   * As a result, after a call to {@link ISimulator#reset()} this set 
   * contains the current position of the picobot.
   * 
   * @return a set of (x,y) positions, e.g. { "1,1", "1,2", "2,3"}
   */
 public Set<Cell> getTraversedCells();

 /** Returns true if the mission is completed, i.e. if all free areas
   * of the map have been traversed at least once by one picobot.
   */
 public boolean isMissionCompleted();

 /** Runs the simulation for n steps. 
  * 
  * Note that passing the number of steps is required, because depending 
  * on the rules, the picobot may enter an infinite loop.
  * 
  * If the ISimulator does not contain a map and a picobot, the method 
  * must throw a RuntimeException.
  * 
  * Typically, one calls getTraversedCells() after having run the 
  * simulator.
  * @param nSteps is the number of steps
  */
 public void run(int nSteps);

 /** Reset the simulator state, by removing all visiting information 
  * and set the picobot to a new random position that must be a free area.
  */
 public void reset(); 

} // end interface ISimulator

