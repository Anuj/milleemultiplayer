import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;


public class JoinGame extends Screen implements Runnable {

	private Image horrorImage, comedyImage, actionImage;
	private int characterChoice, gameChoice;
	StringItem msg;
	Gauge gauge;
	private Command cancelCommand;
	
	ClientServer clientServer;
	private boolean m_bRunThread = false;
	private boolean m_bIsServer = false, isServer = false;
	 
	
	public JoinGame(String title) {
		super(title);
		
		cancelCommand = new Command("Cancel", Command.CANCEL, 0);
		
		msg = new StringItem(null, "You have joined game #" + gameChoice);
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
		Thread thread = new Thread(this);
        thread.start();
        
        m_bRunThread = true;
		
	}
	
	public void run() {
		int numClients = 1;
        while (m_bRunThread) {
            try {
            	// Just by creating a new Application object, you run the application
            	// either as a client or server depending on the m_bIsServer variable.
                /*if (app == null) {
                	app = new IMApplication(m_bIsServer, 1);
                	//exitMIDlet();
                }*/
            	
            	clientServer = new ClientServer(isServer, 0);
            	if (isServer) {
        			//this.numClients = numClients;
        			clientServer.InitServer(numClients);
        		} else {
        			clientServer.InitClient();
        			try {
        				// need to manually wait for client to be connected to server
        				// because client doesn't block until it's connected.
        				synchronized(clientServer.connected) {
        					clientServer.connected.wait();
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
            	
            	System.out.println("client connected!");

            	this.append("All the clients have connected.");
            	this.append("Choose START to begin the game");
            	this.addCommand(startCommand);
            	//clientServer.printToScreen("Application", "Connected.");
        		//runApplication(isServer);
                
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
 
        }
	}
}
