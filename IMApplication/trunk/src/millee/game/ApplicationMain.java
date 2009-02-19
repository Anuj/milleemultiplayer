package millee.game;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import millee.game.initialize.ChooseCharacter;
import millee.game.initialize.ChooseGame;
import millee.game.initialize.JoinGame;
import millee.game.initialize.StartAGame;
import millee.game.initialize.StartOrJoinGame;
import millee.game.initialize.StartScreen;
import millee.game.initialize.WinnerScreen;
import millee.network.Network;
/**
 * @author Priyanka
 *
 */

public class ApplicationMain extends MIDlet implements CommandListener {
	
	private static final int NUM_ROUNDS = 2;
	private static final int NUM_LEVELS = 2;
	
	public static Display theDisplay;
	
	int characterChoice, gameChoice;
	int numLevelsLeft, numRoundsLeft;
	boolean isServer;
	
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
	
	private Vector _players;
	
	public ApplicationMain () {
		theDisplay = display = Display.getDisplay(this);
		

		network = new Network(this);
		
		charForm = new ChooseCharacter("Choose your character");
		charForm.setCommandListener(this);
		startScreen = new StartScreen ("Colour Colour");		
		startScreen.setCommandListener(this);
		chooseGame = new ChooseGame ("Choose a Game");		
		chooseGame.setCommandListener(this);
		joinGame = new JoinGame ("Join a Game", network);		
		joinGame.setCommandListener(this);
		levelStartPage = new LevelStartPage("Level 1", network, this.characterChoice, isServer);
		levelStartPage.setCommandListener(this);

		startOrJoinGame = new StartOrJoinGame("Start or join a game?");
		startOrJoinGame.setCommandListener(this);
		
		startAGame = new StartAGame("Start a game?", network);
		startAGame.setCommandListener(this);
		
		winnerScreen = new WinnerScreen("Winner!");
		winnerScreen.setCommandListener(this);
		
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
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
	
	public void commandAction(Command c, Displayable d) {
        
		if (c == startScreen.startCommand()) {
			
			// TODO: Reverse these blocks so that the intro screens are shown
			/*game = createNewRound();
			numLevelsLeft--;
			//System.out.println("numLevelsLeft: " + numLevelsLeft + ", numRoundsLeft: " + numRoundsLeft);
			game.start();
			display.setCurrent(game);*/
			
			
			display.setCurrent(charForm);
		} else if (c == charForm.okCommand()) {
			characterChoice = charForm.getListSelection();
			display.setCurrent(startOrJoinGame);
		} else if (c == startOrJoinGame.okCommand()){
			if (startOrJoinGame.getListSelection() == 0) {
				isServer = true;
				display.setCurrent(startAGame);
				_players = startAGame.setupNetworkPlayers("Raj", "/dancer_small.png");
			} else {
				isServer = false;
				display.setCurrent(chooseGame);
			}
		} else if (c == startAGame.startCommand()) {
			display.setCurrent(levelStartPage);
			
		} else if (c == chooseGame.okCommand()) {
			gameChoice = chooseGame.getListSelection();
			joinGame.setCharacterChoice(characterChoice);
			joinGame.setGameChoice(gameChoice);
			joinGame.initClient();
			display.setCurrent(joinGame);
		} else if (c == joinGame.startCommand()) {
			//network.sendReceive();
			display.setCurrent(levelStartPage);
		} else if (c == levelStartPage.startCommand()) {
			game = createNewRound();
			numLevelsLeft--;
			//System.out.println("numLevelsLeft: " + numLevelsLeft + ", numRoundsLeft: " + numRoundsLeft);
			game.start();
			display.setCurrent(game);
		} else if (c == game.okCmd) {
			numRoundsLeft--;
			System.out.println("numLevelsLeft: " + numLevelsLeft + ", numRoundsLeft: " + numRoundsLeft);
			if (numLevelsLeft <= 0 && numRoundsLeft <= 0) {		// end of game
				display.setCurrent(winnerScreen);
			} else if (numRoundsLeft <= 0) {	// end of current level
				game.hideNotify();
				numRoundsLeft = NUM_ROUNDS;
				levelStartPage = new LevelStartPage("Level 1", network, this.characterChoice, isServer);
				levelStartPage.setCommandListener(this);
				//network.sendReceive();
				display.setCurrent(levelStartPage);
			} else if (numRoundsLeft > 0) {	// end of current round, move on to next round
				game.hideNotify();
				game = createNewRound();
				game.start();
				display.setCurrent(game);
			}
		}

		else if (c.getLabel() == "Exit") {
			System.out.println("exiting");
		}
		
		//game.start();
		//display.setCurrent(game);
		/** TODO: Fix this incomplete statement
		while (Thread.currentThread() == game) {
			wait();
		}
		*/
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
								scoreAssignment, possibleTokenPaths, possibleTokenText, 4, isServer, network);
		
		//Round game = new Round(0, 2, numRoundsLeft, numLevelsLeft, false, "Colours", playerNames, playerImagePaths, 
		//						scoreAssignment, "/tiles.png", possibleTokenPaths, possibleTokenText, 4, isServer, network);
		game.setCommandListener(this);
		return game;
	}

}
