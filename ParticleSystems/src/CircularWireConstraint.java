import java.util.Vector;

/**
 * Created by leo on 20-5-16.
 */
public class CircularWireConstraint extends Constraint {
    private double r;
    private double[] center;

    public CircularWireConstraint(Vector<Particle> pVector, double[] center, double r){
        setParticles(pVector);
        this.center = center;
        this.r = r;
    }

    public CircularWireConstraint(Particle p1, double[] center, double r){
        Vector<Particle> pVec = new Vector<Particle>();
        pVec.add(p1);
        this.setParticles(pVec);
        setParticles(pVector);
        this.center = center;
        this.r = r;
    }

    void setParticles(Vector<Particle> pVector){
        assert (pVector.size() == 1);
        this.pVector = pVector;
    }

    @Override
    double getC0(){
        Particle p0 = pVector.get(0);
        double[] diff = VectorMath.subtract(p0.m_Position, center);
        return VectorMath.dotProd(diff, diff) - r*r;
    }

    @Override
    double getC1(){
        Particle p0 = pVector.get(0);
        double[] diff = VectorMath.subtract(p0.m_Position, center);
        return 2*VectorMath.dotProd(diff, p0.m_Velocity);
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

    @Override
    public double[][] getRecipe(){
        double[][] recipe = new double[3][];
        recipe[0] = new double[]{1}; //Drawing style
        recipe[1] = center;
        recipe[2] = new double[]{r};
        return recipe;
    }
}
