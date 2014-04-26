
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

public class Butterfly 
{
	private Float rotateY=0f,
				size = .5f,		//Size of butterflies. They scale. 
				tranX=0f,		//X Coordinate. 
				tranZ=0f,  		//Z coordinate
				tranY=5-size,	// this is their height. Note: Bigger butterflies fly lower because we don't want them to hit the ceiling.
				timer=0f,		// simply counts every time it's drawn. 
				buttSpeed =.05f;// How fast are butterflies?
	public int HP = 1; 
	
	//Motion is based on timer; Animation based on Frame. There is 20 "frames" of motion (the wings flapping). 
	//Animation is based on a mod function, animates quickly back to 0 from max. This is by design to show the wing coming down quickly to provide
	//an upward thrust. 
	//starts on random frame at random time to help make the motion not so synchronous and creepy.
	private int frame = (int)(Math.random()*100); // "Frames" are every X many "timer" increments. This allows us to change animation rates without 
												  // changing the movement speed. 
	
	private GLUquadric quadric; 	// to control properties of quadric-based objects here
	public boolean move = true, paused = false; // "move" decides if it will move, or animate in place. "paused" also kills animation. 
	
	
	private GLU glu = new GLU();
	public Float size(){
		return size;
	}
	public Float[] getPos(){
		Float[] temp = {tranX, tranY, tranZ}; // :C 
		return temp;
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
	
	// Draw everything
	public void draw(GL2 gl)	{
		int limit = 20; 
		gl.glPushMatrix();
		gl.glTranslatef(tranX, tranY,tranZ); 
		gl.glRotatef(rotateY+90, 0, 1, 0);//+90 because i made them sideways, accidentally. 
		//draw stuff here
		Butt(gl);
		gl.glPopMatrix();
		move(limit);
	}
	//movement code
	private void move(int limit){
		limit = 160; // stops them from going "outside" the map.
		if (paused) return; //if paused, don't animate or move. 
		timer++; 	
		if (timer>=5) { //every 5 ticks of timer, go ahead and animate 1 frame. 
			frame++; timer=0f;
		}
		if (frame > 100) //keeps the frames from incrementing til it throws an error. 
			frame = 0;
		if (HP<=0)		//if the butterfly is dead, it "falls" to the floor gently. 
		{	tranY-=.1f;
			return; 	}
		//if you want to move(=true) then calculate the movement of it. 
		if (move){
			Float movex = new Float(tranX+buttSpeed*Math.cos(Math.toRadians(rotateY))); 
			Float movez = new Float(tranZ-buttSpeed*Math.sin(Math.toRadians(rotateY)));	
			//if you're about to collide into the wall, spin around. 
			//if not, move forward. 
			if (!Debugger.wallcollide(movex, movez)) {
				tranX = movex; tranZ = movez; 
			} else 
				rotateY+=180;
		}
		//every 10 frames of animation, rotate up to 20 degrees in either direction. 
		if (frame%10==0 && move)
			rotateY = rotateY + (new Float(Math.random()*40) -20f); 
	}

	//needed to change the name from butterfly to avoid it looking like a constructor...
	//My 4 letter naming system has betrayed me here.
	//Draws a butterfly
	private void Butt(GL2 gl){
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
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
		// i loop draws left/right. 
		// j loop draws front/back. 
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

