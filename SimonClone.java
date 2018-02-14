import java.applet.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class SimonClone - creates a clone of the game Simon.
 * 
 * How to play: There are four colored buttons on the screen. The buttons light up in a certain 
 * random pattern, and then the player must repeat this pattern by pressing the buttons in the 
 * correct order. Each time the player successfully simulates the pattern, the pattern gets longer. 
 * The player tries to match the pattern for as long as possible.
 * 
 * @Author: Scarlet Nguyen 
 * @version 3
 * Last modified June 17, 2017
 * Applet Frame: height-500, width-690
 * 
 */
public class SimonClone extends JApplet implements KeyListener, ActionListener {
    //declare variables 
    String buttonPressed; //read and store button's action events
    ActionEvent e; 
    Random rand = new Random(); //random number generator
    
    Button red, blue, yellow, green; //create 4 color buttons on the screen
    
    AudioClip redSound, blueSound,yellowSound,greenSound; 
    AudioClip applause;
    AudioClip loseSound;
    
    Color initRed = new Color(204,0,0);         //import colors for the buttons
    Color initBlue = new Color(0,0,158);
    Color initYellow = new Color(255,208,0);
    Color initGreen = new Color(0,194,0);
    
    int level; 
    int currentStep;
    int currentScore, highScore;
    
    Boolean pressed; //to check whether "Enter" is pressed
    Boolean wait_for_Input; //to verify if the function is waiting for the user's input, which the buttons in this case
    Boolean lose; //to check if the user lost
    Boolean levelPassed; //to check if the user has passed the current level

    List<String> pattern; //an array that stores the color pattern
    List<String> reply; //an array that stores the user's input
    
    /*
     * I used the ArrayList instead of Array because it's easier to add elements into the array in ArrayList
     * ==> ArrayList is more suitable to add and store new values
     */
    public void init(){
        getContentPane().setBackground(Color.black); //set the background color to black
        
        pattern = new ArrayList<String>(); //initializes the 
        reply = new ArrayList<String>();   // arrays to null
        
        level = 1; //initializes the first level    
        currentStep = 0; //everything is zero 
        currentScore = 0; //because nothing has 
        highScore = 0; //moved or scored yet 
        
        lose = false;
        wait_for_Input = false;
        pressed = false;
        levelPassed = false;
        
        //Initialize the buttons        
        red = new Button("RED");
        setLayout(null);
        red.setBounds(25,25,200,200);
        red.setBackground(initRed); //the Foreground and Background is the same color
        red.setForeground(initRed); //so user won't see the text inside the button
        red.addActionListener(this);
        add(red);
        
        blue = new Button("BLUE");
        setLayout(null);
        blue.setBounds(250,25,200,200);
        blue.setBackground(initBlue);
        blue.setForeground(initBlue);
        blue.addActionListener(this);
        add(blue);
        
        yellow = new Button("YELLOW");
        setLayout(null);
        yellow.setBounds(25,250,200,200);
        yellow.setBackground(initYellow);
        yellow.setForeground(initYellow);
        yellow.addActionListener(this);
        add(yellow);
        
        green = new Button("GREEN");
        setLayout(null);
        green.setBounds(250,250,200,200);
        green.setBackground(initGreen);
        green.setForeground(initGreen);
        green.addActionListener(this);
        add(green);
        
        //Import sounds
        redSound = getAudioClip(getCodeBase(),"sounds/sound1.wav");
        blueSound = getAudioClip(getCodeBase(),"sounds/sound2.wav");
        yellowSound = getAudioClip(getCodeBase(),"sounds/sound3.wav");
        greenSound = getAudioClip(getCodeBase(),"sounds/sound4.wav");
        applause = getAudioClip(getCodeBase(),"sounds/applause.wav");
        loseSound = getAudioClip(getCodeBase(),"sounds/lose.wav");

        addKeyListener(this);
        requestFocus();
    }


    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5)); //set thickness of a line      
        g.setColor(Color.white);
        g.setFont(new Font("Calisto MT", Font.BOLD, 18)); //set text's font and size
        g.drawString("  Score: " + currentScore, 540, 90); 
        g.drawString("Level " + level, 550, 140);
        g2.drawRect(540,110,80,50);  
        g.drawString("      PRESS \"ENTER\"", 480, 230);
        g.drawString("TO START / RESTART", 480, 255);
        g.drawString("High Score: " + highScore, 525, 360);
        requestFocus();
    }


    public void keyPressed(KeyEvent event)  { 
        int k;
        k = event.getKeyCode();
        if (k == 10 && pressed == false) /*Enter Button*/ {                                    
            red.setEnabled(true); //Enable the buttons
            blue.setEnabled(true); //These lines are for 
            yellow.setEnabled(true); //later purpose, which
            green.setEnabled(true); //is restarting
            
            level = 1; //always set the level to 1 everytime the user starts/restarts
            repaint(); //repaint the 'level' faster (after restart)
            lose = false;
            currentStep = 0;
            pattern = new ArrayList<String>(); //set both of the arrays
            reply = new ArrayList<String>(); //to null
            pressed = true; //so the user won't be able to re-press the "Enter" until they lose
            wait_for_Input = false; //because the pattern hasn't played yet
            levelPassed = false; //because the user hasn't played
            currentScore = 0; //set the score to 0
            //thread.sleep - makes the user wait 1 second before running into the main game loop
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            } 
            mainGameLoop(); //runs the game
        }
        repaint();
        requestFocus();
    }

    public void keyReleased(KeyEvent event) {

    }

    public void keyTyped(KeyEvent event) {

    }

    public void actionPerformed(ActionEvent e) {
        buttonPressed=e.getActionCommand(); 
        if (buttonPressed == "RED") {
            redSound.play();
            if (wait_for_Input == true) /*if the button is pressed after the pattern is played*/{
                reply.add("RED"); //add button's color into the ArrayList reply
                if (reply.get(currentStep) == pattern.get(currentStep)) /*compare the same position of 2 ArrayLists 
                                                                          to see if they are the same*/{
                    currentStep++; //if the user's input match the pattern so far:
                    currentScore++; //increases the current step of the user and the score
                    repaint();
                } else {
                    lose = true; //else the user loses
                }
            }
        } else if (buttonPressed == "BLUE") {
            blueSound.play();    
            if (wait_for_Input == true) {
                reply.add("BLUE");
                if (reply.get(currentStep) == pattern.get(currentStep)) {
                    currentStep++;
                    currentScore++;
                    repaint();
                } else {
                    lose = true;
                }
            }
        } else if (buttonPressed == "YELLOW") {
            yellowSound.play();
            if (wait_for_Input == true) {
                reply.add("YELLOW");
                if (reply.get(currentStep) == pattern.get(currentStep)) {
                    currentStep++;
                    currentScore++;
                    repaint();
                } else {
                    lose = true;
                }
            }
        } else if (buttonPressed == "GREEN") {
            greenSound.play();
            if (wait_for_Input == true) {
                reply.add("GREEN");
                if (reply.get(currentStep) == pattern.get(currentStep)) {
                    currentStep++;
                    currentScore++;
                    repaint();
                } else {
                    lose = true;
                }
            }
        }       
        
        if (wait_for_Input == true && reply.size() == pattern.size() && lose != true) {
            //when the user's repeated correctly the pattern: increases the level and run the mainGameLoop again
            level++;
            wait_for_Input = false;
            levelPassed = true;
            try {
                Thread.sleep(1700);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            mainGameLoop();
        }
        
        if (lose == true) {
            levelPassed = false;
            try { //wait until the playing sound is finished
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            loseSound.play(); //play lose sound
            if (currentScore > highScore) {
                highScore = currentScore;
                try { //wait until the lose sound is finished
                    Thread.sleep(1000);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                applause.play(); //play applause sound
            }
            red.setEnabled(false); //disable the buttons
            blue.setEnabled(false); //so the user can't
            yellow.setEnabled(false); //press them until he/she
            green.setEnabled(false); //presses "Enter" to restart the game
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            pressed = false; //enable the "enter" button so the user can restart
        }
        repaint();
        requestFocus();
    }
    
    public void mainGameLoop() {
        playSound(level); //play the patterns
        wait_for_Input = true; //the game is now waiting for the user for inputs
        actionPerformed(e); //record user's input
    }
    
    /*
     * This method plays the sound patterns in the game
     */
    public void playSound(int level) {
        for (int a = 1; a <= level * 2; a++) {
            int n = rand.nextInt(4) + 1; //n will choose random values between 1 and 4
            if (n == 1) {
                redSound.play();
                //light up the button in 0.8 seconds during the sound is played
                red.setBackground(Color.red);
                red.setForeground(Color.red);
                try {
                    Thread.sleep(800);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                //and then return to its initial color
                red.setBackground(initRed);
                red.setForeground(initRed);
                //add new element into the ArrayList pattern, in this case, red
                pattern.add("RED");
            } else if (n == 2) {
                blueSound.play();
                blue.setBackground(Color.blue);
                blue.setForeground(Color.blue);
                try {
                    Thread.sleep(800);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                blue.setBackground(initBlue);
                blue.setForeground(initBlue);
                pattern.add("BLUE");
            } else if (n == 3) {
                yellowSound.play();
                yellow.setBackground(Color.yellow);
                yellow.setForeground(Color.yellow);
                try {
                    Thread.sleep(800);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                yellow.setBackground(initYellow);
                yellow.setForeground(initYellow);
                pattern.add("YELLOW");
            } else if (n == 4) {
                greenSound.play();
                green.setBackground(Color.green);
                green.setForeground(Color.green);
                try {
                    Thread.sleep(800);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                green.setBackground(initGreen);
                green.setForeground(initGreen);
                pattern.add("GREEN");
            }
            //give a little break between the sounds
            try {
                    Thread.sleep(200);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
        }
    }
}
