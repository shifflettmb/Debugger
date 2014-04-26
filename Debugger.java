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
	// coordinate arrays


	private String timeRunning;
	private int damage = 20, range = 4; 
	private Butterfly[] butterfly = new Butterfly[4];
	//centi = centipedes
	private Centipede[] centi = new Centipede[2]; 
	// Little miss muffet= spiders.
	private Spider muffet;
	private Net net;
	Float[] spidCoord = new Float[3];
	Float[][] buttCoord = new Float[butterfly.length][3];
	Float[][][] centiCoord = new Float[centi.length][5][3];
	private World Earth; 
	private Float rotateY=-95f;
	private GLCanvas canvas;
	private GLU glu = new GLU();
	private static GLUT glut = new GLUT();
	private static Robot droid;
	private float viewangle=0f, stepsize=.5f, pan=2f;
	public float eyex=10f, eyey=4f, eyez=10f; 
	public boolean BETAmode=false, paused=false, endGame=false;
	private static JFrame frame;
	public static int width;
	public static int height;
	private static long startTime = System.currentTimeMillis(), pausedTime, totalPaused=0, currentPaused;




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
			butterfly[i] = new Butterfly(gl, canvas, 21f*(i+1), 21f*(i+1));
		//instantiating all Spiders
		muffet=new Spider(gl, canvas, 110f, 110f); 
		//instantiating all centipedes
		for(int i=0; i<centi.length; i++)
			centi[i] = new Centipede(gl, canvas, (float)(45*(i+1)), (float)(45*(i+1)));
		//instantiating the Net
		net = new Net(gl, canvas, eyex, eyez);
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
		int totalCount = 0;


		if(!endGame)
		{
			gl.glRasterPos2i(x, y);
			for(int i=0; i<butterfly.length; i++) 
			{
				if (butterfly[i].HP > 0) 
				{
					creatureCount++;
				}
			}
			glut.glutBitmapString(5, "Butterflies Remaining: " + creatureCount);
			totalCount+=creatureCount;

			y += 55; creatureCount = 0;
			gl.glRasterPos2i(x, y);
			for(int i=0; i<centi.length; i++) 
			{
				if (centi[i].HP > 0) 
				{
					creatureCount++;
				}
			}
			glut.glutBitmapString(5, "Centipedes Remaining: " + creatureCount);
			totalCount+=creatureCount;

			y += 55; creatureCount = 0;
			gl.glRasterPos2i(x, y);
			if (muffet.HP > 0) 
			{
				creatureCount++;
			}
			totalCount+=creatureCount;
			glut.glutBitmapString(5, "Spiders Remaining: " + creatureCount);

			if (totalCount<1) 
			{
				endGame=true; 
				pausedTime=System.currentTimeMillis();
				paused=true;
			}

			y += 55;		
			long currentTime = System.currentTimeMillis();
			if (paused) {
				currentPaused = currentTime-pausedTime;
			} else {
				currentPaused = 0;
			}
			timeRunning = (((currentTime - startTime -totalPaused- currentPaused) / (1000 * 60)) % 60) + ":" + 
					(((currentTime - startTime-totalPaused-currentPaused) / 1000) % 60);

			gl.glRasterPos2i(x, y);
			glut.glutBitmapString(5, "Debugging Time: " + timeRunning);
		}

		if(endGame)
		{
			y += 165;
			gl.glRasterPos2i(x,y);
			glut.glutBitmapString(5, "Final Debug Time: " + timeRunning);

			y += 55;
			gl.glRasterPos2i(x,y);
			glut.glutBitmapString(5, "Program. Game Over.");
			y += 55;
			gl.glRasterPos2i(x,y);
			glut.glutBitmapString(5, "You Debugged The Entire");
		}



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
		muffet.draw(gl);
		for(int i=0; i<centi.length; i++)
			centi[i].draw(gl);


		net.draw(gl);




		//Mini-map
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		// msize is map size and is defined at the beginning.
		gl.glViewport(width-msize, height-msize, msize, msize);	
		gl.glOrtho(0, msize/4, 0, msize/4, 1, 12);
		//glu.gluPerspective(90., 1., 20.1, 29.6); 		// fov, aspect, near-clip, far clip
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex-msize/8, 7, eyez-msize/8, 
				eyex+1f-msize/8, -15, eyez-msize/8,
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


		muffet.draw(gl);
		for(int i=0; i<centi.length; i++)
			centi[i].draw(gl);


		net.draw(gl);




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
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:	
			if (paused)
				break;
			viewangle-=pan;
			net.rotateY = viewangle;
			break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			if (paused)
				break;
			viewangle+=pan;
			net.rotateY = viewangle;
			break;
		case KeyEvent.VK_SHIFT: 
			if (endGame) break;
			paused=!paused;
			if (paused)
				pausedTime=System.currentTimeMillis();
			else 
				totalPaused = currentPaused+totalPaused;
			for (int i=0; i <butterfly.length; i++)
				butterfly[i].paused=paused;
			muffet.paused = paused;
			for (int i=0; i <centi.length; i++)			
				centi[i].paused = paused;
			if (BETAmode)
				butterfly[3].paused=!butterfly[3].paused;
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			if (paused)
				break;
			movex = new Float(eyex+stepsize*Math.cos(Math.toRadians(viewangle))); 
			movez = new Float(eyez-stepsize*Math.sin(Math.toRadians(viewangle)));
			//if you're trying to step into a wall, don't move forward. 
			if (wallcollide(movex, movez))
				break;
			eyex = movex;
			eyez = movez;
			net.netX = eyex; //set x,z based
			net.netZ = eyez; //on eye position
			break;
		//this key will instantly kill all bugs for testing purposes
		/*case KeyEvent.VK_O:
			for (Butterfly X : butterfly)
				X.HP=0;
			muffet.HP=0;
			for (Centipede X : centi)
				X.HP=0;
			break;*/
		case KeyEvent.VK_DOWN:	//look down  
		case KeyEvent.VK_S:	
			if (paused)
				break;
			movex = new Float(eyex-stepsize*Math.cos(Math.toRadians(viewangle))); 
			movez = new Float(eyez+stepsize*Math.sin(Math.toRadians(viewangle)));
			if (wallcollide(movex, movez))
				break;
			eyex = movex;
			eyez = movez;
			net.netX = eyex; //set x,z based
			net.netZ = eyez; //on eye position
			break;
		case KeyEvent.VK_SPACE: 	
			if (paused)
				break;
			if(net.visible) 
				net.swing = true; //swing net
			//Float hitx = new Float(eyex-1*Math.cos(Math.toRadians(viewangle))), hitz= new Float(eyez+1*Math.sin(Math.toRadians(viewangle)));
			caughtBug(eyex,eyez); 
			break;




		case KeyEvent.VK_Q:	System.exit(0);  break;
		}




	}




	//Determine if a monster has been caught
	//Monster has been "caught" if it is within 2 units of the current
	//camera position




	public boolean caughtBug(Float x, Float z) {
		//storing positions 
		for (int i = 0; i < centi.length; i++) 
			centiCoord[i] = centi[i].getPos();
		for (int i = 0; i < butterfly.length; i++) 
			buttCoord[i] = butterfly[i].getPos();
		spidCoord = muffet.getPos();
		//calculating
		for (int i = 0; i < butterfly.length; i++) 
			if (Math.abs(x - buttCoord[i][0]) < range && Math.abs(z - buttCoord[i][2]) < range) {
				butterfly[i].HP -= damage;
				return true;
			}		
		if (Math.abs(x - spidCoord[0]) < range+2 && Math.abs(z - spidCoord[2]) < range+2) {
			muffet.HP -= damage;
			return true;
		}
		for (int i = 0; i < centi.length; i++) {
			for (int j = 0; j < centi[i].bodyLength()-1; j++){
				if (Math.abs(x - centiCoord[i][j][0]) < range && Math.abs(z - centiCoord[i][j][2]) <  range+1) {
					centi[i].HP -= damage;
					return true;
				}
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
		for (int i = 0; i<9; i++) {
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
		if (x<300-variance) { if(!paused){viewangle+=pan; net.rotateY = viewangle;}} else if (x > 300+variance) { if(!paused){viewangle-=pan; net.rotateY = viewangle; }} 
		if (y>300+variance) { rotateY-=pan; } else if (y<300-variance) { rotateY+=pan; 	}
		droid.mouseMove(300, 300); 
	}




}
