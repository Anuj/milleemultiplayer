package millee.game.mechanics;
import java.util.Hashtable;

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
	private GoodieStack _goodies;
	
	// Group Score is a total of all the players scores.
	private static int _groupScore = 0;
	
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
	public Player(String name, String imgPath, int virtualID, boolean isLocal) { //, int physicalID) {
		
		this._name = name;
		this._id = virtualID;
		
		// The color that this character must collect is its ID+1
		this._assignedColor = virtualID+1;
		//_goodies = new GoodieStack(_assignedColor);
		
		Image avatar = null;
		
		// Avatars are now hardcoded based on the virtualID
		/*if (isLocal) {
			avatar = Utilities.createImage(imgPath);
		}
		else {
			// Ignore the chosen avatar...
			//avatar = Utilities.createImage(Utilities.DEFAULT_IMAGE);
			avatar = Utilities.createImage(imgPath);
		}*/
		
		String myImagePath = "/dancer_1.png";
		
		if (virtualID == 0) {
			avatar = Utilities.createImage("/dancer_1.png");
			myImagePath = "/dancer_1.png";
		} else if (virtualID == 1) {
			avatar = Utilities.createImage("/dancer_2.png");
			myImagePath = "/dancer_2.png";
		} else if (virtualID == 2) {
			avatar = Utilities.createImage("/dancer_3.png");
			myImagePath = "/dancer_3.png";
		} else if (virtualID == 3) {
			avatar = Utilities.createImage("/dancer_1.png");
			myImagePath = "/dancer_1.png";
		}
		
		// Keep track of avatar usage counts
		int nUsage = 1; // Default
		if (avatarUsageCounts.containsKey(myImagePath)) {
			// Get and increment value
			nUsage = ((Integer) avatarUsageCounts.get(myImagePath)).intValue();
			nUsage++;
		}
		avatarUsageCounts.put(myImagePath, new Integer(nUsage));

		// Alter color if necessary
		//if (nUsage > 1) {
		//	sprite = new Sprite(applyVariation(avatar,nUsage));
		//}
		//else {
			sprite = new Sprite(avatar);
		//}
	}
	
	public static int getGroupScore() {
		return _groupScore;
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
	
	// Get and set this player's assigned color and refresh their GoodieStack
	public void setColor(int color) {
		_assignedColor = color;
		_goodies = new GoodieStack(_assignedColor);
	}
	
	public int assignedColor() {
		return _assignedColor;
	}
	
	// Update the player's score in game-like ways
	private void incrementScore() {
		_score += 10;
		_groupScore += 10;
	}
	
	private void decrementScore() {
		_score -= 10;
		_groupScore -= 10;
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
		Goodie g = (Goodie) _goodies.pop();
		if (g.getType() == this._assignedColor) { this.decrementScore(); }
		return g;
	}
	
	public GoodieStack getGoodieStack() {
		return _goodies;
	}
	/*
	public void flushGoodieStack() {
		_goodies = new GoodieStack(_assignedColor);
	}
	*/
	public boolean hasCorrectGoodies() {
		Goodie g = null;
		for (int i = 0; i < _goodies.size(); i++) {
			g = (Goodie) _goodies.elementAt(i);
			if (g.getType() != this._assignedColor) { return false; }
		}
		
		return true;
	}
}
