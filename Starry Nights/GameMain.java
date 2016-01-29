import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.*;
import javax.sound.sampled.*;
import java.util.ArrayList;
//http://www3.ntu.edu.sg/home/ehchua/programming/java/J8d_Game_Framework.html
//http://opengameart.org/content/8-bit-jump-free-sound-effects-1
public class GameMain extends JPanel{   // main class for the game
   
   public static final String TITLE = "Starry Nights";
   static final int CANVAS_WIDTH  = 1200; // width and height of the game screen
   static final int CANVAS_HEIGHT = 840;
   static final int UPDATES_PER_SEC = 60;    // number of game update per second
   static final long UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC;  // nanoseconds
   private Color COLOR_PIT = new Color(100,200,255);
   int dayTimer = 0, dayPhase = 2; //dayphase 0=sunrise, 1=trans to midday, 2=wait at midday
                                   //         3=sunset,  4=trans to midnight, 5=wait at midnight
                                   
   private boolean leftPressed, rightPressed;
   MainMenu m = new MainMenu();
   Thread runner;

   static GameState state;   // current state of the game
   
   Player player;
   Block[][][] blocks = new Block[2][CANVAS_HEIGHT/15][CANVAS_WIDTH/15];
   Block[][][] blockPatterns = new Block[7][CANVAS_HEIGHT/15][CANVAS_WIDTH/15]; //56x80
   private GameCanvas pit;
   public static Clip songClip;
   public int cameraY = 0, currentPattern = 1, screenNum=0;
   int sceneTimer = 0, height, maxHeight, stars;
   boolean moveStart = false, inSpace = false;
   ArrayList<Star> starList = new ArrayList<Star>();
   // Constructor to initialize the UI components and game objects
   public GameMain() {
      gameInit();
      // UI components
      pit = new GameCanvas();
      pit.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
      add(pit);
      // Start the game.
      gameStart();
   }

   // ------ All the game related codes here ------
   
   // Initialize all the game objects, run only once in the constructor of the main class.
   public void gameInit() {
      state = GameState.INITIALIZED;
      player = new Player(10, 500, 30, 62, "PlayerWalkSmall.png");//sprites are 15x31
      try{
        Scanner scanner = new Scanner(new File("map.txt"));;
        for (int a = 0; a < 7; a++)
            for(int b = 0; b < 56; b++)
            {
              String[] currentLine = scanner.nextLine().trim().split("\\s+"); 
                 for (int j = 0; j < currentLine.length; j++) {
                    blockPatterns[a][b][j] = new Block(15*j-1,15*b,Integer.parseInt(currentLine[j]));
                 }
            }
          }
      catch(FileNotFoundException ex) {
                System.out.println(
                    "Unable to open file 'map.txt'");
          }
        for(int z = 0; z < blockPatterns.length; z++)
        for(int r = 0; r < blockPatterns[0].length; r++)
        for(int c = 0; c < blockPatterns[0][0].length; c++)
        if (blockPatterns[z][r][c] != null && blockPatterns[z][r][c].type == BlockType.AIR)
             blockPatterns[z][r][c] = null;
         blocks[0] = blockPatterns[0];
        for(int r = 0; r < blocks[0].length; r++)
        for(int c = 0; c < blocks[0][0].length; c++){
            if(blockPatterns[1][r][c] != null)
            blocks[1][r][c] = blockPatterns[1][r][c].increaseY(-840);
        }
   }
   
   public void gameReset() { //reset everything but music and maxheight, and skip the opening scene
             blocks = new Block[2][CANVAS_HEIGHT/15][CANVAS_WIDTH/15];
             blockPatterns = new Block[7][CANVAS_HEIGHT/15][CANVAS_WIDTH/15];
             gameInit();
             stars = 0;
             height = 0;
             dayTimer = 0; dayPhase = 2;
             starList = new ArrayList<Star>();
             cameraY = 0; currentPattern = 1; screenNum=0;
             COLOR_PIT = new Color(100,200,255);
             moveStart = false;
             leftPressed = false; rightPressed = false;
             inSpace = false;
             state = GameState.PLAYING;
             
   }
   
   public void gameStart() { 
      // Create a new thread
      Thread gameThread =  new Thread() {
         // Override run() to provide the running behavior of this thread.
         @Override
         public void run() {
                gameLoop();
         }
      };
      // Start the thread. start() calls run(), which in turn calls gameLoop().
      gameThread.start();
   }
   // Run the game loop here.
   private void gameLoop() {
      if (state == GameState.INITIALIZED) {
         state = state.MAIN_MENU;
      }
      // Game loop
      long beginTime, timeTaken, timeLeft;   // in msec
      while (true) {
         beginTime = System.nanoTime();
          if (state == GameState.RESET) { 
              gameReset();
         }
         if (state == GameState.PLAYING) {
            gameUpdate();
         }
         else if (state == GameState.SCENE) {
            sceneUpdate();
         }
         // Refresh the display
         repaint();
         // Delay timer to provide the necessary delay to meet the target rate
         timeTaken = System.nanoTime() - beginTime;
         timeLeft = (UPDATE_PERIOD_NSEC - timeTaken) / 1000000;  // in milliseconds
         if (timeLeft < 10) timeLeft = 10;  // set a minimum
         try {
            // Provides the necessary delay and also yields control so that other thread can do work.
            Thread.sleep(timeLeft);
         } catch (InterruptedException ex) { }
      }
   }
   public void sceneUpdate() { 
      switch (state){
          case SCENE:
          if(sceneTimer < 175 && sceneTimer != 130)
          ( player).update(false, true);
          else if (sceneTimer == 130)
          (player).jump();
          else if(sceneTimer == 175)
          (player).jump();
          else if(sceneTimer == 200)
          (player).jump();
          else if(sceneTimer > 200 && sceneTimer < 213)
          (player).update(true, false);
          else 
           (player).update(false, false);
          if (sceneTimer > 400){
           state = GameState.PLAYING;
          }
          break;
      }
      sceneTimer++;
      processCollision();
   }
   // Update the state and position of all the game objects,
   // detect collisions and provide responses.
   public void gameUpdate() { 
      player.update(leftPressed, rightPressed);
      processCollision();
      cameraChange();
      if (moveStart && !inSpace)
        dayCycle();
      else if (inSpace && COLOR_PIT != new Color(0,5,10))
        COLOR_PIT = new Color(Math.max(0,COLOR_PIT.getRed()-1),
                                Math.max(5,COLOR_PIT.getGreen()-1), 
                                Math.max(10,COLOR_PIT.getBlue()-1));
      height = screenNum*840+523-(int)player.getY();
      if (!inSpace && height > 3195)
        inSpace = true;
      maxHeight = Math.max(height, maxHeight);
      if (player.getY() + cameraY > 840)
      {
          state = GameState.MAIN_MENU;
          playSound("Randomize3.wav");
          COLOR_PIT = new Color(0,5,10);
          m.endGame(stars,maxHeight, starList);
      }
   }
   
   // Collision detection and response
   public void processCollision() {
       player.setMidair(true);
       boolean xCollide = false, yCollide = false;
           for(int a = 0;  a < 2; a++)
           for(int i = 0; i < 56; i++)
           for(Block b: blocks[a][i])
               if (b != null && b.type != BlockType.STAR && player.testCollisionL(b))
               {
                   player.setX(b.getX() - player.getWidth());
                   xCollide = true;
               }
           for(int a = 0;  a < 2; a++)
           for(int i = 0; i < 56; i++)
           for(Block b: blocks[a][i])
               if (b != null && b.type != BlockType.STAR && player.testCollisionR(b))
               {
                   player.setX(b.getX() + b.getWidth());
                   xCollide = true;
               }
           for(int a = 0;  a < 2; a++)
           for(int i = 0; i < 56; i++)
           for(Block b: blocks[a][i])
               if (b != null && b.type != BlockType.STAR && player.testCollisionB(b))
               {
                   player.setY(b.getY() + b.getHeight());
                   yCollide = true;
               }
           for(int a = 0;  a < 2; a++)
           for(int i = 0; i < 56; i++)
           for(Block b: blocks[a][i])
               if (b != null && b.type != BlockType.STAR && player.testLanding(b))
               {
                   player.land(b);//note that the land method does NOT change the velocity of the gobj
                   yCollide = true;
                   player.setY(b.getY() - player.getHeight());
                   (player).midairJumps = 1;
                   player.setJumpAvailability(true);
               }
           for(int a = 0;  a < 2; a++)
           for(int i = 0; i < 56; i++)
           for(int b = 0; b < 80; b++)
               if (blocks[a][i][b] != null && blocks[a][i][b].type == BlockType.STAR && player.testStarIntersect(blocks[a][i][b]))
               {
                   blocks[a][i][b] = null;
                   playSound("Pickup.wav");
                   stars++;
                   starList.add(new Star());
               }

       if (xCollide)
            player.setXVelocity(0);
       if (yCollide)
            player.setYVelocity(0);
   }
   public void cameraChange()
   {
       if (moveStart)
          cameraY++;
       if (cameraY > 840)
       {
            player.setY(player.getY() + 840);
            cameraY -= 840;
            if(!inSpace)
                currentPattern = currentPattern+1;
            else if(currentPattern != 5)
                currentPattern = 5;
            else
                currentPattern = 6;
        for(int r = 0; r < blocks[0].length; r++)
        for(int c = 0; c < blocks[0][0].length; c++){
            if(blocks[1][r][c] != null)
                blocks[0][r][c] = blocks[1][r][c].increaseY(840);
            else
                blocks[0][r][c] = null;
            if(blockPatterns[currentPattern][r][c] != null)
                blocks[1][r][c] = blockPatterns[currentPattern][r][c].increaseY(-840);
            else
                blocks[1][r][c] = null;
            
        }
        screenNum++;
       }
   }
   public void dayCycle()
   {
      if(dayTimer % 8 == 0 && dayPhase == 0)//sunrising
           COLOR_PIT = new Color(Math.min(220,COLOR_PIT.getRed()+2),
                                Math.min(60,COLOR_PIT.getGreen()+1),
                                Math.min(60,COLOR_PIT.getBlue()+1));
      else if(dayTimer % 8 == 0 && dayPhase == 1)//midday
           COLOR_PIT = new Color(Math.max(100,COLOR_PIT.getRed()-4),
                                Math.min(200,COLOR_PIT.getGreen()+1), 
                                Math.min(255,COLOR_PIT.getBlue()+2));
      else if(dayTimer % 8 == 0 && dayPhase == 3)//sunsetting
           COLOR_PIT = new Color(Math.max(70,COLOR_PIT.getRed()-2),
                                Math.max(20,COLOR_PIT.getGreen()-2), 
                                Math.max(40,COLOR_PIT.getBlue()-2));
      else if(dayTimer % 8 == 0 && dayPhase == 4)//midnight
           COLOR_PIT = new Color(Math.max(0,COLOR_PIT.getRed()-2),
                                Math.max(5,COLOR_PIT.getGreen()-2), 
                                Math.max(10,COLOR_PIT.getBlue()-2));
      dayTimer++;
      if (dayTimer == 1600)
       {
           dayPhase = (dayPhase+1)%6;
           dayTimer = 0;
       }
   }
   // Refresh the display. Called back via rapaint(), which invoke the paintComponent().
   private void gameDraw(Graphics g) {
       switch (state)
       {
            case INITIALIZED:
                break;
            case PLAYING:
                if(inSpace)
                for(Star s : starList)
                    s.draw(g);
                player.draw(g, cameraY);
                for(int a = 0; a < 2; a++)
                for(int i = blocks[0].length-1; i >= 0; i--)
                for(Block b: blocks[a][i])
                if (b != null && b.getY() + cameraY +16 >= 0 && b.getY() + cameraY <= 840)
                 b.draw(g, cameraY);
                g.setFont(new Font("Sylfaen", Font.PLAIN, 30));
                g.setColor(new Color(120,60,3));
                g.drawString("Current Height: "+ height, CANVAS_WIDTH/4, 31);
                g.drawString("Max Height: "+ maxHeight, CANVAS_WIDTH/2, 31);
                g.drawString("Stars: "+ stars, 3*CANVAS_WIDTH/4, 31);
                
                g.setColor(Color.YELLOW);
                g.drawString("Current Height: "+ height, CANVAS_WIDTH/4, 30);
                g.drawString("Max Height: "+ maxHeight, CANVAS_WIDTH/2, 30);
                g.drawString("Stars: "+ stars, 3*CANVAS_WIDTH/4, 30);
                break;
            case SCENE:
                player.draw(g, cameraY);
                for(int a = 0; a < 2; a++)
                for(int i = blocks[0].length-1; i >= 0; i--)
                for(Block b: blocks[a][i])
                if (b != null && b.getY() + cameraY +16 >= 0)
                 b.draw(g, cameraY);
                g.setColor(Color.WHITE);
                if (sceneTimer > 240)
                    g.fillOval(826,277,7,7);
                if (sceneTimer > 265)
                    g.fillOval(840,260,7,7);
                if (sceneTimer > 290)
                    g.fillOval(855,220,200,50);
                if (sceneTimer > 300)
                {
                    g.setFont(new Font("Sylfaen", Font.PLAIN, 15));
                    g.setColor(Color.BLACK);
                    g.drawString("I wonder what's up there...",870,250);
                }
                break;
            case MAIN_MENU:
                m.draw(g);
                break;
            case PAUSED:
                g.setFont(new Font("Sylfaen", Font.PLAIN, 30));
                g.setColor(new Color(2,58,89));
                g.drawString("Game Paused", CANVAS_WIDTH/2-70, 60);
                g.setColor(new Color(50,150,200));
                g.drawString("Game Paused", CANVAS_WIDTH/2-70, 62);                
                break;

       }
   }
   
   // Process a key-pressed event. Update the current state.
   public void gameKeyPressed(int keyCode) {
      switch (keyCode) {
         case KeyEvent.VK_UP:
            if (player.getJumpAvailability() && state == GameState.PLAYING){
                 if (player.midair)
                    (player).midairJumps--;
                (player).jump();
                player.setJumpAvailability(false);
                if(!moveStart && player.getY()<80 && player.getX() < 600)
                    moveStart = true;
            }
            break;
         case KeyEvent.VK_LEFT:
            leftPressed = true;
            break;
         case KeyEvent.VK_RIGHT:
            rightPressed = true;
            break;
      }
   }

      public void gameKeyReleased(int keyCode) {
      switch (keyCode) {
         case KeyEvent.VK_UP:
         if(player.midair && (player).midairJumps > 0)
         {
            player.setJumpAvailability(true);
         }
            break;
          
         case KeyEvent.VK_LEFT:
            leftPressed = false;
            break;
         case KeyEvent.VK_RIGHT:
            rightPressed = false;
            break;
         case KeyEvent.VK_P:
            if (state == GameState.PLAYING)
                state = GameState.PAUSED;
            else if (state == GameState.PAUSED){
                state = GameState.PLAYING;
            }
            break;
      }
   }
   public static void playSound(String fileName) 
   {
        try {
            File yourFile = new File(fileName);
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;
        
            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        }
        catch (Exception e) {
            System.out.println("failed to play sound: " + fileName);
        }
   }
   public static void playSongLoop(String fileName) 
   {
        try {
            File yourFile = new File(fileName);
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            songClip = (Clip) AudioSystem.getLine(info);
            songClip.open(stream);
            songClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (Exception e) {
            System.out.println("failed to play song: " + fileName);
        }
   }

   // Custom drawing panel, written as an inner class.
   class GameCanvas extends JPanel implements KeyListener{
      // Constructor
      public GameCanvas() {
         setFocusable(true);  // so that can receive key-events
         requestFocus();
         addKeyListener(this);
      }
   
      // Override paintComponent to do custom drawing.
      // Called back by repaint().
      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);   // paint background
         setBackground(COLOR_PIT);
         // Draw the game objects
         gameDraw(g);
      }
   
      // KeyEvent handlers
      @Override
      public void keyPressed(KeyEvent e) {
         if (state == GameState.PLAYING)
            gameKeyPressed(e.getKeyCode());
         else if (state == GameState.MAIN_MENU)
            m.keyPressed(e.getKeyCode());
      }
   
      @Override
      public void keyReleased(KeyEvent e) {
         if (state == GameState.PLAYING || state == GameState.PAUSED)
            gameKeyReleased(e.getKeyCode());
         else if (state == GameState.MAIN_MENU)
            m.keyReleased(e.getKeyCode());
      }
   
      @Override
      public void keyTyped(KeyEvent e) {}
   }
   
   // main
   public static void main(String[] args) {
      // Use the event dispatch thread to build the UI for thread-safety.
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain());  // main JPanel as content pane
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            
            frame.setLocationRelativeTo(null); // center the application window
            frame.setVisible(true);            // show it
         }
      });
   }
}