import java.util.Vector;

/**
 * Created by leo on 16-5-16.
 */
public abstract class Force {
    protected Vector<Particle> pVector;

    abstract void setParticles(Vector<Particle> pVector);

    abstract void apply();
}
