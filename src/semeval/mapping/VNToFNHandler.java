package semeval.mapping;

import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class VNToFNHandler extends DefaultHandler {

	HashSet<VNToFN> mappings;

	public VNToFNHandler() {

		this.mappings = new HashSet<VNToFN>();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, name);
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void startElement(@SuppressWarnings("unused")
	String uri, @SuppressWarnings("unused")
	String localName, String qualName, Attributes atts) throws SAXException {

		if (qualName.equals("vncls")) {

			VNToFN mapping = new VNToFN(atts.getValue("class"), atts
					.getValue("vnmember"), atts.getValue("fnframe"), atts
					.getValue("fnlexent"), atts.getValue("versionID"));

			mappings.add(mapping);
		}
	}

	public HashSet<VNToFN> getMappings() {

		return this.mappings;
	}

}
