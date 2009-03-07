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
	private int formElementNumber = 0;
	private Object replaceObject = new Object();

	// TODO: move NUMCLIENTS + setupPlayers code elsewhere
	public static final int NUMCLIENTS = 2;
	
	
	public StartAGame(String title, Network network, ApplicationMain _app) {
		super(title);
		
		this._app = _app;
		this.network = network;
		
		this.append("Started a game...");
		this.append("Waiting for players to join");
        formElementNumber+=2;
        this.append("");
        formElementNumber+=1;
		//this.addCommand(backCommand);
        
		
		this.addCommand(cancelCommand);
        
        
	}
	
	public void setupNetworkPlayers(String myName, String myImagePath) {
		//Network network = new Network();
		network.initializeNetwork(true, NUMCLIENTS, _app);
		
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
	
	public synchronized void replaceLastMessage (String msg) {
		//synchronized (replaceObject) {
			System.out.println("in replaceLastMessage: about to remove msg at " + (formElementNumber-1));
			try {
				this.delete(formElementNumber-1);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("from delete command");
				e.printStackTrace();
			}
			System.out.println("adding msg = " + msg);
			try {
				this.append(msg);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("from add command");
				e.printStackTrace();
			}
		//}
	}

}
