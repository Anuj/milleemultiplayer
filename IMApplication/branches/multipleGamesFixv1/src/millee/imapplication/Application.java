package millee.imapplication;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

/** Represents the game that will run on the bluetooth middleware layer
 * 
 * @author Priyanka Reddy
 *
 */
public abstract class Application implements CommandListener {
	
	
	/** Runs the application/game
	 * @return error code, 0 if none
	 */
	abstract int runApplication(boolean isServer);

    /** Listener for commands
     */
    public abstract void commandAction(Command c, Displayable s);
}
