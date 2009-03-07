package millee.game.initialize;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;


public class Screen extends Form {
	
	protected Command okCommand, backCommand, exitCommand, startCommand, cancelCommand, joinCommand;
	protected ChoiceGroup choiceGroup;
	
	public Screen(String title) {
		super(title);
		okCommand=new Command("OK",Command.OK,1);
		backCommand=new Command("Back",Command.BACK,0);
		exitCommand =new Command("Exit",Command.EXIT,0);
		startCommand=new Command("Start",Command.SCREEN,1);
		cancelCommand=new Command("Cancel", Command.CANCEL, 0);
		joinCommand=new Command("Join",Command.OK,1);
        
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
	
	public Command joinCommand() {
		return joinCommand;
	}

}
