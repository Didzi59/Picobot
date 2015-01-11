package opl.iagl.alloy.picobot;

import opl.iagl.alloy.util.Position;
import picobot.implementation.*;
import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IRule;

import java.io.File;
import java.util.List;

/**
 * @author Romain Philippon
 */
public class OracleSimulator {

    private File map;
    private File rules;
    private Position initialPicobotPosition;
    private int maxNbSteps;
    private Simulator simulator;
    private int nbCrossedCells;


    public OracleSimulator(String pathMap, String pathRule, Position initialPicobotPosition, int nbSteps) {
        this.map = new File(pathMap);
        this.rules = new File(pathRule);
        this.initialPicobotPosition = initialPicobotPosition;
        this.maxNbSteps = nbSteps;
    }

    public void run() throws SimulationException {
        Factory factory = new Factory();
        this.simulator = (Simulator)factory.createSimulator();
        this.nbCrossedCells = 0;

        MapBuilder mb = (MapBuilder)factory.createMapBuilder();
        mb.parseMap(this.map);
        IMap m = mb.getCurrentMap();

        this.simulator.loadMap(m);

        Picobot picobot = (Picobot)factory.createPicobot();
        List<IRule> rules = ((RuleBuilder)factory.createRuleBuilder()).parseRules(this.rules);

        picobot.loadRules(rules);
        this.simulator.loadPicobot(picobot);
        picobot.setInitialPosition(this.initialPicobotPosition.x(), this.initialPicobotPosition.y());

        for (int i=0; i< this.maxNbSteps ; i++) {
            try {
                simulator.step();
                this.nbCrossedCells = this.simulator.getNbTraversedCells();
                //System.out.println(this.nbCrossedCells);
            }
            catch (SimulationException se) {
                throw se;
            }

            //Alloy
            if (simulator.isMissionCompleted()) break;
        }
    }

    public int getNbCrossedCells() {
        return this.nbCrossedCells;
    }
}
