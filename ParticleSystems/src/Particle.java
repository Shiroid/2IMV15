public class Particle
{
	public double[] m_ConstructPos;
	public double[] m_Position;
	public double[] m_Velocity;
	
	public Particle(double x, double y)
	{
		m_ConstructPos = new double[] {x, y};
		m_Position = new double[] {0, 0};
		m_Velocity = new double[] {0, 0};
	}
	
	public void reset()
	{
		m_Position = m_ConstructPos;
		m_Velocity = new double[] {0, 0};
	}
	
	public void draw()
	{
		
	}
}
