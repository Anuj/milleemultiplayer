import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public class TestImage extends MIDlet implements CommandListener {
	
	private List mlist;
	private Command mExitCommand, mNextCommand;
	
	public TestImage() {
		// TODO Auto-generated constructor stub
		String[] stringElements = {"Flower"};
		Image[] imageElements = {loadImage("/flower2.png")};
		mlist = new List ("Resr type", List.IMPLICIT, stringElements,
				imageElements);
		mNextCommand = new Command("Next", Command.SCREEN, 0);
		mExitCommand = new Command ("Exit", Command.EXIT, 0);
		mlist.addCommand(mNextCommand);
		mlist.addCommand(mExitCommand);
		mlist.setCommandListener(this);
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		Display.getDisplay(this).setCurrent(mlist);
	}
	
	public void commandAction(Command c, Displayable s) {
		if (c == mNextCommand || c == List.SELECT_COMMAND) {
			int index = mlist.getSelectedIndex();
			Alert alert = new Alert("Your selection", "You chose " + mlist.getString(index) + ".", 
					null, AlertType.INFO);
			Display.getDisplay(this).setCurrent(alert, mlist);
		}
		else if (c == mExitCommand)
			notifyDestroyed();
	}
	
	private Image loadImage (String name) {
		Image image = null;
		try {
			image = Image.createImage(name);
		}
		catch (IOException ioe) {
			System.out.print(ioe);
		}
		
		return image;
	}

}
