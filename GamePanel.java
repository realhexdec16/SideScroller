import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import MatterFolder.*;

public class GamePanel extends JPanel implements KeyListener {

	String[][] fileArray;
	Matter[][] matter;
	
	Player player;

	//this could maybe be used to keep track of which one of the matters
	//  contains the actual player. there might be a better way to
	//  do this though.
	
	//if a key is being held down or not
	boolean pressed;

	public GamePanel() {

	}

	public void setLevel(int level) {
        	FileRead fr = new FileRead(level);
		fileArray = fr.getLevelArray();
		matter = new Matter[fileArray.length][fileArray[0].length];
		transFileToArray();
        	addKeyListener(this);
		//fileArray will be strings
		//s = no background(sky)
		//g = ground block
		//p = player
	}


	public void paint(Graphics g) {
		//Add collision detection, camera motion, and other movement calls here.
		//Or they can be put in another method that will separately update the figures, in another thread,
		//while this method is repeatedly called by itself with or without the changes from the other update method

		//also this method/class will have to interact with the camera so that it knows what part of the map to paint
		//this panel should probably use its own camera so there should be a global Camera c = new Camera() or something

		//draws everything onto the screen
		for (int i = 0; i < matter.length; i++) {
			for (int j = 0; j < matter[0].length; j++) {
				//we're gonna have to paint the square icon wherever and get its location,
				//which should all be in the class so I think it'd be something like:
				// g.drawImage(matter[i][j].getImage(), matter[i][j].getX(), matter[i][j].getY(), null);
				g.setColor(matter[i][j].getColor());
				g.fillRect(matter[i][j].getX(), matter[i][j].getY(), 100, 100);
			}
		}
		
		g.setColor(Color.MAGENTA);
		g.fillRect(player.getX(),player.getY(),100,100);
	}

	public void transFileToArray() {
		int playerX; //only need this for player's original position
		int playerY;
		for (int i = 0; i < fileArray.length; i++) {
			for (int j = 0; j < fileArray[0].length; j++) {
				if (fileArray[i][j].equals("s")) {//sky
					matter[i][j] = new Block(j * 100, i * 100, Color.CYAN); //multiply by 100 since each block is 100 x 100				} else if (fileArray[i][j].equals("g")) {//ground
					matter[i][j] = new Block(j * 100, i * 100, Color.DARK_GRAY);
				} else if (fileArray[i][j].equals("p")){//player
					playerX = j * 100;
					playerY = i * 100;
					matter[i][j] = new Block(j * 100, i * 100, Color.CYAN); //we'll have the player drawn sepperatly 
				} else{
					matter[i][j] = new Block(j * 100, i * 100 , Color.BLACK);
				}
			}
		}
		player = new Player(playerX,playerY);
	}

	/*
	* direction: 
	* 0 = right
	* 1 = left
	* 2 = up
	* releasing key sets pressed to false which should stop this
	*/
    	//pr0ves
    	//you could use the method from the Player class directly to move the character and delete this one


	/*
	don't actually know if any of this key stuff will work because I don't think
	it accounts for a key being held down. we need to figure out how to make it
	work if they are holding down the key
	*/
	
	/*
	we do need to figure out how to handle holding down
	multiple keys though as well
	*/
	//keep this lowercase bc its not a real class
	@Override
	public void keyTyped(KeyEvent ke) {
		//could maybe use for something like opening the pause menu or something
	}
	
	@Override
	public void keyPressed(KeyEvent ke) { //this should work if the code runs through here each time it sees key is pressed
					      //If not we'll get fancy	
		if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
            		//left arrow key
			player.moveLeft();
		} else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
			//right arrow key
			player.moveRight();
		} else if (ke.getKeyCode() == KeyEvent.VK_UP) {
			//up arrow key
			player.moveUp();
		} else if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
			//space bar
			//also jump?
			player.moveUp();
		} else {
			//not supported yet
		}
	}

	@Override
	public void keyReleased(KeyEvent ke) {
	    //stop movement
	}
}
