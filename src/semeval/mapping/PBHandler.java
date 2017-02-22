package semeval.mapping;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PBHandler extends DefaultHandler {
	
	private String currentElement;
	
	private ArrayList<PBRoleset> rolesets;
	
	private String currentLemma;


	public PBHandler() {
		
		rolesets = new ArrayList<PBRoleset>();
		
		currentElement = "";


	}

	public void startDocument() {

	}

	/**
	 * This overrides {@link org.xml.sax.helpers.DefaultHandler#startElement}.
	 * This method is called when the parser has found an opening element tag.
	 * 
	 * @param uri
	 *            a <code>String</code> with the namespace URI, empty if
	 *            parser factory is not namespace aware (default)
	 * @param localName
	 *            a <code>String</code> with the local name (without prefix),
	 *            empty if parser factory is not namespace aware (default)
	 * @param qualName
	 *            a <code>String</code> with the qualified (with prefix) name,
	 *            or the empty string if qualified names are not available
	 * @param atts
	 *            <code>Attributes</code> attached to the element, empty if
	 *            there are no attributes
	 * @throws SAXException
	 *             if an error occurs, possibly wrapping another exception
	 */
	public void startElement(@SuppressWarnings("unused")
	String uri, @SuppressWarnings("unused")
	String localName, String qualName, Attributes atts) throws SAXException {
		currentElement = qualName;

		if (qualName.equals("predicate")) {
			
			currentLemma = atts.getValue("lemma");

		} else if (qualName.equals("roleset")) {
			
			String id = atts.getValue("id");
			
			String name = atts.getValue("name");
			
			PBRoleset newRoleset = new PBRoleset(id, name, currentLemma); 
			
			rolesets.add(newRoleset);

		} else if (qualName.equals("fe-relation")) {
			
		} 
		
	}

	/**
	 * This overrides {@link org.xml.sax.helpers.DefaultHandler#endElement}.
	 * This method is called when the parser has found a closing element tag.
	 * 
	 * This method is not used at the moment.
	 * 
	 * @param uri
	 *            a <code>String</code> with the namespace URI, empty if
	 *            parser factory is not namespace aware (default)
	 * @param localName
	 *            a <code>String</code> with the local name (without prefix),
	 *            empty if parser factory is not namespace aware (default)
	 * @param qualName
	 *            a <code>String</code> with the qualified (with prefix) name,
	 *            or the empty string if qualified names are not available
	 * @throws SAXException
	 *             if an error occurs, possibly wrapping another exception
	 */
	public void endElement(@SuppressWarnings("unused")
	String uri, @SuppressWarnings("unused")
	String localName, String qualName) throws SAXException {

	}

	public void characters(char[] c, int start, int length) throws SAXException {

	
	
	}

	public void ignorableWhitespace(char[] c, int start, int length)
			throws SAXException {

	}
	
	/**
	 * Returns the <code>PBRoleset</code> described in the XML file. 
	 */
	public ArrayList<PBRoleset> getRolesets() {
		return rolesets;
	}

	

}
