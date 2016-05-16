import java.util.Vector;

/**
 * Created by leo on 16-5-16.
 */
public abstract class Force {

    abstract void setParticles(Vector<Particle> pVector);

    abstract void apply();
}
