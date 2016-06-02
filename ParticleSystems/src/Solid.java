import java.util.Vector;

/**
 * Created by leo on 2-6-16.
 */
public abstract class Solid {
    protected Vector<Particle> pVector;
    protected boolean isOn;
    protected double eps;
    protected double kr;

    abstract void setParticles(Vector<Particle> pVector);

    public void setOn(boolean on){
        this.isOn = on;
    }

    abstract void apply();

    abstract public double[][] getRecipe();
}
