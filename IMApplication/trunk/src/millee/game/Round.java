package millee.game;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import millee.game.initialize.Utilities;
import millee.game.mechanics.GameGrid;
import millee.game.mechanics.Goodie;
import millee.game.mechanics.Player;
import millee.network.Message;
import millee.network.Network;

/**
 * @author Priyanka
 *
 */
public class Round extends GameCanvas implements Runnable {
	
	// Class elements
	private volatile Thread thread;
	private Random random;
	private ApplicationMain _app;
	
	// Stuff for drawing
	private Graphics graphics;
	private Command noCmd, okCmd;
	private int _cellWidth, _cellHeight;
	
	// Data structures of the game
	private GameGrid _grid;
	private Vector _players;
	
	// Status indicators
	private boolean stopGame = false;
	private String command = "";
	
	private int _nPlayers, _roundID, _levelID;
	private boolean _bLastRound;
	private String _levelName;
	private int _numGoodies;
	private int localPlayerID;
	private boolean isServer;
	private Network _network;
	
	// Constants
	private static final int SLEEP_TIME = 400;
	private static final int TILE_DIMENSIONS = 20;

	
	/**
	 * Constructor for Round	
	 * @param players
	 * @param round
	 * @param level
	 * @param lastRoundInLevel
	 * @param levelName
	 * @param totalNumGoodies
	 * @param isServer
	 * @param network
	 * @param localPlayerId
	 */
	public Round (ApplicationMain app, Vector players, int round, int level, boolean lastRoundInLevel, 
					String levelName, int totalNumGoodies,
					boolean isServer, Network network, int localPlayerId) {
		
		super(true);
		// this.setFullScreenMode(true);
		
		this._nPlayers = players.size();
		this._players = players;
		
		this._cellWidth = this.getWidth()/TILE_DIMENSIONS;
		this._cellHeight = this.getHeight()/TILE_DIMENSIONS;
		
		this._roundID = round;
		this._levelID = level;
		this._bLastRound = lastRoundInLevel;
		this._levelName = levelName;
		
		this._numGoodies = totalNumGoodies;
		
		this.isServer = isServer;
		this._network = network;
		
		this.localPlayerID = localPlayerId;
		
		this._app = app;
		noCmd = new Command("No", Command.STOP, 0);
	}
	
	/**
	 * Getter for the OK command
	 * @return Command OkCommand
	 */
	public Command getOkCommand() {
		return this.okCmd;
	}
	
	/**
	 * Getter for the Exit command
	 * @return Command ExitCommand
	 */
	public Command getNoCommand() {
		return this.noCmd;
	}
	
	/**
	 * Server-only. Takes the players in this round, configures them and then broadcasts
	 * the entire setup to the other players.
	 */
	private void serverBuildGridAndBroadcast() {
	
		Player p = null;
		int i, goodieType;
		
		// Random coordinates
		int x = random.nextInt(_cellWidth);
		int y = random.nextInt(_cellHeight);
		
		StringBuffer broadcastString = new StringBuffer("");
		
		// Assign player coordinates
		for (i = 0; i < _nPlayers; i++) {
			p = (Player) _players.elementAt(i);
			
			// Don't overlap players
			while (_grid.hasPlayerAt(x, y)) {
				x = random.nextInt(_cellWidth);
				y = random.nextInt(_cellHeight);
			}
			
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
		for (i = 0; i < _numGoodies; i++) {
			goodieType = random.nextInt(_nPlayers)+1;

			// Don't put goodies in cells that already have them or where players are
			while (_grid.hasPlayerAt(x, y) || _grid.hasGoodieAt(x, y)) {
				x = random.nextInt(_cellWidth);
				y = random.nextInt(_cellHeight);
			}
			
			_grid.insertGoodie(new Goodie(goodieType), x, y);
			broadcastString.append(goodieType);
			broadcastString.append(',');
			broadcastString.append(x);
			broadcastString.append(',');
			broadcastString.append(y);
			broadcastString.append(';');
		}
		
		// Broadcast this information to all clients
		_network.broadcast(broadcastString.toString());
	}
	
	/**
	 * Client-only. Takes the broadcast and breaks it down, reconstructing the intitial
	 * board state.
	 */
	private void buildGridFromBroadcast() {
		
		String input = null;
		while ((input = _network.receiveNow().msg()).indexOf("|") < 0) {
			continue;
		}
		
		String[] blocks = Utilities.split(input, "|", 2);
		
		// Get the player information
		String[] sPlayers = Utilities.split(blocks[0], ";", 0);
		String[] playerInfo = null;
		Player tmpPlayer = null;
		int x,y;
		//ApplicationMain.log.info("sPlayers = " + sPlayers);
		for (int i = 0; i < sPlayers.length; i++) {
			//ApplicationMain.log.info("splayers[" + i + "] = " + sPlayers[i]);
			//ApplicationMain.log.info("putting player " + i + " on the board");
			playerInfo = Utilities.split(sPlayers[i], ",", 3);
			//ApplicationMain.log.info("playerInfo = " + playerInfo);
			// TODO: Deal with the lack of a name, image
			tmpPlayer = (Player) _players.elementAt(i);
			//ApplicationMain.log.info("player's name is " + tmpPlayer);
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
	}
	
	/**
	 * How to tell the Round to begin.
	 */
	public void start() {

		random = new Random();
		graphics = getGraphics();

		// Populate it and tell others, or populate it from the information received
		_grid = new GameGrid(_cellWidth,_cellHeight);
		
		if (isServer) {	serverBuildGridAndBroadcast(); }
		else { buildGridFromBroadcast(); }
		
		// Kick off the thread
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

	/**
	 * The main game loop. Runs continuously as long as this canvas is active.
	 */
	public void run(){
		while( thread == Thread.currentThread() && !stopGame) {
			
			// Check if the grid is empty
			if (_grid.isWon()) {
				stopGame = true;
				for (int i = 0; i < _players.size(); i++) {
					((Player) _players.elementAt(i)).flushGoodieStack();
				}
				ApplicationMain.log.info("Game has ended");
			}
			
			// If the game isn't over, look for inputs
			if (!stopGame) {
				checkUserInput();
				checkRemotePlayerInput();
			}
			
			// If there is a command pending, broadcast it to all other players
			if (!command.equals("")) {
				_network.broadcast(command);
				command = "";
			}
			
			// Draw stuff to screen
			updateGameScreen();
			
			// Now wait...
			try {
				Thread.sleep(SLEEP_TIME);
			}
			catch( InterruptedException e ){}
		}
	}
	
	private void checkRemotePlayerInput() {
		Message msg = _network.receiveLater();
		char command;
		int clientId;
		String[] msgs = null;
		while (msg != null) {
			
			msgs = Utilities.split(msg.msg(), ",", 2);
			clientId = Integer.parseInt(msgs[0]);
			
			if (clientId != localPlayerID) {
				command = msgs[1].charAt(0);
				
				switch (command) {
					case 'u':
						_grid.movePlayer(clientId, 0, -1);
						if (isServer) _network.broadcast(msg.msg());
						break;
					case 'd':
						_grid.movePlayer(clientId, 0, 1);
						if (isServer) _network.broadcast(msg.msg());
						break;
					case 'r':
						_grid.movePlayer(clientId, 1, 0);
						if (isServer) _network.broadcast(msg.msg());
						break;
					case 'l':
						_grid.movePlayer(clientId, -1, 0);
						if (isServer) _network.broadcast(msg.msg());
						break;
					case 'x':
						_grid.playerDrop(clientId);
						if (isServer) _network.broadcast(msg.msg());
						break;
					case 'n':
						break;
				}
				
			}
			msg = _network.receiveLater();
		}
	}
	
	/**
	 * Sends individual commands out onto the network
	 * @param clientId
	 * @param command
	 *
	public void propagateCommand(int clientId, char command) {
		if (isServer) {
			for (int i = 1; i < this.numPlayers; i++) {
				if (i != clientId)
					network.send(i, "" + clientId + "," + command);
			}
		}
	}
	*/
	
	/**
	 * Detects key presses and moves player on grid
	 */
	private void checkUserInput() {
		// Detect key presses
		int state = getKeyStates();
		if(( state & DOWN_PRESSED ) != 0 ){
			_grid.movePlayer(localPlayerID, 0, 1);
			 command += this.localPlayerID + ",d";
		} else if(( state & UP_PRESSED ) != 0 ){
			_grid.movePlayer(localPlayerID, 0, -1);
			command += this.localPlayerID + ",u";
		} else if (( state & LEFT_PRESSED ) != 0) {
			_grid.movePlayer(localPlayerID, -1, 0);
			command += this.localPlayerID + ",l";
		} else if (( state & RIGHT_PRESSED ) != 0) {
			_grid.movePlayer(localPlayerID, 1, 0);
			command += this.localPlayerID + ",r";
		} else if (( state & this.FIRE_PRESSED ) != 0) {
			// Drop a goodie command
			_grid.playerDrop(localPlayerID);
			command += this.localPlayerID + ",x";
		}
	}

	/**
	 * Draw things to the screen
	 */
	private void updateGameScreen() {
		// Blank out the screen
		graphics.setColor(0,0,0);
		graphics.fillRect(0,0,getWidth(),getHeight());
		
		//ApplicationMain.log.info("Your score: " + scores[this.localPlayerID]);
		if (stopGame) {
			// End game drawing
			this.setFullScreenMode(false);

			this.addCommand(noCmd);
			okCmd = new Command("Yes", Command.OK, 1);

			String scoreReport = "";
			Player p;
			for (int i = 0; i<_players.size(); i++) {
				p = ((Player) _players.elementAt(i));
				scoreReport += p.getName() + ": " + p.getScore() + "\n";
			}

			displayNotification("Current Scores", scoreReport);
			
			if (isServer) {
				setFloatingStatusMessage("Round Complete!");
				set2LineStatusMessage("Round Complete!", "Start New Round?");
				this.addCommand(okCmd);
			} else {
				set3LineStatusMessage("Round Complete!", "Waiting for server to", "start next round . . .");
				Thread thread = new Thread(new Runnable () {public void run() {_app.waitForServer();}});
				thread.start();

			}
		} else {
			// Tell the grid to redraw itself
			_grid.redraw(graphics);
			
			// Display messages for local player
			Player p = (Player) _players.elementAt(localPlayerID);
			//setStatusMessage("" + p.getScore());
			
			// Draw goodie stack
			p.getGoodieStack().redraw(graphics, 0, getHeight()-TILE_DIMENSIONS+5);
			if (!p.hasCorrectGoodies()) { setFloatingStatusMessage("DROP!"); }
			
			setLowerStatusMessage("Collect " + colorFromID(p.assignedColor()));
		}
		
		// Draw graphics to the screen
		flushGraphics();
	}
	
	private void setFloatingStatusMessage(String msg) {
		graphics.setColor(0,0,0);
		graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
		graphics.drawString(msg, 3, _cellHeight*TILE_DIMENSIONS, Graphics.BOTTOM | Graphics.LEFT);
	}
	
	private void set3LineStatusMessage(String msg1, String msg2, String msg3) {
		graphics.setColor(255,255,255);
		graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		int fontHeight = graphics.getFont().getHeight();
		graphics.drawString(msg1, 0, getHeight()-2*fontHeight, Graphics.BOTTOM | Graphics.LEFT);
		graphics.drawString(msg2, 0, getHeight()-fontHeight, Graphics.BOTTOM | Graphics.LEFT);
		graphics.drawString(msg3, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
	}
	
	private void set2LineStatusMessage(String msg1, String msg2) {
		graphics.setColor(255,255,255);
		graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		int fontHeight = graphics.getFont().getHeight();
		graphics.drawString(msg1, 0, getHeight()-fontHeight, Graphics.BOTTOM | Graphics.LEFT);
		graphics.drawString(msg2, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
	}
	
	private void setLowerStatusMessage(String msg) {
		graphics.setColor(255,255,255);
		graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		graphics.drawString(msg, getWidth(), getHeight(), Graphics.BOTTOM | Graphics.RIGHT);
	}
	
	private void displayNotification(String title, String msg) {
		// TODO: Get this box in the middle of the screen
		//graphics.setColor(0,0,0);
		//graphics.fillRect(getWidth()/4,getHeight()/4,getWidth()/2,getHeight()/2);
		
		graphics.setColor(255,255,255);
		graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		graphics.drawString(title, 0, 0, Graphics.TOP | Graphics.LEFT);
		
		graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
		int fontHeight = graphics.getFont().getHeight();

		// Draw string per line
		String[] lines = Utilities.split(msg, "\n", 0);
		for (int i = 0; i < lines.length; i++) {
			graphics.drawString(lines[i], 0, (i+1)*fontHeight, Graphics.TOP | Graphics.LEFT);
		}
	}
	
	/**
	 * Mapping between ID number and a color string
	 * @param id
	 * @return String
	 */
	private String colorFromID(int id) {
		switch (id) {
			case 1: return "BLACK";
			case 2: return "RED";
			case 3: return "GREEN";
			case 4: return "BLUE";
			default: return "UNKNOWN_COLOR";
		}
	}
}