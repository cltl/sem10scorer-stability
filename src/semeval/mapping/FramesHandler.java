package semeval.mapping;

import java.util.ArrayList;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FramesHandler extends DefaultHandler {

	private HashSet<Frame> frames;

	private String currentFrameName;

	private ArrayList<Lexunit> lexunits;

	Lexunit currentLexunit;

	public FramesHandler() {

		frames = new HashSet<Frame>();
		lexunits = new ArrayList<Lexunit>();

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

		if (qualName.equals("frame")) {

			currentFrameName = atts.getValue("name");
			Frame frame = new Frame(currentFrameName);

			frames.add(frame);
		}

		if (qualName.equals("lexunit")) {

			currentLexunit = new Lexunit(atts.getValue("ID"), atts
					.getValue("name"), atts.getValue("pos"), currentFrameName);

			lexunits.add(currentLexunit);
		}

		if (qualName.equals("lexeme")) {

			currentLexunit.setLexemeId(atts.getValue("ID"));
			currentLexunit.setLexemeName(atts.getValue("name"));
			currentLexunit.setLexemePos(atts.getValue("pos"));

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

	public HashSet<Frame> getFrames() {

		return frames;
	}

	public ArrayList<Lexunit> getLexunits() {

		return this.lexunits;
	}

}
