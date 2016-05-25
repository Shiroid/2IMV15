import java.util.Vector;

/**
 * Created by leo on 17-5-16.
 */
public class SpringForce extends Force {
    protected double ks; // Spring constant
    protected double kd; // Dampening constant
    protected double r; // Spring rest length

    public SpringForce(Vector<Particle> pVector, double ks, double kd, double r){
        this.setParticles(pVector);
        this.ks = ks;
        this.kd = kd;
        this.r = r;
        this.isOn = true;
    }

    public SpringForce(Particle p1, Particle p2, double ks, double kd, double r){
        Vector<Particle> pVec = new Vector<Particle>();
        pVec.add(p1);
        pVec.add(p2);
        this.setParticles(pVec);
        this.ks = ks;
        this.kd = kd;
        this.r = r;
        this.isOn = true;
    }


    @Override
    void setParticles(Vector<Particle> pVector){
        assert (pVector.size() == 2);
        this.pVector = pVector;
    }


    @Override
    void setTether(double[] center){
        throw new UnsupportedOperationException();
    }

    @Override
    void apply(){
        // Note: l0 is a vector from p1 to p0,
        // and the resulting scalar is positive iff the points should move towards each other,
        // so p0 actually gets the force applied in the negative, not p1.
        double[] l0 = VectorMath.subtract(pVector.get(0).m_Position, pVector.get(1).m_Position);
        double[] l1 = VectorMath.subtract(pVector.get(0).m_Velocity, pVector.get(1).m_Velocity);
        double l0l = Math.sqrt(VectorMath.dotProd(l0, l0));
        double scalar = (ks*(l0l - r) + kd*(VectorMath.dotProd(l1, l0)/l0l))/l0l;
        double[] vec = VectorMath.scale(l0, scalar);

        pVector.get(0).m_Force = VectorMath.subtract(pVector.get(0).m_Force, vec);
        pVector.get(1).m_Force = VectorMath.add(pVector.get(1).m_Force, vec);
    }

    @Override
    public double[][] getRecipe(){
        double[][] recipe = new double[3][];
        recipe[0] = new double[]{2};//Drawing Style
        recipe[1] = pVector.get(0).m_Position;
        recipe[2] = pVector.get(1).m_Position;
        return recipe;
    }
}
