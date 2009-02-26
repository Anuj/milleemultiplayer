package millee.game;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

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
/**
 * @author Priyanka
 *
 */

public class ApplicationMain extends MIDlet implements CommandListener {
	
	private static final int NUM_ROUNDS = 2;
	private static final int NUM_LEVELS = 2;
	private static final String GAME_LOOP_SOUND = "/game_loop.wav";
	
	public static Display theDisplay;
	
	int characterChoice, gameChoice;
	int numLevelsLeft, numRoundsLeft;
	int localPlayerId;
	boolean isServer;
	String myName, myImagePath;
	
	Round game;
	Display display;
	ChooseCharacter charForm;
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
	
	private List _charList = null, _startOrJoinGameList = null;
	private Command listSelection = null;
	private Command _exitCommand = null, _backCommand = null;
	
	private Displayable _previousDisplayable = null;
	
	public ApplicationMain () {
		theDisplay = display = Display.getDisplay(this);
		_exitCommand = new Command ("Exit", Command.EXIT, 0);
		_backCommand = new Command ("Back", Command.BACK, 1);

		network = new Network(this);
		
		charForm = new ChooseCharacter("Choose your character");
		charForm.setCommandListener(this);
		startScreen = new StartScreen ("Colour Colour");		
		startScreen.setCommandListener(this);
		chooseGame = new ChooseGame ("Choose a Game");		
		chooseGame.setCommandListener(this);
		joinGame = new JoinGame ("Join a Game", network);		
		joinGame.setCommandListener(this);

		startOrJoinGame = new StartOrJoinGame("Start or join a game?");
		startOrJoinGame.setCommandListener(this);
		
		startAGame = new StartAGame("Start a game?", network);
		startAGame.setCommandListener(this);
		
		winnerScreen = new WinnerScreen("Winner!");
		winnerScreen.setCommandListener(this);
		
	}
	protected void destroyApp(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
		
	}

	protected void startApp() throws MIDletStateChangeException {
		
		numLevelsLeft = NUM_LEVELS;
		numRoundsLeft = NUM_ROUNDS;
		//game.start();
		//display.setCurrent(game);
		display.setCurrent(startScreen);
	}
	
	void quit() {
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
			display.setCurrent(getCharacterChoiceList());
		} else if (c.getCommandType() == Command.CANCEL) {
			display.setCurrent(getStartOrJoinGameList());
		} else if (c == List.SELECT_COMMAND && d == _charList) {
			characterChoice = _charList.getSelectedIndex();
			myImagePath = "/dancer_small.png";
			if (characterChoice == 0) myName = "Raj";
			else if (characterChoice == 1) myName = "Sri";
			else if (characterChoice == 2) myName = "Neha";
			_previousDisplayable = getCharacterChoiceList();
			display.setCurrent(getStartOrJoinGameList());
		} else if (c == List.SELECT_COMMAND && d == _startOrJoinGameList) {
			_previousDisplayable = getStartOrJoinGameList();
			if (_startOrJoinGameList.getSelectedIndex() == 0) {
				isServer = true;
				display.setCurrent(startAGame);
				startAGame.setupNetworkPlayers(myName, myImagePath);
			} else {
				isServer = false;
				joinGame.setCharacterChoice(characterChoice);
				joinGame.setGameChoice(gameChoice);
				joinGame.initClient();
				display.setCurrent(joinGame);
			}
			// startMusic(); -- ...no
		} else if (c == initialLevelPage.getStartCommand()) {
			network.broadcast("go");
			System.out.println("before creating new round");
			game = createNewRound();
			System.out.println("after creating new round");
			numLevelsLeft--;
			System.out.println("before starting game");
			game.start();
			System.out.println("after starting game");
			display.setCurrent(game);
		} else if (c == game.getOkCommand()) {
			System.out.println("start of game.okCmd");
			numRoundsLeft--;
			
			System.out.println("numLevelsLeft: " + numLevelsLeft + ", numRoundsLeft: " + numRoundsLeft);
			if (numLevelsLeft <= 0 && numRoundsLeft <= 0) {		// end of game
				display.setCurrent(winnerScreen);
			} else if (numRoundsLeft <= 0) {	// end of current level
				game.hideNotify();
				numRoundsLeft = NUM_ROUNDS;
				levelStartPage = new LevelStartPage("Level 1", network, this.characterChoice, isServer, myName, myImagePath, this);
				levelStartPage.setCommandListener(this);
				display.setCurrent(levelStartPage);
			} else if (numRoundsLeft > 0) {	// end of current round, move on to next round
				game.hideNotify();
				game = createNewRound();
				game.start();
				display.setCurrent(game);
			}
			
		}else if (c == levelStartPage.getStartCommand()) {
			game = createNewRound();
			numLevelsLeft--;
			game.start();
			display.setCurrent(game);
		}
		else {
			System.out.println("Shouldn't come here: Sorry your keypresses didn't match anything here");
		}
		
		/** TODO: Fix this incomplete statement
		while (Thread.currentThread() == game) {
			wait();
		}
		*/
    }

	
	public void fullyConnected() {
		
		System.out.println("inside fullyConnected()");
		
		initialLevelPage = new InitialLevelPage("Level 1", network, this.characterChoice, isServer, myName, myImagePath, this);
		initialLevelPage.setCommandListener(this);
		System.out.println("About to display the levelStartPage");
		display.setCurrent(initialLevelPage);
		
		if (isServer) {
			_players = initialLevelPage.setupPlayers(myName, myImagePath);
			initialLevelPage.addCommand(initialLevelPage.getStartCommand());
		}
		else {
			localPlayerId = initialLevelPage.sendPlayerInfo(myName, myImagePath);
			_players = initialLevelPage.createPlayersByClients();
			
			Message msg = network.receiveNow();
			if (msg.msg().equals("go")) {
				startGame();
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
		String[] playerNames = new String[3], playerImagePaths = new String[3];
		int[] scoreAssignment = new int[3];
		String[] possibleTokenPaths = new String[4], possibleTokenText = new String[4];
		
		playerNames[0] = "Raj";
		playerNames[1] = "Sri";
		playerNames[2] = "Neha";
		
		playerImagePaths[0] = "/dancer_small.png";
		playerImagePaths[1] = "/dancer_small.png";
		playerImagePaths[2] = "/dancer_small.png";
		
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
		
		
		Round game = new Round(_players, "/tiles.png", numRoundsLeft, numLevelsLeft, false, "Colours",
								scoreAssignment, possibleTokenPaths, possibleTokenText, 4, isServer, network, localPlayerId);
		game.setCommandListener(this);
		return game;
	}
	
	private List getCharacterChoiceList() {
		
		if (_charList == null) {
		
			Image rajImage = Utilities.createImage("/flower2.png");
	        Image sriImage = Utilities.createImage("/mainScreen.png");
	        Image nehaImage = Utilities.createImage("/flower2.png");
			
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
