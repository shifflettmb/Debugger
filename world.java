import javax.media.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.media.opengl.glu.*;

import java.io.IOException;
import java.io.*;


public class world {
private Texture Grass;
private GLUquadric quadric; 
public float x1, x2, z1, z2; 
private GLU glu = new GLU();
public world (GL2 gl) {
	
	quadric = glu.gluNewQuadric();
    glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
    glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
    glu.gluQuadricTexture  (quadric, true);        // use true to generate texture coordinates

    gl.glEnable(GL2.GL_TEXTURE_2D);

	Grass = CreateTexture(gl, "SRC/textures/tron.jpg");

	x1=z1=0f;
	x2=z2=160f; 
}
public void draw(GL2 gl)	{
	gl.glPushMatrix();
	
	//draw stuff here
	grass(gl); 
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
	gl.glColor3f(1, 1, 1);
	gl.glTexCoord2f(0,0); gl.glVertex3f(x1, -0.01f,  z1);
	gl.glTexCoord2f(0, num); gl.glVertex3f( x1, -0.01f, z2);
	gl.glTexCoord2f(num, num); gl.glVertex3f(x2, -0.01f, z2);
	gl.glColor3f(1, .3f, 1);
	gl.glTexCoord2f(num, 0); gl.glVertex3f(x2, -0.01f,  z1);
	gl.glEnd();

	gl.glDisable(GL2.GL_TEXTURE_2D);
	gl.glPopMatrix();
	
}
}
