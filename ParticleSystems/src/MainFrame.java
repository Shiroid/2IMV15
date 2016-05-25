import javafx.scene.shape.Circle;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
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

	public double[] adjustMousePos(double[] mousePos){
		return this.panel.adjustMousePos(mousePos);
	}

	public int widthRef(){
		return this.getWidth();
	}

	public int heightRef(){
		return this.getHeight();
	}

	public void drawParticles(Vector<Particle> particles)
	{
		panel.setParticles(particles);
	}

	public void drawForces(Vector<Force> forces)
	{
		panel.setForces(forces);
	}

	public void drawConstraints(Vector<Constraint> constraints)
	{
		panel.setConstraints(constraints);
	}

	public void paintSystem(){ panel.paintSystem(); }
	
	class MainPanel extends JPanel
	{
		private Vector<Particle> particles;
		private Vector<Force> forces;
		private Vector<Constraint> constraints;

		private double pSize = 0.03;

		public void setParticles(Vector<Particle> parts)
		{
			particles = parts;
			//this.repaint();
		}

		public void setForces(Vector<Force> parts)
		{
			forces = parts;
		}

		public void setConstraints(Vector<Constraint> parts)
		{
			constraints = parts;
		}

		public void paintSystem(){ this. repaint(); }

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			paintParticles(g2);
			paintForces(g2);
			paintConstraints(g2);
		}

		public void paintParticles(Graphics2D g2)
		{

			int diffX = widthRef()-this.getWidth();
			int diffY = heightRef()-this.getHeight();
			for (int i = 0; i < particles.size(); i++)
			{
				Rectangle2D rect = new Rectangle2D.Double(centerAndScale(particles.get(i).m_Position[0]-pSize/2,true)-diffX,
						centerAndScale(particles.get(i).m_Position[1]-pSize/2,false)-diffY,
						scale(pSize, true), scale(pSize, false));

				g2.fill(rect);
			}
		}

		public void paintForces(Graphics2D g2)
		{

			for (int i = 0; i < forces.size(); i++)
			{
				double[][] recipe = forces.get(i).getRecipe();
				paintFC(g2, recipe);
			}
		}

		public void paintConstraints(Graphics2D g2)
		{

			for (int i = 0; i < constraints.size(); i++)
			{
				double[][] recipe = constraints.get(i).getRecipe();
				paintFC(g2, recipe);
			}
		}

		public void paintFC(Graphics2D g2, double[][] recipe){

			int diffX = widthRef()-this.getWidth();
			int diffY = heightRef()-this.getHeight();
			if(recipe[0][0] == 1){
				// Draw circle
				g2.drawOval(
						(int) centerAndScale(recipe[1][0] - recipe[2][0], true) - diffX,
						(int) centerAndScale(recipe[1][1] - recipe[2][0], false) - diffY,
						(int) scale(2*recipe[2][0], true),
						(int) scale(2*recipe[2][0], false)
						);
			}
			if(recipe[0][0] == 2){
				// Draw line
				Line2D line = new Line2D.Double(
						centerAndScale(recipe[1][0], true) - diffX,
						centerAndScale(recipe[1][1], false) - diffY,
						centerAndScale(recipe[2][0], true) - diffX,
						centerAndScale(recipe[2][1], false) - diffY
				);
				g2.draw(line);
			}
		}

		public double centerAndScale(double s, boolean isX){
			return scale(center(s),isX);
		}

		public double scale(double s, boolean isX){
			return (s/2)*((isX)?widthRef():heightRef());
		}

		public double center(double s){
			return s+1;
		}

		public double[] adjustMousePos(double[] mousePos){
			assert mousePos.length > 1;
			return new double[]{(2*mousePos[0]/widthRef())-1, (2*mousePos[1]/heightRef())-1};
		}
	}
}
