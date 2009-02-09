import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

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

	private Graphics        graphics;
	private Random          random;
	private int             sleepTime = SLEEP_INITIAL;
	private volatile Thread thread;

	public GameplayCanvas(){
		super( true );

		graphics = getGraphics();
		graphics.setColor( 0,  0, 0 );
		graphics.fillRect( 0, 0, getWidth(), getHeight() );
	}

	// When the game canvas is hidden, stop the thread.

	protected void hideNotify(){
		thread = null;
	}

	// The game loop.

	public void run(){
		int w = getWidth();
		int h = getHeight() - 1;

		while( thread == Thread.currentThread() ){

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
					System.out.println("w = " + i);
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
	}

	// When the canvas is shown, start a thread to
	// run the game loop.

	protected void showNotify(){
		random = new Random();

		thread = new Thread( this );
		thread.start();
	}
	
	
	
	
	
	
	
	
	

	/**
	 * @param suppressKeyEvents
	 
	public GameplayCanvas(boolean suppressKeyEvents) {
		super(suppressKeyEvents);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 
	public void run() {
		// TODO Auto-generated method stub
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
