import java.util.Vector;

/**
 * Created by leo on 16-5-16.
 */
public abstract class Force {
    protected Vector<Particle> pVector;
    protected boolean isOn;

    abstract void setParticles(Vector<Particle> pVector);

    abstract void setTether(double[] center);

    public void setOn(boolean on){
        this.isOn = on;
    }

    abstract void apply();

    abstract public double[][] getRecipe();
}
