
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

public class Butterfly 
{
	//starts on random frame at random time to help make the motion not so synchronous and creepy.
	public Float size = .5f;
	private Float rotateY=0f, tranX=0f, tranZ=0f, timer=0f, tranY=5-size;
	public int HP = 1; 
	
	//Motion is based on timer; Animation based on Frame. There is 20 "frames" of motion (the wings flapping). 
	//Animation is based on a mod function, animates quickly back to 0 from max. This is by design to show the wing coming down quickly to provide
	//an upward thrust. 
	private int frame = (int)(Math.random()*100); 
	private GLUquadric quadric; 	// to control properties of quadric-based objects here
	public Float speed = .5f; // walk speed of CAMERA not mobs. 
	private Float buttSpeed =.05f; // butterfly speed
	private boolean move = true; 
	public boolean paused = false; 

	private GLU glu = new GLU();

	public Butterfly(GL2 gl, GLCanvas canvas) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		tranX=0f; 
		tranZ=0f; 

	}
	//to start a Butterfly off at X, Z coords
	public Butterfly(GL2 gl, GLCanvas canvas, Float X, Float Z) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		tranX=X; 
		tranZ=Z; 
	}
	public Butterfly(GL2 gl, GLCanvas canvas, Float X, Float Z, boolean mo) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		tranX=X; 
		tranZ=Z; 
		move=mo;
	}
	// Draw everything
	public void draw(GL2 gl)	{draw(gl, 0, 0, 0, 0);} //redirect to other method. 
	public void draw(GL2 gl, float x, float y, float z, float r)
	{

		if (r != 0)
			rotateY =r;
		int limit = 20; 
		gl.glPushMatrix();
		gl.glTranslatef(x, y,z); 
		//draw stuff here
		grass(gl, limit);
		Butt(gl);
		gl.glPopMatrix();
		move(limit);
	}
	//movement code
	private void move(int limit){
		
		if (paused)
			return;
		timer++;
		if (timer>=5) {
			frame++;
			timer=0f;
		}
		if (frame > 100)
			frame = 0;
		if (HP<=0)
		{
			
			tranY-=.1f;
			return;
		}
		//can make them stationary
	
		if (move){
			Float movex = new Float(tranX+buttSpeed*Math.cos(Math.toRadians(rotateY))); 
			Float movez = new Float(tranZ-buttSpeed*Math.sin(Math.toRadians(rotateY)));	
			tranX = movex; tranZ = movez; 
		}
		if (frame%10==0 && move)
			rotateY = rotateY + (new Float(Math.random()*40) -20f); 
		//this helps limit motion to the range given. Remove if unwanted. 
		//note that it's not a "hard" limit and they may wander, but will continuously "search" for the area.
		//when they get back, their motion becomes less sporatic.
		if ((tranX<-limit || tranX > limit || tranZ<-limit || tranZ > limit) && move)
			rotateY+=180;
	}

	// Grass field
	private void grass(GL2 gl, float size)
	{
		gl.glPushMatrix();
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(0, 1, 0);
		gl.glTexCoord2f(-size,-size); gl.glVertex3f( -size, -0.01f,  -size);
		gl.glTexCoord2f(size,-size); gl.glVertex3f( -size, -0.01f, size);
		gl.glTexCoord2f(size, size); gl.glVertex3f(size, -0.01f, size);
		gl.glColor3f(0, .5f, 0);
		gl.glTexCoord2f(-size, size); gl.glVertex3f(size, -0.01f,  -size);
		gl.glEnd();

		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
	//needed to change the name from butterfly to avoid it looking like a constructor...
	//My 4 letter naming system has betrayed me here.
	private void Butt(GL2 gl){
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
		//5-size to ensure the height of 5 thing.
		gl.glTranslatef(tranX, tranY, tranZ);
		gl.glRotatef(rotateY+90, 0, 1, 0);
		//body
		if (HP>0) // when he dies, the body disappears and the wings flutter to the floor. 
		{
			gl.glPushMatrix(); 
			//head
			gl.glColor3f(0,1,0); 
			gl.glTranslatef(size/2f, 0, size/1.5f);
			glu.gluSphere(quadric, size/4f, 5, 5);
			//eyes 
			gl.glColor3f(1,1,0);
			for(int j = -1; j<2; j+=2)
			{
				gl.glPushMatrix();
				gl.glTranslatef(-size/9f*j, 0, size/4.5f);
				glu.gluSphere(quadric, size/10f, 5, 5);
				gl.glPopMatrix();
			}
			//antennae 
			for (int i = -1; i <2; i+=2){ 
				gl.glPushMatrix(); 
				gl.glColor3f(1,0,0); 
				gl.glRotatef(45, -1.5f, .05f*i, 0);
				gl.glTranslatef(.05f*i, 0, 0);
				glu.gluCylinder(quadric, .05/5, .05/4, .5, 5, 1);
				gl.glPopMatrix();
			}
			//middle
			gl.glTranslatef(0, 0, -size/4f);
			gl.glColor3f(0,.8f,0); 
			glu.gluSphere(quadric, size/4f, 5, 5);
			//tushie
			gl.glTranslatef(0, 0, -size/5f);
			gl.glColor3f(0,.6f,0); 
			glu.gluSphere(quadric, size/8f, 5, 5);
			gl.glPopMatrix();
		}
		//wings		
		for (int j = -1; j<2; j+=2){
			for (int i = -1; i<2; i+=2){
				gl.glPushMatrix();
				// to move the wing to the front/back
				gl.glTranslatef((size/1.375f), 0, (size/1.5f)*i);
				Float winger = new Float((Math.sin((frame)*1.0)*50f-25f)); 
				gl.glRotatef(winger, 0, 0, 1);
				gl.glBegin(GL2.GL_QUADS);
				gl.glColor3f(1, 0, 0);
				gl.glVertex3f( 0, 0,  0);
				gl.glColor3f(0,1, 0);
				gl.glVertex3f( 0, 0, size);
				gl.glColor3f(.5f, 0,.5f);
				gl.glVertex3f(size,0, size);
				gl.glColor3f(0, 0, 1);
				gl.glVertex3f(size, 0,  0);
				gl.glEnd();
				gl.glPopMatrix();
			}
			//flip it around for second set of wings. 
			gl.glTranslatef((size), 0, size);
			gl.glRotatef(180, 0,1,0);
		}
	}

}

