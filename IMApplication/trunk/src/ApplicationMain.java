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


public class ApplicationMain extends MIDlet implements CommandListener {
	
	SampleCanvas game;
	Form form;
	Command cmd;
	ChoiceGroup choiceGroup;
	Image horrorImage, comedyImage, actionImage;
	Display display;
	
	public ApplicationMain () {
		display = Display.getDisplay(this);
		game = new SampleCanvas();
		
		// Create the Form
		form=new Form("Choice Group Demo");
        choiceGroup=new ChoiceGroup("Choose your character:",Choice.EXCLUSIVE);
        cmd=new Command("OK",Command.OK,1);
        try {
	        horrorImage = Image.createImage("/flower2.png");
	        comedyImage = Image.createImage("/mainScreen.png");
	        actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }
        choiceGroup.append("Horror",horrorImage);
        choiceGroup.append("Comedy",comedyImage);
        choiceGroup.append("Action",actionImage);
        form.append(choiceGroup);
        form.addCommand(cmd);
        form.setCommandListener(this);
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
		display.setCurrent(form);
	}
	
	public void commandAction(Command c, Displayable d) {
        
		if (choiceGroup.isSelected(0)) {
			game.start();
			display.setCurrent(game);
			/** TODO: Fix this incomplete statement
			while (Thread.currentThread() == game) {
				wait();
			}
			*/
		}
    }

}
