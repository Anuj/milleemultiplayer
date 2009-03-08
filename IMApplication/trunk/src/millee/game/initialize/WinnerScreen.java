package millee.game.initialize;

import javax.microedition.lcdui.StringItem;


public class WinnerScreen extends Screen {

	public WinnerScreen (String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}
	
	public void start(boolean isServer, String serverName) {
		
		if (isServer) {
			this.append("You ended the game.\n");
			this.append("-----------------------------\n");
		} else {
			this.append(serverName + " ended the game.\n");
			this.append("-----------------------------\n");
		}
		
		StringItem congrats = new StringItem(null, "Your group finished with score = 120.");
		
		this.append(congrats);
		
		this.addCommand(exitCommand);
		
	}
	
	public void addMessage(String msg) {
		this.append(msg);
	}

}
