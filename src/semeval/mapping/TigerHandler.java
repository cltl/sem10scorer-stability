package semeval.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TigerHandler extends DefaultHandler {

	private String currentElement;

	private HashMap<String, Lemma> idToLemma;

	private ArrayList<FrameLemmaPairFN> frameLemmaPairs;

	private HashSet<String> uninterestingFrames;

	boolean withinSentence = false;
	boolean withinTarget = false;

	int pairId = 0;

	String currentFrame;

	public TigerHandler() {

		idToLemma = new HashMap<String, Lemma>();
		frameLemmaPairs = new ArrayList<FrameLemmaPairFN>();
		uninterestingFrames = new HashSet<String>();
		uninterestingFrames.add("Coreference");
		uninterestingFrames.add("Support");
		uninterestingFrames.add("Relativization");
		uninterestingFrames.add("Unannotated");

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

		if (currentElement.equals("s")) {
			withinSentence = true;
		}
		if (currentElement.equals("target")) {
			withinTarget = true;
		}
		if (currentElement.equals("fe")) {
			withinTarget = false;
		}

		if (withinSentence && currentElement.equals("frame")) {

			currentFrame = atts.getValue("name");
		}

		if (withinSentence && currentElement.equals("t")) {

			String id = atts.getValue("id");
			String lemma = atts.getValue("lemma");

			String pos = atts.getValue("pos");


			if (!lemma.equals("<unknown>")) {
				Lemma newLemma = new Lemma(id, lemma);

				newLemma.setPos(pos);

				idToLemma.put(id, newLemma);
			}
		}

		if (withinSentence && withinTarget && currentElement.equals("fenode")) {

			String idref = atts.getValue("idref");

			if (!uninterestingFrames.contains(currentFrame)) {

				if (atts.getValue("is_split") != null
						&& atts.getValue("is_split").equals("yes")) {
					idref = idref.substring(0, idref.length() - 3);
				}

				FrameLemmaPairFN newPair = new FrameLemmaPairFN(pairId,
						new Frame(currentFrame), idref);

				frameLemmaPairs.add(newPair);

				pairId++;

			}

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

		if (qualName.equals("s")) {
			withinSentence = false;
		}
		if (qualName.equals("target")) {
			withinTarget = false;
		}

	}

	public void characters(char[] c, int start, int length) throws SAXException {

	}

	public void ignorableWhitespace(char[] c, int start, int length)
			throws SAXException {

	}

	public ArrayList<FrameLemmaPairFN> getFrameLemmaPairs() {

		return this.frameLemmaPairs;
	}

	public HashMap<String, Lemma> getIdToLemma() {

		return this.idToLemma;
	}
}
