package millee.game.mechanics;

import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import millee.game.Round;

/**
 * A two-dimensional grid of Cells
 * @author Simon
 *
 */
public class GameGrid {
	// Dimensions in cells
	private int _width;
	private int _height;
	
	// Data structure backing the grid
	private GameCell[][] _cells;
	
	// Player objects
	private Vector _players = new Vector();
	
	// Constants
	private int _tileDimensions;
	
	// Drawing stuff
	private LayerManager _layers = new LayerManager();
	private TiledLayer _tiledLayer;
	
	// Utility variables
	private Random random;
	
	private int _nGoodies = 0;
	
	// Builds the game world and prepares to draw it
	public GameGrid(int width, int height, Image backgroundImage, int tileDimensions) {
		_width = width;
		_height = height;
		_cells = new GameCell[_height][_width];
		_tileDimensions = tileDimensions;
		_tiledLayer = new TiledLayer(_width, _height, backgroundImage, _tileDimensions, _tileDimensions);
		
		random = new Random();
		
		// Populate GameCell arrays and tiledArray
		for (int i = 0; i < _height; i++) {
			for (int j = 0; j < _width; j++) {
				// Currently, cell can be one of two tiles (id: 1 or 2)
				_cells[i][j] =  new GameCell(null, null);
				_tiledLayer.setCell(j, i, random.nextInt(2)+1);
			}
		}

		_layers.append(_tiledLayer);
	}
	
	// Place one player on the field
	public void insertPlayer(Player p, int cellX, int cellY) {
		p.x = cellX;
		p.y = cellY;
		p.sprite.setPosition(_tileDimensions*cellX, _tileDimensions*cellY);
		
		// TODO: Have this player's color set some other way
		p.setColor(Goodie.TOMATO);
		
		_players.addElement(p);
		_layers.insert(p.sprite, 0);
	}
	
	// Place one "token" on the field
	public void insertGoodie(Goodie g, int cellX, int cellY) {
		g.x = cellX;
		g.y = cellY;
		g.sprite.setPosition(_tileDimensions*cellX, _tileDimensions*cellY);
		
		_cells[cellY][cellX].setGoodie(g);
		_layers.insert(g.sprite, 0);
		_nGoodies++;
	}
	
	// Moves one player character in terms of cells
	public void movePlayer(int id, int dx, int dy) {
		Player p = (Player) _players.elementAt(id);
		
		// Wraps around borders
		p.x = (p.x + _width + dx) % _width;
		p.y = (p.y + _height + dy) % _height;
		//System.out.println("P: " + p.x + ", " + p.y);
		p.sprite.setPosition(_tileDimensions*p.x, _tileDimensions*p.y);
		
		GameCell c = _cells[p.y][p.x];
		// Now check for 'collisions'
		if (c.hasGoodie() && p.assignedColor() == c.getGoodie().getType()) {
			Round.incrementScore(id);
			c.unsetGoodie();
			_nGoodies--;
		}
	}
	
	// Tells the entire grid to redraw itself, players and all
	public void redraw(Graphics g) {
		_layers.paint(g, 0, 0);
	}
	
	public boolean isWon() {
		return (_nGoodies == 0);
		/* Dumb search
		for (int i = 0; i < _height; i++) {
			for (int j = 0; j < _width; j++) {
				if (_cells[i][j].hasGoodie()) {
					return false;
				}
			}
		}
		return true;
		*/
	}

}
