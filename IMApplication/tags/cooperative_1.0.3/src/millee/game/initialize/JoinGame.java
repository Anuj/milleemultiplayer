package millee.game.initialize;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

import millee.network.Network;


public class JoinGame extends Screen{

	private Image horrorImage, comedyImage, actionImage;
	private int characterChoice, gameChoice;
	StringItem msg;
	Gauge gauge;
	private Command cancelCommand;
	private Network network;
	
	public JoinGame(String title, Network network) {
		super(title);
        this.network = network;
	}
	
	public void joinTheGame() {
		
		Thread thread = new Thread(new Runnable() {public void run() {network.clientServer.joinGame(gameChoice); }} );
		thread.start();
		
		
		cancelCommand = new Command("Cancel", Command.CANCEL, 0);
		
		msg = new StringItem(null, "You have joined " + ((String) network.clientServer.devicesDiscoveredNames.elementAt(gameChoice)) + "'s game.");
        try {
	        horrorImage = Image.createImage("/flower2.png");
	        comedyImage = Image.createImage("/mainScreen.png");
	        actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }
        
        gauge = new Gauge("Connecting to game #" + gameChoice + "...",
        					false,
        					Gauge.INDEFINITE,
        					Gauge.CONTINUOUS_RUNNING);
        
        
        this.append(msg);
        this.append(horrorImage);
        this.append(gauge);
        this.addCommand(cancelCommand);
	}
	
	public void setCharacterChoice(int characterChoice) {
		this.characterChoice = characterChoice;
	}
	
	public void setGameChoice (int gameChoice) {
		this.gameChoice = gameChoice;
	}
	
	public void initClient() {
		
		network.initializeNetwork(false, 1);
		
		Thread thread = new Thread(network);
        thread.start();
	}
}
