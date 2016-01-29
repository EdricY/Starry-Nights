import java.awt.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
/**
 * Abstract class GravityObject - write a description of the class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class GravityObject
{
    public static enum Status
    {LEFT, RIGHT, STILL, LEFTAIR, RIGHTAIR}
    private static final double GCONSTANT = 1;
    private GravityObject.Status status;
    private double x, y, prevX, prevY, velX, velY; 
    int height, width, maxVelX, maxVelY;
    public boolean jumpAvailable, midair=true;
    Image img;

    public GravityObject(double x, double y, int width, int height, String imgFile)
    {
        this.x = x;
        this.y = y;
        this.img = img; 
        this.width = width;
        this.height = height;
        maxVelX = 5;
        maxVelY = 20;
        status = Status.STILL;

        jumpAvailable = true;
    }
    public double getX(){ return x;}
    public double getY(){ return y;} 
    public void setX(double x){ this.x = x;}
    public void setY(double y){ this.y = y;}
    public void setMidair(boolean b){midair = b;}
    public int getWidth(){ return width;}
    public int getHeight(){ return height;}
    public double getVelX(){ return velX;}
    public double getVelY(){ return velY;}
    public boolean getJumpAvailability(){return jumpAvailable;}
    public void setJumpAvailability(boolean b) {jumpAvailable = b;}
    public void update() {
        if(midair)
        velY += GCONSTANT;
        if (velY > 0)
        velY = Math.min(maxVelY, velY);
        if (velX > 0)
        velX = Math.min(maxVelX, velX);
        if (velX < 0)
        velX = Math.max(-1*maxVelX, velX);
        move();
    }
    public void land(Block plat){
        midair = false;
        velY = 0;
        setY(plat.getY() + getHeight());
    }
    public void draw(Graphics g, int yPlus) {
        g.drawImage(img, (int)x, (int)y  + yPlus, null);
    }
 
    public void addVelocity(double xAmt, double yAmt)
    {
        velX += xAmt;
        velY += yAmt;
    }
    public void setXVelocity(double xAmt)
    {
        velX = xAmt;
    }
    public void setYVelocity(double yAmt)
    {
        velY = yAmt;
    }
    public void move()
    {
        x += velX;
        y += velY;
    }
    public boolean testLanding(Block plat)
    {
        return (getY() + getHeight() >= plat.getY() &&
        getY() + getHeight() - getVelY() <= plat.getY() &&
        getX() + getWidth() > plat.getX() &&
        getX() < plat.getX() + plat.getWidth());
    }
    public boolean testCollisionL(Block plat)
    {
        return (getX() + getWidth() >= plat.getX() &&
        getX() + getWidth() - getVelX() <= plat.getX() &&
        getY() < plat.getHeight() + plat.getY() &&
        getY() + getHeight() > plat.getY());
    }
    public boolean testCollisionR(Block plat)
    {
        return (getX() <= (plat.getX() + plat.getWidth()) &&
        getX() - getVelX() >= plat.getX() + plat.getWidth() &&
        getY() < plat.getHeight() + plat.getY() &&
        getY() + getHeight() > plat.getY());
    }
    public boolean testCollisionB(Block plat)
    {
        return (getY() <= plat.getY() + plat.getHeight() &&
        getY() - getVelY() >= plat.getY() + plat.getHeight() &&
        getX() + getWidth() > plat.getX() &&
        getX() < plat.getX() + plat.getWidth());
    }
    public boolean testStarIntersect(Block b)
    {
        return (getY() < b.getHeight() + b.getY() &&
        getY() + getHeight() > b.getY() &&
        getX() + getWidth() > b.getX() &&
        getX() < b.getX() + b.getWidth());
    }
    public static BufferedImage loadImage(String ref) {  
        BufferedImage bimg = null;  
        try {  
  
            bimg = ImageIO.read(new File(ref));
        } catch (Exception e) {  
            e.printStackTrace();
        }  
        return bimg;  
    }
    public static BufferedImage whiteToTransparent(BufferedImage bimg) {  
        Color c;
        BufferedImage newbimg = bimg;
        for(int y = 0; y < bimg.getHeight(); y++)
        for(int x = 0; x < bimg.getWidth(); x++)
        {
            c = new Color(bimg.getRGB(x,y));
            if (c.getRGB() == -1)
               newbimg.setRGB(x, y, 0 & 0);  
        }
        return newbimg;
    }
}
