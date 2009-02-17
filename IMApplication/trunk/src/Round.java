import java.io.IOException;
import java.util.Random;

import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import millee.game.mechanics.GameGrid;
import millee.game.mechanics.Goodie;
import millee.game.mechanics.Player;
import millee.network.Network;

/**
 * 
 */

/**
 * @author Priyanka
 *
 */
public class Round extends GameCanvas implements Runnable {
	
	private GameGrid _grid;
	
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
	
	private static char _possibleCommands[] = {'U', 'D', 'R', 'L'};
	
	private boolean stopGame = false;
	private String command = "";
	
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
	boolean isServer;
	Network network;
	int nonce;
	
	String playerName, playerImagePath;
	
	/* Static Variables */
	int[] scores;
	
	/* Instance Variables */
	//Sprite[] playerSprites;
	Sprite[] tokenSprites;
	Image backgroundImage;
	Player[] players;
	
	int localPlayerX, localPlayerY;
	
	public Round (int numPlayers, String backgroundPath, int round, int level, boolean lastRoundInLevel, 
					String levelName, String playerName, String playerImagePath, int[] scoreAssignment,
					String[] possibleTokenPaths, String[] possibleTokenText, int totalNumTokensToDisplay,
					boolean isServer, Network network) {
		super(true);
		
		this.numPlayers = numPlayers;
		this.round = round;
		this.level = level;
		this.lastRoundInLevel = lastRoundInLevel;
		this.levelName = levelName;
		this.scoreAssignment = scoreAssignment;
		this.backgroundPath = backgroundPath;
		this.possibleTokenPaths = possibleTokenPaths;
		this.possibleTokenText = possibleTokenText;
		this.totalNumTokensToDisplay = totalNumTokensToDisplay;
		this.isServer = isServer;
		this.playerName = playerName;
		this.playerImagePath = playerImagePath;
		
		this.network = network;
		
		tokenSprites = new Sprite[totalNumTokensToDisplay];
		players = new Player[numPlayers];
		
	}
	
	/*public Round (int localPlayer, int numPlayers, int round, int level, boolean lastRoundInLevel, String levelName, String[] playerNames, String[] playerImagePaths,
					int[] scoreAssignment, String backgroundPath, String[] possibleTokenPaths, String[] possibleTokenText,
					int totalNumTokensToDisplay, boolean isServer, Network network){
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
		this.isServer = isServer;
		
		this.network = network;
		
		//playerSprites = new Sprite[numPlayers];
		tokenSprites = new Sprite[totalNumTokensToDisplay];
		players = new Player[numPlayers];
	}*/
	
	public Command getOkCommand() {
		return this.okCmd;
	}
	
	public Command getExitCommand() {
		return this.exitCmd;
	}
	
	public void initialize() {

		try {
			Image tmpImage = null;
		
			if (!isServer) {
				nonce = random.nextInt(100);
				network.send(String.valueOf(nonce));
				network.send(playerName);
				network.send(playerImagePath);	// character image path
			}

			
			tmpImage = Image.createImage(backgroundPath);
			// TODO: Eliminate hard-coding of grid dimensions
			_grid = new GameGrid(12, 11, tmpImage, 15);
			
			for (int i = 0; i < numPlayers; i++) {
				String playerImagePath, playerName;
				if (isServer) {
					if (i == 0) {
						localPlayer = i;
						playerImagePath = this.playerImagePath;
						playerName = this.playerName;
						nonce = random.nextInt(100);
					} else {
						nonce = Integer.parseInt(network.receiveNow());
						playerName = network.receiveNow();
						playerImagePath = network.receiveNow();
					}
				
					tmpImage = Image.createImage(playerImagePath);
					players[i] = new Player(playerName, tmpImage, (i == 0));
					
					int x = random.nextInt(12);
					int y = random.nextInt(11);
					
					_grid.insertPlayer(players[i], x, y);
					
					network.send(String.valueOf(nonce));
					network.send(String.valueOf(i));
					network.send(playerName);
					network.send(playerImagePath);
					network.send(String.valueOf(x));
					network.send(String.valueOf(y));
				}
				else {

					int nonce = Integer.parseInt(network.receiveNow());
					int num = Integer.parseInt(network.receiveNow());
					playerName = network.receiveNow();
					playerImagePath = network.receiveNow();
					int x = Integer.parseInt(network.receiveNow());
					int y = Integer.parseInt(network.receiveNow());
					
					if (this.playerName.equals(playerName) && nonce == this.nonce) {
						localPlayer = i;
					}
					
					tmpImage = Image.createImage(playerImagePath);
					players[i] = new Player(playerName, tmpImage, (i == 0));
					
					_grid.insertPlayer(players[i], x, y);
				}
				
			}
			
			for (int i = 0; i<totalNumTokensToDisplay; i++) {
				tmpImage = Image.createImage(this.possibleTokenPaths[random.nextInt(4)]);
				if (isServer) {
					int x = random.nextInt(12);
					int y = random.nextInt(11);
					network.send(String.valueOf(x));
					network.send(String.valueOf(y));
					_grid.insertGoodie(new Goodie(Goodie.TOMATO, tmpImage), x, y);
				} else {
					int x = Integer.parseInt(network.receiveNow());
					int y = Integer.parseInt(network.receiveNow());
					//network.send(String.valueOf(x));
					//network.send(String.valueOf(y));
					_grid.insertGoodie(new Goodie(Goodie.TOMATO, tmpImage), x, y);
				}
			}
			
		} catch (IOException e) {
			System.err.println("Failed to gather image resources: " + e);
		}
		
		
		/*
		
		try {
			tmpImage = Image.createImage(backgroundPath);
			// TODO: Eliminate hard-coding of grid dimensions
			_grid = new GameGrid(12, 11, tmpImage, 15);
			
			for (int i = 0; i < numPlayers; i++) {
				tmpImage = Image.createImage(playerImagePaths[i]);
				players[i] = new Player(playerNames[i], tmpImage, (i == localPlayer));
				if (isServer ) {
					int x = random.nextInt(12);
					int y = random.nextInt(11);
					
					System.out.println("x: " + String.valueOf(x));
					System.out.println("network: " + network);
					network.send(String.valueOf(x));
					network.send(String.valueOf(y));
					
					_grid.insertPlayer(players[i], x, y);
				} else {
					int x = Integer.parseInt(network.receiveNow());
					int y = Integer.parseInt(network.receiveNow());
					
					_grid.insertPlayer(players[i], x, y);
				}
			}
			
			for (int i = 0; i<totalNumTokensToDisplay; i++) {
				tmpImage = Image.createImage(this.possibleTokenPaths[random.nextInt(4)]);
				if (isServer) {
					int x = random.nextInt(12);
					int y = random.nextInt(11);
					network.send(String.valueOf(x));
					network.send(String.valueOf(y));
					_grid.insertGoodie(new Goodie(Goodie.TOMATO, tmpImage), x, y);
				} else {
					int x = Integer.parseInt(network.receiveNow());
					int y = Integer.parseInt(network.receiveNow());
					//network.send(String.valueOf(x));
					//network.send(String.valueOf(y));
					_grid.insertGoodie(new Goodie(Goodie.TOMATO, tmpImage), x, y);
				}
			}
			
			//network.send("n");	// sent initially to avoid deadlock
			
		} catch (IOException e) {
			System.err.println("Failed to gather image resources: " + e);
		}*/
		
	}
	
	public void start() {
		
		random = new Random();

		graphics = getGraphics();
		graphics.setColor( 0,  0, 0 );
		graphics.fillRect( 0, 0, getWidth(), getHeight() );
		
		// Set up the layer manager and the first tiled layer
		//layers = new LayerManager();
		
		
		
		// Loading and using images to build grid
		
		// initialize all players and tokens on the board
		initialize();
		
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
		if (stopGame) { return; }

		for (int i = 0; i<numPlayers; i++) {
			if (i != localPlayer) {
				char command = getCommand(); // Is this supposed to be a hook into network stuff later?
				while (command != 'n') {
					switch (command) {
						case 'u':
							_grid.movePlayer(i, 0, -1);
							break;
						case 'd':
							_grid.movePlayer(i, 0, 1);
							break;
						case 'r':
							_grid.movePlayer(i, 1, 0);
							break;
						case 'l':
							_grid.movePlayer(i, -1, 0);
							break;
						case 'n':
							break;
					}
					command = getCommand();
				}
			}
		}
	}

	private void checkUserInput() {
		// Detect key presses and speed up or slow down accordingly
		int state = getKeyStates();

		if(( state & DOWN_PRESSED ) != 0 ){
			_grid.movePlayer(localPlayer, 0, 1);
			command = "d";
		} else if(( state & UP_PRESSED ) != 0 ){
			_grid.movePlayer(localPlayer, 0, -1);
			command = "u";
		} else if ((state & LEFT_PRESSED) != 0) {
			_grid.movePlayer(localPlayer, -1, 0);
			command = "l";
		} else if ((state & RIGHT_PRESSED) != 0) {
			_grid.movePlayer(localPlayer, 1, 0);
			command = "r";
		}
	}

	private void updateGameScreen(Graphics g) {
		
		if (command != "")
			network.send(command);
			command = "";
		
		if (stopGame) {
			
			exitCmd = new Command("Exit", Command.EXIT, 0);
			okCmd = new Command("OK", Command.OK, 1);
			this.addCommand(exitCmd);
			this.addCommand(okCmd);
			g.drawString("COLLISION!!", getWidth()/2, getHeight()/2, g.TOP | g.LEFT);
		
		} else {
			// Redraw with the LayerManager
			_grid.redraw(graphics);
		}
		flushGraphics();

	}

	private char getCommand() {
		String command = network.receiveLater();
		return (command == null) ? 'n' : command.charAt(0);
	}
	
}
