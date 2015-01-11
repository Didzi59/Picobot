package opl.iagl.alloy.picobot;

/**
 * Created by jimipepper on 1/10/15.
 */
public class SimulationPicobotException extends RuntimeException  {

    private static final long serialVersionUID = -5939450552732016502L;
    private int nbCrossedCells;

    public SimulationPicobotException(String string, int nbCrossedCells) {
        super(string);
        this.nbCrossedCells = nbCrossedCells;
    }

    public int getNbCrossedCells() {
        return this.nbCrossedCells;
    }
}
