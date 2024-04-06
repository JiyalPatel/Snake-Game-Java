import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_HEIGHT * SCREEN_WIDTH) / UNIT_SIZE;
    static final int FRAME_DELAY = 85;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 3;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean gameOver = false;
    boolean gameStarted = false;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.addKeyListener(new MyKeyAdapter());
    }

    public void gameStarted(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(30F));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Enter Space Bar to Begin Game", (SCREEN_WIDTH - metrics.stringWidth("Enter Space Bar to Begin Game"))/2, SCREEN_HEIGHT/2);
    }

    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(FRAME_DELAY, this);
        timer.start();
    }

    public void restartGame(){
        timer.stop();

        // Reset game variables
        bodyParts = 3;
        applesEaten = 0;
        direction = 'R';

        // Reset snake position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        newApple();
        running = true;

        timer.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if (!gameStarted){
            gameStarted(g);
        } else{
            if (running){

                // to show game units and background in grid
                for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
                    g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                    g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
                }

                g.setColor(Color.RED);
                g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);

                for (int i = 0; i < bodyParts; i++) {
                    if (i == 0){
                        g.setColor(Color.green);
                    } else{
                         g.setColor(new Color(45, 180, 0)); //single color - light green
//                       g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255))); //RGB Snake
                    }
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }

                //displays Score
                g.setColor(Color.red);
                g.setFont(new Font("Ink Free", Font.BOLD, 40));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Score : " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score : " + applesEaten))/2, UNIT_SIZE*2);

            } else {
                gameOver(g);
            }
        }

    }

    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move(){
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple(){
        if ((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){

        //checks for the collision with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }

        //checks for collision with border
        //leftBorder
        if (x[0] < 0){
            running = false;
        }
        //rightBorder
        if (x[0] > SCREEN_WIDTH){
            running = false;
        }
        //topBorder
        if (y[0] < 0){
            running = false;
        }
        //bottomBorder
        if (y[0] > SCREEN_HEIGHT){
            running = false;
        }

        //stop the game
        if (!running){
            timer.stop();
        }

    }

    public void gameOver(Graphics g){

        gameOver = true;

        //display game over
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

        //displays Score
        if (applesEaten <= 5){
            g.setColor(Color.red);
        } else {
            g.setColor(Color.green);
        }
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score : " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score : " + applesEaten))/2, SCREEN_HEIGHT - UNIT_SIZE * 10);


        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(30F));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Press enter to try again...", (SCREEN_WIDTH - metrics2.stringWidth("Press enter to try again..."))/2, SCREEN_HEIGHT - UNIT_SIZE * 3);

    }


    @Override
    public void actionPerformed(ActionEvent e){
        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent key){
            switch (key.getKeyCode()){

                //left arrow & A
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (direction != 'R'){
                        direction = 'L';
                    }
                    break;
                //right arrow & D
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (direction != 'L'){
                        direction = 'R';
                    }
                    break;
                //top arrow & W
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (direction != 'D'){
                        direction = 'U';
                    }
                    break;
                //down arrow & S
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if (direction != 'U'){
                        direction = 'D';
                    }
                    break;
                //Pause
                case KeyEvent.VK_ESCAPE:
                    if (running){
                        running = false;
                        timer.stop();
                    } else {
                        running = true;
                        timer.start();
                    }
                    break;
                //Restart
                case KeyEvent.VK_ENTER:
                    if (gameOver) {
                        gameOver = false;
                        restartGame();
                    }
                    break;
                //StartGame
                case KeyEvent.VK_SPACE:
                    if (!gameStarted) {
                        gameStarted = true;
                        startGame();
                    }
                    break;
            }
        }
    }

}
