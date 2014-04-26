
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

public class Spider 
{
	//starts on random frame at random time to help make the motion not so synchronous and creepy.
	private Float rotateY=0f, tranX=0f, tranZ=0f, timer=0f;
	public int HP = 80;
	public Float size = .5f, legsize=1f;
	//Motion is based on timer; Animation based on Frame. There is 20 "frames" of motion (the wings flapping). 
	//Animation is based on a mod function, animates quickly back to 0 from max. This is by design to show the wing coming down quickly to provide
	//an upward thrust. 
	private int frame = (int)(Math.random()*100); 
	private GLUquadric quadric; 	// to control properties of quadric-based objects here
	private Float spidSpeed =.0024f; // Spider speed
	private boolean move = true; 
	public boolean paused = false; 

	private GLU glu = new GLU();

	public Spider(GL2 gl, GLCanvas canvas) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		tranX=0f; 
		tranZ=0f; 

	}
	public Float[] getPos(){
		Float[] temp = {tranX, size+legsize,tranZ}; 
		return temp;
	}
	//to start a Spider off at X, Z coords
	public Spider(GL2 gl, GLCanvas canvas, Float X, Float Z) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		tranX=X; 
		tranZ=Z; 
	}
	public Spider(GL2 gl, GLCanvas canvas, Float X, Float Z, boolean mo) 
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
		spid(gl);
		gl.glPopMatrix();
		move(limit);
	}
	//movement code, same as butterfly down to the letter. 
	private void move(int limit){
		spidSpeed = (float)HP/2000;
		limit = 150;
		if (HP < 0)
			return;
		else if (HP<20)
			spidSpeed=0f;
		if (paused)
			return;
		timer++;
		if (move){
			Float movex = new Float(tranX+spidSpeed*Math.cos(Math.toRadians(rotateY))); 
			Float movez = new Float(tranZ-spidSpeed*Math.sin(Math.toRadians(rotateY)));	
			//collision code. 
			if (!Debugger.wallcollide(movex, movez)) {
				tranX = movex; tranZ = movez; 
			} else 
				rotateY+=180; 
		}
		if (frame%10==0 && move)
			rotateY = rotateY + (new Float(Math.random()*40) -20f); 

		if (timer>=5) {
			frame++;
			timer=0f;
		}
		if (frame > 100)
			frame = 0;
	}
	private void spid(GL2 gl){
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glTranslatef(tranX, size+legsize, tranZ);
		gl.glRotatef(rotateY+90, 0, 1, 0);
		//body
		gl.glPushMatrix();
		if (HP>0)
		{	//head
			gl.glColor3f(.2f,.2f,.2f); 
			gl.glTranslatef(size/2f, 0, size/1.5f);
			glu.gluSphere(quadric, size, 8, 8);
			//eyes 

			Float eyesize = size/10f;
			for(int j = -1; j<2; j+=2)
			{
				gl.glPushMatrix();
				gl.glColor3f(.25f,0,0);
				gl.glTranslatef(-size*j/1.5f, 0, size/(1.5f));
				glu.gluSphere(quadric, eyesize, 5, 5);
				gl.glColor3f(.5f,0,0);
				gl.glTranslatef(0, eyesize*2, 0);
				glu.gluSphere(quadric, eyesize, 5, 5);
				gl.glColor3f(.75f,0,0);
				gl.glTranslatef(eyesize*2*j, eyesize*2, 0);
				glu.gluSphere(quadric, eyesize, 5, 5);
				gl.glColor3f(1f,0,0);
				gl.glTranslatef(eyesize*2*j, -eyesize*2 ,eyesize*2);
				glu.gluSphere(quadric, eyesize, 5, 5);
				gl.glPopMatrix();
			}
			//middle
			gl.glTranslatef(0, size/2, -size*1.75f);
			gl.glColor3f(.1f,.1f,.1f); 
			glu.gluSphere(quadric, size*1.5f, 20, 20);
			//legs
		}
		gl.glColor3f(.2f,.2f,.2f);
		int j = 1; 
		for (int i = 1; i<=(HP/20); i++)
			for( j = -1; j<2; j+=2)
			{	
				float color=.5f-.1f*i;
				gl.glColor3f(color,color,color);
				gl.glPushMatrix();
				gl.glRotatef(j*20*i, 0, 1, 0);
				gl.glTranslatef(legsize*j/2,-size+legsize/8, size/2);
				float legspin = (float)(Math.sin((frame+i)*1.0)*30f*Math.pow(-1, i)*Math.pow(-1, j)); 
				//legspin+=45*j;
				gl.glRotatef(legspin, 1, 0, 1);
				//leg
				glu.gluCylinder(quadric, legsize/8, legsize/20, legsize, 8, 1);

				//foot

				gl.glPopMatrix();
			}
	gl.glPopMatrix();
}

}

