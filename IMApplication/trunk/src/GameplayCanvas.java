import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

/**
 * 
 */

/**
 * @author Simon
 *
 */
public class GameplayCanvas extends GameCanvas implements Runnable {
	
	private static final int SLEEP_INCREMENT = 10;
	private static final int SLEEP_INITIAL = 150;
	private static final int SLEEP_MAX = 300;
	
	private static final String TILE_IMAGE = "/tiles.png";
	private static final int TILE_WIDTH = 12;
	private static final int TILE_HEIGHT = 11;
	private static final int TILE_DIMENSIONS = 15;
	
	private static final String FLOWER_IMAGE = "/flower2.png";

	private Graphics        graphics;
	private Random          random;
	private int             sleepTime = SLEEP_INITIAL;
	private volatile Thread thread;
	
	// Drawing stuff
	private LayerManager layers;
	private TiledLayer tiledLayer;
	private Sprite flower;

	public GameplayCanvas(){
		super( true );
		
		random = new Random();

		graphics = getGraphics();
		graphics.setColor( 0,  0, 0 );
		graphics.fillRect( 0, 0, getWidth(), getHeight() );
		
		System.out.println("Dimensions: " + getWidth() + " x " + getHeight());
		
		// Set up the layer manager and the first tiled layer
		layers = new LayerManager();
		
		Image tiledImage = null;
		Image flowerImage = null;
		
		try {
			tiledImage = Image.createImage(TILE_IMAGE);
			flowerImage = Image.createImage(FLOWER_IMAGE);
		} catch (IOException e) {
			System.err.println("Failed to gather image resources: " + e);
		}
		
		tiledLayer = new TiledLayer(TILE_WIDTH, TILE_HEIGHT, tiledImage, TILE_DIMENSIONS, TILE_DIMENSIONS);
		
		// Half grassy
		for (int i = 0; i < TILE_HEIGHT; i++) {
			for (int j = 0; j < TILE_WIDTH; j++) {
				// Currently, cell can be one of two tiles (id: 1 or 2)
				tiledLayer.setCell(j, i, random.nextInt(2)+1);
			}
		}
		
		// Create a sprite on top
		flower = new Sprite(flowerImage); 
		
		layers.append(flower);
		layers.append(tiledLayer);
	}

	// When the game canvas is hidden, stop the thread.

	protected void hideNotify(){
		thread = null;
	}

	// The game loop.
	public void run(){
		int w = getWidth();
		int h = getHeight() - 1;

		layers.paint(graphics, 0, getHeight()-(TILE_HEIGHT*TILE_DIMENSIONS));

		/*
		while( thread == Thread.currentThread() ){
		
		tiledLayer.setCell(j, i, random.nextInt(2)+1);

			// Increment or decrement the scrolling interval
			// based on key presses

			int state = getKeyStates();

			if( ( state & DOWN_PRESSED ) != 0 ){
				sleepTime += SLEEP_INCREMENT;
				if( sleepTime > SLEEP_MAX ) 
					sleepTime = SLEEP_MAX;
			} else if( ( state & UP_PRESSED ) != 0 ){
				sleepTime -= SLEEP_INCREMENT;
				if( sleepTime < 0 ) sleepTime = 0;
			}

			// Repaint the screen by first scrolling the
			// existing starfield down one and painting in
			// new stars...

			graphics.copyArea( 0, 0, w, h, 0, 1,
			Graphics.TOP | Graphics.LEFT );

			graphics.setColor( 0, 0, 0 );
			graphics.drawLine( 0, 0, w, 1 );
			graphics.setColor( 200, 200, 200); //255, 255, 255 );

			for( int i = 0; i < w; ++i ){
				int test = Math.abs( random.nextInt() ) % 200;
				if( test < 4 ){
					//System.out.println("w = " + i);
					graphics.drawLine( i, 0, w, 0 );
				}
			}

			flushGraphics();

			// Now wait...

			try {
				Thread.currentThread().sleep( sleepTime );
			}
			catch( InterruptedException e ){
			}
		}
		*/
	}

	// When the canvas is shown, start a thread to
	// run the game loop.

	protected void showNotify(){
		random = new Random();

		thread = new Thread( this );
		thread.start();
	}
	
	
	
	
	
	
	
	
	

	/**OLDSTUFF
	 * @param suppressKeyEvents
	 
	public GameplayCanvas(boolean suppressKeyEvents) {
		super(suppressKeyEvents);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 
	public void run() {
	    Graphics g = getGraphics();
	    
	      while(true) {
	         // update the game state
	         // ...
	    	  g.drawString("Hello", 5, 5, Graphics.LEFT);
	    	  //g.drawLine(30,15,30,30);
	         int k = getKeyStates();
	         // respond to key events
	         flushGraphics();
	      }
	}
	
	*/

}
