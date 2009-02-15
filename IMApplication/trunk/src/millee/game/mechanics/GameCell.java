package millee.game.mechanics;

import java.util.Vector;

import javax.microedition.lcdui.List;

public class GameCell {
	
	private Vector _occupants;
	private Goodie _item;

	public GameCell(Player[] players, Goodie g) {
		if (players == null) {
			_occupants = new Vector();
		}
		else {
			_occupants = new Vector(players.length);
			for (int i = 0; i < players.length; i++) {
				_occupants.addElement(players[i]);
			}
		}
		
		_item = g;
	}
	
	public Vector getPlayers() {
		return _occupants;
	}
	
	public void addPlayer(Player p) {
		_occupants.addElement(p);
	}
	
	
	public boolean hasGoodie() {
		return (_item != null);
	}
	
	public void setGoodie(Goodie g) {
		_item = g;
	}
	
	public void unsetGoodie() {
		_item = null;
	}

}
