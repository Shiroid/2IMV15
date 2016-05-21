import java.util.Vector;

/**
 * Created by leo on 20-5-16.
 */
public class CircularWireConstraint extends Constraint {
    private double[] center;
    private double r;

    public CircularWireConstraint(Vector<Particle> pVector, double[] center, double r){
        setParticles(pVector);
        this.center = center;
        this.r = r;
    }

    void setParticles(Vector<Particle> pVector){
        assert (pVector.size() == 1);
        this.pVector = pVector;
    }

    @Override
    ConstraintValue getC0(){
        Particle p0 = pVector.get(0);
        double[] diff = VectorMath.subtract(p0.m_Position, center);
        return new ConstraintValue(new int[]{p0.id},
                new double[]{(VectorMath.dotProd(diff, diff) - r)});
    }

    @Override
    ConstraintValue getC1(){
        Particle p0 = pVector.get(0);
        double[] diff = VectorMath.subtract(p0.m_Position, center);
        return new ConstraintValue(new int[]{p0.id},
                new double[]{VectorMath.dotProd(diff, p0.m_Velocity)});
    }


    @Override
    ConstraintDerivative getCd0(){
        Particle p0 = pVector.get(0);
        double[] diff = VectorMath.subtract(p0.m_Position, center);
        return new ConstraintDerivative(new int[]{p0.id},
                new double[][]{VectorMath.scale(diff, 2)});
    }

    @Override
    ConstraintDerivative getCd0dt(){
        Particle p0 = pVector.get(0);
        double[] vdiff = p0.m_Velocity;
        return new ConstraintDerivative(new int[]{p0.id},
                new double[][]{VectorMath.scale(vdiff, 2)});
    }
}
