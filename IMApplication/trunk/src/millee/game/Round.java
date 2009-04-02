package millee.game;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import millee.game.initialize.Utilities;
import millee.game.mechanics.GameController;
import millee.network.Message;
import millee.network.Network;

/**
 * @author Priyanka
 *
 */
public class Round extends GameCanvas implements Runnable {
	// Meta variables
	private volatile Thread thread;
	private ApplicationMain _app;
	
	// Visual stuff
	public static Graphics graphics;
	private Command noCmd, okCmd;
	
	// Status variables
	private boolean _bStop = false;
	private String _sCommand = "";
	private static int _roundID = 0;

	// Stuff about ourselves
	private int _localPlayerID;
	private boolean isServer;
	
	// Main objects
	private GameController _game;
	private Network _network;
	
	// Constants
	private static final int SLEEP_TIME = 300;
	
	/**
	 * Constructor for a new Round
	 * @param app
	 * @param players
	 * @param isServer
	 * @param network
	 * @param localPlayerId
	 */
	public Round (ApplicationMain app, Network network, Vector players, int localPlayerID, boolean isServer) {
		super(true);
		
		this._app = app;
		this._network = network;
		this._localPlayerID = localPlayerID;
		this.isServer = isServer;
		
		_game = new GameController(players, getWidth(), getHeight());
		
		noCmd = new Command("No", Command.STOP, 0);
		_roundID++;
	}
	
	/**
	 * How to tell the Round to begin.
	 */
	public void start() {
		// Initialize the graphics
		graphics = getGraphics();

		String config;
		if (isServer) {
			// Build and Broadcast configuration to all clients
			config = _game.buildConfiguration().toString();
			_network.broadcast(Message.GO + config);
			ApplicationMain.log.info("Server broadcasts configuration: " + config);
		}
		else {
			// Poll the network until we get the configuration
			while (!(config = _network.receiveNow().msg()).startsWith(Message.GO)) { }
			ApplicationMain.log.info("Client receives configuration: " + config.toString().substring(2));
			_game.buildFromConfiguration(config.toString());
		}
		
		// Pick which GoodieStack to render
		_game.loadGoodieStackVisual(_localPlayerID);
		
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
	public void run() {
		while( thread == Thread.currentThread() && !_bStop) {
			
			// Check if the game believes it's over
			if (_game.isDone()) {
				_bStop = true; // Throw flag
				ApplicationMain.log.info("Round " + _roundID + " has ended.");
			}
			
			// If the game isn't over, look for inputs
			if (!_bStop) {
				checkUserInput();
				
				// If there is a command pending, broadcast it to all other players
				if (!_sCommand.equals("")) {
					_network.broadcast(_sCommand);
					_sCommand = "";
				}

				checkRemotePlayerInput();
			}
			
			// Clear the screen
			graphics.setColor(0,0,0);
			graphics.fillRect(0,0,getWidth(),getHeight());
			
			// Draw stuff on it depending on whether the game is over or not
			if (_bStop) { showEndGame(); }
			else { _game.updateScreen(_localPlayerID); }
			flushGraphics();
			
			// Now wait...
			try {
				Thread.sleep(SLEEP_TIME);
				_game.clockTick();
			}
			catch( InterruptedException e ){
				ApplicationMain.log.error("Round Thread was interrupted!");
			}
		}
	}
	
	/**
	 * Detects key presses and moves player on grid
	 * @return boolean Whether an action was taken
	 */
	private boolean checkUserInput() {
		boolean hasAction = false;
		char command = 0;
		
		// Detect key presses
		int state = getKeyStates();
		ApplicationMain.log.trace(Integer.toBinaryString(state));
		
		if(( state & DOWN_PRESSED ) != 0 ){
			command = 'd';
			_sCommand += _localPlayerID + "," + command + ",";
			if (isServer) { _game.interpretCommand(_localPlayerID, command); }
			hasAction = true;
		}
		if(( state & UP_PRESSED ) != 0 ){
			command = 'u';
			_sCommand += _localPlayerID + "," + command + ",";
			if (isServer) { _game.interpretCommand(_localPlayerID, command); }
			hasAction = true;
		}
		if (( state & LEFT_PRESSED ) != 0) {
			command = 'l';
			_sCommand += _localPlayerID + "," + command + ",";
			if (isServer) { _game.interpretCommand(_localPlayerID, command); }
			hasAction = true;
		}
		if (( state & RIGHT_PRESSED ) != 0) {
			command = 'r';
			_sCommand += _localPlayerID + "," + command + ",";
			if (isServer) { _game.interpretCommand(_localPlayerID, command); }
			hasAction = true;
		}
		if (( state & Round.FIRE_PRESSED ) != 0) {
			command = 'x';
			_sCommand += _localPlayerID + "," + command + ",";
			if (isServer) { _game.interpretCommand(_localPlayerID, command); }
			hasAction = true;
		}
		
		return hasAction;
	}
	
	private void checkRemotePlayerInput() {
		Message msg = _network.receiveLater();
		String[] msgs = null;
		while (msg != null) {
			msgs = Utilities.split(msg.msg(), ",", 0);
			//ApplicationMain.log.debug(msgs);
			
			// Server - broadcast it first to minimize everyone else's lag
			if (isServer) _network.broadcast(msg.msg());
			
			for (int i = 0; i < msgs.length / 2; i++) {
				//ApplicationMain.log.debug(msgs[i*2]);
				//ApplicationMain.log.debug(msgs[i*2+1]);
				
				_game.interpretCommand(Integer.parseInt(msgs[i*2]), msgs[i*2+1].charAt(0));
			}
			
			msg = _network.receiveLater();
		}
	}
	
	private void showEndGame() {
		// Add new commands to the screen
		this.addCommand(noCmd);
		okCmd = new Command("Yes", Command.OK, 1);

		showFullNotification("Current Scores", _game.generateScoreReport());
		
		if (isServer) {
			_game.setFloatingStatusMessage("Round Complete!");
			show2LineStatusMessage("Round Complete!", "Start New Round?");
			this.addCommand(okCmd);
		} else {
			show3LineStatusMessage("Round Complete!", "Waiting for server to", "start next round . . .");
			Thread thread = new Thread(new Runnable () {public void run() {_app.waitForServer();}});
			thread.start();
		}
	}

	private void showFullNotification(String title, String msg) {
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
	
	private void show2LineStatusMessage(String msg1, String msg2) {
		graphics.setColor(255,255,255);
		graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		int fontHeight = graphics.getFont().getHeight();
		graphics.drawString(msg1, 0, getHeight()-fontHeight, Graphics.BOTTOM | Graphics.LEFT);
		graphics.drawString(msg2, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
	}
	
	private void show3LineStatusMessage(String msg1, String msg2, String msg3) {
		graphics.setColor(255,255,255);
		graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		int fontHeight = graphics.getFont().getHeight();
		graphics.drawString(msg1, 0, getHeight()-2*fontHeight, Graphics.BOTTOM | Graphics.LEFT);
		graphics.drawString(msg2, 0, getHeight()-fontHeight, Graphics.BOTTOM | Graphics.LEFT);
		graphics.drawString(msg3, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
	}
	
	/**
	 * Getter for the OK command
	 * @return Command OkCommand
	 */
	protected Command getOkCommand() {
		return this.okCmd;
	}
	
	/**
	 * Getter for the Exit command
	 * @return Command ExitCommand
	 */
	protected Command getNoCommand() {
		return this.noCmd;
	}
}