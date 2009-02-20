package millee.game;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
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

import millee.game.initialize.Utilities;
import millee.game.mechanics.GameGrid;
import millee.game.mechanics.Goodie;
import millee.game.mechanics.Player;
import millee.network.Message;
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
	int localPlayerId;
	boolean isServer;
	Network network;
	int nonce;
	
	private Vector _players;
	
	String playerName, playerImagePath;
	
	/* Static Variables */
	int[] scores;
	
	/* Instance Variables */
	//Sprite[] playerSprites;
	Sprite[] tokenSprites;
	Image backgroundImage;
	
	int localPlayerX, localPlayerY;
	
	public Round (Vector players, String backgroundPath, int round, int level, boolean lastRoundInLevel, 
					String levelName, int[] scoreAssignment,
					String[] possibleTokenPaths, String[] possibleTokenText, int totalNumTokensToDisplay,
					boolean isServer, Network network, int localPlayerId) {
		super(true);
		
		this.numPlayers = players.size();
		this._players = players;
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
		
		this.localPlayerId = localPlayerId;
		
		this.network = network;
		
		tokenSprites = new Sprite[totalNumTokensToDisplay];
		
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
	
	// Only the server uses this.
	private void createGridAndBroadcast() {
		
		try {
			Player p = null;
			int i, x, y, goodieType;
			StringBuffer broadcastString = new StringBuffer("");
			
			// Assign player coordinates
			for (i = 0; i < numPlayers; i++) {
				x = random.nextInt(12);
				y = random.nextInt(11);
				p = (Player) _players.elementAt(i);
				_grid.insertPlayer(p, x, y);
				broadcastString.append(i);
				broadcastString.append(',');
				broadcastString.append(x);
				broadcastString.append(',');
				broadcastString.append(y);
				broadcastString.append(';');
			}
			
			broadcastString.append('|');
			
			// Add goodies
			for (i = 0; i < totalNumTokensToDisplay; i++) {
				// TODO: Don't hard code which goodie, or the number of goodie types
				goodieType = Goodie.TOMATO;
				x = random.nextInt(12);
				y = random.nextInt(11);
				_grid.insertGoodie(new Goodie(goodieType), x, y);
				broadcastString.append(goodieType);
				broadcastString.append(',');
				broadcastString.append(x);
				broadcastString.append(',');
				broadcastString.append(y);
				broadcastString.append(';');
			}
			
			// Broadcast this information to all clients
			network.broadcast(broadcastString.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	// Only the clients use this.
	private void createGridFromBroadcast() {
		
		try {
			String input = null;
			while ((input = network.receiveNow().msg()).indexOf("|") < 0) {
				continue;
			}
			
			String[] blocks = Utilities.split(input, "|", 2);
			
			// Get the player information
			String[] sPlayers = Utilities.split(blocks[0], ";", 0);
			String[] playerInfo = null;
			Player tmpPlayer = null;
			int x,y;
			System.out.println("sPlayers = " + sPlayers);
			for (int i = 0; i < sPlayers.length; i++) {
				System.out.println("splayers[" + i + "] = " + sPlayers[i]);
				System.out.println("putting player " + i + " on the board");
				playerInfo = Utilities.split(sPlayers[i], ",", 3);
				System.out.println("playerInfo = " + playerInfo);
				// TODO: Deal with the lack of a name, image
				tmpPlayer = (Player) _players.elementAt(i);
				System.out.println("player's name is " + tmpPlayer);
					//new Player("PLAYER " + playerInfo[0], Utilities.createImage("/dancer_small.png"), Integer.parseInt(playerInfo[0]), 0);
				x = Integer.parseInt(playerInfo[1]);
				y = Integer.parseInt(playerInfo[2]);
				_grid.insertPlayer(tmpPlayer, x, y);
			}
			
			// Get the goodie information
			String[] sGoodies = Utilities.split(blocks[1], ";", 0);
			String[] goodieInfo = null;
			Goodie tmpGoodie = null;
			for (int i = 0; i < sGoodies.length; i++) {
				goodieInfo = Utilities.split(sGoodies[i], ",", 3);
				// TODO: Deal with the lack of a name, image
				tmpGoodie = new Goodie(Integer.parseInt(goodieInfo[0]));
				x = Integer.parseInt(goodieInfo[1]);
				y = Integer.parseInt(goodieInfo[2]);
				_grid.insertGoodie(tmpGoodie, x, y);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initialize() {
		// TODO: Eliminate hard-coding of grid dimensions
		_grid = new GameGrid(12, 11, Utilities.createImage(backgroundPath), 15);

		if (isServer) {
			createGridAndBroadcast();
		}
		else {
			createGridFromBroadcast();
		}
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
			
			//verifyGameState();
			if (_grid.isWon()) {
				stopGame = true;
				//break;
			}
			if (!stopGame) {
				checkUserInput();
				checkRemotePlayerInput();
			}
			updateGameScreen(getGraphics());
			
			// Now wait...
			try {
				Thread.sleep( sleepTime );
			}
			catch( InterruptedException e ){
			}
		}
		
		//TODO: See if we actually won this
		//Alert a = new Alert("END OF ROUND", "Congratulations!", null,null);
		//a.setTimeout(Alert.FOREVER);
//		
//		exitCmd = new Command("Exit", Command.EXIT, 0);
//		okCmd = new Command("OK", Command.OK, 1);
//		this.addCommand(exitCmd);
//		this.addCommand(okCmd);
//		graphics.drawString("COLLISION!!", getWidth()/2, getHeight()/2, graphics.TOP | graphics.LEFT);
//		System.out.println("stopping the round");
		
		
		//mDisplay.setCurrent(a, mMainForm);
		//this.setTitle("End of this Runnable");
		//this.notifyAll();
	}
	
	private void verifyGameState() {
		  if (_grid.isWon()) {
			  stopGame = true;
		  }
	}
	
	private void checkRemotePlayerInput() {
		if (stopGame) { return; }

		Message msg = network.receiveLater();
		char command;
		int clientId;
		String[] msgs = null;
		while (msg != null) {
			
			msgs = Utilities.split(msg.msg(), ",", 2);
			clientId = Integer.parseInt(msgs[0]);
			
			if (clientId != localPlayerId) {
				command = msgs[1].charAt(0);
				
				switch (command) {
					case 'u':
						_grid.movePlayer(clientId, 0, -1);
						if (isServer) network.broadcast(msg.msg());
						//propogateCommand(clientId, command);
						break;
					case 'd':
						_grid.movePlayer(clientId, 0, 1);
						if (isServer) network.broadcast(msg.msg());
						//propogateCommand(clientId, command);
						break;
					case 'r':
						_grid.movePlayer(clientId, 1, 0);
						if (isServer) network.broadcast(msg.msg());
						//propogateCommand(clientId, command);
						break;
					case 'l':
						_grid.movePlayer(clientId, -1, 0);
						if (isServer) network.broadcast(msg.msg());
						//propogateCommand(clientId, command);
						break;
					case 'n':
						break;
				}
				
			}
			msg = network.receiveLater();
		}
	}
	
	public void propogateCommand(int clientId, char command) {
		if (isServer) {
			for (int i = 1; i < this.numPlayers; i++) {
				if (i != clientId)
					network.send(i, "" + clientId + "," + command);
			}
		}
	}
	
	/*
		
		for (int i = 0; i<numPlayers; i++) {
			if (i != localPlayerId) {
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
*/
	private void checkUserInput() {
		// Detect key presses and speed up or slow down accordingly
		int state = getKeyStates();
		//command += this.localPlayerId;
		if(( state & DOWN_PRESSED ) != 0 ){
			_grid.movePlayer(localPlayerId, 0, 1);
			 command += this.localPlayerId + ",d";
		} else if(( state & UP_PRESSED ) != 0 ){
			_grid.movePlayer(localPlayerId, 0, -1);
			command += this.localPlayerId + ",u";
		} else if ((state & LEFT_PRESSED) != 0) {
			_grid.movePlayer(localPlayerId, -1, 0);
			command += this.localPlayerId + ",l";
		} else if ((state & RIGHT_PRESSED) != 0) {
			_grid.movePlayer(localPlayerId, 1, 0);
			command += this.localPlayerId + ",r";
		}
	}

	private void updateGameScreen(Graphics g) {
		
		
		if (command != "")
			network.broadcast(command);
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
}
