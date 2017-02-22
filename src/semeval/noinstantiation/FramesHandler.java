package semeval.noinstantiation;

import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class FramesHandler extends DefaultHandler {

	private HashSet<FrameWithFE> frames;

	private String currentFrameName;

	private FrameWithFE currentFrame;
	
	public FramesHandler() {

		frames = new HashSet<FrameWithFE>(1000);
		

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
			FrameWithFE frame = new FrameWithFE(currentFrameName);

			frames.add(frame);
			currentFrame = frame;
		}
		
		else if (qualName.equals("fe")) {
			String feName = atts.getValue("name");
			String core = atts.getValue("coreType");
			boolean isCore = core.equals("Core") || core.equals("Core-Unexpressed");
			
			FrameElement fe = new FrameElement(isCore, feName);
			currentFrame.addFe(fe);
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
		
		if (qualName.equals("fes")) {
			currentFrame = null;
		} else if (qualName.equals("frames")) {
			currentFrame = null;
		}

	}

	public void characters(char[] c, int start, int length) throws SAXException {

	}

	public void ignorableWhitespace(char[] c, int start, int length)
			throws SAXException {

	}
	
	public HashSet<FrameWithFE> getFrames() {
		return frames;
	}

}
