import java.util.Vector;

/**
 * Created by leo on 25-5-16.
 */
public class RodConstraint extends Constraint{
    private double r;

    public RodConstraint(Vector<Particle> pVector, double r){
        setParticles(pVector);
        this.r = r;
    }

    public RodConstraint(Particle p1, Particle p2, double r){
        Vector<Particle> pVec = new Vector<Particle>();
        pVec.add(p1);
        pVec.add(p2);
        this.setParticles(pVec);
        setParticles(pVector);
        this.r = r;
    }

    void setParticles(Vector<Particle> pVector){
        assert (pVector.size() == 2);
        this.pVector = pVector;
    }

    @Override
    double getC0(){
        Particle p0 = pVector.get(0);
        Particle p1 = pVector.get(1);
        double[] diff = VectorMath.subtract(p0.m_Position, p1.m_Position);
        return VectorMath.dotProd(diff, diff) - r*r;
    }

    @Override
    double getC1(){
        Particle p0 = pVector.get(0);
        Particle p1 = pVector.get(1);
        double[] diff = VectorMath.subtract(p0.m_Position, p1.m_Position);
        double[] diffV = VectorMath.subtract(p0.m_Velocity, p1.m_Velocity);
        return 2*VectorMath.dotProd(diff, diffV);
    }

    @Override
    ConstraintDerivative getCd0(){
        Particle p0 = pVector.get(0);
        Particle p1 = pVector.get(1);
        double[] diff = VectorMath.subtract(p0.m_Position, p1.m_Position);
        return new ConstraintDerivative(new int[]{p0.id, p1.id},
                new double[][]{VectorMath.scale(diff, 2), VectorMath.minus(VectorMath.scale(diff, 2))});
    }

    @Override
    ConstraintDerivative getCd0dt(){
        Particle p0 = pVector.get(0);
        Particle p1 = pVector.get(1);
        double[] vdiff = p0.m_Velocity;
        return new ConstraintDerivative(new int[]{p0.id, p1.id},
                new double[][]{VectorMath.scale(vdiff, 2), VectorMath.minus(VectorMath.scale(vdiff, 2))});
    }

    @Override
    public double[][] getRecipe(){
        double[][] recipe = new double[3][];
        recipe[0] = new double[]{2};//Drawing style
        recipe[1] = pVector.get(0).m_Position;
        recipe[2] = pVector.get(1).m_Position;
        return recipe;
    }
}
