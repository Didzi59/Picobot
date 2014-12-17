package picobot.implementation;

/** A problem happened during the simulation 
 * (for instance, the picobot does not know how to move)
 *
 */
public class SimulationException extends RuntimeException  {

  private static final long serialVersionUID = -5939450552732016502L;

  public SimulationException(String string) {    
    super(string);
  }

  public SimulationException(Exception e) {
    super(e);
  }

}
