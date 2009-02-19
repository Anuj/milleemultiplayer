package millee.game.mechanics;

import javax.microedition.lcdui.Image;
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
		case 0:
		case 1:
		default:
			sprite = new Sprite(Utilities.createImage(TOMATO_PATH));
		}
	}
	
	public int getType() {
		return _type;
	}
	
	public static int TOMATO = 1;
	public static int ORANGE = 2;
	public static int GRAPE = 3;
	public static int BANANA = 4;
	
	private static final String TOMATO_PATH = "/tomato.png";
	
	// Maybe include types by color?
	
}
