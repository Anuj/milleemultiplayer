package millee.game.mechanics;
import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

import millee.game.ApplicationMain;
import millee.game.initialize.Utilities;


public class Player {
	// Personalized information
	private String _name;
	//private String _imagePath;
	
	// Location in cell coordinates
	protected int x;
	protected int y;
	
	// Player sprites/avatar variables
	private Sprite sprite;
	private Sprite _originalSprite;
	private Sprite _alternateSprite;
	private static Vector avatars = null;
	//private static Hashtable avatarUsageCounts = new Hashtable();
	
	// State variables
	private int _targetNumGoodies = 2;
	protected boolean isInGoodStanding = true;
	protected boolean isFinished = false;
	
	// Assigned color to collect -- Currently related to ID
	private int _assignedColor;
	private int _id;
	
	// Player keeps track of his own score & goodies collected
	private int _score = 0;
	protected int timeFinished = Integer.MAX_VALUE; 
	protected GoodieStack goodies;
	
	// Group Score is a total of all the players scores.
	public static int groupScore = 0;
	
	// Constants
	private static final int SCORE_INCREMENT = 10;
	
	/**
	 * virtualID is determined by the server, depending on what order the client
	 * sends their initial message. PhysicalID is the order that the clients join. 
	 * @param name personalized name
	 * @param imgPath avatar image
	 * @param virtualID
	 */
	public Player(String name, String imgPath, int virtualID) {
		this._name = name;
		//this._imagePath = imgPath;
		this._id = virtualID;
		
		// The color that this character must collect is its ID+1
		this._assignedColor = virtualID+1;
		
		initializeAvatars();

		/* Avatar images handled elsewhere...
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
			sprite = new Sprite(applyVariation(avatar,nUsage));
		}
		else {
			sprite = _originalSprite = new Sprite((Image) avatars.elementAt(0));
			_alternateSprite = new Sprite(applyVariation(avatar,RED));
		}
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
	
	/**
	 * Alters an image by a certain color you choose.
	 * @param img
	 * @param var
	 * @return Image - new altered image
	 */
	private Image applyVariation(Image img, int var) {
		// Pull out ARGB data
		int[] rgbData = new int[img.getWidth()*img.getHeight()];
		img.getRGB(rgbData, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
		
		// Affect the ARGB data according to the variation type
		switch (var) {
		
		case (ColourEnum.BLUE):
			for (int i = 0; i < rgbData.length; i++) {
				//rgbData[i] = (30 + (rgbData[i] & 0xFF)) | (rgbData[i] & 0xFFFFFF00);
				rgbData[i] = rgbData[i] & 0xFF0000FF;
			}
			break;
			
		case (ColourEnum.GREEN):
			for (int i = 0; i < rgbData.length; i++) {
				//rgbData[i] = (30 + (rgbData[i] & 0xFF)) | (rgbData[i] & 0xFFFFFF00);
				rgbData[i] = rgbData[i] & 0xFF00FF00;
			}
			break;
			
		case (ColourEnum.RED):
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

	protected int getID() {
		return _id;
	}
	
	// Get and set this player's assigned color and refresh their GoodieStack
	protected void setColor(int color) {
		_assignedColor = color;
		goodies = new GoodieStack(_assignedColor);
	}
	
	protected int getColor() {
		return _assignedColor;
	}
	
	protected void setupSprites(LayerManager l) {
		Image avatar = (Image) avatars.elementAt(_assignedColor-1);
		sprite = _originalSprite = new Sprite(avatar);
		_alternateSprite = new Sprite(this.applyVariation(avatar, ColourEnum.RED));
		_alternateSprite.setVisible(false);
		
		l.insert(_originalSprite, 0);
		l.insert(_alternateSprite, 0);
	}
	
	protected void placeSprites(int x, int y) {
		_originalSprite.setPosition(x, y);
		_alternateSprite.setPosition(x, y);
	}
	
	// Update the player's score in game-like ways
	private void incrementScore() {
		_score += SCORE_INCREMENT;
		groupScore += SCORE_INCREMENT;
	}
	
	/**
	 * Give a bonus to this player.
	 * @param bonusValue
	 */
	protected void incrementScore(int bonusValue) {
		_score += bonusValue;
		groupScore += bonusValue;
	}
	
	private void decrementScore() {
		_score -= SCORE_INCREMENT;
		groupScore -= SCORE_INCREMENT;
	}
	
	public int getScore() {
		return _score;
	}
	
	public String getName() {
		return _name;
	}
	
	protected void collectGoodie(Goodie g) {
		goodies.push(g);
		if (g.getType() == this._assignedColor) {
			this.incrementScore();
			checkStatus();
			ApplicationMain.log.info("Player " + _id + " collected CORRECT goodie at (" + g.x + "," + g.y + "): " + g.getType());
		}
		else {
			this.decrementScore();
			//this.setAltAvatar();
			this.isInGoodStanding = false;
			this.isFinished = false;
			ApplicationMain.log.info("Player " + _id + " collected WRONG goodie at (" + g.x + "," + g.y + "): " + g.getType());
		}
	}
	
	protected Goodie dropGoodie() {
		if (goodies.isEmpty()) {
			ApplicationMain.log.info("Player " + _id + " tried to drop but he had no goodies.");
			return null;
		}
		Goodie g = (Goodie) goodies.pop();
		
		if (g.getType() == this._assignedColor) {
			this.decrementScore();
			this.isFinished = false;
			ApplicationMain.log.info("Player " + _id + " dropped a CORRECT goodie: " + g.getType());
		}
		else {
			checkStatus();
			ApplicationMain.log.info("Player " + _id + " dropped a WRONG goodie: " + g.getType());
		}
		return g;
	}

	private boolean hasCorrectGoodies() {
		Goodie g = null;
		for (int i = goodies.size()-1; i >= 0; i--) {
			g = (Goodie) goodies.elementAt(i);
			if (g.getType() != this._assignedColor) { return false; }
		}
		
		return true;
	}
	
	protected void setTargetNumGoodies(int target) {
		this._targetNumGoodies = target;
	}
	
	/**
	 * Runs a check and sets the state variables
	 * isFinished
	 * isInGoodStanding
	 */
	private void checkStatus() {
		if (this.hasCorrectGoodies()) {
			setOrigAvatar();
			isInGoodStanding = true;
			
			if (goodies.size() == _targetNumGoodies) {
				isFinished = true;
				timeFinished = GameController.clock;
				ApplicationMain.log.info("Player " + _id + " FINISHES at time: " + timeFinished);
			}
			else { isFinished = false; }
		}
		else {
			setAltAvatar();
			isInGoodStanding = false;
			isFinished = false;
		}
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
		
		//_alternateSprite.setPosition(_originalSprite.getX(), _originalSprite.getY());
		this.sprite = _alternateSprite;
	}
	
	private void setOrigAvatar() {
		_alternateSprite.setVisible(false);
		_originalSprite.setVisible(true);
		
		//_originalSprite.setPosition(_alternateSprite.getX(), _alternateSprite.getY());
		this.sprite = _originalSprite;
	}
	
	protected void animateIfNeeded() {
		// Flash (flipAvatar()) if player is holding someone else's goodie
		if (!isInGoodStanding) {
			this.flipAvatar();
			//this.sprite.paint(Round.graphics);
		}
	}
}