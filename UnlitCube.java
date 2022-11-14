package javaapplication4;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

/**
 * Use JOGL to draw a simple cube
 * with each face being a different color.  Rotations
 * can be applied with the arrow keys, the page up
 * key, and the page down key.  The home key will set
 * all rotations to 0.  Initial rotations about the
 * x, y, and z axes are 15, -15, and 0.  
 *
 * This program is meant as an example of using modeling
 * transforms, with glPushMatrix and glPopMatrix.
 *
 * Note that this program does not use lighting.
 */
public class UnlitCube extends GLJPanel implements GLEventListener, KeyListener {
    
    /**
     * A main routine to create and show a window that contains a
     * panel of type UnlitCube.  The program ends when the
     * user closes the window.
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("Madison's Project 2");
        UnlitCube panel = new UnlitCube();
        window.setContentPane(panel);
        window.pack();
        window.setLocation(50,50);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        panel.requestFocusInWindow();
    }
    
    /**
     * Constructor for class UnlitCube.
     */
    public UnlitCube() {
        super( new GLCapabilities(null) ); // Makes a panel with default OpenGL "capabilities".
        setPreferredSize( new Dimension(700,500) );
        addGLEventListener(this); // A listener is essential! The listener is where the OpenGL programming lives.
        addKeyListener(this);
    }
    
    //-------------------- methods to draw the cube ----------------------
    
    double rotateX = 15;    // rotations of the cube about the axes
    double rotateY = -15;
    double rotateZ = 0;
    
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
    public static void uvCone(GL2 gl) {
        uvCone(gl,0.5,1,32,10,5,true);
    }
    
    public static void uvCone(GL2 gl, double radius, double height, 
            int slices, int stacks, int rings, boolean makeTexCoords) {
        if (radius <= 0)
            throw new IllegalArgumentException("Radius must be positive.");
        if (height <= 0)
            throw new IllegalArgumentException("Height must be positive.");
        if (slices < 3)
            throw new IllegalArgumentException("Number of slices must be at least 3.");
        if (stacks < 2)
            throw new IllegalArgumentException("Number of stacks must be at least 2.");
        for (int j = 0; j < stacks; j++) {
            double z1 = (height/stacks) * j;
            double z2 = (height/stacks) * (j+1);
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int i = 0; i <= slices; i++) {
                double longitude = (2*Math.PI/slices) * i;
                double sinLong = Math.sin(longitude);
                double cosLong = Math.cos(longitude);
                double x = cosLong;
                double y = sinLong;
                double nz = radius/height;
                double normLength = Math.sqrt(x*x+y*y+nz*nz);
                gl.glNormal3d(x/normLength,y/normLength,nz/normLength);
                if (makeTexCoords)
                    gl.glTexCoord2d(1.0/slices * i, 1.0/stacks * (j+1));
                gl.glVertex3d((height-z2)/height*radius*x,(height-z2)/height*radius*y,z2);
                if (makeTexCoords)
                    gl.glTexCoord2d(1.0/slices * i, 1.0/stacks * j);
                gl.glVertex3d((height-z1)/height*radius*x,(height-z1)/height*radius*y,z1);
            }
            gl.glEnd();
        }
        if (rings > 0) {
            gl.glNormal3d(0,0,-1);
            for (int j = 0; j < rings; j++) {
                double d1 = (1.0/rings) * j;
                double d2 = (1.0/rings) * (j+1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {
                    double angle = (2*Math.PI/slices) * i;
                    double sin = Math.sin(angle);
                    double cos = Math.cos(angle);
                    if (makeTexCoords)
                        gl.glTexCoord2d(0.5*(1+cos*d2),0.5*(1+sin*d2));
                    gl.glVertex3d(radius*cos*d2,radius*sin*d2,0);
                    if (makeTexCoords)
                        gl.glTexCoord2d(0.5*(1+cos*d1),0.5*(1+sin*d1));
                    gl.glVertex3d(radius*cos*d1,radius*sin*d1,0);
                }
                gl.glEnd();
            }
        }
    } // end uvCone
  
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void square(GL2 gl2, double r, double g, double b) {
        gl2.glColor3d(r,g,b);
        gl2.glBegin(GL2.GL_TRIANGLE_FAN);
        gl2.glVertex3d(-0.5, -0.5, 0.5);
        gl2.glVertex3d(0.5, -0.5, 0.5);
        gl2.glVertex3d(0.5, 0.5, 0.5);
        gl2.glVertex3d(-0.5, 0.5, 0.5);
        gl2.glEnd();
    }
    
    
    private void cube(GL2 gl2, double size) {
        gl2.glPushMatrix();
        gl2.glScaled(size,size,size); // scale unit cube to desired size
        // Move the squares to offset 3,3 
        gl2.glTranslated(3,3,0);
        square(gl2,1, 0, 0); // red front face
        
        gl2.glPushMatrix();
        gl2.glRotated(90, 0, 1, 0);
       
        square(gl2,0, 1, 0); // green right face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(-90, 1, 0, 0);
        square(gl2,0, 0, 1); // blue top face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(180, 0, 1, 0);
        square(gl2,0, 1, 1); // cyan back face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(-90, 0, 1, 0);
        square(gl2,1, 0, 1); // magenta left face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(90, 1, 0, 0);
        square(gl2,1, 1, 0); // yellow bottom face
        gl2.glPopMatrix();
        
        gl2.glPopMatrix(); // Restore matrix to its state before cube() was called.
    }
    
        private void triangle(GL2 gl2, double r, double g, double b) {
           gl2.glBegin(GL2.GL_TRIANGLE_FAN); //EDITING 
        gl2.glColor3d( 1, 0, 0 ); // red
        gl2.glVertex2d( -0.8, -0.8 );
        gl2.glColor3d( 0, 1, 0 ); // green
        gl2.glVertex2d( 0.8, -0.8 );
        gl2.glColor3d( 0, 0, 1 ); // blue
        gl2.glVertex2d( 0, 0.9 );
        gl2.glEnd(); 
        
    }
    
     private void pyramid(GL2 gl2, double size) {
        gl2.glPushMatrix();
        gl2.glScaled(size,size,size); // scale unit cube to desired size
        // Move the squares to offset 3,3 
        gl2.glTranslated(-3,-3,0);
        triangle(gl2,1, 0, 0); // red front face
        
        gl2.glPushMatrix();
        gl2.glRotated(90, 0, 1, 0);
       
        triangle(gl2,0, 1, 0); // green right face
        gl2.glPopMatrix();

        gl2.glPushMatrix();
        gl2.glRotated(180, 0, 1, 0);
        triangle(gl2,0, 1, 1); // cyan back face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(-90, 0, 1, 0);
        triangle(gl2,1, 0, 1); // magenta left face
        gl2.glPopMatrix();
        
        gl2.glPopMatrix(); // Restore matrix to its state before cube() was called.
    }
     
             private void triangle2(GL2 gl2, double r, double g, double b) {
           gl2.glBegin(GL2.GL_TRIANGLE_FAN); //EDITING 
        //gl2.glColor3d( 1, 0, 0 ); // red
        gl2.glVertex2d( -0.8, -0.8 );
       // gl2.glColor3d( 0, 1, 0 ); // green
        gl2.glVertex2d( 0.8, -0.8 );
        //gl2.glColor3d( 0, 0, 1 ); // blue
        gl2.glVertex2d( 0, 0.9 );
        gl2.glEnd(); 
        
    }
             
              private void pyramid2(GL2 gl2, double size) {
        gl2.glPushMatrix();
        gl2.glScaled(size,size,size); // scale unit cube to desired size
        // Move the squares to offset 3,3 
        gl2.glTranslated(-3,3,0);
        triangle2(gl2,1, 0, 0); // red front face
        
        gl2.glPushMatrix();
        gl2.glRotated(90, 0, 1, 0);
       
        triangle2(gl2,0, 1, 0); // green right face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(-90, 1, 0, 0);
        triangle2(gl2,0, 0, 1); // blue top face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(180, 0, 1, 0);
        triangle2(gl2,0, 1, 1); // cyan back face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(-90, 0, 1, 0);
        triangle2(gl2,1, 0, 1); // magenta left face
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glRotated(90, 1, 0, 0);
        triangle2(gl2,1, 1, 0); // yellow bottom face
        gl2.glPopMatrix();
        
        gl2.glPopMatrix(); // Restore matrix to its state before cube() was called.
    }
    

    //-------------------- GLEventListener Methods -------------------------

    /**
     * The display method is called when the panel needs to be redrawn.
     * The is where the code goes for drawing the image, using OpenGL commands.
     */
    public void display(GLAutoDrawable drawable) {    
        
        GL2 gl2 = drawable.getGL().getGL2(); // The object that contains all the OpenGL methods.
         
        gl2.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        
        gl2.glLoadIdentity();             // Set up modelview transform. 
        gl2.glRotated(rotateZ,0,0,1);
        gl2.glRotated(rotateY,0,1,0);
        gl2.glRotated(rotateX,1,0,0);

        cube(gl2,1);
        pyramid(gl2,1);
        pyramid2(gl2,1);
        uvCone(gl2,0.5,1,32,10,5,true);
       
       // Add an Index Face set
       // Note using Graph paper is the best way to figure these vertices.
       // You can make about any shape you want this way
        double[][] vertexList =
         {  {1,-0.5,1}, {1,-.5,-1}, {1,.5,-1}, {1,.5,1}, {.75,.75,0},
               {-.75,.75,0}, {-1,-.5,1}, {-1,.5,1}, {-1,.5,-1}, {-1,-.5,-1}  };
         
int[][] faceList =
         {  {0,1,2,3}, {3,2,4}, {7,3,4,5}, {2,8,5,4}, {5,8,7},
               {0,3,7,6}, {0,6,9,1}, {2,1,9,8}, {6,7,8,9}  };
     
          
            for (int i = 0; i < faceList.length; i++) {
    gl2.glColor3f(1,0,1 );  // Set color for face number i.PINK CUBE
    gl2.glBegin(GL2.GL_TRIANGLE_FAN);
    for (int j = 0; j < faceList[i].length; j++) {
        int vertexNum = faceList[i][j];  // Index for vertex j of face i.
        double[] vertexCoords = vertexList[vertexNum];  // The vertex itself.
        gl2.glVertex3dv( vertexCoords, 0 );
    }
    gl2.glEnd();
    
    // You can add more shapes here.
    
 
    
}

   	
        
    } // end display()

    public void init(GLAutoDrawable drawable) {
           // called when the panel is created
        GL2 gl2 = drawable.getGL().getGL2();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        // gl2.glOrtho(-1, 1 ,-1, 1, -1, 1);
        // Changing this is your coordinate -x,x,-y,y,-z,z)
        // Larger numbers zooms out.
        // Play with this to make sure you see your shapes.
         gl2.glOrtho(-5, 5 ,-5, 5, -5, 5);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glClearColor( 0, 0, 0, 1 );
        gl2.glEnable(GL2.GL_DEPTH_TEST);
    }

    public void dispose(GLAutoDrawable drawable) {
            // called when the panel is being disposed
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            // called when user resizes the window
    }
    
    // ----------------  Methods from the KeyListener interface --------------

    public void keyPressed(KeyEvent evt) {
        int key = evt.getKeyCode();
        if ( key == KeyEvent.VK_LEFT )
            rotateY -= 15;
         else if ( key == KeyEvent.VK_RIGHT )
            rotateY += 15;
         else if ( key == KeyEvent.VK_DOWN)
            rotateX += 15;
         else if ( key == KeyEvent.VK_UP )
            rotateX -= 15;
         else if ( key == KeyEvent.VK_PAGE_UP )
            rotateZ += 15;
         else if ( key == KeyEvent.VK_PAGE_DOWN )
            rotateZ -= 15;
         else if ( key == KeyEvent.VK_HOME )
            rotateX = rotateY = rotateZ = 0;
        repaint();
    }

    public void keyReleased(KeyEvent evt) {
    }
    
    public void keyTyped(KeyEvent evt) {
    }
    
}
