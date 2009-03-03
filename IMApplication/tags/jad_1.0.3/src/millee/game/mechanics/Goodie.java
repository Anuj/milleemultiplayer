package millee.game.mechanics;

import javax.microedition.lcdui.game.Sprite;

import millee.game.initialize.Utilities;

public class Goodie {

	private int _type;
	
	// Location in cell coordinates
	protected int x;
	protected int y;
	
	// Sprite must have its own coordinates
	protected Sprite sprite;
	
	public Goodie(int type) {
		_type = type;
		switch (type) {
		case 1: sprite = new Sprite(Utilities.createImage(BLACK_BERRY_PATH)); break;
		case 2: sprite = new Sprite(Utilities.createImage(RED_TOMATO_PATH)); break;
		case 3: sprite = new Sprite(Utilities.createImage(BLUE_BERRY_PATH)); break;
		case 4: sprite = new Sprite(Utilities.createImage(GREEN_BANANA_PATH)); break;
		default:
			sprite = new Sprite(Utilities.createImage(RED_TOMATO_PATH));
		}
	}
	
	public int getType() {
		return _type;
	}
	
	// Goodie types
	public static int BLACK_BERRY = 1;
	public static int RED_TOMATO = 2;
	public static int BLUE_BERRY = 3;
	public static int GREEN_BANANA = 4;
	
	private static final String BLACK_BERRY_PATH = "/black_berry.png";
	private static final String RED_TOMATO_PATH = "/tomato.png";
	private static final String BLUE_BERRY_PATH = "/blue_berry.png";
	private static final String GREEN_BANANA_PATH = "/green_banana.png";
}
