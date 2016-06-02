import java.util.Vector;

/**
 * Created by leo on 2-6-16.
 */
public class Boundary extends Solid {
    protected Vector<Particle> pVector;
    protected boolean isOn;
    protected double[] bp;
    protected double[] normal;

    public Boundary(Vector<Particle> pVector, double[] boundaryPoint, double[] normal, double kr, double eps){
        setParticles(pVector);
        this.eps = eps;
        this.kr = kr;
        this.normal = normal;
        this.bp = boundaryPoint;
        this.isOn = true;
    }

    @Override
    void setParticles(Vector<Particle> pVector){
        this.pVector = pVector;
    }

    @Override
    void apply(){
        if(isOn){
            for (int i = 0; i < pVector.size(); i++)
            {
                Particle p = pVector.get(i);
                double dir = VectorMath.dotProd(normal,p.m_Velocity);
                double dist = VectorMath.dotProd(VectorMath.subtract(p.m_Position, bp), normal);
                if(dir < 0 && dist < eps){
                    double[] vn = VectorMath.scale(normal, dir);
                    p.m_Velocity = VectorMath.subtract(p.m_Velocity, VectorMath.scale(vn, 1+kr));
                }
            }
        }
    }

    @Override
    public double[][] getRecipe(){
        double[][] recipe = new double[1][];
        recipe[0] = new double[]{0};//Drawing Style
        return recipe;
    }
}
