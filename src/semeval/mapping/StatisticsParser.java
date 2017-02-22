package semeval.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Function see main method.
 */
public final class StatisticsParser {

	String newline = System.getProperty("line.separator");

	HashMap<String, Lemma> idToLemma;
	ArrayList<FrameLemmaPairFN> frameLemmaPairs;

	HashSet<Frame> frames1_2;
	ArrayList<Lexunit> lexunits;

	HashSet<VNToFN> vntofnMappings;

	HashSet<PBToVN> pbtovnMappings;

	HashMap<FrameLemmaPairFN, Integer> numberOfMappingsFnToVn;

	public StatisticsParser() throws Exception {

	}

	public static void main(String[] args) {

		try {

			StatisticsParser sm = new StatisticsParser();
			sm.run();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public String toString() {

		StringBuilder buffer = new StringBuilder();

		for (VNToFN mapping : vntofnMappings) {

			buffer.append(mapping.getVnclass() + " " + mapping.getVnmember()
					+ " " + mapping.getFnframe() + " " + mapping.getFnlexent()
					+ newline);

		}

		return buffer.toString();
	}

	private void run() throws Exception {
		this.initialize();

	}

	private void initialize() throws ParserConfigurationException,
			SAXException, IOException

	{

		numberOfMappingsFnToVn = new HashMap<FrameLemmaPairFN, Integer>();

		TigerParser tigerParser = new TigerParser();

		tigerParser.parseGraphFromFile("files\\tigerchen_merged.xml");

		this.idToLemma = tigerParser.getIdToLemma();
		this.frameLemmaPairs = tigerParser.getFrameLemmaPairs();

		mergeFramesAndLemmas();

		// suche nur nach Typen, statt nach Instanzen
		// frameLemmaPairs = extractTypes(frameLemmaPairs);

		FramesParser framesParser = new FramesParser();

		framesParser.parseGraphFromFile("files\\frames1.2.xml");

		this.frames1_2 = framesParser.getFrames();

		lexunits = framesParser.getLexunits();

		VNToFNParser vntofnParser = new VNToFNParser();

		vntofnParser
				.parseGraphFromFile("files\\semlink1.1\\vn-fn\\VNclass-FNframeMappings.xml");

		vntofnMappings = vntofnParser.getMappings();

		PBToVNParser pbtovnParser = new PBToVNParser();

		pbtovnParser
				.parseGraphFromFile("files\\semlink1.1\\vn-pb\\type_map.xml");

		pbtovnMappings = pbtovnParser.getMappings();

		HashMap<FrameLemmaPairFN, Boolean> frameIsInFN2 = checkFramesToFN12();

		HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairVN>> pairsFNToVN = 
			checkPairsToVN(frameIsInFN2);

		double types = frameLemmaPairs.size();

		// Wie viele Typen gibt es insgesamt?
		System.out.println("Typen insgesamt: " + types);

		// Wie viele Typen sind auf FN1.2 abbildbar?
		double inFN12 = 0;
		for (FrameLemmaPairFN pairFN : frameIsInFN2.keySet()) {

			if (frameIsInFN2.get(pairFN)) {
				inFN12++;
			}
		}
		System.out.println("Davon in FrameNet1.2: " + inFN12 + " ("
				+ (inFN12 / types) + ")");

		// Wie viele Typen sind auf VerbNet eindeutig abbildbar?
		int inVN = 0;

		for (FrameLemmaPairFN pairFN : pairsFNToVN.keySet()) {

			if (null != pairsFNToVN.get(pairFN)
					&& pairsFNToVN.get(pairFN).size() == 1) {
				inVN++;

			}
		}
		System.out.println("Davon eindeutig nach VerbNet abbildbar: " + inVN
				+ " (" + inVN / types + ")");

		// Wie viele Typen sind auf PropBank eindeutig abbildbar?

		HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairPB>> pairsFNToPB = checkFNToPB(pairsFNToVN);
		int noMapping = 0;
		int oneMapping = 0;
		int twoMappings = 0;
		int threeMappings = 0;
		int moreMappings = 0;
		for (FrameLemmaPairFN pairFN : pairsFNToPB.keySet()) {

			StringBuilder buffer = new StringBuilder();

			buffer.append(pairFN.getFrame().getName() + ", LU: "
					+ pairFN.getLemma().getName());

			int numberOfMappings = pairsFNToPB.get(pairFN).size();
			if (numberOfMappings == 0) {
				noMapping++;
			} else if (numberOfMappings == 1) {
				oneMapping++;
			} else if (numberOfMappings == 2) {
				twoMappings++;
			} else if (numberOfMappings == 3) {
				threeMappings++;
			} else if (numberOfMappings > 3) {
				moreMappings++;
			}

			for (FrameLemmaPairPB pairPB : pairsFNToPB.get(pairFN)) {

				String pb_roleset = pairPB.getPb_roleset();

				buffer.append(" -> " + pb_roleset);
			}

		}
		System.out.println("Davon eindeutig auf PropBank abbildbar: "
				+ oneMapping + " (" + oneMapping / types + ")");

	}

	private void mergeFramesAndLemmas() {

		HashSet<FrameLemmaPairFN> badPairs = new HashSet<FrameLemmaPairFN>();

		for (FrameLemmaPairFN pair : frameLemmaPairs) {

			String lemmaId = pair.getLemmaId();

			if (null == idToLemma.get(lemmaId)) {

				badPairs.add(pair);
			} else {

				pair.setLemma(idToLemma.get(pair.getLemmaId()));
			}
		}

		for (FrameLemmaPairFN pair : badPairs) {

			frameLemmaPairs.remove(pair);
		}

	}

	private ArrayList<FrameLemmaPairFN> extractTypes(
			ArrayList<FrameLemmaPairFN> frameLemmaPairs) {

		ArrayList<FrameLemmaPairFN> frameLemmaPairTypes = new ArrayList<FrameLemmaPairFN>();
		HashSet<String> types = new HashSet<String>();

		for (FrameLemmaPairFN pairFN : frameLemmaPairs) {

			String lemma = pairFN.getLemma().getName();
			String frame = pairFN.getFrame().getName();

			if (!types.contains(lemma + "XXX" + frame)) {

				types.add(lemma + "XXX" + frame);
				frameLemmaPairTypes.add(pairFN);
			}
		}

		return frameLemmaPairTypes;
	}

	private HashMap<FrameLemmaPairFN, Boolean> checkFramesToFN12() {

		HashMap<FrameLemmaPairFN, Boolean> frameIsInFN12 = new HashMap<FrameLemmaPairFN, Boolean>();

		for (FrameLemmaPairFN pair : frameLemmaPairs) {

			if (lexunitExists(pair)) {

				frameIsInFN12.put(pair, true);
			} else {
				frameIsInFN12.put(pair, false);

			}

		}

		return frameIsInFN12;
	}

	// und: es gibt eine Lexunit, die unter dem Frame ein Lexem
	// beinhaltet, das gleich dem Lemma von FrameLemmaPair ist.
	private boolean lexunitExists(FrameLemmaPairFN pair) {

		// iteriere über lexunits
		for (Lexunit lexunit : lexunits) {

			if (lexunit.getFrameName().equals(pair.getFrame().getName())
					&& lexunit.getLexemeName()
							.equals(pair.getLemma().getName())) {
				return true;
			}
		}

		return false;

	}

	private HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairVN>> checkPairsToVN(
			HashMap<FrameLemmaPairFN, Boolean> isInFN2) {

		HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairVN>> mappingPairs
		= new HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairVN>>();

		for (FrameLemmaPairFN pairFN : frameLemmaPairs) {

			if (isInFN2.get(pairFN)) {

				String lexunitID = "";

				for (Lexunit lexunit : lexunits) {

					if (lexunit.getFrameName().equals(
							pairFN.getFrame().getName())
							&& lexunit.getLexemeName().equals(
									pairFN.getLemma().getName())) {

						lexunitID = lexunit.getId();

						break;
					}
				}

				if (null == mappingPairs.get(pairFN)) {
					mappingPairs.put(pairFN, new ArrayList<FrameLemmaPairVN>());
				}
				int numberOfMappings = 0;
				for (VNToFN mapping : vntofnMappings) {

					if (mapping.getFnframe()
							.equals(pairFN.getFrame().getName())) {

						if (mapping.getFnlexent().equals(lexunitID)) {

							FrameLemmaPairVN pairVN = new FrameLemmaPairVN(
									mapping.getVnclass(), mapping.getVnmember());

							ArrayList<FrameLemmaPairVN> pairsVN = mappingPairs
									.get(pairFN);

							pairsVN.add(pairVN);

							numberOfMappings++;
						}
					}
				}

				numberOfMappingsFnToVn.put(pairFN, numberOfMappings);

			} else {

			}
		}

		return mappingPairs;
	}

	private HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairPB>> checkFNToPB(
			HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairVN>> fnvnMapping) {

		HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairPB>> fnpbMapping = new HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairPB>>();

		for (FrameLemmaPairFN pairFN : fnvnMapping.keySet()) {

			ArrayList<FrameLemmaPairVN> pairsVN = fnvnMapping.get(pairFN);

			ArrayList<FrameLemmaPairPB> pairsPB = new ArrayList<FrameLemmaPairPB>();

			// betrachte nur eindeutige Mappings
			if (null != pairsVN && pairsVN.size() == 1) {

				FrameLemmaPairVN pairVN = pairsVN.remove(0);

				String vnclass = pairVN.getVnclass();
				String lemma = pairVN.getVnmember();

				for (PBToVN pbtovn : pbtovnMappings) {

					if (pbtovn.getVn_class().equals(pairVN.getVnclass())
							&& pbtovn.getLemma().equals(pairVN.getVnmember())) {

						FrameLemmaPairPB pairPB = new FrameLemmaPairPB(pbtovn
								.getPb_roleset(), pbtovn.getLemma());
						pairsPB.add(pairPB);
					}
				}
			}

			fnpbMapping.put(pairFN, pairsPB);
		}

		return fnpbMapping;
	}
}
