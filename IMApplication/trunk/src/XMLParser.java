import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.util.*;

class XMLParser extends DefaultHandler {
	
	private BlueToothExp midlet;
	private Vector nodes = new Vector();
	private Stack tagStack = new Stack();
	
	public XMLParser(BlueToothExp midlet) {
		System.out.println("created XMLParser");
		this.midlet = midlet;
	}
	
	public void startDocument() throws SAXException {
		System.out.println("startDocument");
	}

	public void startElement(String uri, String localName, String qName, 
	    Attributes attributes) throws SAXException
	{
	    if(qName.equals("node1"))
	    {
	      Noden node = new Noden();
	      nodes.addElement(node);
	    }
	    tagStack.push(qName);
	}

	  public void characters(char[] ch, int start, int length) throws SAXException
	  {
	    String chars = new String(ch, start, length).trim();

	    if(chars.length() > 0)
	    {
	      String qName = (String)tagStack.peek();
	      Noden currentnode = (Noden)nodes.lastElement();

	      if (qName.equals("name"))
	      {
	        currentnode.setName(chars);
	      }
	      else if(qName.equals("type"))
	      {
	        currentnode.setType(chars);
	      }
	    }
	  }

	  public void endElement(String uri, String localName, String qName, 
	    Attributes attributes) throws SAXException
	  {
	    tagStack.pop();
	  }

	  public void endDocument() throws SAXException
	  {
	    StringBuffer result = new StringBuffer();
	    for (int i=0; i<nodes.size(); i++)
	    {
	      Noden currentnode = (Noden)nodes.elementAt(i);
	      result.append("Name : "+ currentnode.getName() + " Type : " + 
	        currentnode.getType() + "\n");
	    }
	    midlet.alert(result.toString());
	  }


	  class Noden
	  {
	    private String name;
	    private String type;

	    public Noden()
	    {}

	    public void setName(String name)
	    {
	      this.name = name;
	    }

	    public void setType(String type)
	    {
	      this.type = type;
	    }

	    public String getName()
	    {
	      return name;
	    }

	    public String getType()
	    {
	      return type;
	    }
	  };
}