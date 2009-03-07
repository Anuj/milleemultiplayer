package millee.game.mechanics;
import java.util.Hashtable;
import java.util.Stack;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import millee.game.initialize.Utilities;


public class Player {
	private String _name;
	
	// Location in cell coordinates
	protected int x;
	protected int y;
	
	// Sprite must have its own coordinates
	protected Sprite sprite;
	
	// Assigned color to collect -- TODO: Are these related?
	private int _assignedColor;
	private int _id;
	
	// Player keeps track of his own score & goodies collected
	private int _score = 0;
	private Stack _goodies = new Stack();
	
	// Player sprite color variations
	public static final int BLACK = 1;
	public static final int RED = 2;
	public static final int GREEN = 3;
	public static final int BLUE = 4;
	
	private static Hashtable avatarUsageCounts = new Hashtable();
	
	/** virtualID is determined by the server, depending on what order the client
	 * sends their initial message.  physicalID is the order that the clients join. 
	 * @param name
	 * @param avatar
	 * @param virtualID
	 */
	public Player(String name, String imgPath, int virtualID) { //, int physicalID) {
		
		this._name = name;
		this._id = virtualID;
		
		// The color that this character must collect is its ID+1
		this._assignedColor = virtualID+1;
		
		Image avatar = Utilities.createImage(imgPath);
		
		// Keep track of avatar usage counts
		int nUsage = 1; // Default
		if (avatarUsageCounts.containsKey(imgPath)) {
			// Get and increment value
			nUsage = ((Integer) avatarUsageCounts.get(imgPath)).intValue();
			nUsage++;
		}
		avatarUsageCounts.put(imgPath, new Integer(nUsage));

		// Alter color if necessary
		//if (nUsage > 1) {
		//	sprite = new Sprite(applyVariation(avatar,nUsage));
		//}
		//else {
			sprite = new Sprite(avatar);
		//}
	}
	
	private Image applyVariation(Image img, int var) {
		// Pull out ARGB data
		int[] rgbData = new int[img.getWidth()*img.getHeight()];
		img.getRGB(rgbData, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
		
		// Affect the ARGB data according to the variation type
		switch (var) {
		
		// TODO: Verify color change works; otherwise, just use preset images
		
		case BLUE:
			for (int i = 0; i < rgbData.length; i++) {
				//rgbData[i] = (30 + (rgbData[i] & 0xFF)) | (rgbData[i] & 0xFFFFFF00);
				rgbData[i] = rgbData[i] & 0xFF0000FF;
			}
			break;
			
		case GREEN:
			for (int i = 0; i < rgbData.length; i++) {
				//rgbData[i] = (30 + (rgbData[i] & 0xFF)) | (rgbData[i] & 0xFFFFFF00);
				rgbData[i] = rgbData[i] & 0xFF00FF00;
			}
			break;
			
		case RED:
			for (int i = 0; i < rgbData.length; i++) {
				//rgbData[i] = (30 + (rgbData[i] & 0xFF)) | (rgbData[i] & 0xFFFFFF00);
				rgbData[i] = rgbData[i] & 0xFFFF0000;
			}
			break;
		}
		
        /** Alternative: Replace only a particular color in the original avatar (like costume changing)
        for (int j = 0; j < maskedOldColor.length; j++) {
            if (currentMaskedPixel == maskedOldColor[j]) {
                raw[i] = (currentPixel & alphaOnlyBitmask) | maskedNewColor[j];
                break;
            }
        }
		**/
		
		return Image.createRGBImage(rgbData, img.getWidth(), img.getHeight(), true);
	}

	public int getID() {
		return _id;
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
	
	// Get and set this player's assigned color
	public void setColor(int color) {
		_assignedColor = color;
	}
	
	public int assignedColor() {
		return _assignedColor;
	}
	
	// Update the player's score in game-like ways
	private void incrementScore() {
		_score += 10;
	}
	
	private void decrementScore() {
		_score -= 10;
	}
	
	public int getScore() {
		return _score;
	}
	
	public String getName() {
		return _name;
	}
	
	public void collect(Goodie g) {
		_goodies.push(g);
		if (g.getType() == this._assignedColor) { this.incrementScore(); }
		else { this.decrementScore(); }
	}
	
	public Goodie dropGoodie() {
		if (_goodies.isEmpty()) { return null; }
		return (Goodie) _goodies.pop();
	}
}
