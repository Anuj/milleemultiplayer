package millee.game.initialize;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Image;

public class Utilities {
	
	private static final String DEFAULT_IMAGE = "/dancer_small.png";

	public static Image createImage(String path) {
		try {
			return Image.createImage(path);
		}
        catch (IOException e) {
        	System.err.println("IOException: " + e);
        	// TODO: Create and return a default image
        	//return Image.createImage(DEFAULT_IMAGE);
        	return null;
        }
	}
	
	/**
	 * A class for splitting strings in J2ME applications as can be done by 
	 * using java.lang.String#split() in J2SE.
	 *
	 * @author Lasse Koskela
	 */
	    /**
	     * A method for splitting a string in J2ME.
	     *
	     * @param splitStr The string to split.
	     * @param delimiter The characters to use as delimiters.
	     * @param limit The result threshold as documented in 
	     *              java.lang.String#split(String, int).
	     * @return An array of strings.
	     */
	    public static String[] split(String splitStr, String delimiter, int limit)
	    {
	        // some input validation / short-circuiting
	        if (delimiter == null || delimiter.length() == 0)
	        {
	            return new String[] { splitStr };
	        }
	        else if (splitStr == null)
	        {
	            return new String[0];
	        }
	        
	        // enabling switches based on the 'limit' parameter
	        boolean arrayCanHaveAnyLength = false;
	        int maximumSplits = Integer.MAX_VALUE;
	        boolean dropTailingDelimiters = true;
	        if (limit < 0)
	        {
	            arrayCanHaveAnyLength = true;
	            maximumSplits = Integer.MAX_VALUE;
	            dropTailingDelimiters = false;
	        }
	        else if (limit > 0)
	        {
	            arrayCanHaveAnyLength = false;
	            maximumSplits = limit - 1;
	            dropTailingDelimiters = false;
	        }
	        
	        StringBuffer token = new StringBuffer();
	        Vector tokens = new Vector();
	        char[] chars = splitStr.toCharArray();
	        boolean lastWasDelimiter = false;
	        int splitCounter = 0;
	        for (int i = 0; i < chars.length; i++)
	        {
	            // check for a delimiter
	            if (i + delimiter.length() <= chars.length && splitCounter < maximumSplits)
	            {
	                String candidate = new java.lang.String(chars, i, delimiter.length());
	                if (candidate.equals(delimiter))
	                {
	                    tokens.addElement(token.toString());
	                    token.setLength(0);
	                    
	                    lastWasDelimiter = true;
	                    splitCounter++;
	                    i = i + delimiter.length() - 1;
	                    
	                    continue; // continue the for-loop
	                }
	            }
	            
	            // this character does not start a delimiter -> append to the token
	            token.append(chars[i]);
	            lastWasDelimiter = false;
	        }
	        // don't forget the "tail"...
	        if (token.length() > 0 || (lastWasDelimiter && !dropTailingDelimiters))
	        {
	            tokens.addElement(token.toString());
	        }
	        // convert the vector into an array
	        String[] splitArray = new String[tokens.size()];
	        for (int i = 0; i < splitArray.length; i++)
	        {
	            splitArray[i] = (String) tokens.elementAt(i);
	        }
	        return splitArray;
	    }

}
