import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import org.apache.commons.math3.linear.*;

import javax.swing.JFrame;

public class App implements KeyListener, MouseListener
{
	// Simulation constants
	static int N = 64;
	static double dt = 0.1;
	static double d = 5;
	
	// Simulation variables
	boolean dsim, dump_frames;
	int frame_number;

	Vector<Particle> pVector;
	Vector<Force> fVector;
	Vector<Constraint> cVector;
	
	MainFrame frame;
	
	public App()
	{
		dsim = false;
		dump_frames = false;
		frame_number = 0;
	}
	
	/***
	 * Initiate system & window
	 */
	
	public void initSystem()
	{
		double dist = 0.2;
		double[] center = {0, 0};
		double[] offset = {dist, 0};
		
		// Create three particles, attach them to each other, then add a
		// circular wire constraint to the first.
		pVector = new Vector<Particle>();
		fVector = new Vector<Force>();
		cVector = new Vector<Constraint>();
		
		addParticle(new Particle(center[0] + offset[0], center[1] + offset[1]));
		addParticle(new Particle(center[0] + offset[0] + offset[0], center[1] + offset[1] + offset[1]));
		addParticle(new Particle(center[0] + offset[0] + offset[0] + offset[0], center[1] + offset[1] + offset[1] + offset[1]));

		// TODO: Add constraints
		addForce(new DirectionalForce(pVector, new double[]{0, 0.0001}));//Gravity
		addForce(new SpringForce(pVector.get(0), pVector.get(1), 0.01, 0.1, 0.3));
	}
	
	public void openWindow()
	{
		// Open window
		frame = new MainFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(800, 600);
		
		// Set listeners
		frame.addKeyListener(this);
		frame.addMouseListener(this);
	}

	public void addParticle(Particle p){
		int id = pVector.size();
		pVector.add(p);
		p.setID(id);
	}

	public void addForce(Force f){
		int id = fVector.size();
		fVector.add(f);
	}

	public void addConstraint(Constraint c){
		int id = cVector.size();
		cVector.add(c);
		c.setID(id);
	}
	
	/***
	 * Render functions
	 */
	
	public void updateParticles()
	{
		if (dsim)
		{
			// Step 1: Clear forces 
			for (int i = 0; i < pVector.size(); i++)
			{
				pVector.get(i).clearForce();
			}
			// Step 2: Calculate forces
			for (int i = 0; i < fVector.size(); i++)
			{
				fVector.get(i).apply();
			}
			// Step 3: Calculate constraint forces (correction step)
			// q, Q, M, W, C, lambda
			for (int i = 0; i < cVector.size(); i++)
			{
				
			}
			
			// Step 4: Calculate the derivative (update step, explicit Euler)
			for (int i = 0; i < pVector.size(); i++)
			{
				Particle p = pVector.get(i);
				p.updateVelocity(dt);
				p.updatePosition(dt);
			}
		}
		else
		{
			resetGUI();
		}
	}
	
	public void updateView()
	{
		// Draw forces, constraints & particles
		frame.drawParticles(pVector);
	}
	
	public void resetGUI()
	{
		// Reset particles to initial position
		for (int i = 0; i < pVector.size(); i++)
		{
			pVector.get(i).reset();
		}
	}
	
	/***
	 * Event listeners
	 */
	
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_C:
				break;
			case KeyEvent.VK_D:
				dump_frames = !dump_frames;
				break;
			case KeyEvent.VK_Q:
				System.exit(0);
				break;
			case KeyEvent.VK_SPACE:
				dsim = !dsim;
				break;
		}
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public static void main(String[] args) throws InterruptedException
	{
		/*ConjugateGradient cg = new ConjugateGradient(100, 0.0000000001, false);
		//OpenMapRealMatrix m = new OpenMapRealMatrix(new double[][]{{1,2},{3,4}});
		OpenMapRealMatrix m = new OpenMapRealMatrix(5, 5);
		//ArrayRealVector b = new ArrayRealVector(new double[]{7,15});
		//RealVector x = cg.solve(m, b);
		System.out.println(m.toString());*/
		
		
		// Instructions
		System.out.println("\n\nHow to use this application:\n\n");
		System.out.println("\t Toggle construction/simulation display with the spacebar key\n");
		System.out.println("\t Dump frames by pressing the 'd' key\n");
		System.out.println("\t Quit by pressing the 'q' key\n");
		
		// Open app
		App app = new App();
		app.initSystem();
		app.openWindow();
		
		// Loop
		while (true)
		{
			app.updateParticles();
			app.updateView();
			
			Thread.sleep(1);
		}
	}
}
