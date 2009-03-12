package millee.game.mechanics;

import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.TiledLayer;

import millee.game.ApplicationMain;
import millee.game.initialize.Utilities;

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
	private static final int TILE_DIMENSIONS = 20;
	private static final String TILED_IMAGE = "/tiles.png";
	private static final int FRUIT_INDEX = 3; // Where fruit tiles begin
	private static final int CHARACTER_INDEX = 8;
	
	// Drawing stuff
	private LayerManager _layers = new LayerManager();
	private TiledLayer _tiledLayer;
	
	// Utility variables
	private Random random;
	
	private int _nGoodies = 0;
	
	// Builds the game world and prepares to draw it
	public GameGrid(int width, int height) { //, Image backgroundImage, int tileDimensions) {
		_width = width;
		_height = height;
		_cells = new GameCell[_height][_width];

		_tiledLayer = new TiledLayer(_width, _height, Utilities.createImage(TILED_IMAGE), TILE_DIMENSIONS, TILE_DIMENSIONS);
		
		random = new Random();
		
		// Populate GameCell arrays and tiledArray
		for (int i = 0; i < _height; i++) {
			for (int j = 0; j < _width; j++) {
				// Currently, empty cell can be one of two tiles (id: 1 or 2)
				_cells[i][j] =  new GameCell();
				_tiledLayer.setCell(j, i, 1); //FRUIT_INDEX-1);// random.nextInt(NUM_EMPTY_TILES)+1);
			}
		}

		_layers.append(_tiledLayer);
	}
	
	// Place one player on the field
	public void insertPlayer(Player p, int cellX, int cellY) {
		p.x = cellX;
		p.y = cellY;
		p.sprite.setPosition(TILE_DIMENSIONS*cellX, TILE_DIMENSIONS*cellY);
		
		_players.addElement(p);
		_cells[cellY][cellX].addPlayer(p);
		_layers.insert(p.sprite, 0);
	}
	
	// Place one "token" on the field
	public void insertGoodie(Goodie g, int cellX, int cellY) {
		g.x = cellX;
		g.y = cellY;
		
		_tiledLayer.setCell(cellX, cellY, g.getType()+FRUIT_INDEX-1);
		//g.sprite.setPosition(_tileDimensions*cellX, _tileDimensions*cellY);
		_cells[cellY][cellX].setGoodie(g);
		//_layers.insert(g.sprite, 0);
		_nGoodies++;
	}
	
	// Moves one player character in terms of cells
	public void movePlayer(int id, int dx, int dy) {
		Player p = (Player) _players.elementAt(id);
		
		_cells[p.y][p.x].removePlayer(p);
		
		// Wraps around borders
		p.x = (p.x + _width + dx) % _width;
		p.y = (p.y + _height + dy) % _height;
		//ApplicationMain.log.trace("P: " + p.x + ", " + p.y);
		p.sprite.setPosition(TILE_DIMENSIONS*p.x, TILE_DIMENSIONS*p.y);
		
		GameCell cNew = _cells[p.y][p.x];
		cNew.addPlayer(p);
		
		// Now check for 'collisions' with goodies
		if (cNew.hasGoodie()) { // && p.assignedColor() == cNew.getGoodie().getType()) {
			p.collect(cNew.getGoodie());
			ApplicationMain.log.trace("Picked up Goodie: " + cNew.getGoodie());
			cNew.unsetGoodie();
			_tiledLayer.setCell(p.x, p.y, 1); //FRUIT_INDEX-1);
			_nGoodies--;
		}
	}
	
	public void playerDrop(int id) {
		Player p = (Player) _players.elementAt(id);
		Goodie g = p.dropGoodie();
		
		ApplicationMain.log.trace("Goodie dropped: " + g);
		if (g == null) { return; }
		
		_tiledLayer.setCell(p.x, p.y, g.getType()+FRUIT_INDEX-1);
		//g.sprite.setPosition(_tileDimensions*p.x, _tileDimensions*p.y);
		_cells[p.y][p.x].setGoodie(g);
		_nGoodies++;
		
		this.movePlayer(id, 0, -1);
	}
	
	// Tells the entire grid to redraw itself, players and all
	public void redraw(Graphics g) {
		_layers.paint(g, 0, 0);
	}
	
	public boolean isWon() {
		if (!(_nGoodies == 0)) { return false; }
		
		Player p = null;
		
		// Check that each player has only his respective color goodies
		for (int i = 0; i < _players.size(); i++) {
			p = (Player) _players.elementAt(i);
			if (!p.hasCorrectGoodies()) { return false; }
		}
		
		return true;
		
		
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
	
	/**
	 * Detects presence of goodie at a certain location...
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public boolean hasGoodieAt(int x, int y) {
		return _cells[y][x].hasGoodie();
	}
	
	public boolean hasPlayerAt(int x, int y) {
		return _cells[y][x].hasPlayer();
	}

}
