/**
 * Bug Class
 * Parent class for the bugs in Tron-World
 */

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;


public abstract class Bug {

	private Float rotateY = 0f,
			size = 0.5f,		//Size of bugs. They scale. 
			tranX = 0f,		//x Coordinate. 
			tranY = 0f,
			tranZ = 0f,  		//z coordinate
			timer = 0f,		// simply counts every time it's drawn. 
			speed = 0.1f;// How fast are butterflies?
	private int HP; 
	private int frame = (int)(Math.random()*100); 	// "Frames" are every X many "timer" increments. This allows us to change animation rates without 
	  												// changing the movement speed.
	private GLUquadric quadric; 	// to control properties of quadric-based objects here
	private boolean	move = true, 
					paused = false; // "move" decides if it will move, or animate in place. "paused" also kills animation. 
	private GLU glu = new GLU();

	//Abstract methods
	/**
	 * Draw the creature
	 * @param gl OpenGL drawable object
	 */		
	public abstract void draw(GL2 gl);	
	/**
	 * Moves the creature
	 */
	protected abstract void move();


	public Bug(GL2 gl, GLCanvas canvas, Float x, Float z) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		tranX = x; 
		tranZ = z; 

	}
	
	/**
	 * Get the current position of the bug
	 * @return Position array {x, y, z}
	 */
	public Float[] getPos(){
		Float[] temp = {tranX, tranY, tranZ}; // 
		return temp;

	} //End getPos

	/**
	 * Asks the bug if they are still alive
	 * @return True if the bug has positive HP, False otherwise
	 */
	public boolean isAlive() {
		if (this.HP > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Get the bug's health
	 * @return Bug HP
	 */
	public int getHP() {
		return this.HP;
	}
	
	/**
	 * Specify the bug's health
	 * @param hp Bug HP
	 */
	public void setHP(int hp) {
		this.HP = hp;
	}
	
	/**
	 * Decrement the HP of the bug
	 * @return True if the bug had HP to decrement, False otherwise
	 */
	public boolean hitBug() {
		if (this.HP < 1) {return false;}
		this.HP--;
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
