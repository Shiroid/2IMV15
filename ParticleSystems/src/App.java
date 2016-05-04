import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

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
		
		pVector.add(new Particle(center[0] + offset[0], center[1] + offset[1]));
		pVector.add(new Particle(center[0] + offset[0] + offset[0], center[1] + offset[1] + offset[1]));
		pVector.add(new Particle(center[0] + offset[0] + offset[0] + offset[0], center[1] + offset[1] + offset[1] + offset[1]));
		
		// TODO: Add constraints
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
	
	/***
	 * Render functions
	 */
	
	public void updateParticles()
	{
		if (dsim)
		{
			// TODO: Do simulation step
			for (int i = 0; i < pVector.size(); i++)
			{
				pVector.get(i).m_Position[1] += 1;
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
			pVector.get(i).m_Position = pVector.get(i).m_ConstructPos;
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
			
			Thread.sleep(5);
		}
	}
}
