import java.awt.*;
import java.util.*;
public class Star
    {
    private int x, y;
    Image img = Toolkit.getDefaultToolkit().getImage("Star.png");
    public Star()
    {
        x = (int)(Math.random() * 1200);
        y = (int)(Math.random() * 840);
    }
    public void draw(Graphics g) {
        g.drawImage(img, x, y, null);
    }
}