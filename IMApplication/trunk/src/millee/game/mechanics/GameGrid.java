package millee.game.mechanics;

import javax.microedition.lcdui.game.TiledLayer;

import millee.game.initialize.Utilities;

/**
 * A two-dimensional grid of Cells
 * @author Simon
 *
 */
public class GameGrid {
	// Dimensions in cells
	protected int width;
	protected int height;
	
	// Data structure backing the grid
	private GameCell[][] _cells;
	
	// Constants
	private static final String TILED_IMAGE = "/tiles.png";
	private static final int FRUIT_INDEX = 3; // Where fruit tiles begin
	//private static final int CHARACTER_INDEX = 8;
	
	// Drawing stuff
	private TiledLayer _tiledLayer;
	
	// Builds the game world and prepares to draw it
	protected GameGrid(int cellWidth, int cellHeight, int tileDimensions) {
		width = cellWidth;
		height = cellHeight;
		_cells = new GameCell[height][width];

		_tiledLayer = new TiledLayer(width, height, Utilities.createImage(TILED_IMAGE), tileDimensions, tileDimensions);
		
		// Populate GameCell arrays and tiledArray
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// Currently, empty cell are only of one type
				_cells[i][j] =  new GameCell();
				_tiledLayer.setCell(j, i, 1); //FRUIT_INDEX-1);// random.nextInt(NUM_EMPTY_TILES)+1);
			}
		}
	}
	
	protected GameCell getCellAt(int cellX, int cellY) {
		return _cells[cellY][cellX];
	}
	
	// Place one "token" on the field
	protected void insertGoodie(Goodie g, int cellX, int cellY) {
		g.x = cellX;
		g.y = cellY;
		
		_tiledLayer.setCell(cellX, cellY, g.getType()+FRUIT_INDEX-1);
		_cells[cellY][cellX].setGoodie(g);
	}
	
	/**
	 * Sets a tile location to be the base type (no fruit)
	 * @param x
	 * @param y
	 */
	protected void setTileGrass(int x, int y) {
		_tiledLayer.setCell(x, y, 1);
	}
	
	/**
	 * Detects presence of goodie at a certain location...
	 * @param x
	 * @param y
	 * @return boolean
	 */
	protected boolean hasGoodieAt(int x, int y) {
		return _cells[y][x].hasGoodie();
	}
	
	protected boolean hasPlayerAt(int x, int y) {
		return _cells[y][x].hasPlayer();
	}

	protected TiledLayer getTiledLayer() {
		return _tiledLayer;
	}
}
