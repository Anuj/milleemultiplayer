package millee.game.initialize;

import java.util.Vector;

import millee.game.ApplicationMain;
import millee.game.mechanics.Player;


public class WinnerScreen extends Screen {

	public WinnerScreen (String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}
	
	public void start(boolean isServer, String serverName, Vector _players) {
		
		if (isServer) {
			ApplicationMain.log.info("You ended the game");
			this.append("You ended the game.\n");
			this.append("-----------------------------\n");
		} else {
			ApplicationMain.log.info("Server ended the game");
			this.append("The game has been ended.\n");
			this.append("-----------------------------\n");
		}
		
		ApplicationMain.log.info("Your group finished with score = " + Player.getGroupScore());
		String congrats = new String("Your group finished with score = " + Player.getGroupScore());
		congrats += "\n-----------------------------\n";

		Player p;
		for (int i = 0; i<_players.size(); i++) {
			p = ((Player) _players.elementAt(i));
			ApplicationMain.log.info(p.getName() + ": " + p.getScore());
			congrats += "		" + p.getName() + ": " + p.getScore() + "\n";
		}
		
		this.append(congrats);
		
		this.addCommand(exitCommand);
		
	}
	
	public void addMessage(String msg) {
		this.append(msg);
	}

}
