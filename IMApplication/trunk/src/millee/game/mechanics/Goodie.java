package millee.game.mechanics;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class Goodie {

	private int _type;
	
	// Location in cell coordinates
	protected int x;
	protected int y;
	
	// Sprite must have its own coordinates
	protected Sprite sprite;
	
	public Goodie(int type, Image img) {
		_type = type;
		sprite = new Sprite(img);
	}
	
	public int getType() {
		return _type;
	}
	
	public static int TOMATO = 1;
	
	// Maybe include types by color?
	
}
