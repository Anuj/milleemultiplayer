package millee.game.initialize;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;

import millee.game.ApplicationMain;


public class Screen extends Form {
	
	protected Command okCommand, backCommand, exitCommand, startCommand, cancelCommand;
	protected ChoiceGroup choiceGroup;

	protected int formElementNumber = 0;
	
	public Screen(String title) {
		super(title);
		okCommand=new Command("OK",Command.OK,1);
		backCommand=new Command("Back",Command.BACK,0);
		exitCommand =new Command("Exit",Command.EXIT,0);
		startCommand=new Command("Start",Command.SCREEN,1);
		cancelCommand=new Command("Cancel", Command.CANCEL, 0);
        
		// TODO Auto-generated constructor stub
	}
	
	public Command okCommand () {
		return okCommand;
	}
	
	public Command backCommand () {
		return backCommand;
	}
	
	public Command getStartCommand () {
		return startCommand;
	}
	
	public int getListSelection () {
		if (choiceGroup != null)
			return choiceGroup.getSelectedIndex();
		return -1;
	}
	/*
	public void addMessage(String msg) {
		this.append(msg);
	}*/
	
	/*public int append(String msg) {
		int ret = super.append(msg);
		ApplicationMain.log.trace("in append: about to add msg = " + msg + " at formElementNumber = " + formElementNumber);
		formElementNumber+=1;
		ApplicationMain.log.trace("after formLEmentNUmebr inc");
		return ret;
		return -1;
	}*/
	
	
	public synchronized void replaceLastMessage (String msg) {
		//synchronized (replaceObject) {
			ApplicationMain.log.trace("in replaceLastMessage: about to remove msg at " + (formElementNumber-1));
			try {
				this.delete(formElementNumber-1);
			} catch (IndexOutOfBoundsException e) {
				ApplicationMain.log.trace("from delete command");
				e.printStackTrace();
			}
			ApplicationMain.log.trace("adding msg = " + msg);
			try {
				this.append(msg);
			} catch (IndexOutOfBoundsException e) {
				ApplicationMain.log.trace("from add command");
				e.printStackTrace();
			}
		//}
	}

}
