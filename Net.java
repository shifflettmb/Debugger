/************************************************************************************
 							Class Name - Net
 							
Description: Creates a net that can be used to catch the bugs

************************************************************************************/
import java.io.File;
import java.io.IOException;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Net {
	
	public float rotateY =0f, netX = 0f, netZ = 0f; //coordinates of entire net
	private float netRotation = 0, netRotationInc = -5f, maxNetRotation = 0, minNetRotation = -90; //for swinging net
	private float centerX, centerY, centerZ; //center of the net webbing
	private Texture texNet;
	private int count = 0; //used to keep track of swinging
	public boolean swing = false; //swing the net
	public boolean inProgress = false; //swinging inProgress
	public boolean visible = true; //for possibly switching weapons 
	private GLUquadric quadric;
   	// private GLCanvas canvas;
    	private GLU glu = new GLU();
    
	public Net(GL2 gl, GLCanvas canvas) 
	{
		//this.canvas = canvas;
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates

		netX=0f; 
		netZ=0f; 
		centerX = netX;
		centerY = 4.5f;
		centerZ = netZ;
		gl.glEnable(GL2.GL_TEXTURE_2D);

		//all the different texture
		texNet = CreateTexture(gl, "textures/MSTronNet.jpg");
	}
	
	
	public Net(GL2 gl, GLCanvas canvas, Float x, Float z) 
	{
	//	this.canvas = canvas;
		quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL); // GLU_POINT, GLU_LINE, GLU_FILL, GLU_SILHOUETTE
		glu.gluQuadricNormals  (quadric, GLU.GLU_NONE); // GLU_NONE, GLU_FLAT, or GLU_SMOOTH
		glu.gluQuadricTexture  (quadric, false);        // use true to generate texture coordinates

		netX=x; 
		netZ=z; 
		centerX=netX;
		centerY=4.5f;
		centerZ=netZ;
		gl.glEnable(GL2.GL_TEXTURE_2D);

		//all the different texture
		texNet = CreateTexture(gl, "textures/tronNet.jpg");
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
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR );
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR );

		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT );
		gl.glTexParameterf( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT );

		//		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		return tex;
	}
	
	/**********************************************************************************
	 	Draws everything, if the net is visible meaning the player has the net
	 	as their weapon then the player can swing the net. inProgress boolean is used
	 	so that you can't switch weapons while swinging the net.
	 *********************************************************************************/
	public void draw(GL2 gl)
	{
		if(visible) //current weapon
		{
			gl.glPushMatrix();	
			gl.glTranslatef(10, 3f, 10);
			gl.glRotatef(rotateY, 0, 1, 0);
			if(swing)
			{
				inProgress = true; //for weapon switching
				netRotation = netRotation + netRotationInc;
				if(netRotation >= maxNetRotation || netRotation <= minNetRotation)
				{
					netRotationInc = -netRotationInc;
					count++; //increases count when hits min and then max
				}
				if(count == 2) //went down and come back up
				{
					swing = false; //done swinging the net
					count = 0;
					inProgress = false; //no longer in progress
				}
				
			}
			gl.glRotatef(netRotation, 0, 0, 1);
			//centerOfnet(gl); //for debugging to get center of net
			net(gl);
			gl.glPopMatrix();
		}
	}

	/***********************************************************
		Get x,y,z coordinates of the center of the net
	 **********************************************************/
	public Float[] getCenterOfNet()
	{
		Float[] temp = {centerX, centerY, centerZ};
		return temp;
	}
	
	/*************************************************************
	 	Creates a sphere at the center of the net webbing
	 	for testing purposes can be deleted later
	 **************************************************************/
	/*private void centerOfnet(GL2 gl)
	{
		gl.glPushMatrix();
		gl.glTranslatef(0, 1.5f, 0); //since the center of net without swinging is netX, 4.5f, netZ;
		gl.glColor3f(1,0,0);
		glu.gluSphere(quadric, .1, 20, 20);
		gl.glPopMatrix();
		gl.glColor3f(1,1,1);
	}*/
	
	/*******************************************
		Creates the net
	 *******************************************/
	private void net(GL2 gl)
	{ 	
		gl.glPushMatrix();
		gl.glScalef(.5f, .5f, .5f);  //scaled the net in half
		gl.glPushMatrix();
		glu.gluQuadricTexture(quadric, true);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		texNet.enable(gl);
		texNet.bind(gl);
		gl.glColor3f(1,0,0);
		gl.glRotatef(-90, 1, 0, 0);
		glu.gluCylinder(quadric, .3, .3, 2.4, 10, 10); //creates the stick
		gl.glColor3f(1,1,1); //set the color back to white so it won't bleed
		gl.glTranslatef(-2, 0, 3);
		gl.glRotatef(-90, 1, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		glu.gluCylinder(quadric, 0, 1, 3, 10, 10); //creates net cylinder
		gl.glPopMatrix();
		gl.glPopMatrix();
		gl.glDisable(GL2.GL_TEXTURE_2D);
		
	}
	
}
