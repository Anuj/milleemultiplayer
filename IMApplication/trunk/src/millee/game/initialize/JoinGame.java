package millee.game.initialize;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;

import millee.game.ApplicationMain;
import millee.network.ClientServer;
import millee.network.Network;


public class JoinGame extends Screen {

	private int characterChoice, gameChoice;
	StringItem msg;
	Gauge gauge;
	private Command cancelCommand;
	private Network network;
	
	public static ClientServer clientServer;
	private boolean m_bRunThread = false;
	private boolean m_bIsServer = false, isServer = false;
	private ApplicationMain _app = null;
	
	public JoinGame(String title, Network network, ApplicationMain _app) {
		super(title);
		
		cancelCommand = new Command("Cancel", Command.CANCEL, 0);
        gauge = new Gauge("",
        					false,
        					Gauge.INDEFINITE,
        					Gauge.CONTINUOUS_RUNNING);
        
        this._app = _app;
        this.network = network;
        
        this.append(gauge);
        this.append("Connecting to existing game...");
        
        // TODO: Only show success message on next page?
		//msg = new StringItem(null, "You have joined game #" + gameChoice);
        //this.append(msg);
        
        //this.addCommand(cancelCommand);
	}
	
	public void setCharacterChoice(int inChoice) {
		this.characterChoice = inChoice;
	}
	
	public void setGameChoice (int gameChoice) {
		this.gameChoice = gameChoice;
	}
	
	public void initClient() {
		
		network.initializeNetwork(false, 1, _app);
		
		Thread thread = new Thread(network);
        thread.start();
	}
	
	public void addMessage(String msg) {
		this.append(msg);
	}
}
