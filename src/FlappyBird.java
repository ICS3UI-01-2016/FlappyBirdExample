
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author lamon
 */
public class FlappyBird extends JComponent implements KeyListener {

    // Height and Width of our game
    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    // sets the framerate and delay for our game
    // you just need to select an approproate framerate
    long desiredFPS = 60;
    long desiredTime = (1000) / desiredFPS;

    // game variables
    Color skyColour = new Color(116, 211, 242);
    Rectangle bird = new Rectangle(100, 200, 50, 50);
    int gravity = 1;
    int dy = 0;
    int jumpVelocity = -12;
    BufferedImage birdPic = loadImage("bird.png");
    BufferedImage background = loadImage("bg.png");
    BufferedImage topTubePic = loadImage("toptube.png");
    BufferedImage bottomTubePic = loadImage("bottomtube.png");

    // jump key variable
    boolean jump = false;
    boolean lastJump = false;

    // wait to start
    boolean start = false;
    boolean dead = false;

    Rectangle[] topPipes = new Rectangle[5];
    Rectangle[] bottomPipes = new Rectangle[5];
    boolean[] passedPipe = new boolean[5];
    
    int score = 0;
    Font scoreFont = new Font("Arial", Font.BOLD, 42);
    
    // the gap between top and bottom
    int pipeGap = 200;
    // distance between the pipes
    int pipeSpacing = 200;
    // the width of a single pipe
    int pipeWidth = 100;
    // the height of a pipe
    int pipeHeight = HEIGHT - 50;
    // minimum distance from edge
    int minDistance = 200;

    // speed of the game
    int speed = 3;

    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    @Override
    public void paintComponent(Graphics g) {
        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);

        // change to colour the sky
        g.setColor(skyColour);
        // draw the sky background
        //g.fillRect(0, 0, WIDTH, HEIGHT);
        g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
                
        // draw the pipes
        g.setColor(Color.GREEN);
        for (int i = 0; i < topPipes.length; i++) {
            //g.fillRect(topPipes[i].x, topPipes[i].y, topPipes[i].width, topPipes[i].height);
            g.drawImage(topTubePic, topPipes[i].x, topPipes[i].y, topPipes[i].width, topPipes[i].height, null);
            //g.fillRect(bottomPipes[i].x, bottomPipes[i].y, bottomPipes[i].width, bottomPipes[i].height);
            g.drawImage(bottomTubePic, bottomPipes[i].x, bottomPipes[i].y, bottomPipes[i].width, bottomPipes[i].height, null);
        }

        // draw the bird
        g.setColor(Color.YELLOW);
        
        g.drawImage(birdPic, bird.x, bird.y, bird.width, bird.height, null);
        //g.drawRect(bird.x, bird.y, bird.width, bird.height);
        g.setColor(Color.WHITE);
        g.setFont(scoreFont);
        g.drawString("" + score, WIDTH/2, 50);

        // GAME DRAWING ENDS HERE
    }

    public BufferedImage loadImage(String filename){
        BufferedImage img = null;
        try{
            File file = new File(filename);
            img = ImageIO.read(file);
        }catch(Exception e){
            // if there is an error, print it
            e.printStackTrace();
        }
        return img;
    }
    
    
    public void reset() {
        // set up the pipes
        score = 0;
        
        int pipeX = 600;
        Random randGen = new Random();
        for (int i = 0; i < topPipes.length; i++) {
            // generating a random y position
            int pipeY = randGen.nextInt(HEIGHT - 2 * minDistance) + minDistance;
            bottomPipes[i] = new Rectangle(pipeX, pipeY, pipeWidth, pipeHeight);
            topPipes[i] = new Rectangle(pipeX, pipeY - pipeGap - pipeHeight, pipeWidth, pipeHeight);
            // move the pipeX value over
            pipeX = pipeX + pipeWidth + pipeSpacing;
            passedPipe[i] = false;
        }

        // resetthe bird
        bird.y = 200;
        dy = 0;
        start = false;
        dead = false;
    }

    public void setPipe(int pipePosition) {
        // a random number generator
        Random randGen = new Random();
        // generate a random Y position
        int pipeY = randGen.nextInt(HEIGHT - 2 * minDistance) + minDistance;
        // generate the new pipe X coordinate
        int pipeX = topPipes[pipePosition].x;
        pipeX = pipeX + (pipeWidth + pipeSpacing) * topPipes.length;

        bottomPipes[pipePosition].setBounds(pipeX, pipeY, pipeWidth, pipeHeight);
        topPipes[pipePosition].setBounds(pipeX, pipeY - pipeGap - pipeHeight, pipeWidth, pipeHeight);
        
        passedPipe[pipePosition] = false;
    }

    // The main game loop
    // In here is where all the logic for my game will go
    public void run() {
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime;

        // set up the pipes
        int pipeX = 600;
        Random randGen = new Random();
        for (int i = 0; i < topPipes.length; i++) {
            // generating a random y position
            int pipeY = randGen.nextInt(HEIGHT - 2 * minDistance) + minDistance;
            bottomPipes[i] = new Rectangle(pipeX, pipeY, pipeWidth, pipeHeight);
            topPipes[i] = new Rectangle(pipeX, pipeY - pipeGap - pipeHeight, pipeWidth, pipeHeight);
            // move the pipeX value over
            pipeX = pipeX + pipeWidth + pipeSpacing;
            passedPipe[i] = false;
        }

        // the main game loop section
        // game will end if you set done = false;
        boolean done = false;
        while (!done) {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();

            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 
            if (start) {
                // get the pipes moving
                if (!dead) {
                    for (int i = 0; i < topPipes.length; i++) {
                        topPipes[i].x = topPipes[i].x - speed;
                        bottomPipes[i].x = bottomPipes[i].x - speed;
                        // check if a pipe is off the screen
                        if (topPipes[i].x + pipeWidth < 0) {
                            // move the pipe
                            setPipe(i);
                        }
                    }
                }
                
                // see if we passed a pipe
                for(int i = 0; i < topPipes.length; i++){
                    if(!passedPipe[i] && bird.x > topPipes[i].x + pipeWidth){
                        score++;
                        passedPipe[i] = true;
                    }
                }

                // get the bird to fall
                // apply gravity
                dy = dy + gravity;
                // make the bird fly
                if (jump && !lastJump && !dead) {
                    dy = jumpVelocity;
                }
                lastJump = jump;

                // apply the change in y to the bird
                bird.y = bird.y + dy;

                // check if bird hits top or bottom of screen
                if (bird.y < 0) {
                    bird.y = 0;
                    dead = true;
                } else if (bird.y + bird.height > HEIGHT) {
                    dead = true;
                    bird.y = HEIGHT - bird.height;
                    reset();
                }

                // did the bird hit a pipe?
                // go through all the pipes
                for (int i = 0; i < topPipes.length; i++) {
                    // did the bird hit one of the top pipes
                    if (bird.intersects(topPipes[i])) {
                        dead = true;
                        // did the bird hit a bottom pipe
                    } else if (bird.intersects(bottomPipes[i])) {
                        dead = true;
                    }
                }
            }

            // GAME LOGIC ENDS HERE 
            // update the drawing (calls paintComponent)
            repaint();

            // SLOWS DOWN THE GAME BASED ON THE FRAMERATE ABOVE
            // USING SOME SIMPLE MATH
            deltaTime = System.currentTimeMillis() - startTime;
            try {
                if (deltaTime > desiredTime) {
                    //took too much time, don't wait
                    Thread.sleep(1);
                } else {
                    // sleep to make up the extra time
                    Thread.sleep(desiredTime - deltaTime);
                }
            } catch (Exception e) {
            };
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates a windows to show my game
        JFrame frame = new JFrame("My Game");

        // creates an instance of my game
        FlappyBird game = new FlappyBird();
        // sets the size of my game
        game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // adds the game to the window
        frame.add(game);

        // add the key listener 
        frame.addKeyListener(game);

        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);

        // starts my game loop
        game.run();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            jump = true;
            start = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            jump = false;
        }
    }
}
