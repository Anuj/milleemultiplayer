package millee.game.initialize;

import java.io.IOException;

import millee.game.ApplicationMain;
import millee.network.ClientServer;
import millee.network.Network;


public class StartAGame extends Screen {
	
	public static ClientServer clientServer;
	private boolean m_bRunThread = false;
	private boolean m_bIsServer = true, isServer = true;
	private Network network;
	private ApplicationMain _app = null;
	private Object replaceObject = new Object();

	// TODO: move NUMCLIENTS + setupPlayers code elsewhere
	public static final int NUMCLIENTS = 2;
	
	
	public StartAGame(String title, Network network, ApplicationMain _app) {
		super(title);
		
		this._app = _app;
		this.network = network;
	}
	
	public void start() {
	
		network.initializeNetwork(true, NUMCLIENTS, _app);
        
        String gameName = network.clientServer.getDeviceName();
        
		this.append("Started a game called " + gameName.toUpperCase());
		this.append("Waiting for players to join");
        formElementNumber+=2;
        this.append("");
        formElementNumber+=1;
		//this.addCommand(backCommand);
        
		
		this.addCommand(cancelCommand);
	}
	
	public void setupNetworkPlayers(String myName, String myImagePath) {
		//Network network = new Network();
		//network.initializeNetwork(true, NUMCLIENTS, _app);
		
		Thread thread = new Thread(network);
        thread.start();
        
	}
	
	/*public int append(String msg) {
		int ret = super.append(msg);
		System.out.println("in append: about to add msg = " + msg + " at formElementNumber = " + formElementNumber);
		formElementNumber+=1;
		System.out.println("after formLEmentNUmebr inc");
		return ret;
	}*/

}
