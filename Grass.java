
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
//moved grass to seperate entity to prevent needless rendering of a new "grass" every time we made a butterfly. 
public class Grass {
	
		public Float size = .5f, tranX, tranY, tranZ; // walk speed of CAMERA not mobs. 
		
		public Grass(GL2 gl, GLCanvas canvas) 
		{
			tranX=0f;
			tranY=0f;
			tranZ=0f; 
		}
		//to start a Butterfly off at X, Z coords
		public Grass(GL2 gl, Float X, Float Y, Float Z, Float sizer) 
		{		
			tranX=X;
			tranY=Y;
			tranZ=Z; 
			size=sizer;
		}
		// Draw everything
		public void draw(GL2 gl)
		{			
			gl.glPushMatrix();
			gl.glTranslatef(tranX, tranY,tranZ); 
			grass(gl, size);
			gl.glPopMatrix();
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

}
