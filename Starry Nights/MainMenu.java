import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.util.ArrayList;
/**
 * Abstract class Menu - write a description of the class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public class MainMenu
{
    public int selected=0;
    public enum MenuState {
          MAIN, CREDITS, GAMEOVER
    }
    public MenuState mState = MenuState.MAIN;
    int stars, height;
    ArrayList<Star> starList;
    boolean first = true;
    public MainMenu()
    {
        GameMain.playSongLoop("Symmetries.wav");
    }
    public void draw(Graphics g)
    {
        switch(mState){
            case MAIN:
            g.drawImage(Toolkit.getDefaultToolkit().getImage("mainbackgroundnew.jpg"),0,0,1200,840,null);
            g.setFont(new Font("Sylfaen", Font.PLAIN, 70));
            g.setColor(new Color(2,58,89));
            g.drawString("Starry  Nights", 415, 202);
            g.setColor(new Color(50,150,200));    
            g.drawString("Starry  Nights", 415, 200);
            
            g.setColor(new Color(120,60,3)); 
            g.setFont(new Font("Sylfaen", Font.PLAIN, 30));
            g.drawString("Begin Game", 100, 701);
            g.drawString("Credits", 550,701);
            g.drawString("Quit", 1000,701);
            g.setColor(Color.YELLOW); 
            g.setFont(new Font("Sylfaen", Font.PLAIN, 30));
            g.drawString("Begin Game", 100, 700);
            g.drawString("Credits", 550,700);
            g.drawString("Quit", 1000,700);
            
            g.setColor(new Color(2,58,89));
            g.fillPolygon(new int[]{450*selected+85,450*selected+85,450*selected+95},new int[]{683,703,693},3);
            g.setColor(new Color(50,150,200));
            g.fillPolygon(new int[]{450*selected+85,450*selected+85,450*selected+95},new int[]{680,700,690},3);
            break;
            case CREDITS:
             g.drawImage(Toolkit.getDefaultToolkit().getImage("CreditsPage.png"),0,0,1200,840,null);
                break;
            case GAMEOVER:
                for(Star s : starList)
                    s.draw(g);
                g.drawImage(Toolkit.getDefaultToolkit().getImage("GameOverPic.png"),0,0,1200,840,null);
                g.setFont(new Font("Sylfaen", Font.PLAIN, 30));
                g.setColor(new Color(120,60,3)); 
                g.drawString("Max Height: "+ height, 301, 31);
                g.drawString("Stars: "+ stars, 701, 31);
                g.drawString("Try Again", 301, 501);
                g.drawString("Main Menu", 701, 501);
                g.setColor(Color.YELLOW);
                g.drawString("Max Height: "+ height, 300, 30);
                g.drawString("Stars: "+ stars, 700, 30);
                g.drawString("Try Again", 300, 500);
                g.drawString("Main Menu", 700, 500);
                g.setColor(new Color(2,58,89));
                g.fillPolygon(new int[]{400*selected+285,400*selected+285,400*selected+295},new int[]{483,503,493},3);
                g.setColor(new Color(50,150,200));
                g.fillPolygon(new int[]{400*selected+285,400*selected+285,400*selected+295},new int[]{480,500,490},3);
                break;
        }
    }
    public void keyPressed(int keyCode)
    {
        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE){
            GameMain.playSound("Explosion.wav");
            if (mState == MenuState.MAIN)
            switch (selected) {
                case 0://begin game
                
                    GameMain.songClip.close();
                    GameMain.playSongLoop("BrighterDay.wav");
                    if (first)
                      GameMain.state = GameState.SCENE;
                    else
                      GameMain.state = GameState.RESET;
                break;
                case 1://credits
                    mState = MenuState.CREDITS;
                break;
                case 2://quit
                    System.exit(0);
                break;
            }
            else if (mState == MenuState.CREDITS)
                mState = MenuState.MAIN;
            else if (mState == MenuState.GAMEOVER)
                switch (selected) {
                    case 0://try again
                        selected = 0;
                        GameMain.songClip.close();
                        GameMain.playSongLoop("BrighterDay.wav");
                        GameMain.state = GameState.RESET;
                    break;
                    case 1://main menu
                        selected = 0;
                        GameMain.songClip.close();
                        GameMain.playSongLoop("Symmetries.wav");
                        mState = MenuState.MAIN;
                    break;
                }
            }
    }
    public void endGame(int stars, int maxHeight, ArrayList<Star> s)
    {
        GameMain.songClip.close();
        GameMain.playSongLoop("The Afterglow.wav");
        this.stars = stars;
        height = maxHeight;
        mState = MenuState.GAMEOVER;
        starList = s;
        first = false;
    }
    public void keyReleased(int keyCode)
    {
        if(mState == MenuState.MAIN)
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            selected = (selected+2)%3;
            GameMain.playSound("Blip.wav");
            break;
            case KeyEvent.VK_RIGHT:
            selected = (selected+1)%3;
            GameMain.playSound("Blip.wav");
            break;
        }
        else if(mState == MenuState.GAMEOVER)
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            selected = (selected+1)%2;
            GameMain.playSound("Blip.wav");
            break;
            case KeyEvent.VK_RIGHT:
            selected = (selected+1)%2;
            GameMain.playSound("Blip.wav");
            break;
        }
    }
}


