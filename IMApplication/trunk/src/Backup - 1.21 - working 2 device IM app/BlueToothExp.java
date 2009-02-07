import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;

/** BlueToothExp is where the Application gets initialized and run
 * 
 * @author Priyanka Reddy
 *
 */
public class BlueToothExp extends MIDlet implements CommandListener, Runnable {
    private List menu;
    private boolean m_bRunThread = false;
    private boolean m_bIsServer = false;
    
    static final int MAX_TEXT_SIZE = 10000;
    
    static TextBox textBox;
    static Display display;
    static TextBox input;
    static String userInput;
    static Object obj = new Object();
    
    Application app = null;
 
    public BlueToothExp() {
        initialize();
    }
    
    // Handles the initial display
    public void commandAction(Command command, Displayable displayable) {
    	display = Display.getDisplay(this);
    	
        if (displayable == menu) {
            
            if (command == menu.SELECT_COMMAND) {
            	
            	if (m_bRunThread == false) {
            		if (getMenu().getSelectedIndex() == 1) {
                        m_bIsServer = true;
            		} else if (getMenu().getSelectedIndex() == 2) {
            			m_bIsServer = false;
            		}
            		
        			Thread thread = new Thread(this);
                    
                    // Install the TextBox as the current screen
                    display.setCurrent(textBox);
                    thread.start();
                    
                    m_bRunThread = true;
            	}		
            }
        }
    }
 
    private void initialize() {
        getDisplay().setCurrent(getMenu());
    }
 
    public Display getDisplay() {
        return Display.getDisplay(this);
    }
 
    public void exitMIDlet() {
        getDisplay().setCurrent(null);
        destroyApp(true);
        notifyDestroyed();
    }
    
    public List getMenu() {
        if (menu == null) {
            menu = new List(
                    null, 
                    Choice.IMPLICIT, new String[] {"Client Server", "Server","Client"}, 
                    new Image[] {null, null, null});
            
            menu.setCommandListener(this);
            menu.setSelectedFlags(new boolean[] {false,false,false});
        }
        return menu;
    }
 
    public void startApp() {
    }
 
    public void pauseApp() {
    }
 
    public void destroyApp(boolean unconditional) {
        m_bRunThread = false;
    }
    
    // Runs the application
    public void run() {
    	Display display = Display.getDisplay(this);
    	display.setCurrent(textBox);
    	
        while (m_bRunThread) {
            try {
            	// Just by creating a new Application object, you run the application
            	// either as a client or server depending on the m_bIsServer variable.
                if (app == null) {
                	app = new IMApplication(m_bIsServer, 1);
                	exitMIDlet();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
 
        }
    }
}