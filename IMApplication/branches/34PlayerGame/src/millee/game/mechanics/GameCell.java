package millee.game.mechanics;

import java.util.Vector;

public class GameCell {
	
	// Things that this cell might hold
	private Vector _occupants = new Vector();
	private Goodie _item = null;

	protected GameCell() {	}
	
	protected void addPlayer(Player p) {
		_occupants.addElement(p);
	}
	
	protected void removePlayer(Player p) {
		_occupants.removeElement(p);
	}
	
	protected boolean hasPlayer() {
		return !(_occupants.size() == 0);
	}
	
	protected boolean hasGoodie() {
		return (_item != null);
	}
	
	protected void setGoodie(Goodie g) {
		_item = g;
	}
	
	protected void unsetGoodie() {
		_item = null;
	}
	
	protected Goodie getGoodie() {
		return _item;
	}
}
