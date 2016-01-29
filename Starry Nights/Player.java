import java.awt.*;
import java.awt.AlphaComposite;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.*;
public class Player extends GravityObject
    {
    BufferedImage walkingSpriteSheet;
    public int midairJumps = 1;
    int frame = 0;
    boolean facingR = true;
    BufferedImage[] leftWalkImgs = new BufferedImage[4];
    BufferedImage[] rightWalkImgs = new BufferedImage[4];
    BufferedImage airL;
    BufferedImage airR;
    public Player(int x, int y, int width, int height, String imgFile)
    {
        super(x, y, width, height, imgFile);
        walkingSpriteSheet = loadImage(imgFile);
        walkingSpriteSheet = whiteToTransparent(walkingSpriteSheet);
        for (int i = 0; i < 4; i++)
        {
            leftWalkImgs[i] = walkingSpriteSheet.getSubimage(45-15*i, 0, 15, 31);
            rightWalkImgs[i] = walkingSpriteSheet.getSubimage(60+15*i, 0, 15, 31);
        }
        airL = walkingSpriteSheet.getSubimage(0, 32, 15, 31);
        airR = walkingSpriteSheet.getSubimage(16, 32, 15, 31);
    }
    
    public void jump()
    {
         GameMain.playSound("SFX_Jump_17.wav");
         setYVelocity(-15); // player can jump onto a pillar 7 blocks tall
         midair = true;
    }
    public void update(boolean left, boolean right)
    {
        if (left )//&& !midair)
            addVelocity(-1,0);
        if (right )//&& !midair)
            addVelocity(1,0);
        if (!right && !left && getVelX() != 0)
            setXVelocity((getVelX()*.75));
        if(getVelX() > 0)
            facingR = true;
        else if(getVelX() < 0)
            facingR = false;
        if (Math.abs(getVelX()) < .1)
            setXVelocity(0);
        super.update();
    }
    public void draw(Graphics g, int yPlus)
    {
        if(midair)
        {
            frame = 0;
            if (!facingR)
                g.drawImage(airL, (int)getX(), (int)getY() + yPlus, getWidth(), getHeight(),null,null);
            else
                g.drawImage(airR, (int)getX(), (int)getY() + yPlus, getWidth(), getHeight(),null,null);
        }
        else
        {
            if(Math.abs(getVelX()) <= .3)
            {
                if (!facingR)
                    g.drawImage(leftWalkImgs[0], (int)getX(), (int)getY() + yPlus, getWidth(), getHeight(),null,null);
                else if (facingR)
                    g.drawImage(rightWalkImgs[0], (int)getX(), (int)getY() + yPlus, getWidth(), getHeight(),null,null);
                frame = 0;
            }
                
            else{
                if(getVelX() < -.3)
                    g.drawImage(leftWalkImgs[frame/10+1], (int)getX(), (int)getY() + yPlus, getWidth(), getHeight(),null,null);
                else if(getVelX() > .3)
                    g.drawImage(rightWalkImgs[frame/10+1], (int)getX(), (int)getY() + yPlus, getWidth(), getHeight(),null,null);
                frame = (frame+1)%30;
            }
        }
    }    
}