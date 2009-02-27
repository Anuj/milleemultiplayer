package millee.game.initialize;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;


public class StartScreen extends Screen {
	private Image startImage;
	
	// Nokia parameters
	private static final int SCREEN_WIDTH = 120;
	private static final int SCREEN_HEIGHT = 115;
	
	private static final String GAME_INTRO_SOUND = "/game_intro.wav";
	
	public StartScreen (String title) {
		super(title);
		
        try {
	        startImage = Image.createImage("/mainScreen.png");
        } catch (IOException e) {}
        
        this.append(Utilities.resizeImage(startImage, getWidth(), getHeight())); //SCREEN_WIDTH, SCREEN_HEIGHT));
        this.addCommand(startCommand);
        
        // Play some music!
		try {
			InputStream is = getClass().getResourceAsStream(GAME_INTRO_SOUND); 
		    Player p = Manager.createPlayer(is, "audio/X-wav"); 
		    p.prefetch(); // prefetch
		    p.realize(); // realize

		    p.start(); // and start
		    
		    // Free resources
		    is.close();
		    is = null;
		}
		catch (IOException ioe) { }
		catch (MediaException me) { } 
		
	}
	
	public Command getStartCommand() {
		return startCommand;
	}
}
