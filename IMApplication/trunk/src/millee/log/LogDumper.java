/**
 * 
 */
package millee.log;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Screen;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

import net.sf.microlog.Logger;
import net.sf.microlog.appender.ConsoleAppender;
import net.sf.microlog.format.PatternFormatter;
import net.sf.microlog.util.Properties;

/**
 * @author Simon
 *
 */
public class LogDumper extends MIDlet implements CommandListener {
	
	// Logger instance to be used to write out
	private static final Logger log = Logger.getLogger(LogDumper.class);
	
	private Display _display;
	private List _logList;

	/**
	 * @throws MIDletStateChangeException 
	 * 
	 */
	public LogDumper() throws MIDletStateChangeException {
		log.removeAllAppenders();
		ConsoleAppender ca = new ConsoleAppender();
		PatternFormatter p = new PatternFormatter();
		p.configure(new Properties());
		
		ca.setFormatter(p);
		log.addAppender(ca);
		
		startApp();
		dump();
	}
			
	
	private void dump() {
		String[] recordStores = RecordStore.listRecordStores();
		if (recordStores == null) { return; }
		
		byte[] recordBytes = null;
		RecordStore rs = null;
		String rsName = null;
		
		// Iterate through the record stores on this Suite...
		for (int i = 0; i < recordStores.length; i++) {
			try {
				rs = RecordStore.openRecordStore( recordStores[i], false );
				rsName = rs.getName();
				log.info("\n===" + rsName + "===");
			
				// Iterate through each record in this record store...
				RecordEnumeration re = rs.enumerateRecords(null, null, false);
				while (re.hasNextElement()) {
					recordBytes = re.nextRecord();
					
					// Process the retrieved bytes outwards
					log.info(new String(recordBytes));
					// TODO: Blast this out over bluetooth serial? Use microlog to dump the log?
				}
				
				re.destroy();
				rs.closeRecordStore();
				
				// TODO: Delete the record store afterwards?
				//RecordStore.deleteRecordStore(rsName);				
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}	
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean arg0) {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		_display = Display.getDisplay(this);

		Command _exitCommand = new Command ("Exit", Command.EXIT, 0);
		Command _deleteCommand = new Command ("Delete", Command.ITEM, 1);
		
		String[] logNames = RecordStore.listRecordStores();
		if (logNames != null) {
			_logList = new List("Log Files", logNames.length, logNames, null);
			_logList.addCommand(_exitCommand);
			_logList.addCommand(_deleteCommand);
			_display.setCurrent(_logList); //new LogDumpDisplay("Log Files", RecordStore.listRecordStores()));
		}
		else {
			Alert a = new Alert("No log files to dump!");
			a.addCommand(_exitCommand);
			_display.setCurrent(a);
		}
	}



	public void commandAction(Command c, Displayable d) {
		// TODO Auto-generated method stub
		if (c.getCommandType() == Command.EXIT) {
			System.out.println("Exit was hit");
			destroyApp(true);
			notifyDestroyed();
		} else if (c.getCommandType() == Command.ITEM) {
			System.out.println("ITEM was hit");
			boolean[] checkedItems = new boolean[_logList.size()];
			String individualItem = null;
			
			_logList.getSelectedFlags(checkedItems);
			for (int i = 0; i < checkedItems.length; i++) {
				if (checkedItems[i]) {
					individualItem = _logList.getString(i);
					try {
						RecordStore.deleteRecordStore(individualItem);
					} catch (RecordStoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
