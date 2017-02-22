package semeval.noinstantiation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Aufgabe: Mache eine Liste mit allen LUs, die in mehr als 2 Frames vorkommen
 */
public class AmbiguityExtractor {

	String[] allFileNames;

	String fileDirectory = "files\\FrameNet\\leXML\\";

	ArrayList<LexicalEntry> lexicalEntries;

	HashSet<FrameWithFE> frames;

	String newline = System.getProperty("line.separator");

	public AmbiguityExtractor() {

		lexicalEntries = new ArrayList<LexicalEntry>();
		allFileNames = new File(fileDirectory).list();

	}

	public static void main(String[] args) {

		AmbiguityExtractor ne = new AmbiguityExtractor();

		try {
			ne.run();

		} catch (Exception e) {

			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void run() throws SAXException, ParserConfigurationException,
			IOException {

		/*
		 * Parse alle leXML-Dateien und überführe sie als Datenstrukturen in die
		 * ArrayList lexicalEntries.
		 */
		for (int i = 0; i < allFileNames.length; i++) {

			// parse die Datei und bekomme einen LexicalEntry.
			LEParser leParser = new LEParser();

			leParser.parseLEFromFile(fileDirectory + allFileNames[i]);

			lexicalEntries.add(leParser.getLexicalEntry());

		}

		StringBuilder buffer = new StringBuilder();

		HashMap<String, ArrayList<String>> leNameToFrames = new HashMap<String, ArrayList<String>>();
		
	
		for (LexicalEntry le : lexicalEntries) {

			String frame = le.getFrame();
			String leName = le.getName();
			
			if (null == leNameToFrames.get(leName)) {
				ArrayList<String> frames = new ArrayList<String>();
				
				frames.add(frame);
				leNameToFrames.put(leName, frames);
			} else {
				ArrayList<String> frames = leNameToFrames.get(leName);
				
				frames.add(frame);
			}
		}
		System.out.println(leNameToFrames.keySet().size());
		/*
		 * Schreibe jetzt aus leNameToFrames all diejenigen LUs raus, 
		 * die auf mehr als einen Frame gemappt werden.
		 */
		for (String lexicalUnit : leNameToFrames.keySet()) {
			
			ArrayList<String> frames = leNameToFrames.get(lexicalUnit);
			
			if (null != frames) {
				
				if (frames.size() > 1) {
					
					buffer.append(lexicalUnit + "\t" + frames.size() + "\t");
					
					for (String frame : frames) {
						buffer.append(frame + ",");
					}
					buffer.deleteCharAt(buffer.length() - 1);
					buffer.append(newline);
				}
			}
			
		}
		writeToFile(buffer.toString());

	}

	private void writeToFile(String text) throws IOException {

		File outputFile = new File("output.txt");
		FileWriter out = new FileWriter(outputFile);
		out.write(text);
		out.close();
	}

	private boolean isCore(String frame, String fe) {
		boolean isCore = false;

		for (FrameWithFE currentFrame : frames) {

			if (currentFrame.getName().equals(frame)) {
				for (FrameElement currentFe : currentFrame.getFes()) {
					if (currentFe.getName().equalsIgnoreCase(fe)
							&& currentFe.isCore()) {
						isCore = true;

					}
				}
			}
		}

		return isCore;
	}
}
