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
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			paintParticles(g2);
		}

		public void paintParticles(Graphics2D g2)
		{

			double pSize = 0.03;

			for (int i = 0; i < particles.size(); i++)
			{
				Rectangle2D rect = new Rectangle2D.Double(centerAndScale(particles.get(i).m_Position[0]+pSize/2,true),
						centerAndScale(particles.get(i).m_Position[1]+pSize/2,false),
						scale(pSize, true), scale(pSize, false));

				g2.fill(rect);
			}
		}

		public double centerAndScale(double s, boolean isX){
			return scale(center(s),isX);
		}

		public double scale(double s, boolean isX){
			return (s/2)*((isX)?this.getWidth():this.getHeight());
		}

		public double center(double s){
			return s+1;
		}
	}
}
