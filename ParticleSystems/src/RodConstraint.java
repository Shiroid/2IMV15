import java.util.Vector;

/**
 * Created by leo on 25-5-16.
 */
public class RodConstraint extends Constraint{
    private double r;

    public RodConstraint(Vector<Particle> pVector, double[] center, double r){
        setParticles(pVector);
        this.r = r;
    }

    void setParticles(Vector<Particle> pVector){
        assert (pVector.size() == 2);
        this.pVector = pVector;
    }

    @Override
    ConstraintValue getC0(){
        Particle p0 = pVector.get(0);
        Particle p1 = pVector.get(1);
        double[] diff = VectorMath.subtract(p0.m_Position, p1.m_Position);
        double result = (VectorMath.dotProd(diff, diff) - r*r)/2;
        return new ConstraintValue(new int[]{p0.id, p1.id},
                new double[]{result, result});
    }

    @Override
    ConstraintValue getC1(){
        Particle p0 = pVector.get(0);
        Particle p1 = pVector.get(1);
        double[] diff = VectorMath.subtract(p0.m_Position, p1.m_Position);
        double[] diffV = VectorMath.subtract(p0.m_Velocity, p1.m_Velocity);
        return new ConstraintValue(new int[]{p0.id, p1.id},
                new double[]{VectorMath.dotProd(diff, diffV),
                        VectorMath.dotProd(VectorMath.minus(diff), VectorMath.minus(diffV))});
    }

    @Override
    ConstraintDerivative getCd0(){
        Particle p0 = pVector.get(0);
        Particle p1 = pVector.get(1);
        double[] diff = VectorMath.subtract(p0.m_Position, p1.m_Position);
        return new ConstraintDerivative(new int[]{p0.id},
                new double[][]{VectorMath.scale(diff, 2)});
    }

    @Override
    ConstraintDerivative getCd0dt(){
        Particle p0 = pVector.get(0);
        Particle p1 = pVector.get(1);
        double[] vdiff = p0.m_Velocity;
        return new ConstraintDerivative(new int[]{p0.id},
                new double[][]{VectorMath.scale(vdiff, 2)});
    }
}
