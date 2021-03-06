package semeval.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This parser parses an XML-file.
 * 
 * @author Fabian Shirokov
 * 
 */
public class VNToFNParser {

	/**
	 * This contains the SAX parser.
	 */
	private SAXParser parser;

	/**
	 * This contains the handler.
	 */
	private VNToFNHandler handler;

	/**
	 * This creates a new instance of <code>Parser</code>.
	 * 
	 * @throws ParserConfigurationException
	 *             if there is a problem with the parser factory
	 * @throws SAXException
	 *             if there is a problem when creating the parser
	 */
	public VNToFNParser() throws ParserConfigurationException, SAXException {

		// init parser factory
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		parserFactory.setValidating(true);

		parserFactory.setNamespaceAware(false);
		// create parser
		this.parser = parserFactory.newSAXParser();

		// create handler
		this.handler = new VNToFNHandler();
	}

	/**
	 * This parses an XML document from the given file with our
	 * <code>Handler</code> and our parser.
	 * 
	 * @param aFileName
	 *            a <code>String</code> with the file name to read the XML
	 *            document from
	 * @throws IOException
	 *             if there is a problem when reading the file
	 * @throws SAXException
	 *             if there is a problem when parsing the XML document
	 */
	public void parseGraphFromFile(String aFileName) throws IOException,
			SAXException {

		FileInputStream is = new FileInputStream(new File(aFileName));
		this.parser.parse(new InputSource(new InputStreamReader(is, "UTF-8")),
				this.handler);

	}

	public HashSet<VNToFN> getMappings() {

		return this.handler.getMappings();
	}

}
