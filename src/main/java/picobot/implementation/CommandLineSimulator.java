package picobot.implementation;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IRule;

public class CommandLineSimulator {
  public static void main(String[] args) {
    if (args.length != 4) {
            throw new IllegalArgumentException(
            "you must provide 4 arguments: mapFile RuleFile initialPosition nSteps\nfor instance: maps/map1-mission1-empty.map rules/rules4empty.rules 1,3 10");
    }
    
    Factory factory = new Factory();
    Simulator sim = (Simulator)factory.createSimulator();
    
    MapBuilder mb = (MapBuilder)factory.createMapBuilder();
    mb.parseMap(new File(args[0]));
    IMap m = mb.getCurrentMap();
    
    sim.loadMap(m);
    
    Picobot picobot = (Picobot)factory.createPicobot();
    List<IRule> rules = ((RuleBuilder)factory.createRuleBuilder()).parseRules(new File(args[1]));

    picobot.loadRules(rules);
    
    sim.loadPicobot(picobot);
    
    StringTokenizer st=new StringTokenizer(args[2],",");
    int x = Integer.parseInt(st.nextToken());
    int y = Integer.parseInt(st.nextToken());
    picobot.setInitialPosition(x,y);
    // positioning the picobot
    //sim.positionThePicobot();
    
    
    for (int i=0; i< Integer.parseInt(args[3]) ; i++) {
      sim.step();
      int[] position = picobot.getPosition();
      System.out.println(position[0]+","+position[1]);
      //Alloy
      if (sim.isMissionCompleted()) break;
    }
    
    System.out.println("Jeu fini");
    
  }
}
