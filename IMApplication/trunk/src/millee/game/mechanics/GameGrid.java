package millee.game.mechanics;

import java.util.Vector;

import javax.microedition.lcdui.game.LayerManager;

/**
 * A two-dimensional grid of Cells
 * @author Simon
 *
 */
public class GameGrid {
	
	//private LayerManager lm = new LayerManager();
	
	// Dimensions in cells
	private int _width;
	private int _height;
	
	// Data structure backing the grid
	private GameCell[][] _cells;
	
	// Player objects
	private Vector _players;
	
	public GameGrid(int width, int height) {
		_width = width;
		_height = height;
		_cells = new GameCell[height][width];
	}

	
	
	

}
