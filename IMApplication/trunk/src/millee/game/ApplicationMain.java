package millee.game;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.j2me.global.DateTimeFormat;

import millee.game.initialize.ChooseCharacter;
import millee.game.initialize.ChooseGame;
import millee.game.initialize.JoinGame;
import millee.game.initialize.StartAGame;
import millee.game.initialize.StartOrJoinGame;
import millee.game.initialize.StartScreen;
import millee.game.initialize.Utilities;
import millee.game.initialize.WinnerScreen;
import millee.network.Message;
import millee.network.Network;
import net.sf.microlog.Logger;
import net.sf.microlog.appender.ConsoleAppender;
import net.sf.microlog.appender.FileAppender;
import net.sf.microlog.appender.RecordStoreAppender;
import net.sf.microlog.format.PatternFormatter;
import net.sf.microlog.time.DateFormatter;
import net.sf.microlog.util.Properties;
/**
 * @author Priyanka
 *
 */

public class ApplicationMain extends MIDlet implements CommandListener {
	
	// Logger instance to be used everywhere
	public static final Logger log = Logger.getLogger(); //ApplicationMain.class);
	
	private static final int NUM_ROUNDS = 2;
	private static final int NUM_LEVELS = 2;
	private static final String GAME_LOOP_SOUND = "/game_loop.wav";
	
	public static final int START_SCREEN = 0;
	public static final int CHOOSE_CHAR = 1;
	public static final int CHOOSE_GAME = 2;
	public static final int JOIN_GAME = 3;
	public static final int LEVEL_START_PAGE = 4;
	public static final int WINNER_SCREEN = 5;
	public static final int START_OR_JOIN_SCREEN = 6;
	public static final int START_A_GAME = 7;
	public static final int INITIAL_LEVEL_GAME = 8;
	
	public static Display theDisplay;
	
	int characterChoice, gameChoice;
	int numLevelsLeft, numRoundsLeft;
	int localPlayerId;
	boolean isServer;
	String myName, myImagePath;
	String serverName = null;
	
	Round game;
	Display display;
	//ChooseCharacter charForm;
	StartScreen startScreen;
	ChooseGame chooseGame;
	JoinGame joinGame;
	LevelStartPage levelStartPage;
	WinnerScreen winnerScreen;
	StartOrJoinGame startOrJoinGame;
	StartAGame startAGame;
	Network network;
	InitialLevelPage initialLevelPage;
	
	private Vector _players;
	
	private List _charList = null, _startOrJoinGameList = null, _chooseNumPlayersList = null;
	private Command listSelection = null;
	private Command _exitCommand = null, _backCommand = null;
	
	private Displayable _previousDisplayable = null;
	
	public ApplicationMain () {
		theDisplay = display = Display.getDisplay(this);
		_exitCommand = new Command ("Exit", Command.EXIT, 0);
		_backCommand = new Command ("Back", Command.BACK, 1);

		network = new Network(this);
		
		startScreen = new StartScreen ("Colour Colour");		
		startScreen.setCommandListener(this);
		chooseGame = new ChooseGame ("Choose a Game");		
		chooseGame.setCommandListener(this);
		joinGame = new JoinGame ("Join a Game", network, this);		
		joinGame.setCommandListener(this);

		startOrJoinGame = new StartOrJoinGame("Start or join a game?");
		startOrJoinGame.setCommandListener(this);
		
		startAGame = new StartAGame("Start a game?", network, this);
		startAGame.setCommandListener(this);
		
		winnerScreen = new WinnerScreen("Game Over");
		winnerScreen.setCommandListener(this);
	}
	
	protected void destroyApp(boolean arg0) {
		log.info("Destroying application...");
		try {
			log.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void pauseApp() {
		log.info("Pausing application...");
	}

	protected void startApp() throws MIDletStateChangeException {
		setupLogging();
		log.info("Starting application...");

		numLevelsLeft = NUM_LEVELS;
		numRoundsLeft = NUM_ROUNDS;
		display.setCurrent(startScreen);
	}
	
	/*
	 * Setup and begin logging.
	 */
	private void setupLogging() {
		log.removeAllAppenders();
		
		// Make the pattern include time in a readable format
		PatternFormatter p = new PatternFormatter();
		p.setPattern("%d: [%P] %m %T");
		
		// Console logging
		ConsoleAppender ca = new ConsoleAppender();
		ca.setFormatter(p);
		log.addAppender(ca);
		
		// RecordStore logging...
		RecordStoreAppender ra = new RecordStoreAppender();
		//Date rightNow = new Date();
		ra.setRecordStoreName("MILLEE." + System.currentTimeMillis() + ".log");
		ra.setMaxRecordEntries(100);
		ra.setFormatter(p);
		log.addAppender(ra);
		
		/* File logging - probably will not work...
		FileAppender fileApp = new FileAppender();
		fileApp.setFileName("MILLEE." + System.currentTimeMillis() + ".log");
		log.addAppender(fileApp);
		log.addAppender(new ConsoleAppender());
		*/
	}
	
	private void quit() {
		destroyApp(true);
		notifyDestroyed();
	}
	
	public void commandAction(Command c, Displayable d) {
        		
		if (c.getCommandType() == Command.EXIT) {
			quit();
		} else if (c.getCommandType() == Command.BACK) {
			display.setCurrent(_previousDisplayable);
		} else if (c == startScreen.getStartCommand()) {
			_previousDisplayable = startScreen;
			//display.setCurrent(getCharacterChoiceList());
			display.setCurrent(getStartOrJoinGameList());
		} else if (c.getCommandType() == Command.CANCEL) {
			display.setCurrent(getStartOrJoinGameList());
		} else if (c == List.SELECT_COMMAND && d == _charList) {
			characterChoice = _charList.getSelectedIndex();
			
			// TODO: Not hardcode this, find better player pictures
			if (characterChoice == 0) {
				myName = "Raj";
				myImagePath = "/dancer_1.png";
			}
			else if (characterChoice == 1) {
				myName = "Sri";
				myImagePath = "/dancer_2.png";
			}
			else if (characterChoice == 2) {
				myName = "Neha";
				myImagePath = "/dancer_3.png";
			}
			
			_previousDisplayable = getCharacterChoiceList();
			display.setCurrent(getStartOrJoinGameList());
		} else if (c == List.SELECT_COMMAND && d == _startOrJoinGameList) {
			_previousDisplayable = getStartOrJoinGameList();
			if (_startOrJoinGameList.getSelectedIndex() == 0) {
				ApplicationMain.log.info("I am the server.");
				isServer = true;
				display.setCurrent(getChooseNumPlayersList());
			} else {
				ApplicationMain.log.info("I am the client");
				isServer = false;
				joinGame.initClient();
				joinGame.setCharacterChoice(characterChoice);
				joinGame.setGameChoice(gameChoice);
				display.setCurrent(joinGame);
			}
			// startMusic(); -- ...no
		} else if (c == List.SELECT_COMMAND && d == _chooseNumPlayersList) {
			ApplicationMain.log.info("Number of players = " + _chooseNumPlayersList.getSelectedIndex());
			startAGame.start(_chooseNumPlayersList.getSelectedIndex());
			display.setCurrent(startAGame);
			startAGame.startNetwork();
		} else if (c == initialLevelPage.getStartCommand()) {
			network.broadcast(Message.GO);
			game = createNewRound();
			numLevelsLeft--;
			game.start();
			display.setCurrent(game);
		} else if (c == game.getOkCommand()) {
			
			/*if (numLevelsLeft <= 0 && numRoundsLeft <= 0) {		// end of game
				numRoundsLeft--;
				display.setCurrent(winnerScreen);
			} else if (numRoundsLeft <= 0) {	// end of current level
				game.hideNotify();
				numRoundsLeft = NUM_ROUNDS;
				levelStartPage = new LevelStartPage("Level 1", network, this.characterChoice, isServer, myName, myImagePath, this);
				levelStartPage.setCommandListener(this);
				display.setCurrent(levelStartPage);
			} else if (numRoundsLeft > 0) {	// end of current round, move on to next round
				game.hideNotify();

				ApplicationMain.log.info("end of round.  start of next round");
				if (isServer) {
					network.broadcast(Message.GO);
					game = createNewRound();
					game.start();
					display.setCurrent(game);
					
				}
			}*/	
			
		/*if (numLevelsLeft <= 0 && numRoundsLeft <= 0) {		// end of game
			numRoundsLeft--;
			display.setCurrent(winnerScreen);
		}*/
			//if (numRoundsLeft <= 0) {	// end of current level
				//display.setCurrent(winnerScreen);
			//} else if (numRoundsLeft > 0) {	// end of current round, move on to next round
				game.hideNotify();
				
				if (isServer) {
					network.broadcast(Message.GO);
					game = createNewRound();
					game.start();
					display.setCurrent(game);
				}
		} else if (c == game.getNoCommand()) {
			winnerScreen.start(isServer, network.clientServer.getServerName(), _players);
			display.setCurrent(winnerScreen);
			network.broadcast(Message.GAME_OVER);
			network.clientServer.closeAllConnections();
		} else if (c == levelStartPage.getStartCommand()) {
			game = createNewRound();
			game.start();
			if (isServer) network.broadcast(Message.GO);
			if (!isServer && (network.receiveNow().equals(Message.GO)));
			display.setCurrent(game);
		}
		else {
			ApplicationMain.log.info("Shouldn't come here: Sorry your keypresses didn't match anything here");
		}
    }

	/*public void updateDevicesDiscovered(Vector devicesDiscoveredNames) {
		ApplicationMain.log.info("in updateDevicesDiscovered");
		chooseGame = new ChooseGame ("Choose a Game", devicesDiscoveredNames);
		chooseGame.setCommandListener(this);
		display.setCurrent(chooseGame);
	}*/
	
	/*public void addMsgToGameScreen(int gameScreen, String msg) {
		switch (gameScreen) {
			case START_SCREEN: startScreen.addMessage(msg);
			case CHOOSE_CHAR: charForm.addMessage(msg);
			case CHOOSE_GAME: chooseGame.addMessage(msg);
			case JOIN_GAME: joinGame.addMessage(msg);
			case LEVEL_START_PAGE: levelStartPage.addMessage(msg);
			case WINNER_SCREEN: winnerScreen.addMessage(msg);
			case START_OR_JOIN_SCREEN: startOrJoinGame.addMessage(msg);
			case START_A_GAME: startAGame.addMessage(msg);
			case INITIAL_LEVEL_GAME: initialLevelPage.addMessage(msg);
		}
	}*/
	
	public void replaceMsgOnGameScreen(int gameScreen, String msg) {
		switch (gameScreen) {
			case START_SCREEN: startScreen.replaceLastMessage(msg);
			//case CHOOSE_CHAR: charForm.replaceLastMessage(msg);
			case CHOOSE_GAME: chooseGame.replaceLastMessage(msg);
			case JOIN_GAME: joinGame.replaceLastMessage(msg);
			case LEVEL_START_PAGE: levelStartPage.replaceLastMessage(msg);
			case WINNER_SCREEN: winnerScreen.replaceLastMessage(msg);
			case START_OR_JOIN_SCREEN: startOrJoinGame.replaceLastMessage(msg);
			case START_A_GAME: startAGame.replaceLastMessage(msg);
			//case INITIAL_LEVEL_GAME: initialLevelPage.replaceLastMessage(msg);
		}
	}
	
	
	public void fullyConnected() {
		
		myName = network.clientServer.getDeviceName();
		initialLevelPage = new InitialLevelPage("Colour, Colour", network, this.characterChoice, isServer, myName, myImagePath, this);
		initialLevelPage.setCommandListener(this);
		display.setCurrent(initialLevelPage);
		
		if (isServer) {
			ApplicationMain.log.debug("About to setup players");
			_players = initialLevelPage.setupPlayers(myName, myImagePath);
			ApplicationMain.log.debug("before adding command to initialLevelPage");
			initialLevelPage.addCommand(initialLevelPage.getStartCommand());
			ApplicationMain.log.debug("after adding command");
		}
		else {
			localPlayerId = initialLevelPage.sendPlayerInfo(myName, myImagePath);
			_players = initialLevelPage.createPlayersByClients();
			
			Message msg = network.receiveNow();
			if (msg.msg().equals(Message.GO)) {
				ApplicationMain.log.info("Waiting for server to start the round.");
				startGame();
			}
			
		}
	}
	
	public void waitForServer() {
		String input;

		game.hideNotify();

		ApplicationMain.log.info("Waiting for server to start the round.");
		
		while (true) {
			input = network.receiveNow().msg();
			
			
			if (input.equals(Message.GAME_OVER)) {
				ApplicationMain.log.info("Server ended the game");
				winnerScreen.start(isServer, network.clientServer.getServerName(), _players);
				network.clientServer.closeAllConnections();
				display.setCurrent(winnerScreen);
				break;
			}
			else if (input.equals(Message.GO)) {
				ApplicationMain.log.info("Server started the game");
				game = createNewRound();
				game.start();
				display.setCurrent(game);
				break;
			}
		}
		
	}
	
	public void startGame() {
		game = createNewRound();
		numLevelsLeft--;
		game.start();
		display.setCurrent(game);
	}
	
	private Round createNewRound() {
		/* Not needed any more...
		
		int[] scoreAssignment = new int[3];
		String[] possibleTokenPaths = new String[4], possibleTokenText = new String[4];

		scoreAssignment[0] = 5;
		scoreAssignment[1] = 10;
		scoreAssignment[2] = 20;
		
		possibleTokenPaths[0] = "/tomato.png";
		possibleTokenPaths[1] = "/tomato.png";
		possibleTokenPaths[2] = "/tomato.png";
		possibleTokenPaths[3] = "/tomato.png";
		
		possibleTokenText[0] = "Red";
		possibleTokenText[1] = "Blue";
		possibleTokenText[2] = "Green";
		possibleTokenText[3] = "Yellow";
		*/
		
		Round game = new Round(this, _players, isServer, network, localPlayerId);
		game.setCommandListener(this);
		return game;
	}
	
	private List getCharacterChoiceList() {
		
		if (_charList == null) {
		
			Image rajImage = Utilities.createImage("/dancer_1.png");
	        Image sriImage = Utilities.createImage("/dancer_2.png");
	        Image nehaImage = Utilities.createImage("/dancer_3.png");
			
			_charList = new List("Characters", List.IMPLICIT);
			_charList.append("Raj", rajImage);
			_charList.append("Sri", sriImage);
			_charList.append("Neha", nehaImage);
			
			_charList.addCommand(_backCommand);
			_charList.addCommand(_exitCommand);
			_charList.setCommandListener(this);
		} 
		
		return _charList;	
	}
	
	private List getStartOrJoinGameList() {
		
		if (_startOrJoinGameList == null) {
			listSelection = new Command("Select",Command.OK,0);
	
			_startOrJoinGameList = new List("Start or Join game?", List.IMPLICIT);
			
			_startOrJoinGameList.append("Start a Game", null);
			_startOrJoinGameList.append("Join a Game", null);
			
			
			_startOrJoinGameList.setCommandListener(this);
			_startOrJoinGameList.addCommand(_exitCommand);
			_startOrJoinGameList.addCommand(_backCommand);
		}
		
		return _startOrJoinGameList;		
	}
	
	private List getChooseNumPlayersList() {
		
		if (_chooseNumPlayersList == null) {
			listSelection = new Command("Select",Command.OK,0);
	
			_chooseNumPlayersList = new List("How many players in the game?", List.IMPLICIT);
			
			_chooseNumPlayersList.append("1 player", null);
			_chooseNumPlayersList.append("2 players", null);
			_chooseNumPlayersList.append("3 players", null);
			_chooseNumPlayersList.append("4 players", null);
			
			_chooseNumPlayersList.setCommandListener(this);
			_chooseNumPlayersList.addCommand(_exitCommand);
			_chooseNumPlayersList.addCommand(_backCommand);
		}
		
		return _chooseNumPlayersList;		
	}
	
	/**
	 * Start some infinite looping music...
	 *
	private void startMusic() {
        // Play some music!
		try {
			InputStream is = getClass().getResourceAsStream(GAME_LOOP_SOUND); 
		    Player p = Manager.createPlayer(is, "audio/X-wav"); 
		    p.setLoopCount(-1);
		    
		    p.prefetch(); // prefetch
		    p.realize(); // realize
		    p.start(); // and start
		    
		    // Free resources
		    is.close();
		    is = null;
		}
		catch (IOException ioe) { }
		catch (MediaException me) { } 
	}
	*/
}
