import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;


public class StartAGame extends Screen implements Runnable {
	
	ClientServer clientServer;
	private boolean m_bRunThread = false;
	private boolean m_bIsServer = true, isServer = true;
	    
	
	public StartAGame(String title) {
		super(title);
		
		StringItem str = new StringItem("", "Starting a game...");
        
		this.append(str);
        this.addCommand(backCommand);
        
	}
	
	public void startServer() {
		
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
            	
            	clientServer = new ClientServer(isServer, numClients);
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
            	
            	System.out.println("server connected!");
            	
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
