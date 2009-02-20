package millee.game.mechanics;
import java.util.Random;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Player {
	private String name;
	
	private Random random;
	private boolean localPlayer;
	private boolean finishedRound;
	
	// Location in cell coordinates
	protected int x;
	protected int y;
	
	// Sprite must have its own coordinates
	protected Sprite sprite;
	
	// Assigned color to collect
	private int _color;
	private int _id;
	private int physicalID;
	
	/** virtualID is determined by the server, depending on what order the client
	 * sends their initial message.  physicalID is the order that the clients join. 
	 * @param name
	 * @param avatar
	 * @param virtualID
	 * @param physicalID
	 */
	public Player(String name, Image avatar, int virtualID, int physicalID) {
		random = new Random();
		
		this._id = virtualID;
		this.name = name;
		this.localPlayer = localPlayer;
		this.finishedRound = false;
		this.physicalID = physicalID;
		
		sprite = new Sprite(avatar);
	}
	
	public int physicalID () {
		return physicalID;
	}
	
	/** Avoid using this
	public void setPosition(int inX, int inY) {
		this.x = inX;
		this.y = inY;
	}
	*/
	
//	public void interpretMove() {
//		if (!finishedRound) {
//			char command = getCommand();
//			
//			switch (command) {
//				case 'U':
//					y--;
//					sprite.move(0, -15);
//					break;
//				case 'D':
//					y++;
//					sprite.move(0, 15);
//					break;
//				case 'R':
//					x++;
//					sprite.move(15, 0);
//					break;
//				case 'L':
//					x--;
//					sprite.move(-15, 0);
//					break;
//			}
//
//			/*
//			if (command == 'U')
//				sprite.setPosition(x, y-1);
//			else if (command == 'D')
//				sprite.setPosition(x, y+1);
//			else if (command == 'R')
//				sprite.setPosition(x+1, y);
//			else if (command == 'L')
//				sprite.setPosition(x-1, y);
//				
//				*/
//		}
//	}
//	
//	private char getCommand() {
//		int index = random.nextInt(4);
//		return _possibleCommands[index];
//	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void finishedRound() {
		this.finishedRound = true;
	}
	
	// Get and set this player's assigned color
	public void setColor(int color) {
		_color = color;
	}
	
	public int assignedColor() {
		return _color;
	}
}
