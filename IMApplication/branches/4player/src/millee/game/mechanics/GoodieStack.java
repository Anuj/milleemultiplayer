package millee.game.mechanics;

import java.util.Stack;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.TiledLayer;

import millee.game.ApplicationMain;
import millee.game.initialize.Utilities;

/**
 * @author Simon
 *
 */
public class GoodieStack extends Stack {

	// Constants
	private static final int TILE_DIMENSIONS = 15;
	private static final String TILED_IMAGE = "/goodie_stack.png";
	private static final int MAX_FRUIT = 6;
	
	// Settings from Player
	private int _correctType;
	
	// Drawing stuff
	private LayerManager _layers = new LayerManager();
	private TiledLayer _tiledLayer;
	
	// Animated tile indices
	private int _blackAnimatedTileIndex, _redAnimatedTileIndex, _greenAnimatedTileIndex, _blueAnimatedTileIndex;
	private int _flipCounter = 0;
	
	/**
	 * Constructor of Stack.
	 */
	public GoodieStack(int type) {
		super();
		this._correctType = type;
		
		_tiledLayer = new TiledLayer(MAX_FRUIT, 1, Utilities.createImage(TILED_IMAGE), TILE_DIMENSIONS, TILE_DIMENSIONS);
		
		// Create animated tiles
		_blackAnimatedTileIndex = _tiledLayer.createAnimatedTile(Goodie.BLACK_BERRY);
		_redAnimatedTileIndex = _tiledLayer.createAnimatedTile(Goodie.RED_TOMATO);
		_greenAnimatedTileIndex = _tiledLayer.createAnimatedTile(Goodie.GREEN_BANANA);
		_blueAnimatedTileIndex = _tiledLayer.createAnimatedTile(Goodie.BLUE_BERRY);
		
		System.out.println("Animated tiles: " +  _blackAnimatedTileIndex + _redAnimatedTileIndex + _greenAnimatedTileIndex + _blueAnimatedTileIndex);
		
		_layers.append(_tiledLayer);
	}
	
	public Object push(Object o) {
		if (this.size() >= MAX_FRUIT) { return null; }
		
		Goodie g = (Goodie) o;
		super.push(g);
		
		int tileIndex;
		if (g.getType() == _correctType) {
			tileIndex = (g.getType()*2)-1;
		}
		else {
			tileIndex = -(g.getType());
		}
		
		_tiledLayer.setCell(this.size()-1, 0, tileIndex);
		return g;
	}
	
	public Object pop() {
		Goodie g = (Goodie) super.pop();
		
		_tiledLayer.setCell(this.size(), 0, 0);
		return g;
	}
	
	public void redraw(Graphics g, int x, int y) {
		_flipCounter = (_flipCounter+1)%2;
		_tiledLayer.setAnimatedTile(_blackAnimatedTileIndex, (Goodie.BLACK_BERRY*2)-1+_flipCounter);
		_tiledLayer.setAnimatedTile(_redAnimatedTileIndex, (Goodie.RED_TOMATO*2)-1+_flipCounter);
		_tiledLayer.setAnimatedTile(_greenAnimatedTileIndex, (Goodie.GREEN_BANANA*2)-1+_flipCounter);
		_tiledLayer.setAnimatedTile(_blueAnimatedTileIndex, (Goodie.BLUE_BERRY*2)-1+_flipCounter);
		_layers.paint(g, x, y);
	}
}
