
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
//import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Robot;
//Controls: WASD to move, arrow keys to pan camera. IE: 'W' moves you forward, 'UpArrow' makes you look up. 
// 			**You may also use your mouse to pan the camera around. **
//			PRESS Q TO QUIT! Alternatively, press SHIFT to pause the animation for a better look. 

// Note: Because of the mouse controls (and JoGL's GLUT not allowing you to use warpPointer to move the mouse, I couldn't do relative positions of 
// the mouse and had to do absolute coordinates, while the mouseListener uses relative coordinates-- simple solution: make them one and the same!
// Unfortunately, this means the game has to be run in full screen. 

//	Note on centipede-- it's a known bug with a known bug. This particular insect does not like my "if out of the box, turn around" code. This is because 
//  of how it is moved-- it's really one head segment that has body segments "following" it. This is because I hope to have it "split" in my game. That being 
//  said, the sudden 180 causes the tail to "whip" behind it too quickly and it "breaks"... and if it lands outside of the box a second time, it's possible 
//  that the body segments appear seperated, and begin to, for a lack of a better word, "helicopter." You'll know it if you see it. I was going to fix this,
//  but to be honest it's so funny I wanted to keep it. 
public class Debugger implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	private Butterfly[] Creature = new Butterfly[4];
	private centipede[] centi = new centipede[2]; 
	private Spider[] Muffet = new Spider[1]; 
	private world Earth; 
	private Float rotateY=-95f;
	private GLCanvas canvas;
	private GLU glu = new GLU();
	private static Robot droid;
	private float viewangle=0f, stepsize=.5f, pan=2f;
	public float eyex=10f, eyey=4f, eyez=10f; 
	public boolean BETAmode=false, paused=false;
	private static JFrame frame;
	public static int width;
	public static int height;
	public void init(GLAutoDrawable drawable) 
	{
		GL2 gl = drawable.getGL().getGL2();
		gl.setSwapInterval(1);			// for animation synchronized to refresh rate
		gl.glClearColor(0f,0f,0f,0f);	// black background

		gl.glShadeModel(GL2.GL_SMOOTH);	// smooth or flat 		
		gl.glClearDepth(1.0f);			// depth handling routines	
		gl.glEnable(GL2.GL_DEPTH_TEST);	
		gl.glDepthFunc(GL2.GL_LEQUAL);
		//  How nice is the drawing?
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		Earth = new world(gl); //probably the most "I am alpha and omega" line of code ever. 
		for(int i=0; i<Creature.length; i++)
			Creature[i] = new Butterfly(gl, canvas, 80f, 80f);
		for(int i=0; i<Muffet.length; i++)
			Muffet[i]=new Spider(gl, canvas, 80f, 80f); 

		for(int i=0; i<centi.length; i++)
			centi[i] = new centipede(gl, canvas, 80f, 80f);

	}

	public void display(GLAutoDrawable drawable)
	{
		//width = height = Math.min(drawable.getWidth(), drawable.getHeight());
		GL2 gl  = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();


		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, width/2, height); 
		glu.gluPerspective(90., 1., .5, 180.); 		// fov, aspect, near-clip, far clip
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex, eyey, eyez, 					// eye location
				eyex+Math.cos(Math.toRadians(viewangle)),	// point to look at (near middle)
				eyey, 
				eyez-Math.sin(Math.toRadians(viewangle)),	
				0f,1f,0f); 						// the "up" direction

		Earth.draw(gl);
		for (int i =0; i<Creature.length; i++)
			Creature[i].draw(gl);
		for (int i =0; i<Muffet.length; i++)
			Muffet[i].draw(gl);
		for(int i=0; i<centi.length; i++)
			centi[i].draw(gl);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(width/2, 0, width/2, height);	
		gl.glOrtho(-width/2, width/2, -height/2, height/2, 0.1, 9.9);
		//glu.gluPerspective(90., 1., 20.1, 29.6); 		// fov, aspect, near-clip, far clip
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex, 10, eyez, 					// eye location
				eyex+Math.cos(Math.toRadians(viewangle)),	// point to look at (near middle)
				eyey, 
				eyez-Math.sin(Math.toRadians(viewangle)),	
				0f,1f,0f); 						// the "up" direction

		Earth.draw(gl);
		for (int i =0; i<Creature.length; i++)
			Creature[i].draw(gl);
		for (int i =0; i<Muffet.length; i++)
			Muffet[i].draw(gl);
		for(int i=0; i<centi.length; i++)
			centi[i].draw(gl);

		// check for errors
		int error = gl.glGetError();
		if (error != GL.GL_NO_ERROR)
			System.out.println("OpenGL Error: " + error);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		GL2  gl  = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		//this avoids different x,y scaling
		gl.glViewport(0, 0, width, height);
		//System.out.println((width/height));
		glu.gluPerspective(90, (width/height), .5, 300.); 		// fov, aspect, near-clip, far clip
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex, eyey, eyez, 					// eye location
				eyex+Math.cos(Math.toRadians(viewangle)),	// point to look at (near middle)
				eyey, eyez-Math.sin(Math.toRadians(viewangle)),	
				0f,1f,0f); 						// the "up" direction (y)

	}

	public static void main(String args[])
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)screenSize.getWidth();
		height = (int)screenSize.getHeight();
		System.setProperty("sun.awt.noerasebackground", "true"); // sometimes necessary to avoid erasing the finished window
		Toolkit t = Toolkit.getDefaultToolkit();
		Image i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Cursor noCursor = t.createCustomCursor(i, new Point(0, 0), "none"); 

		frame = new JFrame("Debugger!");
		frame.setCursor(noCursor);
		GLCanvas canvas = new GLCanvas();
		canvas.setPreferredSize(new Dimension(width,height));  // desired size, not guaranteed
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);  
		frame.setUndecorated(true);  
		try {
			droid = new Robot();
			droid.mouseMove(300, 300);
		} catch (Exception error){}

		Debugger renderer = new Debugger();
		canvas.addGLEventListener(renderer);
		canvas.addKeyListener(renderer);
		canvas.addMouseMotionListener(renderer);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); // make just big enough to hold objects inside
		frame.setVisible(true);

		canvas.requestFocusInWindow();	// Sets focus to the main window

		// This is for continual automatic redraws, to stop, comment out both lines
		Animator animator = new Animator(canvas);
		animator.start();		
	}

	//  What to do when a key is pressed
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		Float movex, movez; 
		switch (key)
		{
		case KeyEvent.VK_MINUS:
			if (centi[0].HP>0)
				centi[0].HP--;  
			Creature[0].HP=0;
			Muffet[0].HP--;
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:	
			viewangle-=pan;  break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:		
			viewangle+=pan;  break;
		case KeyEvent.VK_UP:
			rotateY+=5;
			System.out.println(rotateY);
			break;
		case KeyEvent.VK_SHIFT: // allows you to "pause" as well as release the cursor. For testing purposes only. 
			paused=!paused;
			for (int i=0; i <Creature.length; i++){
				Creature[i].paused=!Creature[i].paused;
				centi[i].paused = paused;
				Muffet[i].paused = paused;
			}
			if (BETAmode)
				Creature[3].paused=!Creature[3].paused;

			break;
		case KeyEvent.VK_W:
			movex = new Float(eyex+stepsize*Math.cos(Math.toRadians(viewangle))); 
			movez = new Float(eyez-stepsize*Math.sin(Math.toRadians(viewangle)));
			//makes people fly! :D
			//movey = new Float(eyey+stepsize*Math.cos(Math.toRadians(rotateY)));
			if (tooClose(movex, 0f) || tooClose(movez, 0f) || tooClose(movex, 160f) || tooClose(movez, 160f))
				break;
			eyex = movex;
			eyez = movez;	
			break;
		case KeyEvent.VK_DOWN:	
			rotateY-=5;
			//System.out.println(rotateY);
			break; 
		case KeyEvent.VK_S:	
			movex = new Float(eyex-stepsize*Math.cos(Math.toRadians(viewangle))); 
			movez = new Float(eyez+stepsize*Math.sin(Math.toRadians(viewangle)));
			//movey = new Float(eyey-stepsize*Math.cos(Math.toRadians(rotateY)));
			if (tooClose(movex, 0f) || tooClose(movez, 0f) || tooClose(movex, 160f) || tooClose(movez, 160f))
				break;
			eyex = movex;
			eyez = movez;
			break;

		case KeyEvent.VK_Q:	System.exit(0);  break;
		}

	}
	// if your step would land you within 1 unit of the wall (.5 on either side), you're too close. 
	boolean tooClose(Float x, Float coord){
		if (Math.abs(x-coord)<1)
			return true;
		else
			return false; 
	}
	// same as above but allows for varying buffer zone. 
	boolean tooClose(Float x, Float coord, Float buffer){
		if (Math.abs(x-coord)<buffer)
			return true;
		else
			return false; 
	}
	// lets you see if you'll land on a point in a given line (great for our semi-2D walls. 
	boolean between(Float x, Float coord1, Float coord2) { 
		if (x >= coord1 && x <= coord2)
			return true;
		else
			return false; 
	}
	// Ignore all of these-- but they look fun D:
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void dispose(GLAutoDrawable drawable) {}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent key){}
	public void mouseReleased(MouseEvent key){}
	public void mousePressed(MouseEvent mouse){}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {
		// Update mouse position
		int x=e.getX(), y = e.getY();
		int variance = 12;
		if (x<300-variance) {
			viewangle+=pan;
		} else if (x > 300+variance) { 
			viewangle-=pan;
		} 
		if (y>300+variance) {
			rotateY-=pan; 
		} else if (y<300-variance) {
			rotateY+=pan;
		}
		droid.mouseMove(300, 300); 
	}

}

