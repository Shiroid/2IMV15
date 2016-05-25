import java.util.Vector;

/**
 * Created by leo on 16-5-16.
 */
public class DirectionalForce extends Force {
    protected double[] vec;

    public DirectionalForce(Vector<Particle> pVector, double[] vec){
        this.setParticles(pVector);
        this.vec = vec;
        this.isOn = true;
    }

    @Override
    void setParticles(Vector<Particle> pVector){
        this.pVector = pVector;
    }


    @Override
    void setTether(double[] center){
        throw new UnsupportedOperationException();
    }

    @Override
    void apply(){
        for (int i = 0; i < pVector.size(); i++)
        {
            pVector.get(i).m_Force = VectorMath.add(pVector.get(i).m_Force, vec);
        }
    }
}
