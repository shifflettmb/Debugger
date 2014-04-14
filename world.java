import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.media.opengl.glu.*;

import java.io.*;


public class world {
	private Texture Grass, Di;
	private GLUquadric quadric; 
	public float x1, x2, z1, z2, rotate=0f;
	public tronRoom rooms[] = new tronRoom[8]; 
	private GLU glu = new GLU();
	public world (GL2 gl) {

		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, true);        // use true to generate texture coordinates

		gl.glEnable(GL2.GL_TEXTURE_2D);
		Grass = CreateTexture(gl, "textures/tron.jpg");
		Di =  CreateTexture(gl, "textures/tronDi.jpg");
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
			
			rooms[i]=new tronRoom(gl, (x+(((x-20)/40)*.01f)),0f,z+(z-20)/40*.01f,c1,c2, c3);
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
		di(gl);
		gl.glPopMatrix();

	}  
	private void di(GL2 gl){
		gl.glDisable(GL2.GL_TEXTURE_2D);
		Di.enable(gl);
		Di.bind(gl);
		gl.glPushMatrix();
		Float size = 2f;
		Float z = new Float((size)*1.375);
		Float magic =  new Float(z*1.235);
		Float scalar = 1f;
		gl.glTranslatef(80, size*3+2, 80);
		gl.glRotatef(rotate, 0f, 1.0f, 0.0f);
		rotate+=.5f;
		//gl.glRotatef(rotateY,0f,1f,0f);
		for(int q = 1; q<3; q++){
			for (int i = 1; i<6; i++) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
				gl.glColor3f(1f,1f,1f);
				gl.glBegin(GL2.GL_POLYGON); {
					//please ignore the terrible naming conventions for the coordinates.
					//z is not necessary on the z axis. Magic is z when z is not z. 
					gl.glTexCoord2f(0, 0f); gl.glVertex3f(-size, 0, z);
					gl.glTexCoord2f(scalar, 0f); gl.glVertex3f(size, 0, z);
					gl.glTexCoord2f(scalar, scalar); gl.glVertex3f(0, -z, magic);
				} gl.glEnd();


				gl.glBegin(GL2.GL_POLYGON);
				{	
					gl.glTexCoord2f(0f, 0f); gl.glVertex3f(-size, 0, z);
					gl.glTexCoord2f(scalar, 0f); gl.glVertex3f(size, 0, z);
					gl.glTexCoord2f(scalar, scalar); gl.glVertex3f(0, size, 0);
				}
				gl.glEnd(); 
				gl.glRotatef(360/5,0, 1, 0); // rotate on the Y

			}
			gl.glTranslatef(0.0f, -z, 0f);
			gl.glRotatef(180,1, 0, 0);

		}

		gl.glDisable(GL2.GL_TEXTURE_2D);
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
