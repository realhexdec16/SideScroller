import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.Integer;
import java.util.Timer;
import java.util.TimerTask;

import MatterFolder.*;

public class GamePanel extends JPanel implements KeyListener {

	//you could make these private
	String[][] fileArray;
	Matter[][] matter;
	boolean isGameFinished = false;
	Player player;
	Block door;
	Timer timer = new Timer();
	private final int BLOCKSIZE = 50;
	int playerX;
	int playerY;
	Color playerColor;
	ObjectDimensions objDim = new ObjectDimensions(BLOCKSIZE);
	Camera c = new Camera(1600);

	public final int MOVESPEED = BLOCKSIZE / 10;
	public final int JUMPHEIGHT = 2 * BLOCKSIZE;
	public final int GRAVITY = 5;//gravity is for later may go somewhere else

	public GamePanel() {
		addKeyListener(this);
		setFocusable(true);
		requestFocus();
	}

	public void setLevel(int level) {
		//initGameComp();
		isGameFinished = false;
		FileRead fr = new FileRead();
		fr.setLevelArray(level);
		fileArray = fr.getLevelArray();
		matter = new Matter[fileArray.length][fileArray[0].length];
		transFileToArray();
		//fileArray will be strings
		//s = no background(sky)
		//g = ground block
		//p = player
		//d = door
        
        	//fps Thread
		timer.scheduleAtFixedRate(new TimerTask() {
            		@Override
            		public void run() {
                		applyGravity();

                		//move the character left
                		if(leftPressed){
                    			if(playerCanMoveX(-MOVESPEED)){
                        			if(c.isCameraLocked()){
                            			player.moveLateral(-MOVESPEED);
                        			} else{
                        				matter = c.moveCharLeft(matter);
                        			}
                    			}
                		}

                		//move the character right
                		if(rightPressed){
                			if(playerCanMoveX(MOVESPEED)){
                        		if(c.isCameraLocked()){
                            		player.moveLateral(MOVESPEED);
                        		} else{
                            		matter = c.moveCharRight(matter);
                        		}
                    		}
                		}
                 	
                		repaint();
            		}
        }, 1, 50);
	}

	public void paint(Graphics g) {
		//Add collision detection, camera motion, and other movement calls here.
		//Or they can be put in another method that will separately update the figures, in another thread,
		//while this method is repeatedly called by itself with or without the changes from the other update method

		//also this method/class will have to interact with the camera so that it knows what part of the map to paint
		//this panel should probably use its own camera so there should be a global Camera c = new Camera() or something
		for (int i = 0; i < matter.length; i++) {
			for (int j = 0; j < matter[0].length; j++) {
				if(matter[i][j].getColor() == Color.CYAN){
                } else if(matter[i][j].isPlayer()){
                } else{
                    g.setColor(matter[i][j].getColor());
                    g.fillRect(matter[i][j].getX(), matter[i][j].getY(), BLOCKSIZE, BLOCKSIZE);
                }
			}
		}

		g.setColor(Color.CYAN);
        g.fillRect(playerX, playerY, BLOCKSIZE, BLOCKSIZE);
        
        g.setColor(player.getColor());
        g.fillRect(player.getX(), player.getY(), BLOCKSIZE, BLOCKSIZE);

		requestFocus();
		checkForLevelFinished();
	}

	public void transFileToArray() {
		for (int i = 0; i < fileArray.length; i++) {
			for (int j = 0; j < fileArray[0].length; j++) {
				if (fileArray[i][j].equals("s")) {//sky
					matter[i][j] = new Block(j * BLOCKSIZE, i * BLOCKSIZE, Color.CYAN);
				} else if (fileArray[i][j].equals("g")) {//ground
					matter[i][j] = new Block(j * BLOCKSIZE, i * BLOCKSIZE, Color.DARK_GRAY);
				} else if (fileArray[i][j].equals("p")) { //player
					playerX = j * BLOCKSIZE;
					playerY = i * BLOCKSIZE;
					playerColor = Color.MAGENTA;
					player = new Player(playerX, playerY, playerColor);
					matter[i][j] = new Block(j * BLOCKSIZE, i * BLOCKSIZE, playerColor);
					matter[i][j].setPlayer(true);
				} else if (fileArray[i][j].equals("d")) {//door
					door = new Block(j * BLOCKSIZE, i * BLOCKSIZE, Color.RED);
                    matter[i][j] = door;
                    matter[i][j].setDoor(true);
				} else {//shouldn't get here (hopefully)
					matter[i][j] = new Block(j * BLOCKSIZE, i * BLOCKSIZE, Color.BLACK);
				}
			}
		}
	}

	public void applyGravity() {
		if (playerCanMoveY(GRAVITY)) {
			player.enteredAir(true);
            player.moveVertical(GRAVITY);
		} else {
            player.enteredAir(false);
        }
	}

	public boolean playerCanMoveX(int move) {
		for (int i = 0; i < matter.length; i++) {
			for (int j = 0; j < matter[0].length; j++) {
				if (matter[i][j].getColor() == Color.DARK_GRAY) {
					if (objDim.collisionCheck(new Player(player.getX() + move, player.getY()), matter[i][j])) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean playerCanMoveY(int move) {
		for (int i = 0; i < matter.length; i++) {
			for (int j = 0; j < matter[0].length; j++) {
				if (matter[i][j].getColor() == Color.DARK_GRAY) {
					if (objDim.collisionCheck(new Player(player.getX(), player.getY() + move), matter[i][j])) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void keyTyped(KeyEvent ke) {
	}
    
    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean downPressed = false;
    boolean spacePressed = false;

	@Override
    public void keyPressed(KeyEvent ke) {
        Integer c = ke.getKeyCode();
        if((c.equals(KeyEvent.VK_LEFT) || c.equals(KeyEvent.VK_A)) && !leftPressed){
            //left arrow key/a
            leftPressed = true;
        }
        if((c.equals(KeyEvent.VK_RIGHT) || c.equals(KeyEvent.VK_D)) && !rightPressed){
            //right arrow key/d
            rightPressed = true;
        }
        if((c.equals(KeyEvent.VK_SPACE) || c.equals(KeyEvent.VK_UP) || c.equals(KeyEvent.VK_W)) && !spacePressed){
            //space bar/up/w
            //jump
            spacePressed = true;
            if(playerCanMoveY(-JUMPHEIGHT) && (!player.isInAir() || player.canDoubleJump())){
                player.moveVertical(-JUMPHEIGHT);
                if(player.isInAir()){
                    player.setCanDoubleJump(false);
                } else{
                    player.setCanDoubleJump(true);
                }
            }
        }
        repaint();
    }

	@Override
	public void keyReleased(KeyEvent ke) {
	    switch(ke.getKeyCode()){
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_UP:
            	spacePressed = false;
            	break;
            case KeyEvent.VK_SPACE:
                spacePressed = false;
                break;
        };
    }

	public void checkForLevelFinished() {
		if(objDim.collisionCheck(player, door)){
			c.resetLevel();
            isGameFinished = true;
		}
	}

	public boolean isGameFinished() {
		return isGameFinished;
    }
}
