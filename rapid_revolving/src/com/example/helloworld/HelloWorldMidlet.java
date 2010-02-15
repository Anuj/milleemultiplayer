package com.example.helloworld;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
 
public class HelloWorldMidlet extends MIDlet implements CommandListener {
 
    private Display display;
    private Form form;
    private StringItem stringItem;

    private int next = 0;
    private Vector numberChoices = new Vector();

    public static Command exitCommand = new Command("Exit", Command.EXIT, 1);
    public static Command startCommand = new Command("Start", Command.ITEM, 0);
    public static Command chooseVersionCommand = new Command("Choose Version", Command.ITEM, 0);
    public static Command chooseCommand = new Command("Choose", Command.ITEM, 0);
    public static Command stopCommand = new Command("Stop", Command.ITEM, 0);
    
    private List _startOrJoinGameList = null, _gamePlayList = null, 
    			_rightChoiceList = null, _wrongChoiceList = null;
    
    public HelloWorldMidlet() {
    }
    
    public void commandAction(Command c, Displayable s) {
    	if (c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        } else if (c == startCommand) {
        	/*versionList = new List("Choose a level:", Choice.IMPLICIT);
            versionList.append("Beginners Level", null);
            versionList.append("Intermediate Level", null);
            
            versionList.addCommand(chooseVersionCommand);
            versionList.addCommand(exitCommand);
            versionList.setCommandListener(this);
            //display.setCurrent(versionList);*/
            display.setCurrent(getStartOrJoinGameList());

        } else if (c == List.SELECT_COMMAND && s == _startOrJoinGameList) {

        	int index = _startOrJoinGameList.getSelectedIndex(); //versionList.getSelectedIndex();
        	
        	Random generator = new Random();
        	int ran = generator.nextInt(55);
            if (ran < 10) {
            	next = 1;
            } else if (ran < 19) {
            	next = 2;
            } else if (ran < 27) {
            	next = 3;
            } else if (ran < 34) {
            	next = 4;
            } else if (ran < 40) {
            	next = 5;
            } else if (ran < 45) {
            	next = 6;
            } else if (ran < 49) {
            	next = 7;
            } else if (ran < 52) {
            	next = 8;
            } else if (ran < 54) {
            	next = 9;
            } else if (ran < 55) {
            	next = 10;
            }
            
            String audioFileName = "/" + numberChoices.elementAt(next-1);
            Player p = null;
            try {
    			InputStream is = getClass().getResourceAsStream(audioFileName); 
    		    p = Manager.createPlayer(is, "audio/X-wav"); 
    		    p.prefetch(); // prefetch
    		    p.realize(); // realize
    		    
    		    VolumeControl vc = (VolumeControl) p.getControl("VolumeControl");
    		    if(vc != null) {
    		       vc.setLevel(100);
    		    }
    		    
    		    p.start(); // and start
    		    
    		    // Free resources
    		    is.close();
    		    is = null;
    		}
    		catch (IOException ioe) { }
    		catch (MediaException me) { }
            
            display.setCurrent(gamePlayList(next));
            
        } else if (c == List.SELECT_COMMAND && s == _gamePlayList) {
        	
        	int index = _gamePlayList.getSelectedIndex();
        	
        	if (index == 0) {			// user chose to continue

        		next--;
        		
        		if (next < 0) {
                    display.setCurrent(wrongChoiceList());        	
	            } else {
	                display.setCurrent(gamePlayList(next));
	            }
	            	            
        	} else if (index == 1){
        		if (next > 0) {
                    display.setCurrent(wrongChoiceList());        	
        		} else {
                    display.setCurrent(rightChoiceList());        	
        		}
        	}
        }
    }
 
    public void startApp() {
    	
        display = Display.getDisplay(this);        

    	SplashScreen gs = new SplashScreen("Title");
    	gs.setCommandListener(this);
    	display.setCurrent(gs);
    	    	
    	numberChoices.addElement("one.wav");
        numberChoices.addElement("two.wav");
        numberChoices.addElement("three.wav");
        numberChoices.addElement("four.wav");
        numberChoices.addElement("five.wav");
        numberChoices.addElement("six.wav");
        numberChoices.addElement("seven.wav");
        numberChoices.addElement("eight.wav");
        numberChoices.addElement("nine.wav");
        numberChoices.addElement("ten.wav");
        
        // Get display for drawing
        //display = Display.getDisplay(this);        
        //sdisplay.setCurrent(form);
    }
 
    // Your MIDlet should not call pauseApp(), only system will call this life-cycle method
    public void pauseApp() {
    }
 
    // Your MIDlet should not call destroyApp(), only system will call this life-cycle method    
    public void destroyApp(boolean unconditional) {
    }
 
    public void exitMIDlet() {
        display.setCurrent(null);
        notifyDestroyed();
    }
    
    private List getStartOrJoinGameList() {
        
        if (_startOrJoinGameList == null) {

            _startOrJoinGameList = new List("Choose a version", List.IMPLICIT);
            
            _startOrJoinGameList.append("Beginner", null);
            _startOrJoinGameList.append("Advanced", null);
            
            _startOrJoinGameList.setCommandListener(this);
            _startOrJoinGameList.addCommand(exitCommand);
        }
        
        return _startOrJoinGameList;            
    }
    
    private List gamePlayList(int next) {
        
    	_gamePlayList = new List("Make a choice", List.IMPLICIT);
            
    	_gamePlayList.append("Continue", null);
    	_gamePlayList.append("Done", null);
    	
    	_gamePlayList.append("", null);
    	if (next == 0) {
        	_gamePlayList.append("Time to stop", null);
    	} else {
    		_gamePlayList.append("" + next + " more left", null);
    	}
        
    	_gamePlayList.setCommandListener(this);
    	_gamePlayList.addCommand(exitCommand);
        
        return _gamePlayList;            
    }
    
    private List rightChoiceList() {
        
        if (_rightChoiceList == null) {

        	_rightChoiceList = new List("Correct!", List.IMPLICIT);
            _rightChoiceList.append("Correct!", null);
        	_rightChoiceList.setCommandListener(this);
        	_rightChoiceList.addCommand(startCommand);
        	_rightChoiceList.addCommand(exitCommand);
        }
        
        return _rightChoiceList;            
    }
    
    private List wrongChoiceList() {
        
        if (_wrongChoiceList == null) {

        	_wrongChoiceList = new List("Wrong!", List.IMPLICIT);
        	_wrongChoiceList.append("Wrong!", null);
        	_wrongChoiceList.setCommandListener(this);
        	_wrongChoiceList.addCommand(startCommand);
        	_wrongChoiceList.addCommand(exitCommand);

        }
        return _wrongChoiceList;            
    }
    
}