import java.util.Vector;

/**
 * Created by leo on 25-5-16.
 */
public class TetheredSpringForce extends SpringForce{
    private double[] center;

    public TetheredSpringForce(Vector<Particle> pVector, double[] center, double ks, double kd, double r){
        super(pVector, ks, kd, r);
        this.center = center;
        this.isOn = false;
    }

    public TetheredSpringForce(Vector<Particle> pVector, double ks, double kd, double r){
        super(pVector, ks, kd, r);
        this.center = pVector.get(0).m_ConstructPos;
        this.isOn = false;
    }


    @Override
    void setParticles(Vector<Particle> pVector){
        assert (pVector.size() == 1);
        this.pVector = pVector;
    }


    @Override
    void setTether(double[] center){
        this.center = center;
    }

    @Override
    void apply(){
        if(isOn){
            double[] l0 = VectorMath.subtract(pVector.get(0).m_Position, center);
            double[] l1 = VectorMath.subtract(pVector.get(0).m_Velocity, new double[]{0, 0});
            double l0l = Math.sqrt(VectorMath.dotProd(l0, l0))+Double.MIN_VALUE;
            double scalar = (ks*(l0l - r) + kd*(VectorMath.dotProd(l1, l0)/l0l))/l0l;
            double[] vec = VectorMath.scale(l0, scalar);

            pVector.get(0).m_Force = VectorMath.subtract(pVector.get(0).m_Force, vec);
        }
    }

    @Override
    public double[][] getRecipe(){
        if(isOn){
            double[][] recipe = new double[3][];
            recipe[0] = new double[]{2};//Drawing Style
            recipe[1] = center;
            recipe[2] = pVector.get(0).m_Position;
            return recipe;
        } else {
            double[][] recipe = new double[1][];
            recipe[0] = new double[]{0};//Drawing Style
            return recipe;
        }
    }
}
