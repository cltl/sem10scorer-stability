package semeval.mapping;

import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PBToVNHandler extends DefaultHandler {

	HashSet<PBToVN> mappings;
	
	String currentLemma;

	public PBToVNHandler() {

		mappings = new HashSet<PBToVN>();
		currentLemma = "NONE";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		super.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {

		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
	
		super.endElement(uri, localName, name);
	}

	@Override
	public void startDocument() throws SAXException {

		super.startDocument();
	}

	@Override
	public void startElement(@SuppressWarnings("unused")
	String uri, @SuppressWarnings("unused")
	String localName, String qualName, Attributes atts) throws SAXException {

		if (qualName.equals("predicate")) {
			
			currentLemma = atts.getValue("lemma");
		}
		
		if (qualName.equals("argmap")) {
			
			String pb_roleset = atts.getValue("pb-roleset");
			String vn_class = atts.getValue("vn-class");
			
			PBToVN mapping = new PBToVN(currentLemma, pb_roleset, vn_class);
			
			mappings.add(mapping);
		}
	}

	public HashSet<PBToVN> getMappings() {
		
		return mappings;
	}
}
