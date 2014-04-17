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
	public Texture Grass, Walls, Ceil;
	public static Texture Wings;
	private Float c1, c2, c3, x, y, z; 
	private GLU glu = new GLU();

	// GL, x y z coordinates, then RGB values. 
	//(marks the front, left corner. So a room from 0,0,0 to 40,0,40 would have the coordinates 0,0,0).
	public tronRoom(GL2 gl, Float x, Float y, Float z, Float c1, Float c2, Float c3) 
	{
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates
		gl.glEnable(GL2.GL_TEXTURE_2D);
		Walls = CreateTexture(gl, "textures/tronWall.jpg");
		Ceil = CreateTexture(gl, "textures/tronCeil.jpg");
		Grass =  CreateTexture(gl, "textures/tron.jpg");
		//Storing the colors for the room.
		this.c1=c1;	this.c2=c2; this.c3=c3;	
		//Storing the coordinates of the room. 
		this.x=x; 	this.y=y;	this.z=z;
	}
	//Ame's CreateTexture method-- don't touch. I don't know how it works. 	
	private Texture CreateTexture(GL2 gl, String filename)
	{
		Texture tex = null;
		try {tex = TextureIO.newTexture(new File(filename), false);}catch (IOException exc) {exc.printStackTrace();System.exit(1);}
		//  Set parameters for the texture
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		return tex;
	}	

	// Draw everything!
	public void draw(GL2 gl)
	{

		//Choose your color from when you constructed it. 
		gl.glColor3f(c1,c2,c3);
		gl.glPushMatrix();
		//move to that coordinate 
		gl.glTranslatef(x, y, z);
		//put walls/ceiling/floor there. 
		walls(gl);
		gl.glPopMatrix();
	}
	
	// Makes the walls, ceiling, and floor of the room... so basically the whole room unless we put objects in them. 
	private void walls(GL2 gl)
	{
		float bx = 1f, by = 1f;
		gl.glDisable(GL2.GL_TEXTURE_2D);
		Walls.enable(gl);
		Walls.bind(gl);
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
		gl.glDisable(GL2.GL_TEXTURE_2D);
		Ceil.enable(gl);
		Ceil.bind(gl);
		//ceiling
		gl.glBegin(GL2.GL_QUADS);	
		gl.glTexCoord2f(1, 1); gl.glVertex3f(0, 10,  0);
		gl.glTexCoord2f(0f, 1); gl.glVertex3f(0, 10, 40);
		gl.glTexCoord2f(0f, 0); gl.glVertex3f(40, 10, 40);
		gl.glTexCoord2f(1,0); gl.glVertex3f(40, 10,  0);
		gl.glEnd();
		//floor 
		gl.glDisable(GL2.GL_TEXTURE_2D);
		Grass.enable(gl);
		Grass.bind(gl);
		gl.glBegin(GL2.GL_QUADS);	
		gl.glTexCoord2f(1, 1); gl.glVertex3f(0, 0,  0);
		gl.glTexCoord2f(0f, 1); gl.glVertex3f(0, 0, 40);
		gl.glTexCoord2f(0f, 0); gl.glVertex3f(40, 0, 40);
		gl.glTexCoord2f(1,0); gl.glVertex3f(40, 0,  0);
		gl.glEnd();
		gl.glDisable(GL2.GL_TEXTURE_2D);


	}
}

