package millee.game.mechanics;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import millee.game.ApplicationMain;
import millee.game.initialize.Utilities;


public class Player {
	private String _name;
	private String _imagePath;
	
	// Location in cell coordinates
	protected int x;
	protected int y;
	
	// Sprite must have its own coordinates
	protected Sprite sprite;
	private Sprite _originalSprite;
	private Sprite _alternateSprite;
	private boolean _hasCorrectGoodies = true;
	
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
	
	private static Vector avatars = null;
	
	//private static Hashtable avatarUsageCounts = new Hashtable();
	
	/** virtualID is determined by the server, depending on what order the client
	 * sends their initial message.  physicalID is the order that the clients join. 
	 * @param name
	 * @param avatar
	 * @param virtualID
	 */
	public Player(String name, String imgPath, int virtualID, boolean isLocal) { //, int physicalID) {
		
		this._name = name;
		this._id = virtualID;
		this._imagePath = "/dancer_0.png";
		
		// The color that this character must collect is its ID+1
		this._assignedColor = virtualID+1;

		/** Avatar images handled in setColor();
		Image avatar = null;
		
		// Avatars are now hardcoded based on the virtualID
		if (isLocal) {
			avatar = Utilities.createImage(imgPath);
		}
		else {
			// Ignore the chosen avatar...
			//avatar = Utilities.createImage(Utilities.DEFAULT_IMAGE);
			avatar = Utilities.createImage(imgPath);
		}
		*/
		
		initializeAvatars();
		
		/*
		_imagePath = "/dancer_0.png";
		if (virtualID == 0) {
			avatar = Utilities.createImage("/dancer_0.png");
			_imagePath = "/dancer_0.png";
		} else if (virtualID == 1) {
			avatar = Utilities.createImage("/tiger_avatar.png");
			_imagePath = "/tiger_avatar.png";
		} else if (virtualID == 2) {
			avatar = Utilities.createImage("/bird_avatar.png");
			_imagePath = "/bird_avatar.png";
		} else if (virtualID == 3) {
			avatar = Utilities.createImage("/panda_avatar.png");
			_imagePath = "/panda_avatar.png";
		}

		// Keep track of avatar usage counts
		int nUsage = 1; // Default
		if (avatarUsageCounts.containsKey(_imagePath)) {
			// Get and increment value
			nUsage = ((Integer) avatarUsageCounts.get(_imagePath)).intValue();
			nUsage++;
		}
		avatarUsageCounts.put(_imagePath, new Integer(nUsage));

		// Alter color if necessary
		//if (nUsage > 1) {
		//	sprite = new Sprite(applyVariation(avatar,nUsage));
		//}
		//else {
			sprite = _originalSprite = new Sprite((Image) avatars.elementAt(0));
			_alternateSprite = new Sprite(applyVariation(avatar,RED));
		//}
		*/
	}
	
	private void initializeAvatars() {
		if (avatars != null) { return; }
		
		avatars = new Vector();
		avatars.addElement(Utilities.createImage("/dancer_0.png"));
		avatars.addElement(Utilities.createImage("/dancer_1.png"));
		avatars.addElement(Utilities.createImage("/dancer_2.png"));
		avatars.addElement(Utilities.createImage("/dancer_3.png"));
	}
	
	public static int getGroupScore() {
		return _groupScore;
	}
	
	public String getImagePath() {
		return _imagePath;
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
		System.out.println("color = " + color);
		
		Image avatar = (Image) avatars.elementAt(color-1);
		sprite = _originalSprite = new Sprite(avatar);
		_alternateSprite = new Sprite(this.applyVariation(avatar, RED));
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
		if (g.getType() == this._assignedColor) {
			this.incrementScore();
			ApplicationMain.log.info("Player " + _id + " collected CORRECT goodie at (" + g.x + "," + g.y + "): " + g.getType());
		}
		else {
			this.decrementScore();
			this.setAltAvatar();
			this._hasCorrectGoodies = false;
			ApplicationMain.log.info("Player " + _id + " collected WRONG goodie at (" + g.x + "," + g.y + "): " + g.getType());
		}
	}
	
	public Goodie dropGoodie() {
		if (_goodies.isEmpty()) {
			ApplicationMain.log.info("Player " + _id + " tried to drop but he had no goodies.");
			return null;
		}
		Goodie g = (Goodie) _goodies.pop();
		
		if (g.getType() == this._assignedColor) {
			this.decrementScore();
			ApplicationMain.log.info("Player " + _id + " dropped a CORRECT goodie: " + g.getType());
		}
		else {
			ApplicationMain.log.info("Player " + _id + " dropped a WRONG goodie: " + g.getType());
			if (this.hasCorrectGoodies()) {
				this.setOrigAvatar();
				this._hasCorrectGoodies = true;
			}
		}
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
	
	private void flipAvatar() {
		if (this.sprite == _originalSprite) {
			setAltAvatar();
		}
		else if (this.sprite == _alternateSprite) {
			setOrigAvatar();
		}
	}
	
	private void setAltAvatar() {
		_originalSprite.setVisible(false);
		_alternateSprite.setVisible(true);
		
		_alternateSprite.setPosition(_originalSprite.getX(), _originalSprite.getY());
		this.sprite = _alternateSprite;
	}
	
	private void setOrigAvatar() {
		_alternateSprite.setVisible(false);
		_originalSprite.setVisible(true);
		
		_originalSprite.setPosition(_alternateSprite.getX(), _alternateSprite.getY());
		this.sprite = _originalSprite;
	}
	
	public void redraw(Graphics g) {
		// Flash (flipAvatar()) if player is holding someone else's goodie?
		if (!_hasCorrectGoodies) { flipAvatar(); }
		
		this.sprite.paint(g);
	}
}
