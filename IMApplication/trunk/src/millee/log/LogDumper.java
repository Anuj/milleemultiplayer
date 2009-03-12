/**
 * 
 */
package millee.log;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import net.sf.microlog.Logger;
import net.sf.microlog.appender.ConsoleAppender;

/**
 * @author Simon
 *
 */
public class LogDumper extends MIDlet {
	
	// Logger instance to be used to write out
	private static final Logger log = Logger.getLogger(LogDumper.class);

	/**
	 * 
	 */
	public LogDumper() {
		log.addAppender(new ConsoleAppender());
		dump();
	}
			
			
	
	private void dump() {
		String[] recordStores = RecordStore.listRecordStores();
		if (recordStores == null) { return; }
		
		byte[] recordBytes = null;
		RecordStore rs = null;
		
		// Iterate through the record stores on this Suite...
		for (int i = 0; i < recordStores.length; i++) {
			try {
				rs = RecordStore.openRecordStore( recordStores[i], false );
				System.out.println("===" + rs.getName() + "===");
			
				// Iterate through each record in this record store...
				RecordEnumeration re = rs.enumerateRecords(null, null, false);
				while (re.hasNextElement()) {
					recordBytes = re.nextRecord();
					
					// Process the retrieved bytes outwards
					System.out.println(new String(recordBytes));
					// TODO: Blast this out over bluetooth serial? Use microlog to dump the log?
				}
				
				// TODO: Delete the record store afterwards?
				RecordStore.deleteRecordStore(rs.getName());				
			} catch (RecordStoreException e) {
				log.error("Problem with RecordStore!");
			}
		}	
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
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
		// TODO Auto-generated method stub

	}

}
