import java.io.IOException;

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
/**
 * @author Priyanka
 *
 */

public class ApplicationMain extends MIDlet implements CommandListener {
	
	private static final int NUM_ROUNDS = 2;
	private static final int NUM_LEVELS = 2;
	
	int characterChoice, gameChoice;
	int numLevelsLeft, numRoundsLeft;
	
	SampleCanvas game;
	Display display;
	ChooseCharacter charForm;
	StartScreen startScreen;
	ChooseGame chooseGame;
	JoinGame joinGame;
	LevelStartPage levelStartPage;
	
	public ApplicationMain () {
		display = Display.getDisplay(this);
		
		charForm = new ChooseCharacter("Choose your character");
		charForm.setCommandListener(this);
		startScreen = new StartScreen ("Colour Colour");		
		startScreen.setCommandListener(this);
		chooseGame = new ChooseGame ("Choose a Game");		
		chooseGame.setCommandListener(this);
		joinGame = new JoinGame ("Join a Game");		
		joinGame.setCommandListener(this);
		levelStartPage = new LevelStartPage("Level 1");
		levelStartPage.setCommandListener(this);
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
			display.setCurrent(charForm);
		} else if (c == charForm.okCommand()) {
			characterChoice = charForm.getListSelection();
			display.setCurrent(chooseGame);
		} else if (c == chooseGame.okCommand()) {
			gameChoice = chooseGame.getListSelection();
			joinGame.setCharacterChoice(characterChoice);
			joinGame.setGameChoice(gameChoice);
			display.setCurrent(joinGame);
		} else if (c == joinGame.okCommand()) {
			display.setCurrent(levelStartPage);
		} else if (c == levelStartPage.startCommand()) {
			game = createNewRound();
			game.start();
			display.setCurrent(game);
		} else if (c == game.okCmd) {
			System.out.println("numRoundsLeft: " + numRoundsLeft);
			if (numRoundsLeft > 0) {
				game.hideNotify();
				numRoundsLeft--;
				game = createNewRound();
				game.start();
				display.setCurrent(game);
			} else if (numLevelsLeft > 0) {
				game.hideNotify();
				numLevelsLeft--;
				numRoundsLeft = NUM_ROUNDS;
				levelStartPage = new LevelStartPage("Level 1");
				levelStartPage.setCommandListener(this);
				display.setCurrent(levelStartPage);
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
	
	private SampleCanvas createNewRound() {
		SampleCanvas game = new SampleCanvas();
		game.setCommandListener(this);
		return game;
	}

}
