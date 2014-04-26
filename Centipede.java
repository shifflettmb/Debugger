
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

import java.util.*;
public class Centipede 
{
	//Motion is based on timer; Animation based on Frame. 
	//Motion in Centipede -- each body segment "follows" another. they are otherwise seperate entities. This allows the body to be fluid but contiguous. 
	//The legs wiggle on a sin() based rotate.  
	//starts on random frame at random time to help make the motion not so synchronous and creepy.
	private Float tranX=0f, tranZ=0f, timer=0f, size = .5f, centSpeed =.04f; 
	public int HP = 100; 
	private ArrayList<Segment> body = new ArrayList<Segment>(0);

	private int frame = (int)(Math.random()*100); 
	private GLUquadric quadric; 	// to control properties of quadric-based objects here
	public boolean paused = false, move = true;  
	private GLU glu = new GLU();

	public Float[][] getPos(){
		Float[][] temp = new Float[(int)HP/20][3]; 
		for (int i = 0; i < temp.length; i++){
			temp[i][0]=body.get(i).x;
			temp[i][1]=body.get(i).y; 
			temp[i][2]=body.get(i).z;
		}
		return temp;
	}
	public Centipede(GL2 gl, GLCanvas canvas) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		tranX=0f; 
		tranZ=0f; 

		for (int i = 0; i <= HP/20; i++){
			body.add( new Segment(tranX+(size*i), 0, tranZ, 0f)); 	
			body.get(i).x=tranX;
			body.get(i).y=size+.5f; 
			body.get(i).z=tranZ+(2*size*i); 
		}
	}
	public int bodyLength(){return (int) HP/20;}
	public Centipede(GL2 gl, GLCanvas canvas, Float X, Float Z) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		tranX=X; 
		tranZ=Z; 
		for (int i = 0; i <= HP/20; i++){
			body.add(new Segment()); 
			body.get(i).x=tranX;
			body.get(i).y=size+.5f; 
			body.get(i).z=tranZ+(2*size*i); 
		}

	}
	// Draw everything
	public void draw(GL2 gl)	{draw(gl, 0, 0, 0, 0);} //redirect to other method. 
	public void draw(GL2 gl, float x, float y, float z, float r)
	{
		if (r != 0)
			body.get(0).rotateY =r;
		if (HP<30)
			HP=-1;
		gl.glPushMatrix();
		gl.glTranslatef(x, y,z); 
		//draw stuff here
		cent(gl);
		gl.glPopMatrix();
		move();
	}
	//movement code
	private void move(){
		if (paused)
			return;
		//can make them stationary
		timer++;	int i =0; 
		if (move && timer % 3==0){

			for (i = 0; i<HP/20; i++) // Every body segment except the "head" is moved up. IE: Body segment 3 is placed at body segment 2's position, and so on.
			{
				body.get(i).x=new Float(body.get(i+1).x +-size*Math.cos(Math.toRadians(body.get(i+1).rotateY))); 
				body.get(i).y=new Float(body.get(i+1).y); 
				body.get(i).z=new Float(body.get(i+1).z-(-size)*Math.sin(Math.toRadians(body.get(i+1).rotateY)));
				body.get(i).rotateY=new Float(body.get(i+1).rotateY);	
			}
			//the "head" is now moved to a new coordinate. 
			Float movex = new Float(body.get(i).x+(centSpeed*.9f)*Math.cos(Math.toRadians(body.get(i).rotateY))); 
			Float movez = new Float(body.get(i).z-(centSpeed*.9f)*Math.sin(Math.toRadians(body.get(i).rotateY)));	
			//collision code, very similar to butterfly's except only applies to head. 
			if (!Debugger.wallcollide(movex, movez)) {
				body.get(i).x = movex; body.get(i).z = movez; 
			} else {
				body.get(i).rotateY+=90;
				movex = new Float(body.get(i).x+centSpeed*Math.cos(Math.toRadians(body.get(i).rotateY))); 
				movez = new Float(body.get(i).z-centSpeed*Math.sin(Math.toRadians(body.get(i).rotateY)));	
				body.get(i).x = movex; body.get(i).z = movez; 
			}
		}
		if (frame%3==0 && move)
			body.get(i).rotateY = body.get(i).rotateY + (new Float(Math.random()*40) -20f); 

		if (timer>=3) {
			frame++;
			timer=0f;
		}
		if (frame > 100)
			frame = 0;


	}

	private void cent(GL2 gl){
		gl.glPushMatrix();
		float color=.5f;
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

		for (int i = 0; i<HP/20-1; i++)
		{
			gl.glPushMatrix();
			gl.glTranslatef(body.get(i).x, body.get(i).y, body.get(i).z);
			gl.glRotatef(body.get(i).rotateY, 0, 1,0);
			gl.glColor3f(1, color, 0);
			glu.gluSphere(quadric, size, 10, 10);
			gl.glColor3f(0, 0, 1);
			gl.glPushMatrix();
			gl.glRotatef(45, 1, 0, 0);
			float legsize = size*2;
			float legRotate = new Float(Math.sin(frame)*Math.pow(-1, i)); //math.pow makes the leg wiggles alternating
			gl.glRotatef(legRotate*20-10, 0f, 1f, 0f);
			glu.gluCylinder(quadric, size/10f, size/20f, legsize, 5, 1);
			gl.glRotatef(90, 1, 0, 0);
			gl.glRotatef(legRotate*20-10, 0f, 1f, 0f);
			glu.gluCylinder(quadric, size/10f, size/20f, legsize, 5, 1);	
			gl.glPopMatrix();
			// Face 
			if (i==HP/20-2){
				gl.glColor3f(1, 1, 0);
				gl.glPushMatrix();
				gl.glTranslatef(size/1.5f, 0, size/1.5f);
				glu.gluSphere(quadric, size/4, 5, 5);
				gl.glPopMatrix();
				gl.glPushMatrix();
				gl.glTranslatef(size/1.5f, 0, -size/1.5f);
				glu.gluSphere(quadric, size/4, 5, 5);
				gl.glPopMatrix();
			}
			gl.glPopMatrix();
			color-=.1f;
		}

		gl.glPopMatrix();
	}

	class Segment {
		public float x = 0; 
		public float y = 0; 
		public float z = 0; 
		public float rotateY = 0; 
		public Segment(){}
		public Segment(float nx, float ny, float nz, float nnY){
			x=nx; 
			x=ny; 
			z=nz; 
			rotateY=nnY; 
		}
		public String toString(){
			return "X: "+this.x+" Y: "+this.y+" Z: "+this.z+" RotateY: "+rotateY; 
		}

	}
}

