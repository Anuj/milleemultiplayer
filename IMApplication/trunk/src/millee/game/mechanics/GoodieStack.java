package millee.game.mechanics;

import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.TiledLayer;

import millee.game.initialize.Utilities;

/**
 * @author Simon
 *
 */
public class GoodieStack extends Stack {

	// Constants
	private static final int TILE_DIMENSIONS = 20;
	private static final String TILED_IMAGE = "/tiles.png";
	private static final int FRUIT_INDEX = 3; // Where fruit tiles begin
	private static final int LAST_INDEX = 14; // Last index to use in tiles
	private static final int MAX_FRUIT = 5;
	
	// Drawing stuff
	private LayerManager _layers = new LayerManager();
	private TiledLayer _tiledLayer;
	
	/**
	 * Constructor of Stack.
	 */
	public GoodieStack() {
		super();
		
		_tiledLayer = new TiledLayer(MAX_FRUIT, 1, Utilities.createImage(TILED_IMAGE), TILE_DIMENSIONS, TILE_DIMENSIONS);
		_layers.append(_tiledLayer);
	}
	
	public Object push(Object o) {
		if (this.size() >= MAX_FRUIT) { return null; }
		
		Goodie g = (Goodie) o;
		super.push(g);
		_tiledLayer.setCell(this.size()-1, 0, g.getType()+FRUIT_INDEX-1);
		return g;
	}
	
	public Object pop() {
		Goodie g = (Goodie) super.pop();
		
		_tiledLayer.setCell(this.size(), 0, LAST_INDEX);
		return g;
	}
	
	public void redraw(Graphics g, int x, int y) {
		_layers.paint(g, x, y);
	}
}
