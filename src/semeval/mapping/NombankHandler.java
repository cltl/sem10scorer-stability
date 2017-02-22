package semeval.mapping;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NombankHandler extends DefaultHandler {

	private String currentLemmaName;

	private HashMap<String, ArrayList<NBRoleset>> lemmaToRolesets;

	private ArrayList<NBRoleset> allRolesets;

	public NombankHandler() {

		lemmaToRolesets = new HashMap<String, ArrayList<NBRoleset>>();

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

		if (qualName.equals("predicate")) {

			currentLemmaName = atts.getValue("lemma");

			allRolesets = new ArrayList<NBRoleset>();

		}

		else if (qualName.equals("roleset")) {

			String id = atts.getValue("id");

			String name = atts.getValue("name");

			String source = atts.getValue("source");

			String vncls = atts.getValue("vncls");

			NBRoleset roleset = new NBRoleset(currentLemmaName, id);

			roleset.setName(name);

			roleset.setSource(source);

			roleset.setVnclass(vncls);

			allRolesets.add(roleset);
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

		if (qualName.equals("predicate")) {
			lemmaToRolesets.put(currentLemmaName, allRolesets);
		}

	}

	public void characters(char[] c, int start, int length) throws SAXException {

	}

	public void ignorableWhitespace(char[] c, int start, int length)
			throws SAXException {

	}

	public HashMap<String, ArrayList<NBRoleset>> getLemmaToRolesets() {
		return lemmaToRolesets;
	}

}
