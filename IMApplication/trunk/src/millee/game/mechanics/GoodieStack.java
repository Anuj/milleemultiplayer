package millee.game.mechanics;

import java.util.Stack;

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
	private TiledLayer _tiledLayer;
	
	// Animated tile indices
	private int _blackAnimatedTileIndex, _redAnimatedTileIndex, _greenAnimatedTileIndex, _blueAnimatedTileIndex;
	private int _flipCounter = 0;
	
	/**
	 * Constructor of Stack.
	 */
	protected GoodieStack(int type) {
		super();
		this._correctType = type;
		
		_tiledLayer = new TiledLayer(MAX_FRUIT, 1, Utilities.createImage(TILED_IMAGE), TILE_DIMENSIONS, TILE_DIMENSIONS);
		
		// Create animated tiles
		_blackAnimatedTileIndex = _tiledLayer.createAnimatedTile(ColourEnum.BLACK);
		_redAnimatedTileIndex = _tiledLayer.createAnimatedTile(ColourEnum.RED);
		_greenAnimatedTileIndex = _tiledLayer.createAnimatedTile(ColourEnum.GREEN);
		_blueAnimatedTileIndex = _tiledLayer.createAnimatedTile(ColourEnum.BLUE);
		
		ApplicationMain.log.trace("Animated tiles: " +  _blackAnimatedTileIndex + _redAnimatedTileIndex + _greenAnimatedTileIndex + _blueAnimatedTileIndex);
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
	
	protected void animate() {
		_flipCounter = (_flipCounter+1)%2;
		_tiledLayer.setAnimatedTile(_blackAnimatedTileIndex, (ColourEnum.BLACK*2)-1+_flipCounter);
		_tiledLayer.setAnimatedTile(_redAnimatedTileIndex, (ColourEnum.RED*2)-1+_flipCounter);
		_tiledLayer.setAnimatedTile(_greenAnimatedTileIndex, (ColourEnum.GREEN*2)-1+_flipCounter);
		_tiledLayer.setAnimatedTile(_blueAnimatedTileIndex, (ColourEnum.BLUE*2)-1+_flipCounter);
	}
	
	protected TiledLayer getTiledLayer() {
		return _tiledLayer;
	}
}
