import java.util.Vector;

/**
 * Created by leo on 20-5-16.
 */
public abstract class Constraint {
    protected Vector<Particle> pVector;

    abstract void setParticles(Vector<Particle> pVector);

    abstract void apply();

    abstract ConstraintValue getC0();

    abstract ConstraintValue getC1();
}
