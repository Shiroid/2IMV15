import java.util.Vector;

/**
 * Created by leo on 20-5-16.
 */
public class CircularWireConstraint extends Constraint {
    private double[] center;

    public CircularWireConstraint(Vector<Particle> pVector, double[] center){
        setParticles(pVector);
        this.center = center;
    }

    void setParticles(Vector<Particle> pVector){
        assert (pVector.size() == 1);
        this.pVector = pVector;
    }

    void apply(){

    }

    @Override
    ConstraintValue getC0(){
        Particle p0 = pVector.get(0);
        return new ConstraintValue(new int[]{p0.id},
                new double[]{0.5*(VectorMath.dotProd(p0.m_Position, p0.m_Position) - 1)});
    }

    @Override
    ConstraintValue getC1(){
        Particle p0 = pVector.get(0);
        return new ConstraintValue(new int[]{p0.id},
                new double[]{VectorMath.dotProd(p0.m_Position, p0.m_Velocity)});
    }
}
