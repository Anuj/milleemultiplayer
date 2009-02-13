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
	
	SampleCanvas game;
	Display display;
	ChooseCharacter charForm;
	StartScreen startScreen;
	
	public ApplicationMain () {
		display = Display.getDisplay(this);
		game = new SampleCanvas();
		
		game.setCommandListener(this);
		charForm = new ChooseCharacter("Choose your character");
		charForm.setCommandListener(this);
		startScreen = new StartScreen ("Colour Colour");		
		startScreen.setCommandListener(this);
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
		
	}

	protected void startApp() throws MIDletStateChangeException {
		
		//game.start();
		//display.setCurrent(game);
		display.setCurrent(startScreen);
	}
	
	public void commandAction(Command c, Displayable d) {
        
		if (c == startScreen.cmd) {
			display.setCurrent(charForm);
		}
		else if (c == charForm.cmd && charForm.choiceGroup.isSelected(0)) {
			game.start();
			display.setCurrent(game);
			/** TODO: Fix this incomplete statement
			while (Thread.currentThread() == game) {
				wait();
			}
			*/
		} else if (c.getLabel() == "Exit") {
			System.out.println("exiting");
		} else if (c.getLabel() == "OK") {
			display.setCurrent(charForm);
		}
    }

}
