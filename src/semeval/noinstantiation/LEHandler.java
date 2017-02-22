package semeval.noinstantiation;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LEHandler extends DefaultHandler {

	LexicalEntry lexicalEntry;
	String currentElement;

	ArrayList<FERealization> feRealizations;
	
	FERealization currentFERealization;

	public LEHandler() {

		currentElement = "";

		feRealizations = new ArrayList<FERealization>();
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

		if (qualName.equals("lexical-entry")) {

			String frame = atts.getValue("frame");

			String name = atts.getValue("name");

			String pos = atts.getValue("pos");

			this.lexicalEntry = new LexicalEntry(frame, name, pos);
		} else if (qualName.equals("FERealization")) {

			int total = Integer.parseInt(atts.getValue("total"));

			currentFERealization = new FERealization(total);
			
			feRealizations.add(currentFERealization);
			
		} else if (qualName.equals("valence-unit")) {
			
			String fe = atts.getValue("fe");
			String pt = atts.getValue("pt");
			String gf = atts.getValue("gf");
			
			ValenceUnit vu = new ValenceUnit(fe, pt, gf);
			
			currentFERealization.addValenceUnit(vu);
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

		if (currentElement.equals("definition")) {

			// add the definition to the lexical entry
			lexicalEntry.setDefinition((new String(c, start, length)));
		}

	}

	public void ignorableWhitespace(char[] c, int start, int length)
			throws SAXException {

	}

	LexicalEntry getLexicalEntry() {
		
		lexicalEntry.setFeRealizations(feRealizations);

		return this.lexicalEntry;
	}

}
