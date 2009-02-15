import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
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
 * @author Priyanka
 *
 */
public class Round extends GameCanvas implements Runnable {
	
	private static final int SLEEP_INCREMENT = 10;
	private static final int SLEEP_INITIAL = 150;
	private static final int SLEEP_MAX = 300;
	
	private static final String TILE_IMAGE = "/tiles.png";
	private static final int TILE_WIDTH = 12;
	private static final int TILE_HEIGHT = 11;
	private static final int TILE_DIMENSIONS = 15;
	
	private static final String FLOWER_IMAGE = "/stain.png";
	private static final String TOMATO_IMAGE = "/tomato.png";

	private Graphics        graphics;
	private Random          random;
	private int             sleepTime = SLEEP_INITIAL;
	private volatile Thread thread;
	
	private boolean stopGame = false;
	
	// Drawing stuff
	private LayerManager layers;
	private TiledLayer tiledLayer;
	private Sprite flower, tomato;
	
	private int flowerX;
	private int flowerY;
	
	Command exitCmd, okCmd;
	
	/* New Variables */
	int numPlayers;
	int round, level;
	boolean lastRoundInLevel;
	String levelName;
	String[] playerNames, playerImagePaths;
	int[] scoreAssignment;
	String backgroundPath;
	String[] possibleTokenPaths, possibleTokenText;
	int totalNumTokensToDisplay;
	int localPlayer;
	
	/* Static Variables */
	int[] scores;
	
	/* Instance Variables */
	//Sprite[] playerSprites;
	Sprite[] tokenSprites;
	Image backgroundImage;
	Player[] players;
	
	int localPlayerX, localPlayerY;
	
	public Round (int localPlayer, int numPlayers, int round, int level, boolean lastRoundInLevel, String levelName, String[] playerNames, String[] playerImagePaths,
					int[] scoreAssignment, String backgroundPath, String[] possibleTokenPaths, String[] possibleTokenText,
					int totalNumTokensToDisplay){
		super( true );
		
		this.localPlayer = localPlayer;
		this.numPlayers = numPlayers;
		this.round = round;
		this.level = level;
		this.lastRoundInLevel = lastRoundInLevel;
		this.levelName = levelName;
		this.playerNames = playerNames;
		this.playerImagePaths = playerImagePaths;
		this.scoreAssignment = scoreAssignment;
		this.backgroundPath = backgroundPath;
		this.possibleTokenPaths = possibleTokenPaths;
		this.possibleTokenText = possibleTokenText;
		this.totalNumTokensToDisplay = totalNumTokensToDisplay;
		
		//playerSprites = new Sprite[numPlayers];
		tokenSprites = new Sprite[totalNumTokensToDisplay];
		players = new Player[numPlayers];
	}
	
	public Command getOkCommand() {
		return this.okCmd;
	}
	
	public Command getExitCommand() {
		return this.exitCmd;
	}
	
	public void start() {
		
		random = new Random();

		graphics = getGraphics();
		graphics.setColor( 0,  0, 0 );
		graphics.fillRect( 0, 0, getWidth(), getHeight() );
		
		// Set up the layer manager and the first tiled layer
		layers = new LayerManager();
		
		Image tiledImage = null;
		/*Image flowerImage = null;
		Image tomatoImage = null;*/
		
		try {
			for (int i = 0; i<numPlayers; i++) {
				
				Player player = new Player(playerNames[i], playerImagePaths[i], (i == localPlayer));
				players[i] = player;
				layers.append(player.getSprite());
				localPlayerX = player.getSprite().getX();
				localPlayerY = player.getSprite().getY();
				//System.out.println("playername: " + playe)
			}
			
			for (int i = 0; i<totalNumTokensToDisplay; i++) {
				int num = random.nextInt(4)%4;
				Image img = Image.createImage(this.possibleTokenPaths[num]);
				Sprite tempSprite = new Sprite(img);
				tempSprite.setPosition(random.nextInt(100), random.nextInt(100));
				tokenSprites[i] = tempSprite;
				layers.append(tempSprite);
			}
			tiledImage = Image.createImage(backgroundPath);
		} catch (IOException e) {
			System.err.println("Failed to gather image resources: " + e);
		} catch (NullPointerException e) {
			System.err.println("null pointer exception");
		}
		
		tiledLayer = new TiledLayer(TILE_WIDTH, TILE_HEIGHT, tiledImage, TILE_DIMENSIONS, TILE_DIMENSIONS);
		
		// Full grassy
		for (int i = 0; i < TILE_HEIGHT; i++) {
			for (int j = 0; j < TILE_WIDTH; j++) {
				// Currently, cell can be one of two tiles (id: 1 or 2)
				tiledLayer.setCell(j, i, 1);
			}
		}
		
		
		// Create a sprite on top
		/*flower = new Sprite(flowerImage);
		flowerX = flower.getX();
		flowerY = flower.getY();*/
		
		/*tomato = new Sprite(tomatoImage);
		int tomatoX = random.nextInt(50);
		int tomatoY = random.nextInt(100);*/
		
		//int tomatoX = random.nextInt(getWidth());
		//int tomatoY = random.nextInt(getHeight());
		
		/*System.out.println("tomatoX: " + tomatoX + ", tomatoY: " + tomatoY);
		tomato.setPosition(tomatoX, tomatoY);*/
		
		// Items get put on the screen in reverse order from how they were appended.
		// (ie. The flower object is put on the screen last, so it becomes the top layer)
		//layers.append(tomato);
		//layers.append(flower);
		layers.append(tiledLayer);
		
		showNotify();
	}
	
	public void showNotify() {
		// Start this thread
		thread = new Thread( this );
		thread.start();
	}

	// When the game canvas is hidden, stop the thread.
	protected void hideNotify(){
		thread = null;
	}

	// The game loop.
	public void run(){
		//int w = getWidth();
		//int h = getHeight() - 1;

		while( thread == Thread.currentThread() && !stopGame) {
			
			verifyGameState();
			checkUserInput();
			checkRemotePlayerInput();
			updateGameScreen(getGraphics());
			
			// Now wait...
			try {
				Thread.sleep( sleepTime );
			}
			catch( InterruptedException e ){
			}
		}
		
		//this.setTitle("End of this Runnable");
		//this.notifyAll();
	}
	
	private void verifyGameState() {
		  // doesn't do anything yet
	}
	
	private void checkRemotePlayerInput() {
		for (int i = 1; i<numPlayers; i++) {
			players[i].updateSpriteLocation();
		}
	}

	private void checkUserInput() {

		// Detect key presses and speed up or slow down accordingly
		int state = getKeyStates();

		if(( state & DOWN_PRESSED ) != 0 ){
			localPlayerY+=5;
		} else if(( state & UP_PRESSED ) != 0 ){
			localPlayerY-=5;
		} else if ((state & LEFT_PRESSED) != 0) {
			localPlayerX-=5;
		} else if ((state & RIGHT_PRESSED) != 0) {
			localPlayerX+=5;
		}
		
		for (int i = 0; i < numPlayers; i++) {
			for (int j = 0; j<this.totalNumTokensToDisplay; j++) {
				if (players[i].getSprite().collidesWith( this.tokenSprites[j], true)) {
					this.tokenSprites[j].setVisible(false);
					players[i].finishedRound();
				}
			}
		}
		
		/*if (flower.collidesWith(tomato, true)) {
			stopGame = true;
			System.out.println("want to stop the game");
		}*/
	}

	private void updateGameScreen(Graphics g) {
		
		if (stopGame) {
			
			exitCmd = new Command("Exit", Command.EXIT, 0);
			okCmd = new Command("OK", Command.OK, 1);
			this.addCommand(exitCmd);
			this.addCommand(okCmd);
			g.drawString("COLLISION!!", getWidth()/2, getHeight()/2, g.TOP | g.LEFT);
		
		} else {
			// Move the flower
			//flower.move(1, 1);
			//flower.setPosition(flowerX, flowerY);
			players[localPlayer].getSprite().setPosition(localPlayerX, localPlayerY);
			// Affect the tiled layer in a random way
			//tiledLayer.setCell(random.nextInt(TILE_WIDTH), random.nextInt(TILE_HEIGHT), random.nextInt(2)+1);
			
			// Redraw with the LayerManager
			layers.paint(graphics, 0, getHeight()-(TILE_HEIGHT*TILE_DIMENSIONS));
			// this call paints off screen buffer to screen
		}
		flushGraphics();

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
