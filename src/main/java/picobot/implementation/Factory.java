package picobot.implementation;

import picobot.interfaces.core.IMapBuilder;
import picobot.interfaces.core.IPicobot;
import picobot.interfaces.core.IRuleBuilder;
import picobot.interfaces.core.ISimulator;

public class Factory implements picobot.interfaces.core.IFactory {

  public IMapBuilder createMapBuilder() {
    return new picobot.implementation.MapBuilder();
  }

  public IPicobot createPicobot() {
    return new Picobot();
  }

  public IRuleBuilder createRuleBuilder() {
    return new RuleBuilder();
  }

  public ISimulator createSimulator() {
    return new Simulator();
  }

  /** This method is generally called by the Bootstrap class.
   * 
   * This class has no state (no field), hence it can be instantiated
   * multiple times with no risk 
   */
  public Factory() {};

//@Override
//public ICell createCell(int x, int y, CellKind kind) {
//	return new Cell(x, y, kind);
//}

}
