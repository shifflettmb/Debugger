import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.media.opengl.glu.*;

import java.io.*;


public class world {
	private Texture Grass;
	private GLUquadric quadric; 
	public float x1, x2, z1, z2;
	public tronRoom rooms[] = new tronRoom[8]; 
	private GLU glu = new GLU();
	public world (GL2 gl) {

		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, true);        // use true to generate texture coordinates

		gl.glEnable(GL2.GL_TEXTURE_2D);

		Grass = CreateTexture(gl, "textures/tron.jpg");

		x1=z1=0f;
		x2=z2=160f; 
		float c1,c2,c3;
		float x = 20f; 
		float z = 20f; 
		//DECLARING ROOMS
		for (int i = 0; i<8; i++) { 
			if (x==60 && z==60)
				x+=40; //skip courtyard
			if (x%3==0 || z==60) {
				c3=c1=1f;
				c2=0f;
			} else {
				c3=c1=0f;
				c2=1f;
			}
			
			rooms[i]=new tronRoom(gl, (x+(i*.01f)),0f,z+(.01f*i),c1,c2, c3);
			x+=40;
			if (x>101){	x=20; z+=40;}
		}

	}
	public void draw(GL2 gl)	{
		gl.glPushMatrix();

		//draw stuff here
		grass(gl); 
		for (int i =0; i<rooms.length; i++)
			rooms[i].draw(gl);
		wall(gl);
		gl.glPopMatrix();

	}  
	private void wall (GL2 gl){
		float num = 5; 
		Grass.enable(gl);
		Grass.bind(gl);
		gl.glPushMatrix(); 
		gl.glColor3f(.8f, .8f, .8f);
		gl.glTranslatef(80,0,80); //move to the center of the room
		for (int i=0; i<5; i++){ 
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0,0); gl.glVertex3f(x1-80, -0.01f,  z1-80);
			gl.glTexCoord2f(num, 0); gl.glVertex3f( x1-80, -0.01f, z2-80);
			gl.glTexCoord2f(num,num/2); gl.glVertex3f(x1-80, 20f,  z2-80);
			gl.glTexCoord2f(0, num/2); gl.glVertex3f( x1-80, 20f, z1-80);
			gl.glEnd();
			gl.glRotatef(90, 0, 1, 0);
		}
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPopMatrix(); 
	}
	private Texture CreateTexture(GL2 gl, String filename)
	{
		Texture tex = null;

		try {
			tex = TextureIO.newTexture(new File(filename), false);

		}
		catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}

		//  Set parameters for the texture
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR );
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR );

		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT );
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT );

		//	gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		return tex;
	}	
	private void grass(GL2 gl)
	{
		float num = 5; 
		Grass.enable(gl);
		Grass.bind(gl);
		gl.glPushMatrix();
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(1, 0, 0);
		gl.glTexCoord2f(0,0); gl.glVertex3f(x1, -0.01f,  z1);
		gl.glColor3f(0, 0, 1);
		gl.glTexCoord2f(0, num); gl.glVertex3f( x1, -0.01f, z2);
		gl.glColor3f(1, 0, 1);
		gl.glTexCoord2f(num, num); gl.glVertex3f(x2, -0.01f, z2);
		gl.glColor3f(0, 1f, 0);
		gl.glTexCoord2f(num, 0); gl.glVertex3f(x2, -0.01f,  z1);
		gl.glEnd();

		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPopMatrix();

	}
}
