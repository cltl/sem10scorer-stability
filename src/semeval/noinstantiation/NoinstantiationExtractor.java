package semeval.noinstantiation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class NoinstantiationExtractor {

	String[] allFileNames;

	String fileDirectory = "files\\FrameNet\\leXML\\";

	ArrayList<LexicalEntry> lexicalEntries;

	HashSet<FrameWithFE> frames;

	String newline = System.getProperty("line.separator");

	public NoinstantiationExtractor() {

		lexicalEntries = new ArrayList<LexicalEntry>();
		allFileNames = new File(fileDirectory).list();

	}

	public static void main(String[] args) {

		NoinstantiationExtractor ne = new NoinstantiationExtractor();

		try {
			ne.run();

		} catch (Exception e) {

			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void run() throws SAXException, ParserConfigurationException,
			IOException {

		for (int i = 0; i < allFileNames.length; i++) {

			// parse die Datei und bekomme einen LexicalEntry.
			LEParser leParser = new LEParser();

			leParser.parseLEFromFile(fileDirectory + allFileNames[i]);

			lexicalEntries.add(leParser.getLexicalEntry());

		}

		// parse alle Frames, um pruefen zu koennen ob sie Core sind
		FramesParser frParser = new FramesParser();
		frParser.parseFramesFromFile("files\\frames.xml");
		frames = frParser.getFrames();

		// entferne alle nicht V, Adj, Prep aus den LEs.
		ArrayList<LexicalEntry> newLexEntries = new ArrayList<LexicalEntry>();
		for (LexicalEntry entry : lexicalEntries) {

			String currentPos = entry.getPos();

			if (currentPos.equals("V") || currentPos.equals("A")
					|| currentPos.equals("PREP")) {

				newLexEntries.add(entry);
			}
		}
		lexicalEntries = newLexEntries;

		StringBuilder buffer = new StringBuilder();

		for (LexicalEntry le : lexicalEntries) {

			String frame = le.getFrame();
			String leName = le.getName();

			int ini = 0;
			int cni = 0;
			int dni = 0;

			HashMap<String, Integer> feToIni = new HashMap<String, Integer>();
			HashMap<String, Integer> feToDni = new HashMap<String, Integer>();
			HashMap<String, Integer> feToCni = new HashMap<String, Integer>();
			HashSet<String> fes = new HashSet<String>();

			for (FERealization real : le.getFeRealizations()) {

				ArrayList<ValenceUnit> valenceUnits = real.getValenceUnits();

				// Suche fuer jedes LU-FramePaar, wie viele DNI, INI und CNI

				String fe = real.getValenceUnits().get(0).getFe();

				ValenceUnit vu = real.getValenceUnits().get(0);

				int total = real.getTotal();

				String pt = vu.getPt();
				if (pt.equals("INI")) {
					if (null == feToIni.get(fe)) {
						feToIni.put(fe, total);
					} else {
						feToIni.put(fe, feToIni.get(fe) + total);
					}
					fes.add(fe);
				} else if (pt.equals("DNI")) {
					if (null == feToDni.get(fe)) {
						feToDni.put(fe, total);
					} else {
						feToDni.put(fe, feToDni.get(fe) + total);
					}
					fes.add(fe);
				} else if (pt.equals("CNI")) {
					if (null == feToCni.get(fe)) {
						feToCni.put(fe, total);
					} else {
						feToCni.put(fe, feToCni.get(fe) + total);
					}

					fes.add(fe);
				}

			}

			for (String fe : fes) {

				ini = 0;
				dni = 0;
				cni = 0;

				StringBuilder currentLine = new StringBuilder();

				currentLine.append("<td>" + frame + "</td><td>" + leName
						+ "</td><td>" + fe + "</td>");

				// number of INI:
				if (null != feToIni.get(fe)) {
					ini = feToIni.get(fe);
					currentLine.append("<td>" + ini + "</td>");

				} else {
					currentLine.append("<td>" + 0 + "</td>");
				}

				// number of CNI:
				if (null != feToCni.get(fe)) {
					cni = feToCni.get(fe);
					currentLine.append("<td>" + cni + "</td>");

				} else {
					currentLine.append("<td>" + 0 + "</td>");
				}

				// number of DNI:
				if (null != feToDni.get(fe)) {
					dni = feToDni.get(fe);
					currentLine.append("<td>" + dni + "</td>");

				} else {
					currentLine.append("<td>" + 0 + "</td>");
				}

				if (!isCore(frame, fe)) {
					if (ini > 0 && dni > 0) {
						buffer.append("<tr bgcolor=\"#9D0606\">" + currentLine
								+ "</tr>" + newline);
					} else if (ini <= 0 && dni <= 0 && cni <= 0) {
						buffer.append("<tr bgcolor=\"blue\">" + currentLine
								+ "</tr>" + newline);
					} else {
						buffer.append("<tr bgcolor=\"yellow\">" + currentLine + "</tr>" + newline);

					}
				} else {
					if (ini > 0 && dni > 0) {
						buffer.append("<tr bgcolor=\"#FF6161\">" + currentLine
								+ "</tr>" + newline);
					} else if (ini <= 0 && dni <= 0 && cni <= 0) {
						buffer.append("<b><tr bgcolor=\"green\">" + currentLine
								+ "</tr></b>" + newline);
					} else {
						buffer.append("<b><tr>" + currentLine + "</tr></b>" + newline);

					}

				}

			}

		}

		writeToFile("<html><body><table><th>Frame</th><th>Lexical Unit</th><th>" +
				"FE</th><th>   INI   </th><th>   CNI   </th><th>   DNI   </th>"
				+ buffer + "</table><table align=\"center\" border=\"5\"><tr bgcolor=\"#FF6161\"><td>FE is Core and " +
						"both DNI and INI exist" +
						"</td></tr><tr><td>FE is Core and some DNI, INI or CNI exists</td></tr><tr bgcolor=\"#9D0606\"><td>FE is " +
						"not Core and both DNI and INI exist</td></tr>" +
						"<tr bgcolor=\"yellow\"><td>FE is not Core and some DNI, INI" +
						" or CNI exists</td></tr></table>Note that the frame" +
						" Cause_confinement, as well as the FE Co_agent of the" +
						" frame Assistance were not declared as Core because " +
						"they did not occur in the frames.xml file of FN release 1.3</body></html>");

	}

	private void writeToFile(String text) throws IOException {

		File outputFile = new File("output.html");
		FileWriter out = new FileWriter(outputFile);
		out.write(text);
		out.close();
	}

	private boolean isCore(String frame, String fe) {
		boolean isCore = false;

		for (FrameWithFE currentFrame : frames) {

			if (currentFrame.getName().equals(frame)) {
				for (FrameElement currentFe : currentFrame.getFes()) {
					if (currentFe.getName().equalsIgnoreCase(fe) && currentFe.isCore()) {
						isCore = true;
						
					}
				}
			}
		}

		return isCore;
	}
}
