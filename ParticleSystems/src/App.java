import java.awt.event.*;
import java.util.Vector;

import jdk.nashorn.internal.scripts.JD;
import org.apache.commons.math3.linear.*;

import javax.swing.JFrame;

public class App implements KeyListener, MouseListener, MouseMotionListener
{
	// Simulation constants
	static int N = 64;
	static double dt = 0.1;
	static double d = 5;
	
	// Simulation variables
	boolean dsim, dump_frames, dfor, dcon;
	int frame_number, integration_type;

	Vector<Particle> pVector;
	Vector<Force> fVector;
	Vector<Constraint> cVector;
	Vector<Solid> sVector;
	
	MainFrame frame;

	private int mouseSpringIndex;
	private int mouseRepulsorIndex;

	private double tetherStrength = 0.5;
	private double tetherDamping = 0.5;

	private double boundEps = 0.02;
	private double boundBounce = 0.5;

	private double constraintKs = 0.01;
	private double constraintKd = 0.01;
	
	public App()
	{
		dsim = false;
		dfor = true;
		dcon = true;
		dump_frames = false;
		frame_number = 0;
	}
	
	/***
	 * Initiate system & window
	 */
	
	public void initSystem()
	{
		double dist = 0.1;
		double[] center = {0, 0};
		double[] offset = {dist, 0};
		
		// Create three particles, attach them to each other, then add a
		// circular wire constraint to the first.
		pVector = new Vector<Particle>();
		fVector = new Vector<Force>();
		cVector = new Vector<Constraint>();
		sVector = new Vector<Solid>();

		// Set boundaries
		addSolid(new Boundary(pVector, new double[]{1,0}, new double[]{-1,0}, boundBounce, boundEps));
		addSolid(new Boundary(pVector, new double[]{-1,0}, new double[]{1,0}, boundBounce, boundEps));
		addSolid(new Boundary(pVector, new double[]{0,1}, new double[]{0,-1}, boundBounce, boundEps));
		addSolid(new Boundary(pVector, new double[]{0,-1}, new double[]{0,1}, boundBounce, boundEps));
		
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
		addCloth(15, 15, 0.05, -0.8, -0.3, 0.1, 0.5, true, true, false);
		addCloth(8, 8, 0.08, 0.3, -0.5, 0.05, 0.9, true, false, false);
		addCloth(4, 3, 0.1, 0.0, 0.3, 0.05, 0.9, false, false, false);
		addHair(20, 0.05, -0.1, -0.8, 0.5, 0.9, 180, true, false, false);
		//addHair(10, 0.05, 0.0, -0.8, 0.5, 0.9, 180, true, false, true);
		addHair(20, 0.05, 0.1, -0.8, 0.5, 0.9, 120, true, false, true);
		addHair(20, 0.05, 0.2, -0.8, 0.5, 0.9, 90, true, false, true);
		addHair(30, 0.05, 0.3, -0.8, 0.5, 0.9, 60, true, false, true);

		// Constraints for spring
		addConstraint(new CircularWireConstraint(pVector.get(0), new double[]{center[0]+offset[0], center[1]+offset[1]-offset[0]}, offset[0]));
		addConstraint(new RodConstraint(pVector.get(0), pVector.get(1), offset[0]));
		addConstraint(new RodConstraint(pVector.get(1), pVector.get(2), offset[0]));
		// Angular spring
		//addForce(new AngularSpringForce(pVector.get(1), pVector.get(0), pVector.get(2), 0.0002, 0.001, 90));
	}

	public void addHair(int hi, double di, double bXi, double bYi, double ksi, double kdi, double angle, boolean dti, boolean constrained, boolean doAngles){
		// Add cloth
		int pOffset = pVector.size();
		int h = hi;// hair length
		double d = di;// Hair particle proximity
		double baseX = bXi;// X position of upper corner
		double baseY = bYi;// Y position of upper corner
		double ks = ksi;// hair spring strength
		double kd = kdi;// hair spring damping
		boolean doTether = dti;// Tether the hair
		double angularForceAdjust = 0.0003;
		for (int j = 0; j < h; j++){
			addParticle(new Particle(baseX, baseY + j*d));
			if(j > 0){
				if(constrained){
					addConstraint(new RodConstraint(
							pVector.get(pOffset+j),
							pVector.get(pOffset+j-1),
							d
					));
				} else {
					addForce(new SpringForce(
							pVector.get(pOffset+j),
							pVector.get(pOffset+j-1),
							ks,
							kd,
							d
					));
				}
				if(doAngles && j > 1){
					addForce(new AngularSpringForce(
							pVector.get(pOffset+j-2),
							pVector.get(pOffset+j-1),
							pVector.get(pOffset+j),
							ks*d*angularForceAdjust,
							kd*d*angularForceAdjust,
							angle
					));
				}
			}
		}
		if(doTether) {
			double radius = di;
			Particle p1 = pVector.get(pOffset);
			addConstraint(new CircularWireConstraint(p1,
					new double[]{p1.m_ConstructPos[0], p1.m_ConstructPos[1] + radius}, radius));
		}
	}

	public void addCloth(int wi, int hi, double di, double bXi, double bYi, double ksi, double kdi, boolean dti, boolean crossed, boolean constrained){
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
		boolean doTether = dti;// Tether the cloth's upper corners
		for (int i = 0; i < w; i++){
			for (int j = 0; j < h; j++){
				addParticle(new Particle(baseX + i*d, baseY + j*d));
				if(i > 0){
					if(constrained){
						addConstraint(new RodConstraint(
								pVector.get(pOffset+i*h+j),
								pVector.get(pOffset+(i-1)*h+j),
								d
						));
					} else{
						addForce(new SpringForce(
								pVector.get(pOffset+i*h+j),
								pVector.get(pOffset+(i-1)*h+j),
								ks,
								kd,
								d
						));
					}
				}
				if(j > 0){
					if(constrained){
						addConstraint(new RodConstraint(
								pVector.get(pOffset+i*h+j),
								pVector.get(pOffset+i*h+j-1),
								d
						));
					} else {
						addForce(new SpringForce(
								pVector.get(pOffset+i*h+j),
								pVector.get(pOffset+i*h+j-1),
								ks,
								kd,
								d
						));
					}
				}
				if(crossed){
					if(i > 0 && j > 0){
						if(constrained) {
							addConstraint(new RodConstraint(
									pVector.get(pOffset+i*h+j),
									pVector.get(pOffset+(i-1)*h+j-1),
									d*sqrt2
							));
						} else {
							addForce(new SpringForce(
									pVector.get(pOffset+i*h+j),
									pVector.get(pOffset+(i-1)*h+j-1),
									ks,
									kd,
									d*sqrt2
							));
						}
					}
					if(i > 0 && j < h-1){
						if(constrained){
							addConstraint(new RodConstraint(
									pVector.get(pOffset+i*h+j),
									pVector.get(pOffset+(i-1)*h+j+1),
									d*sqrt2
							));
						} else {
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
			}
		}
		if(doTether) {
			double radius = di;
			Particle p1 = pVector.get(pOffset);
			Particle p2  = pVector.get(pOffset+(w-1)*h);
			addConstraint(new CircularWireConstraint(p1,
					new double[]{p1.m_ConstructPos[0], p1.m_ConstructPos[1] + radius}, radius));
			addConstraint(new CircularWireConstraint(p2,
					new double[]{p2.m_ConstructPos[0], p2.m_ConstructPos[1] + radius}, radius));
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
		fVector.add(f);
	}

	public void addConstraint(Constraint c){
		int id = cVector.size();
		cVector.add(c);
		c.setID(id);
	}

	public void addSolid(Solid s){
		sVector.add(s);
	}
	
	/***
	 * Render functions
	 */
	
	public void updateParticles(double delT)
	{
		// Step 0: Check collissions
		for (int i = 0; i < sVector.size(); i++)
		{
			sVector.get(i).apply();
		}
		// Step 1: Clear forces
		for (int i = 0; i < pVector.size(); i++)
		{
			pVector.get(i).clearForce();
		}
		// Step 2: Calculate forces
		if(dfor){
			for (int i = 0; i < fVector.size(); i++)
			{
				fVector.get(i).apply();
			}
		}

		// Step 3: Calculate constraint forces (correction step)
		// q, Q, M, W, C, lambda
		// JWJ* lambda = -J.q. -JWQ -ksC - kdC.
		// Q^ = J* lambda
		if(dcon) {
			try {
				int gpVecLen = pVector.size() * VectorMath.VECSIZE;
				int gcVecLen = cVector.size();
				OpenMapRealMatrix W = new OpenMapRealMatrix(gpVecLen, gpVecLen);
				OpenMapRealMatrix J = new OpenMapRealMatrix(gcVecLen, gpVecLen);
				OpenMapRealMatrix Jdot = new OpenMapRealMatrix(gcVecLen, gpVecLen);
				OpenMapRealMatrix JT = new OpenMapRealMatrix(gpVecLen, gcVecLen);
				ArrayRealVector CScaled = new ArrayRealVector(gcVecLen); // scaled by -ks
				ArrayRealVector CdotScaled = new ArrayRealVector(gcVecLen); // scaled by -kd
				ArrayRealVector qdot = new ArrayRealVector(gpVecLen);
				ArrayRealVector Q = new ArrayRealVector(gpVecLen);

				for (int i = 0; i < pVector.size(); i++) {
					Particle p = pVector.get(i);
					for (int k = 0; k < VectorMath.VECSIZE; k++) {
						W.setEntry(VectorMath.VECSIZE * i + k, VectorMath.VECSIZE * i + k, 1 / p.mass);
						Q.setEntry(VectorMath.VECSIZE * i + k, p.m_Force[k]);
						qdot.setEntry(VectorMath.VECSIZE * i + k, p.m_Velocity[k]);
					}
				}

				for (int i = 0; i < cVector.size(); i++) {
					Constraint c = cVector.get(i);
					CScaled.setEntry(i, -constraintKs * c.getC0());
					CdotScaled.setEntry(i, -constraintKd * c.getC1());
					ConstraintDerivative cd0 = c.getCd0();
					for (int j = 0; j < cd0.particles.length; j++) {
						J.setEntry(i, VectorMath.VECSIZE * cd0.particles[j], cd0.values[j][0]);
						J.setEntry(i, VectorMath.VECSIZE * cd0.particles[j] + 1, cd0.values[j][1]);
						JT.setEntry(VectorMath.VECSIZE * cd0.particles[j], i, cd0.values[j][0]);
						JT.setEntry(VectorMath.VECSIZE * cd0.particles[j] + 1, i, cd0.values[j][1]);
					}
					ConstraintDerivative cd0dt = c.getCd0dt();
					for (int j = 0; j < cd0dt.particles.length; j++) {
						Jdot.setEntry(i, VectorMath.VECSIZE * cd0dt.particles[j], cd0dt.values[j][0]);
						Jdot.setEntry(i, VectorMath.VECSIZE * cd0dt.particles[j] + 1, cd0dt.values[j][1]);
					}
				}
				// JWJ* lambda = -J.q. -JWQ -ksC - kdC.
				OpenMapRealMatrix lhs = J.multiply(W).multiply(JT);
				RealVector rhs = CScaled.add(CdotScaled).subtract(Jdot.operate(qdot)).subtract(J.multiply(W).operate(Q));
				ConjugateGradient cg = new ConjugateGradient(2*gcVecLen + 10, 0.000001, false);
				RealVector lambda = cg.solve(lhs, rhs);

				// Q^ = J* lambda
				RealVector Qhat = JT.operate(lambda);
				for (int i = 0; i < pVector.size(); i++) {
					Particle p = pVector.get(i);
					p.m_Force[0] += Qhat.getEntry(VectorMath.VECSIZE * i);
					p.m_Force[1] += Qhat.getEntry(VectorMath.VECSIZE * i + 1);
				}
			} catch (Exception e){
				dsim = !dsim;
				dcon = true;
				System.out.println("Aborted constraint solver.");
			}
		}

		// Step 4: Calculate the derivative (update step, explicit Euler)
		for (int i = 0; i < pVector.size(); i++)
		{
			Particle p = pVector.get(i);
			p.updateVelocity(delT);
			p.updatePosition(delT);
		}
	}

	public void performUpdate(){
		if (dsim)
		{
			switch (integration_type){
				case 1:
					// Do midpoint
					double[][] base = getState();
					updateParticles(dt);
					double[][] eul = getState();
					setState(base);
					double[][] eulDiff = getStateDiff(eul);
					addStateDiff(eulDiff, 1.0/2);

					double[][] mid = getState();
					updateParticles(dt);
					double[][] postmid = getState();
					setState(mid);
					double[][] midDiff = getStateDiff(postmid);
					setState(base);
					addStateDiff(midDiff, 1.0);
					break;
				case 2:
					// Do Runge-Kutta
					double[][] s1b = getState();
					updateParticles(dt);
					double[][] s1 = getState();
					setState(s1b);
					double[][] k1 = getStateDiff(s1);

					addStateDiff(k1, 1.0/2);
					double[][] s2b = getState();
					updateParticles(dt);
					double[][] s2 = getState();
					setState(s2b);
					double[][] k2 = getStateDiff(s2);

					setState(s1b);
					addStateDiff(k2, 1.0/2);
					double[][] s3b = getState();
					updateParticles(dt);
					double[][] s3 = getState();
					setState(s3b);
					double[][] k3 = getStateDiff(s3);

					setState(s1b);
					addStateDiff(k3, 1.0);
					double[][] s4b = getState();
					updateParticles(dt);
					double[][] s4 = getState();
					setState(s4b);
					double[][] k4 = getStateDiff(s4);

					setState(s1b);
					addStateDiff(k1, 1.0/6);
					addStateDiff(k2, 1.0/3);
					addStateDiff(k3, 1.0/3);
					addStateDiff(k4, 1.0/6);
					break;
				default:
					// Do Euler
					updateParticles(dt);
			}
		}
		else
		{
			resetGUI();
		}
	}

	public double[][] getState(){
		double[][] k = new double[pVector.size()][4];
		for (int i = 0; i < pVector.size(); i++){
			Particle p = pVector.get(i);
			k[i][0] = p.m_Position[0];
			k[i][1] = p.m_Position[1];
			k[i][2] = p.m_Velocity[0];
			k[i][3] = p.m_Velocity[1];
		}
		return k;
	}

	public double[][] getStateDiff(double[][] knew){
		double[][] res = new double[pVector.size()][4];
		for (int i = 0; i < pVector.size(); i++){
			Particle p = pVector.get(i);
			res[i][0] = knew[i][0] - p.m_Position[0];
			res[i][1] = knew[i][1] - p.m_Position[1];
			res[i][2] = knew[i][2] - p.m_Velocity[0];
			res[i][3] = knew[i][3] - p.m_Velocity[1];
		}
		return res;
	}

	public void setState(double[][] k){
		for (int i = 0; i < pVector.size(); i++){
			Particle p = pVector.get(i);
			p.m_Position[0] = k[i][0];
			p.m_Position[1] = k[i][1];
			p.m_Velocity[0] = k[i][2];
			p.m_Velocity[1] = k[i][3];
		}
	}

	public void addStateDiff(double[][] k, double fraction){
		for (int i = 0; i < pVector.size(); i++){
			Particle p = pVector.get(i);
			p.m_Position[0] += fraction*k[i][0];
			p.m_Position[1] += fraction*k[i][1];
			p.m_Velocity[0] += fraction*k[i][2];
			p.m_Velocity[1] += fraction*k[i][3];
		}
	}
	
	public void updateView()
	{
		// Draw forces, constraints & particles
		frame.drawParticles(pVector);
		if(dfor) frame.drawForces(fVector);
		if(dcon) frame.drawConstraints(cVector);
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
				dcon = !dcon;
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
			case KeyEvent.VK_F:
				dfor = !dfor;
				break;
			case KeyEvent.VK_G:
				integration_type = (integration_type+1)%3;
				System.out.println("Integration scheme: " + integration_type);
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
		
		
		// Instructions
		System.out.println("\n\nHow to use this application:\n\n");
		System.out.println("\t Toggle construction/simulation display with the spacebar key\n");
		System.out.println("\t Dump frames by pressing the 'd' key\n");
		System.out.println("\t Toggle forces by pressing the 'f' key\n");
		System.out.println("\t Toggle constraints by pressing the 'c' key\n");
		System.out.println("\t Repel particles from cursor by pressing the 'r' key\n");
		System.out.println("\t Cycle through integration schemes by pressing the 'g' key");
		System.out.println("\t 0: Euler, 1: Midpoint, 2: Runge-Kutta\n");
		System.out.println("\t Quit by pressing the 'q' key\n");
		
		// Open app
		App app = new App();
		app.initSystem();
		app.openWindow();
		
		// Loop
		while (true)
		{
			app.performUpdate();
			app.updateView();
			
			Thread.sleep(1);
		}
	}
}
