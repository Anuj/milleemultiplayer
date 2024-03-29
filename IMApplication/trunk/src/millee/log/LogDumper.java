/**
 * 
 */
package millee.log;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import net.sf.microlog.Logger;
import net.sf.microlog.appender.ConsoleAppender;
import net.sf.microlog.bluetooth.BluetoothSerialAppender;
import net.sf.microlog.format.PatternFormatter;

/**
 * @author Simon
 *
 */
public class LogDumper extends MIDlet implements CommandListener {
	
	// Logger instance to be used to write out
	private static final Logger log = Logger.getLogger(); //LogDumper.class);
	
	private static final int HEADER_SIZE = 10;
	
	private String[] _logNames;
	private Display _display;
	private List _logList;
	
	private Command _exitCommand = new Command ("Exit", Command.EXIT, 0);
	private Command _deleteCommand = new Command ("Delete", Command.ITEM, 1);

	/**
	 * @throws MIDletStateChangeException 
	 * 
	 */
	public LogDumper() { }
	
	/*
	 * Sets up external 'logging' to the outside
	 */
	private void setupLogging() {
		log.removeAllAppenders();
		
		ConsoleAppender ca = new ConsoleAppender();
		BluetoothSerialAppender ba = new BluetoothSerialAppender();

		// Make the pattern a simple dump of the existing logs
		PatternFormatter p = new PatternFormatter();
		p.setPattern("%m");
		
		ca.setFormatter(p);
		ba.setFormatter(p);
		
		log.addAppender(ca);
		log.addAppender(ba);
	}
	
	/*
	 * Returns true if there are logs, false if not. Populates the _logNames array.
	 */
	private boolean loadLogNames() {
		_logNames = RecordStore.listRecordStores();
		return !(_logNames == null);
	}
	
	/*
	 * Uses microlog to dump the logs from the RecordStore
	 */
	private void dump() {
		if (!loadLogNames()) {
			log.info("**No logs to dump**");
			return;
		}
		
		log.info("**BEGIN LOG DUMP**");
		
		byte[] recordBytes = null;
		RecordStore rs = null;
		String rsName = null;
		
		// Iterate through the record stores on this Suite...
		for (int i = 0; i < _logNames.length; i++) {
			try {
				rs = RecordStore.openRecordStore( _logNames[i], false );
				rsName = rs.getName();
				log.info("\n===" + rsName + "===");
			
				// Iterate through each record in this record store...
				RecordEnumeration re = rs.enumerateRecords(null, null, false);
				while (re.hasNextElement()) {
					recordBytes = re.nextRecord();
					
					// Process the retrieved bytes outwards
					log.info(new String(recordBytes, HEADER_SIZE, recordBytes.length-HEADER_SIZE));
					// TODO: Blast this out over bluetooth serial? Use microlog to dump the log?
				}
				
				re.destroy();
				rs.closeRecordStore();
				
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
			
			try {
				log.info("Record store size = " + rs.getSize());
				log.info("Record store available size = " + rs.getSizeAvailable());
				
			} catch (RecordStoreNotOpenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		log.info("**END LOG DUMP**");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean arg0) {
		try {
			log.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		setupLogging();
		_display = Display.getDisplay(this);

		/*
		for (int i = 0; i<logNames.length; i++) {
			System.out.println("Logname " + i + ": " + logNames[i]);
		}
		*/
		if (loadLogNames()) {
			_logList = new List("Log Files", List.MULTIPLE, _logNames, null);
			_logList.addCommand(_exitCommand);
			_logList.addCommand(_deleteCommand);
			_logList.setCommandListener(this);
			_display.setCurrent(_logList);
		}
		else {
			Alert a = new Alert("No log files to dump!");
			a.setTimeout(3000);
			a.addCommand(_exitCommand);
			a.setCommandListener(this);
			_display.setCurrent(a);
		}
		
		dump();
	}


	public void commandAction(Command c, Displayable d) {
		// TODO Auto-generated method stub
		if (c.getCommandType() == Command.EXIT) {
			destroyApp(true);
			notifyDestroyed();
		} else if (c.getCommandType() == Command.ITEM) {
			boolean[] checkedItems = new boolean[_logList.size()];
			_logList.getSelectedFlags(checkedItems);
			
			for (int i = checkedItems.length-1; i >= 0; i--) {
				if (checkedItems[i]) {
					try {
						deleteRecordStore(_logList.getString(i));
						_logList.delete(i);
					} catch (RecordStoreException e) {
						Alert a = new Alert("Problem deleting RecordStore #" + i);
						a.setTimeout(3000);
						a.addCommand(_exitCommand);
						a.setCommandListener(this);
						_display.setCurrent(a);
						
						// Uncheck the item from the list
						checkedItems[i] = false;
						_logList.setSelectedIndex(i, false);
					}
				}
			}
		}
	}
	
	private void deleteRecordStore(String rsName) throws RecordStoreException {
		try {
			// Ensure it's closed
			RecordStore rs = RecordStore.openRecordStore(rsName, false);
			rs.closeRecordStore();
			rs.closeRecordStore();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			RecordStore.deleteRecordStore(rsName);
		}
	}
	
	/*
	class Comparator implements RecordComparator {
		public int compare(byte[] record1, byte[] record2) {
			super(compare);
		    String string1 = new String(record1), string2 = new String(record2);
		    int comparison = string1.compareTo(string2);
		    if (comparison == 0)
		      return RecordComparator.EQUIVALENT;
		    else if (comparison < 0)
		      return RecordComparator.PRECEDES;
		    else
		      return RecordComparator.FOLLOWS;
		  }
		}
		*/
}
