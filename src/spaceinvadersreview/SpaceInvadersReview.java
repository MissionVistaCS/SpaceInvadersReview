/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvadersreview;


import java.io.*; 
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class SpaceInvadersReview extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 600;
    static final int WINDOW_HEIGHT = 800;
    final int XBORDER = 40;
    final int YBORDER = 40;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;
//Variables related to the cannon.
    int cannonXPos;
    int cannonYPos;
    
//Variables related to the cannonballs.
    int currCB;
    int numCB = 50;
    int cannonBallXPos[] = new int[numCB];
    int cannonBallYPos[] = new int[numCB];
    boolean cannonBallActive[] = new boolean[numCB];
    
//Variables related to the aliens.
    int numAliens = 20;
    int alienXPos[] = new int[numAliens];
    int alienYPos[] = new int[numAliens];
    boolean alienActive[] = new boolean[numAliens];
    int alienValue[] = new int[numAliens];
    int alienXMoveDir;
    
    int score;
    int highScore;
    boolean gameOver;
    int timeCount;
        
    static SpaceInvadersReview frame;
    public static void main(String[] args) {
        frame = new SpaceInvadersReview();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public SpaceInvadersReview() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button
                    if (gameOver)
                        return;

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();
//Shoot the next cannonball.                    
                    cannonBallActive[currCB] = true;
                    cannonBallXPos[currCB] = cannonXPos;
                    cannonBallYPos[currCB] = cannonYPos;
                    currCB++;
//Reuse cannonballs.                    
                    if (currCB >= numCB)
                        currCB = 0;
                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        if (gameOver)
           return;

        cannonXPos = e.getX() - getX(0);
            
        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                } else if (e.VK_DOWN == e.getKeyCode()) {
                } else if (e.VK_LEFT == e.getKeyCode()) {
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
//Display the aliens        
        for (int i = 0;i<numAliens;i++)
        {
            if (alienActive[i])
            {
                g.setColor(Color.green);
                drawAlien(getX(alienXPos[i]),
                getYNormal(alienYPos[i]),0,1,1,i);
            }
        }        
//Display the cannonballs.
        for (int i=0;i<numCB;i++)
        {
            if (cannonBallActive[i])
            {
                g.setColor(Color.black);
                drawCircle(getX(cannonBallXPos[i]),
                getYNormal(cannonBallYPos[i]),0,1,1);
            }
        }        
//Display the cannon.
        g.setColor(Color.red);
        drawCannon(getX(cannonXPos),getYNormal(cannonYPos),0,1,1);
        
//Display the score and high score.        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.PLAIN,20));
        g.drawString("Score: " + score, 60, 60);        
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.PLAIN,20));
        g.drawString("High Score: " + highScore, 360, 60);        

//Display game over when the game is over.
        if (gameOver)
        {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial",Font.PLAIN,50));
            g.drawString("Game Over", 60, 360);        
        }
        
        gOld.drawImage(image, 0, 0, null);
    }


////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,
            double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }    

////////////////////////////////////////////////////////////////////////////

    public void drawAlien(int xpos,int ypos,double rot,
    double xscale,double yscale,int i)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        int xval[] = {10,-10,-10,-5,-10,0,10,5,10};
        int yval[] = {-20,-20,-5,-5,10,0,10,-5,-5};
        g.fillPolygon(xval,yval,xval.length);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.PLAIN,20));
        g.drawString("" + alienValue[i], 0, 0);                
        
        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }          
////////////////////////////////////////////////////////////////////////////
    public void drawCannon(int xpos,int ypos,double rot,
            double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );
      
        int xvals[] = {0,-20,-20,20,20,0};
        int yvals[] = {-30,-20,20,20,-20,-30};        
        g.fillPolygon(xvals,yvals,xvals.length); 

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }

////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = .02;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {
        timeCount = 0;
        score = 0;
        gameOver = false;
//Init variables for the cannon.        
        cannonXPos = getWidth2()/2;
        cannonYPos = 0;
     
//Init variables for the cannonballs.        
        currCB = 0;
        for (int i=0;i<numCB;i++)
        {
            cannonBallXPos[i] = 0;
            cannonBallYPos[i] = 0;
            cannonBallActive[i] = false;
        }
//Init variables for the aliens.
        for (int i = 0;i<numAliens;i++)
        {
            alienValue[i] = (int)(Math.random()*5+1);

            alienActive[i] = true;
            alienXPos[i] = (int)(  Math.random()*getWidth2()/2+getWidth2()/4);
            alienYPos[i] = (int)(Math.random()* getHeight2()/2 + getHeight2()/2); 

        }
        alienXMoveDir = 2;

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
         
            highScore = 0;
            reset();
        }

        if (gameOver)
            return;
//Check to see if a cannonball has hit an alien.        
        for (int j=0;j<numCB;j++)
        {
            for (int i = 0;i<numAliens;i++)
            {
                    if (alienActive[i] && cannonBallActive[j] &&
                        cannonBallXPos[j] > alienXPos[i]-20 &&
                        cannonBallXPos[j] < alienXPos[i]+20 &&
                        cannonBallYPos[j] > alienYPos[i]-20 &&
                        cannonBallYPos[j] < alienYPos[i]+20 )
                    {
                        cannonBallActive[j] = false;
                        
                        score += alienValue[i];

                        if (score > highScore)
                            highScore = score;
//Reuse the aliens.                        
                        alienValue[i] = (int)(Math.random()*5+1);
                        alienXPos[i] = (int)(  Math.random()*getWidth2()/2+getWidth2()/4);
                        alienYPos[i] = getHeight2();                       
                    }
            }                    
        }
//Move the cannonballs up.        
        for (int i=0;i<numCB;i++)
        {
            if (cannonBallActive[i])
            {
                cannonBallYPos[i]+=8;
            }

        }      
        
//Move the aliens down slowly.        
        if (timeCount % 10 == 9)
        {
            for (int i=0;i<numAliens;i++)
            {
                if (alienActive[i])
                {
                    alienYPos[i]-=8;                   
//Game over when an alien hits the ground.   
                    if (alienYPos[i] <= 0)
                    {                  
                        gameOver = true;
                    }
                }
            }      
        }
        
//Move the aliens side to side.        
        for (int i=0;i<numAliens;i++)
        {
            if (alienActive[i])
            {
                alienXPos[i]+=alienXMoveDir;
//Switch the x direction of the aliens.     
                if (alienXPos[i] > getWidth2())
                {
                    alienXMoveDir = -2;
                }
                else if (alienXPos[i] < 0)
                {
                    alienXMoveDir = 2;
                }
            }
        }          
        
        timeCount++;
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }
    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }
    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }
    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
}
