package millee.game.initialize;

import millee.game.ApplicationMain;
import millee.network.ClientServer;
import millee.network.Network;


public class StartAGame extends Screen {
	
	public static ClientServer clientServer;
	//private boolean m_bRunThread = false;
	//private boolean m_bIsServer = true, isServer = true;
	private Network network;
	private ApplicationMain _app = null;
	//private Object replaceObject = new Object();

	// TODO: move NUMCLIENTS + setupPlayers code elsewhere
	//public static final int NUMCLIENTS = 2;
	public static int numClients = -1;
	
	
	public StartAGame(String title, Network network, ApplicationMain _app) {
		super(title);

		this._app = _app;
		this.network = network;
	}
	
	public void start(int numClients) {
		
		StartAGame.numClients = numClients;
		network.initializeNetwork(true, StartAGame.numClients, _app);
		
	
		String gameName = network.clientServer.getDeviceName();
		
		ApplicationMain.log.info("Started a game called " + gameName.toUpperCase());
		this.append("Started a game called " + gameName.toUpperCase() + ".\n");
		this.append("-----------------------------\n");
        formElementNumber+=2;
        this.append("");
        formElementNumber+=1;
		//this.addCommand(backCommand);
        
		//this.addCommand(cancelCommand);
	}
	
	public void startNetwork() {
		Thread thread = new Thread(network);
        thread.start();
        
	}
	
	/*public int append(String msg) {
		int ret = super.append(msg);
		ApplicationMain.log.trace("in append: about to add msg = " + msg + " at formElementNumber = " + formElementNumber);
		formElementNumber+=1;
		ApplicationMain.log.trace("after formLEmentNUmebr inc");
		return ret;
	}*/

}
