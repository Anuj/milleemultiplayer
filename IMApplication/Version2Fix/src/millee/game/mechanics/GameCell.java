package millee.game.mechanics;

import java.util.Vector;

public class GameCell {
	
	private Vector _occupants = new Vector();
	private Goodie _item = null;

	public GameCell() {
		/*
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
		*/
	}
	
	public void addPlayer(Player p) {
		_occupants.addElement(p);
	}
	
	public void removePlayer(Player p) {
		_occupants.removeElement(p);
	}
	
	public boolean hasPlayer() {
		return !(_occupants.size() == 0);
	}
	
	
	public boolean hasGoodie() {
		return (_item != null);
	}
	
	public void setGoodie(Goodie g) {
		_item = g;
		_item.sprite.setVisible(true);
	}
	
	public void unsetGoodie() {
		_item.sprite.setVisible(false);
		_item = null;
	}
	
	public Goodie getGoodie() {
		return _item;
	}

}
