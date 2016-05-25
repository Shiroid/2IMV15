import java.util.Vector;

/**
 * Created by leo on 16-5-16.
 */
public class RepulsionForce extends Force {
    protected double[] center;
    protected double strength;
    final double MINDIST = 0.0001;

    public RepulsionForce(Vector<Particle> pVector, double[] center, double strength){
        this.setParticles(pVector);
        this.center = center;
        this.isOn = false;
        this.strength = strength;
    }

    @Override
    void setParticles(Vector<Particle> pVector){
        this.pVector = pVector;
    }

    @Override
    void setTether(double[] center){
        this.center = center;
    }

    @Override
    void apply(){
        if(isOn){
            for (int i = 0; i < pVector.size(); i++)
            {
                double[] repel = VectorMath.subtract(pVector.get(i).m_Position, center);
                double sqrDist = Math.max(VectorMath.dotProd(repel, repel), MINDIST);
                repel = VectorMath.scale(repel, strength/(sqrDist*sqrDist));
                pVector.get(i).m_Force = VectorMath.add(pVector.get(i).m_Force, repel);
            }
        }
    }

    @Override
    public double[][] getRecipe(){
        double[][] recipe = new double[3][];
        recipe[0] = new double[]{0};//Drawing Style
        return recipe;
    }
}
