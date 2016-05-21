import java.util.Vector;

/**
 * Created by leo on 20-5-16.
 */
public abstract class Constraint {
    protected Vector<Particle> pVector;
    public int id;

    abstract void setParticles(Vector<Particle> pVector);

    // Returns the value of the position constraint C
    abstract ConstraintValue getC0();

    // Returns the value of the velocity constraint C'
    abstract ConstraintValue getC1();

    // Returns the position derivatives of position constraint C
    abstract ConstraintDerivative getCd0();

    // Returns the time derivatives of the position derivatives of position constraint C
    abstract ConstraintDerivative getCd0dt();

    // Returns the time derivatives of the position derivatives of position constraint C
    void setID(int i){
        this.id = i;
    }
}
