import java.awt.*;
import java.util.*;
public class Block
    {
    public BlockType type;
    private int x, y, t; //width & height = 16
    private Image blockSheet = Toolkit.getDefaultToolkit().getImage("BlockTexturesSmall.png");
    public Block(int x, int y, int t)
    {
        this.x = x;
        this.y = y;
        this.t = t;
        switch(t){
            case 0:
                type = BlockType.AIR;break;
            case 1:
                type = BlockType.GRASS;break;
            case 2:
                type = BlockType.DIRT;break;
            case 3:
                type = BlockType.STONE;break;
            case 4:
                type = BlockType.SAND;break;
            case 5:
                type = BlockType.ICE;break;
            case 6:
                type = BlockType.SNOW;break;
            case 7:
                type = BlockType.STAR;break;
        }
    }
    
    public void draw(Graphics g, int yPlus) {
        if (type != BlockType.AIR && type != BlockType.STAR)
         g.drawImage(blockSheet,x,y+yPlus,x+16,y+16+yPlus,
                    type.getX(), type.getY(), type.getX()+10, type.getY()+10, null);
        if (type == BlockType.STAR)
         g.drawImage(blockSheet,x,y+yPlus,x+16,y+16+yPlus,
                    type.getX(), type.getY(), type.getX()+16, type.getY()+16, null);
    }
    public int getX(){ return x;}
    public int getY(){ return y;}
    public int getT(){ return t;}
    public int getWidth(){ return 16;}
    public int getHeight(){ return 16;}
    public Block increaseY(int yAmt)
    {
        return new Block(x,y+yAmt,t);
    }
}