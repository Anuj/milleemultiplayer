package millee.game.mechanics;

public class Goodie {
	private int _type;
	
	// Location in cell coordinates
	protected int x;
	protected int y;
	
	protected Goodie(int type) {
		this._type = type;
	}
	
	protected int getType() {
		return _type;
	}
}
