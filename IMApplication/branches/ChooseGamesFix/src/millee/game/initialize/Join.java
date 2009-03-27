package millee.game.initialize;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;

import millee.game.ApplicationMain;
import millee.network.ClientServer;
import millee.network.Network;


public class Join extends Screen {

	//private int characterChoice, gameChoice;
	StringItem msg;
	Gauge gauge;
	//private Command cancelCommand;
	private Network network;
	
	public static ClientServer clientServer;
	//private boolean m_bRunThread = false;
	//private boolean m_bIsServer = false, isServer = false;
	private ApplicationMain _app = null;
	
	public Join(String title, Network network, ApplicationMain _app) {
		super(title);
		
		cancelCommand = new Command("Cancel", Command.CANCEL, 0);
        
        this._app = _app;
        this.network = network;
        //this.clientServer = clientServer;
        
        
        // TODO: Only show success message on next page?
		//msg = new StringItem(null, "You have joined game #" + gameChoice);
        //this.append(msg);
        
        //this.addCommand(cancelCommand);
	}
	/*
	public void setCharacterChoice(int inChoice) {
		this.characterChoice = inChoice;
	}
	
	public void setGameChoice (int gameChoice) {
		this.gameChoice = gameChoice;
	}
	*/
	
	public void initClient(String url, ClientServer clientServer) {

        this.append("Connecting to game: " + url);
        new Thread(new Connect(url, clientServer)).start();
        //Thread thread = new Thread(new Connect(url));
        //thread.start();
        //new Thread(new Runnable() { public void run() {clientServer.connect(url);}});
        
	}
	
	public void addMessage(String msg) {
		this.append(msg);
	}
	
    private class Connect implements Runnable {
    	
    	String url = null;
    	ClientServer clientServer = null;
    	
    	public Connect(String url, ClientServer clientServer) {
    		this.url = url;
    		this.clientServer = clientServer;
    	}
		public void run() {
			System.out.println("in run of Connect Runnable");
			System.out.println("clientServer = " + clientServer);
			clientServer.connect(url);
		}
    }
}
