import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.gl2.GLUT;

import com.jogamp.opengl.util.*;

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
	// 
	private Butterfly[] butterfly = new Butterfly[4];
	//centi = centipedes
	private Centipede[] centi = new Centipede[2]; 
	// Little miss muffet= spiders. 
	private Spider[] muffet = new Spider[1]; 
	private World Earth; 
	private Float rotateY=-95f;
	private GLCanvas canvas;
	private GLU glu = new GLU();
	private static GLUT glut = new GLUT();
	private static Robot droid;
	private float viewangle=0f, stepsize=.5f, pan=2f;
	public float eyex=10f, eyey=4f, eyez=10f; 
	public boolean BETAmode=false, paused=false;
	private static JFrame frame;
	public static int width;
	public static int height;
	private static long startTime = System.currentTimeMillis();
	
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
		Earth = new World(gl); //probably the most "I am alpha and omega" line of code ever.
		
		
		//For the instantiations below, use the parameters (gl, canvas, X, Z)
		//instantiating all butterflies
		for(int i=0; i<butterfly.length; i++)
			butterfly[i] = new Butterfly(gl, canvas, 21f*i, 21f*i);
		//instantiating all Spiders
		for(int i=0; i<muffet.length; i++)
			muffet[i]=new Spider(gl, canvas, 80f, 40f); 
		//instantiating all centipedes
		for(int i=0; i<centi.length; i++)
			centi[i] = new Centipede(gl, canvas, 81f, 80f);

	}

	public void display(GLAutoDrawable drawable)
	{
		//width = height = Math.min(drawable.getWidth(), drawable.getHeight());
		GL2 gl  = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		int msize = 160*2;// MAP SIZE

//Scoreboard
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(width-msize, 0, msize, msize);	
		gl.glOrtho(msize/2, height/2, msize/2, height, -1, 12);
		gl.glColor3f(255.0f, 255.0f, 255.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		int x = msize/2 + 10;
		int y =  msize;
		int creatureCount = 0;
		
		gl.glRasterPos2i(x, y);
		for(int i=0; i<butterfly.length; i++) {
			if (butterfly[i].HP > 0) {creatureCount++;}
		}
		glut.glutBitmapString(5, "Butterflies Remaining: " + creatureCount);
		
		y += 55; creatureCount = 0;
		gl.glRasterPos2i(x, y);
		for(int i=0; i<centi.length; i++) {
			if (centi[i].HP > 0) {creatureCount++;}
		}
		glut.glutBitmapString(5, "Centipedes Remaining: " + creatureCount);
		
		y += 55; creatureCount = 0;
		gl.glRasterPos2i(x, y);
		for(int i=0; i<muffet.length; i++) {
			if (muffet[i].HP > 0) {creatureCount++;}
		}
		glut.glutBitmapString(5, "Spiders Remaining: " + creatureCount);
		
		y += 55;		
		long currentTime = System.currentTimeMillis();
		String timeRunning = (((currentTime - startTime) / (1000 * 60)) % 60) + ":" + 
				(((currentTime - startTime) / 1000) % 60);
		gl.glRasterPos2i(x, y);
		glut.glutBitmapString(5, "Debugging Time: " + timeRunning);

		//Map
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, width-msize, height); 
		glu.gluPerspective(90., 1., .5, 180.); 		// fov, aspect, near-clip, far clip
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex, eyey, eyez, 					// eye location
				eyex+Math.cos(Math.toRadians(viewangle)),	// point to look at (near middle)
				eyey, 
				eyez-Math.sin(Math.toRadians(viewangle)),	
				0f,1f,0f); 						// the "up" direction
	
		//drawing all the perspective objects
		Earth.draw(gl);
		for (int i =0; i<butterfly.length; i++)
			butterfly[i].draw(gl);
		for (int i =0; i<muffet.length; i++)
			muffet[i].draw(gl);
		for(int i=0; i<centi.length; i++)
			centi[i].draw(gl);
		
		
		//Mini-map
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		// msize is map size and is defined at the beginning.
		gl.glViewport(width-msize, height-msize, msize, msize);	
		gl.glOrtho(0, msize/2, 0, msize/2, 1, 12);
		//glu.gluPerspective(90., 1., 20.1, 29.6); 		// fov, aspect, near-clip, far clip
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex-80, 0, eyez-80, 
				eyex+1f-80, -15, eyez-80,
					0f,1f,0f); 	
		GLUquadric quadric = glu.gluNewQuadric(); 
		//the "You are here" dot on the minimap. 
		gl.glPushMatrix();
		gl.glTranslatef(eyex, 5, eyez);
		gl.glColor3f(1,0,0); 
		glu.gluSphere(quadric, 1f, 5, 5);
		gl.glPopMatrix();
		//drawing all the ortho objects
		Earth.draw(gl);
		for (int i = 0; i< butterfly.length; i++)
			butterfly[i].draw(gl);
		for (int i = 0; i<muffet.length; i++)
			muffet[i].draw(gl);
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
		//Screen dimension variables
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
		//When they hit minus, demo the bugs dying (remove later)
		case KeyEvent.VK_MINUS:
			if (centi[0].HP>0)
				centi[0].HP--;  
			butterfly[0].HP=0;
			muffet[0].HP--;
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
		case KeyEvent.VK_SHIFT: // allows you to "pause" animations. For testing purposes only. 
			paused=!paused;
			for (int i=0; i <butterfly.length; i++){
				butterfly[i].paused=!butterfly[i].paused;
				centi[i].paused = paused;
				muffet[i].paused = paused;
			}
			if (BETAmode)
				butterfly[3].paused=!butterfly[3].paused;
			break;
		case KeyEvent.VK_W:
			movex = new Float(eyex+stepsize*Math.cos(Math.toRadians(viewangle))); 
			movez = new Float(eyez-stepsize*Math.sin(Math.toRadians(viewangle)));
			//if you're trying to step into a wall, don't move forward. 
			if (wallcollide(movex, movez))
				break;
			eyex = movex;
			eyez = movez;	
			break;
		case KeyEvent.VK_DOWN:	//look down 
			rotateY-=5;
			//System.out.println(rotateY);
			break; 
		case KeyEvent.VK_S:	
			movex = new Float(eyex-stepsize*Math.cos(Math.toRadians(viewangle))); 
			movez = new Float(eyez+stepsize*Math.sin(Math.toRadians(viewangle)));
			if (wallcollide(movex, movez))
				break;
			eyex = movex;
			eyez = movez;
			break;

		case KeyEvent.VK_Q:	System.exit(0);  break;
		}

	}
	
	//Determine if a monster has been caught
	//Monster has been "caught" if it is within 2 units of the current
	//camera position

	public boolean caughtBug(Float x, Float z) {
		
		Float[] coord;
		Float[][] centiCoord;
		
		for (int i = 0; i < butterfly.length-1; i++) {
			coord = butterfly[i].getPos();
			if (Math.abs(x - coord[0]) < 2 && Math.abs(z - coord[2]) < 2) {
				butterfly[i].HP -= 20;
				return true;
			}
		}

		for (int i = 0; i < centi.length-1; i++) {
			centiCoord = centi[i].getPos();
			for (int j = 0; j < centi[i].bodyLength()-1; j++){
				if (Math.abs(x - centiCoord[j][0]) < 2 && Math.abs(z - centiCoord[j][2]) < 2) {
					centi[i].HP -= 20;
					return true;
				}
			}
		}

		for (int i = 0; i < muffet.length-1; i++) {
			coord = muffet[i].getPos();
			if (Math.abs(x - coord[0]) < 2 && Math.abs(z - coord[2]) < 2) {
				muffet[i].HP -= 20;
				return true;
			}
		}
		
		return false;
	}
	

	// if your step would land you within 1 unit of the wall (.5 on either side), you're too close. 
	public static boolean tooClose(Float x, Float coord){
		if (Math.abs(x-coord)<1)
			return true;
		else
			return false; 
	}
	// same as above but allows for varying buffer zone, rather  than the preset 1. Pass it "stepsize" if you want to be 
	// "if your step lands you less than 1 step away from the wall"
	public static boolean tooClose(Float x, Float coord, Float buffer){
		if (Math.abs(x-coord)<buffer)
			return true;
		else
			return false; 
	}
	// lets you see if you'll land on a point in a given line (great for our semi-2D walls. 
	public static boolean between(Float x, Float coord1, Float coord2) { 
		if (x >= coord1 && x <= coord2)
			return true;
		else
			return false; 
	}
	
	//Answers the question, are you about to walk into a wall? 
	public static boolean wallcollide(Float movex, Float movez){
		float tranX = 20; 
		float tranZ = 20; 
		//this is the collision for the dice. Basically creates a 3xinfinityx3 rectangular prism that cannot be walked into. 
		// for example, to not be able to walk on a bug, it would be something like 
		//if (tooClose(movex, bug.getPos[0], bug.size) || tooClose(movez, bug.getPos[2], bug.size)) return true. 
		if (tooClose(movex,80f, 3f) && tooClose(movez, 80f, 3f))
			return true;
		//this is the collision for the outter wall
		else if (tooClose(movex, 0f) || tooClose(movez, 0f) || tooClose(movex, 160f) || tooClose(movez, 160f))
			return true;
		//the 8 rooms. 
		for (int i = 0; i<8; i++) {
			if (tooClose(movex, tranX) && (between(movez, tranZ,tranZ+17) || between (movez, tranZ+23, tranZ+40)))
				return true; 
			else if (tooClose(movex, tranX+40) &&(between(movez, tranZ,tranZ+17) || between (movez, tranZ+23, tranZ+40)))
				return true;
			else if (tooClose(movez, tranZ) && (between(movex, tranX, tranX+17f) || between (movex, tranX+23f, tranX+40f)))
				return true;
			else if (tooClose(movez, tranZ+40) && (between(movex, tranX, tranX+17f) || between (movex, tranX+23f, tranX+40f)))
				return true;
			tranX+=40;
			if (tranX>101){	tranX=20; tranZ+=40;}
		}
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
	// MOUSE CONTROLS-- Just don't touch them. Believe me. It is irreversible hell if you do. 
	public void mouseMoved(MouseEvent e) {
		// Update mouse position
		int x=e.getX(), y = e.getY();
		//mouse sensitivity. Do not set to 10. I know it's tempting but that will make it wonky. 
		int variance = 12;
		if (x<300-variance) { viewangle+=pan; } else if (x > 300+variance) { viewangle-=pan; } 
		if (y>300+variance) { rotateY-=pan; } else if (y<300-variance) { rotateY+=pan; 	}
		droid.mouseMove(300, 300); 
	}

}

