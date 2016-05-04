import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame
{
	private MainPanel panel;
	
	public MainFrame()
	{
		super("Tinkertoys!");
		
		panel = new MainPanel();
		this.add(panel);
	}
	
	public void drawParticles(Vector<Particle> particles)
	{
		panel.setParticles(particles);
	}
	
	class MainPanel extends JPanel
	{
		private Vector<Particle> particles;
		
		public void setParticles(Vector<Particle> parts)
		{
			particles = parts;
			this.repaint();
		}
		
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;
			
			for (int i = 0; i < particles.size(); i++)
			{
				Rectangle2D rect = new Rectangle2D.Double(particles.get(i).m_Position[0], particles.get(i).m_Position[1], 10, 10);
				
				g2.fill(rect);
			}
		}
	}
}
