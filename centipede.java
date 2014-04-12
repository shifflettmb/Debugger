
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import java.util.*;
public class centipede 
{
	//starts on random frame at random time to help make the motion not so synchronous and creepy.
	private Float tranX=0f, tranZ=0f, timer=0f;
	public int HP = 100; 
	private ArrayList<Segment> body = new ArrayList<Segment>(0);
	public Float size = .5f;
	//Motion is based on timer; Animation based on Frame. 
	//Motion in centipede -- each body segment "follows" another. they are otherwise seperate entities. This allows the body to be fluid but contiguous. 
	//The legs wiggle on a sin() based rotate.  
	private int frame = (int)(Math.random()*100); 
	private GLUquadric quadric; 	// to control properties of quadric-based objects here
	public Float speed = .5f; // walk speed of CAMERA not mobs. 
	private Float centSpeed =.05f; 
	private boolean move = true; 
	public boolean paused = false; 

	private GLU glu = new GLU();

	public centipede(GL2 gl, GLCanvas canvas) 
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
	//to start a Centipede off at X, Z coords
	public centipede(GL2 gl, GLCanvas canvas, Float X, Float Z) 
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
		int limit = 20; 
		gl.glPushMatrix();
		gl.glTranslatef(x, y,z); 
		//draw stuff here
		cent(gl);
		gl.glPopMatrix();
		move(limit-size*body.size());
	}
	//movement code
	private void move(float limit){
		limit = 150;
		if (paused)
			return;
		//can make them stationary
		timer++;	int i =0; 
		if (move && timer % 3==0){

			for (i = 0; i<HP/20; i++)
			{
				body.get(i).x=new Float(body.get(i+1).x +-size*Math.cos(Math.toRadians(body.get(i+1).rotateY))); 
				body.get(i).y=new Float(body.get(i+1).y); 
				body.get(i).z=new Float(body.get(i+1).z-(-size)*Math.sin(Math.toRadians(body.get(i+1).rotateY)));
				body.get(i).rotateY=new Float(body.get(i+1).rotateY);	
			}

			Float movex = new Float(body.get(i).x+(centSpeed*.9f)*Math.cos(Math.toRadians(body.get(i).rotateY))); 
			Float movez = new Float(body.get(i).z-(centSpeed*.9f)*Math.sin(Math.toRadians(body.get(i).rotateY)));	
			body.get(i).x = movex; body.get(i).z = movez; 
		}
		if (frame%3==0 && move)
			body.get(i).rotateY = body.get(i).rotateY + (new Float(Math.random()*40) -20f); 

		if (timer>=3) {
			frame++;
			timer=0f;
		}
		if (frame > 100)
			frame = 0;
		//this helps limit motion to the range given. Remove if unwanted. 
		//note that it's not a "hard" limit and they may wander, but will continuously "search" for the area.
		//when they get back, their motion becomes less sporatic.
		if ((body.get(i).x<10 || body.get(i).x > limit || body.get(i).z<10 || body.get(i).z > limit) && move) {
			body.get(i).rotateY+=90;
			Float movex = new Float(body.get(i).x+centSpeed*Math.cos(Math.toRadians(body.get(i).rotateY))); 
			Float movez = new Float(body.get(i).z-centSpeed*Math.sin(Math.toRadians(body.get(i).rotateY)));	
			body.get(i).x = movex; body.get(i).z = movez; 
		}
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

