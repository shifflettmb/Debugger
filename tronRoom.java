//  Sample room with basic units
// TRIVIA: This room contains 25+ textures, including each side of a 20 sided dice. The dice alone took a week to get the shape of it right. 
// The dice ended up being the product of a nested for loop, the first for loop making a "crown" shape, the second flipping it once and then making 
// another crown. 

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.*;

class tronRoom
{
	private GLUquadric quadric; 	// to control properties of quadric-based objects here
	private Texture Grass;
	private Float c1, c2, c3, x, y, z; 
	private GLU glu = new GLU();

	public tronRoom(GL2 gl) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		gl.glEnable(GL2.GL_TEXTURE_2D);
		Grass = CreateTexture(gl, "textures/tron.jpg");
		c1= c2 = c3=1f; 
		x=y=z=0f;
	}
	public tronRoom(GL2 gl, Float x, Float y, Float z, Float c1, Float c2, Float c3) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		gl.glEnable(GL2.GL_TEXTURE_2D);
		Grass = CreateTexture(gl, "textures/tron.jpg");
		this.c1=c1;
		this.c2=c2; 
		this.c3=c3; 
		this.x=x; 
		this.y=y;
		this.z=z;
	}
	//Creates texture information from a file - assumes filename has proper 3 character graphic extension
	//			e.g.  png, gif, jpg (NOT jpeg!!)	
	private Texture CreateTexture(GL2 gl, String filename)
	{
		Texture tex = null;
		//String type = filename.substring(filename.lastIndexOf('.')+1);

		try {
			tex = TextureIO.newTexture(new File(filename), false);
		}
		catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}
		//  Set parameters for the texture
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

		//		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		return tex;
	}	

	// Draw everything
	public void draw(GL2 gl)
	{

		//stops the grass and walls from being tinted.
		gl.glColor3f(c1,c2,c3);
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z); 
		walls(gl);
		gl.glPopMatrix();
	}
	// Grass field
	private void walls(GL2 gl)
	{
		float bx = 1f, by = 1f;
		gl.glDisable(GL2.GL_TEXTURE_2D);
		Grass.enable(gl);
		Grass.bind(gl);
		// wall one
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0f, 0f); gl.glVertex3f(0,  0, 17);
		gl.glTexCoord2f( bx, 0f); gl.glVertex3f(0,  0,  0);
		gl.glTexCoord2f( bx, by); gl.glVertex3f(0, 10,  0);
		gl.glTexCoord2f( 0f, by); gl.glVertex3f(0, 10, 17);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0f, 0f); gl.glVertex3f(0, 0, 40);
		gl.glTexCoord2f( bx, 0f); gl.glVertex3f(0,  0, 23);
		gl.glTexCoord2f( bx, by); gl.glVertex3f(0,  10,  23);
		gl.glTexCoord2f( 0, by); gl.glVertex3f(0, 10,  40);
		gl.glEnd();
		//wall two
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0f, 0f); gl.glVertex3f( 0,  0, 40);
		gl.glTexCoord2f( bx, 0f); gl.glVertex3f(17,  0, 40);
		gl.glTexCoord2f( bx, by); gl.glVertex3f(17, 10, 40);
		gl.glTexCoord2f( 0f, by); gl.glVertex3f( 0, 10, 40);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0f, 0f); gl.glVertex3f( 23,  0, 40);
		gl.glTexCoord2f( bx, 0f); gl.glVertex3f(40,  0, 40);
		gl.glTexCoord2f( bx, by); gl.glVertex3f(40, 10, 40);
		gl.glTexCoord2f( 0f, by); gl.glVertex3f( 23, 10, 40);
		gl.glEnd();
		//wall three
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0f, 0f); gl.glVertex3f(40,  0, 17);
		gl.glTexCoord2f( bx, 0f); gl.glVertex3f(40,  0,  0);
		gl.glTexCoord2f( bx, by); gl.glVertex3f(40, 10,  0);
		gl.glTexCoord2f( 0f, by); gl.glVertex3f(40, 10, 17);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0f, 0f); gl.glVertex3f(40,  0, 40);
		gl.glTexCoord2f( bx, 0f); gl.glVertex3f(40,  0,  23);
		gl.glTexCoord2f( bx, by); gl.glVertex3f(40, 10,  23);
		gl.glTexCoord2f( 0f, by); gl.glVertex3f(40, 10, 40);
		gl.glEnd();
		//wall four
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0f, 0f); gl.glVertex3f(17,  0, 0);
		gl.glTexCoord2f( bx, 0f); gl.glVertex3f( 0,  0, 0);
		gl.glTexCoord2f( bx, by); gl.glVertex3f( 0, 10, 0);
		gl.glTexCoord2f( 0f, by); gl.glVertex3f(17, 10, 0);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0f, 0f); gl.glVertex3f(40,  0, 0);
		gl.glTexCoord2f( bx, 0f); gl.glVertex3f(23,  0, 0);
		gl.glTexCoord2f( bx, by); gl.glVertex3f(23, 10, 0);
		gl.glTexCoord2f( 0f, by); gl.glVertex3f(40, 10, 0);
		gl.glEnd();
		//ceiling
		gl.glBegin(GL2.GL_QUADS);	
		gl.glTexCoord2f(1, 1); gl.glVertex3f(0, 10,  0);
		gl.glTexCoord2f(0f, 1); gl.glVertex3f(0, 10, 40);
		gl.glTexCoord2f(0f, 0); gl.glVertex3f(40, 10, 40);
		gl.glTexCoord2f(1,0); gl.glVertex3f(40, 10,  0);
		gl.glEnd();
		//floor 
		gl.glBegin(GL2.GL_QUADS);	
		gl.glTexCoord2f(1, 1); gl.glVertex3f(0, 0,  0);
		gl.glTexCoord2f(0f, 1); gl.glVertex3f(0, 0, 40);
		gl.glTexCoord2f(0f, 0); gl.glVertex3f(40, 0, 40);
		gl.glTexCoord2f(1,0); gl.glVertex3f(40, 0,  0);
		gl.glEnd();
		gl.glDisable(GL2.GL_TEXTURE_2D);


	}
}

