import java.awt.event.*;
import java.util.Vector;

import org.apache.commons.math3.linear.*;

import javax.swing.JFrame;

public class App implements KeyListener, MouseListener, MouseMotionListener
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

	private int mouseSpringIndex;
	private int mouseRepulsorIndex;

	private double tetherStrength = 0.5;
	private double tetherDamping = 0.5;
	
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

		// Add mouse spring
		Vector<Particle> m = new Vector<Particle>();
		m.add(pVector.get(0));
		addForce(new TetheredSpringForce(m, tetherStrength, tetherDamping, 0.0));
		mouseSpringIndex = fVector.size()-1;

		// Add mouse repulsor
		addForce(new RepulsionForce(pVector, new double[]{0,0}, 0.00001));
		mouseRepulsorIndex = fVector.size()-1;

		// Add cloth
		addCloth(12, 12, 0.05, -0.5, -0.5, 0.1, 0.5, true);
		addCloth(8, 8, 0.08, 0.3, -0.5, 0.05, 0.5, true);

		// Additional forces
		addForce(new SpringForce(pVector.get(0), pVector.get(1), 0.01, 0.1, 0.3));
	}

	public void addCloth(int wi, int hi, double di, double bXi, double bYi, double ksi, double kdi, boolean dti){
		// Add cloth
		int pOffset = pVector.size();
		int w = wi;// Cloth width
		int h = hi;// Cloth height
		double d = di;// Cloth particle proximity
		double sqrt2 = 1.41421356237;
		double baseX = bXi;// X position of upper corner
		double baseY = bYi;// Y position of upper corner
		double ks = ksi;// Cloth spring strength
		double kd = kdi;// Cloth spring damping
		boolean doTether = true;// Tether the cloth's upper corners
		for (int i = 0; i < w; i++){
			for (int j = 0; j < h; j++){
				addParticle(new Particle(baseX + i*d, baseY + j*d));
				if(i > 0){
					addForce(new SpringForce(
							pVector.get(pOffset+i*h+j),
							pVector.get(pOffset+(i-1)*h+j),
							ks,
							kd,
							d
					));
				}
				if(j > 0){
					addForce(new SpringForce(
							pVector.get(pOffset+i*h+j),
							pVector.get(pOffset+i*h+j-1),
							ks,
							kd,
							d
					));
				}
				if(i > 0 && j > 0){

					addForce(new SpringForce(
							pVector.get(pOffset+i*h+j),
							pVector.get(pOffset+(i-1)*h+j-1),
							ks,
							kd,
							d*sqrt2
					));
				}
				if(i > 0 && j < h-1){

					addForce(new SpringForce(
							pVector.get(pOffset+i*h+j),
							pVector.get(pOffset+(i-1)*h+j+1),
							ks,
							kd,
							d*sqrt2
					));
				}
			}
		}
		if(doTether) {
			Vector<Particle> p1 = new Vector<Particle>();
			p1.add(pVector.get(pOffset));
			TetheredSpringForce t1 = new TetheredSpringForce(
					p1, tetherStrength, tetherDamping, 0.0

			);
			t1.setOn(true);
			Vector<Particle> p2 = new Vector<Particle>();
			p2.add(pVector.get(pOffset+(w-1)*h));
			TetheredSpringForce t2 = new TetheredSpringForce(
					p2, tetherStrength, tetherDamping, 0.0
			);
			t2.setOn(true);
			addForce(t1);
			addForce(t2);
		}
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
		frame.addMouseMotionListener(this);
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
		frame.drawForces(fVector);
		frame.drawConstraints(cVector);
		frame.paintSystem();
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
			case KeyEvent.VK_R:
				fVector.get(mouseRepulsorIndex).setOn(true);
				break;
		}
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_R:
				fVector.get(mouseRepulsorIndex).setOn(false);
				break;
		}
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		Force f = fVector.get(mouseSpringIndex);
		double[] mousePos = new double[]{e.getX(), e.getY()};
		mousePos = frame.adjustMousePos(mousePos);
		Particle clickedParticle = null;
		for (Particle p: pVector){
			double[] diff = VectorMath.subtract(p.m_Position, mousePos);
			double dist = diff[0]*diff[0]+diff[1]*diff[1];
			if(dist < 0.04*0.04){
				clickedParticle = p;
			}
		}
		if(clickedParticle != null){
			Vector<Particle> vec = new Vector<Particle>();
			vec.add(clickedParticle);
			f.setParticles(vec);
			f.setTether(mousePos);
			f.setOn(true);
		}
	}
	public void mouseReleased(MouseEvent e) {
		Force f = fVector.get(mouseSpringIndex);
		f.setOn(false);
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {
		Force f = fVector.get(mouseSpringIndex);
		double[] mousePos = new double[]{e.getX(), e.getY()};
		mousePos = frame.adjustMousePos(mousePos);

		f.setTether(mousePos);
	}
	public void mouseMoved(MouseEvent e) {
		Force f = fVector.get(mouseRepulsorIndex);
		double[] mousePos = new double[]{e.getX(), e.getY()};
		mousePos = frame.adjustMousePos(mousePos);

		f.setTether(mousePos);
	}
	
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
		System.out.println("\t Repel particles from cursor by pressing the 'r' key\n");
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
