public class Particle
{
	public double[] m_ConstructPos;
	public double[] m_Position;
	public double[] m_Velocity;
	public double[] m_Force;
	
	public Particle(double x, double y)
	{
		m_ConstructPos = new double[] {x, y};
		m_Position = new double[] {0, 0};
		m_Velocity = new double[] {0, 0};
		m_Force = new double[] {0, 0};
	}

	public void reset()
	{
		m_Position = m_ConstructPos.clone();
		m_Velocity = new double[] {0, 0};
	}

	public void clearForce()
	{
		m_Force = new double[] {0, 0};
	}

	public void applyForce(double dt)
	{
		m_Velocity = VectorMath.add(m_Velocity, VectorMath.scale(m_Force, dt));
	}

	public void applyVelocity(double dt)
	{
		m_Position = VectorMath.add(m_Position, VectorMath.scale(m_Velocity, dt));
	}
}
