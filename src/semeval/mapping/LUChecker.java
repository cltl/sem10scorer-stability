package semeval.mapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import semeval.noinstantiation.LexicalEntry;

/**
 * <code>LUChecker</code> provides several methods to process the Semlink and
 * PropBank data.
 * 
 * @author Fabian Shirokov
 * 
 */
public class LUChecker {

	/**
	 * Contains a list of the file names of the corpora to be parsed. (Those
	 * corpora to extract the LUs from, like e. g. 'tigerchen_merged.xml')
	 */
	private String[] corporaList;

	/**
	 * Is <code>true</code> if the user wants to work with 'types', instead of
	 * 'instances'.
	 */
	private boolean useTypes;

	/**
	 * This list contains all <code>FrameLemmaPairFN</code> elements that are
	 * in the corpora.
	 */
	private ArrayList<FrameLemmaPairFN> allFNPairs;

	/**
	 * This list contains all <code>FrameLemmaPairFN</code> elements of the
	 * corpus that are in the FrameNet 1.2 release.
	 */
	private ArrayList<FrameLemmaPairFN> allFN12Pairs;

	private HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairVN>> fnToVnMapping;

	private HashMap<FrameLemmaPairFN, String> fnToLexunitMapping;

	private HashMap<FrameLemmaPairVN, ArrayList<FrameLemmaPairPB>> vnToPbMapping;

	private ArrayList<ArrayList<PBRoleset>> rolesetLists;

	private HashMap<String, ArrayList<NBRoleset>> nombankLemmaToRolesets;

	private HashSet<PBToVN> shallowPbToVnMapping;

	private HashSet<VNToFN> shallowVnToFnMapping;

	private ArrayList<Lexunit> lexunits;

	private HashMap<String, String> posMappingPennToFramenet;

	/**
	 * Default constructor that takes a list of Salsa corpora and 'useTypes' as
	 * arguments. The LUs will be extracted out of those corpora.
	 * 
	 * @param corporaList
	 *            Includes a list of the file names of the corpora to be parsed
	 *            (Those corpora to extract the LUs from, like e. g.
	 *            'tigerchen_merged.xml').
	 * @param useTypes
	 *            This is <code>true</code> if you want to regard 'types' of
	 *            LUs, not 'instances' (e. g. the LU Abounding_with<->dotted.a
	 *            can have several instances, but only one type).
	 */
	public LUChecker(String[] corporaList, boolean useTypes)
			throws SAXException, ParserConfigurationException, IOException {
		super();
		this.corporaList = corporaList;
		this.useTypes = useTypes;
		this.fnToLexunitMapping = new HashMap<FrameLemmaPairFN, String>();
		this.posMappingPennToFramenet = initializePosMappingPennToFramenet();

		System.out.print("initialize the tiger corpora... ");

		allFNPairs = initializeAllFNPairs();

		System.out.println("ok");

		System.out.print("initialize FrameNet 1.2... ");

		allFN12Pairs = initializeAllFN12Pairs();

		System.out.println("ok");

		System.out.print("initialize FN-VN mapping... ");

		fnToVnMapping = initializeFnToVnMapping();

		System.out.println("ok");

		System.out.print("initialize VN-PB mapping... ");

		vnToPbMapping = initializeVnToPbMapping();

		System.out.println("ok");

		System.out.print("initialize PropBank... ");

		rolesetLists = initializeRolesetLists();
		System.out.println("ok");

		System.out.print("initialize Nombank... ");

		nombankLemmaToRolesets = initializeNombankLemmaToRolesets();
		System.out.println("ok");

	}

	/**
	 * Returns the value of 'useTypes'.
	 */
	public boolean getUseTypes() {
		return this.useTypes;
	}

	/**
	 * Returns a list of all <code>FrameLemmaPairFN</code> elements that have
	 * been found in the given corpora.
	 */
	public ArrayList<FrameLemmaPairFN> getAllFNPairs() {

		return this.allFNPairs;
	}

	/**
	 * Returns a <code>FrameLemmaPairDB</code> if there is only one PropBank
	 * roleset for the given lemma. Otherwise, it will return <code>null</code>.
	 * This method uses only PropBank, without looking at the Semlink data.
	 * 
	 * @param pairFN
	 * @return
	 */
	public FrameLemmaPairPB getPBDirectlyOf(FrameLemmaPairFN pairFN) {

		ArrayList<FrameLemmaPairPB> suitableRolesets = new ArrayList<FrameLemmaPairPB>();

		for (ArrayList<PBRoleset> currentRolesets : rolesetLists) {

			for (PBRoleset set : currentRolesets) {

				if (set.getLemma().equals(pairFN.getLemma().getName())) {

					suitableRolesets.add(new FrameLemmaPairPB(set.getId(), set
							.getLemma()));
				}
			}
		}

		if (suitableRolesets.size() == 1) {
			return suitableRolesets.get(0);
		} else {

			return null;
		}
	}

	/**
	 * Returns the list of <code>FrameLemmaPairPB</code> elements that
	 * correspond to the given <code>FrameLemmaPairVN</code> element. This is
	 * done by evaluating the Semlink data.
	 * 
	 * @param pairVN
	 * @return
	 */
	public ArrayList<FrameLemmaPairPB> getPBOf(FrameLemmaPairVN pairVN) {

		return vnToPbMapping.get(pairVN);
	}

	/**
	 * Returns the list of <code>FrameLemmaPairVN</code> elements that
	 * correspond to the given <code>FrameLemmaPairFN</code> element. This is
	 * done by evaluating the Semlink data.
	 * 
	 * @param pairFN
	 * @return
	 */
	public ArrayList<FrameLemmaPairVN> getVNOf(FrameLemmaPairFN pairFN) {

		return fnToVnMapping.get(pairFN);
	}

	/**
	 * Returns the list of <code>FrameLemmaPairPB</code> elements that have
	 * been retrieved directly over nombank.
	 */
	public ArrayList<FrameLemmaPairPB> getPBOverNombankDirectly(
			FrameLemmaPairFN pairFN) {

		String currentLemma = pairFN.getLemma().getName();

		ArrayList<NBRoleset> nbrolesets = nombankLemmaToRolesets
				.get(currentLemma);

		ArrayList<FrameLemmaPairPB> pbrolesets = new ArrayList<FrameLemmaPairPB>();

		if (null != nbrolesets) {
			for (NBRoleset nbroleset : nbrolesets) {

				String roleset = nbroleset.getSource();

				String lemma = nbroleset.getLemma();

				FrameLemmaPairPB pairPB = new FrameLemmaPairPB(roleset, lemma);

				pbrolesets.add(pairPB);
			}
		}

		return pbrolesets;
	}

	/**
	 * Returns the list of <code>FrameLemmaPairPB</code> elements that have
	 * been retrieved over nombank and semlink (using nombank first, then
	 * verbnet).
	 */
	public ArrayList<FrameLemmaPairPB> getPBOverNombankAndVerbnet(
			FrameLemmaPairFN pairFN) {

		String currentLemma = pairFN.getLemma().getName();

		ArrayList<NBRoleset> nbrolesets = nombankLemmaToRolesets
				.get(currentLemma);

		ArrayList<FrameLemmaPairPB> pbrolesets = new ArrayList<FrameLemmaPairPB>();

		if (null != nbrolesets) {
			for (NBRoleset nbroleset : nbrolesets) {

				String vncls = nbroleset.getVnclass();

				String lemma = nbroleset.getLemma();

				FrameLemmaPairVN pairVN = null;

				for (FrameLemmaPairVN currentPairVN : vnToPbMapping.keySet()) {

					if (currentPairVN.getVnclass().equals(vncls)
							&& currentPairVN.getVnmember().equals(lemma)) {

						pairVN = currentPairVN;
					}

				}

				if (null != pairVN) {
					ArrayList<FrameLemmaPairPB> currentPbRolesets = getPBOf(pairVN);

					if (null != currentPbRolesets) {

						pbrolesets.addAll(currentPbRolesets);
					}
				}
			}
		}

		return pbrolesets;
	}

	/**
	 * Checks via Semlink if a PropBank-Roleset corresponds to a given FrameNet
	 * frame. (going from PropBank over VerbNet to FrameNet)
	 */
	public boolean correspondsToFrame(FrameLemmaPairPB pairPB, String fnFrame) {

		String originalRoleset = pairPB.getPb_roleset();

		if (originalRoleset.startsWith("verb-")) {
			originalRoleset = originalRoleset.substring(5);
		}

		// Suche in VN-PB Mapping nach passenden FrameLemmaPairVN-Elementen
		ArrayList<FrameLemmaPairVN> pairsVN = new ArrayList<FrameLemmaPairVN>();

		for (PBToVN pbtovn : shallowPbToVnMapping) {

			String pbroleset = pbtovn.getPb_roleset();

			String vncls = pbtovn.getVn_class();

			String lemma = pbtovn.getLemma();

			if (originalRoleset.equals(pbroleset)
					&& pairPB.getLemma().equals(lemma)) {

				FrameLemmaPairVN pairVN = new FrameLemmaPairVN(vncls, lemma);

				pairsVN.add(pairVN);
			}
		}

		// Suche in FN-VN Mapping nach passendem Frame

		for (FrameLemmaPairVN pairVN : pairsVN) {

			String vncls = pairVN.getVnclass();

			String lemma = pairVN.getVnmember();

			for (VNToFN vntofn : this.shallowVnToFnMapping) {

				if (vntofn.getVnclass().equals(vncls)
						&& vntofn.getVnmember().equals(lemma)
						&& vntofn.getFnframe().equals(fnFrame)) {

					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This returns <code>true</code> if the given
	 * <code>FrameLemmaPairFN</code> exists in the FrameNet release 1.2.
	 */
	public boolean isInFN12(FrameLemmaPairFN pair) {

		if (allFN12Pairs.contains(pair)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the POS-Tag of a <code>FrameLemmaPair</code>, e. g. "V" or
	 * "N". (as defined in frames1.2.xml)
	 * 
	 * @param pairFN
	 * @return
	 */
	public String getPosOf(FrameLemmaPairFN pairFN) {

		String pos = posMappingPennToFramenet.get(pairFN.getLemma().getPos());

		if (null == pos) {
			pos = "UNKNOWN";
		}
		
		return pos;
	}

	/**
	 * Reads out the corpora of 'corporaList' and creates a list of
	 * <code>FrameLemmaPairFN</code> out of them.
	 * 
	 * @return
	 */
	private ArrayList<FrameLemmaPairFN> initializeAllFNPairs()
			throws ParserConfigurationException, SAXException, IOException {

		ArrayList<FrameLemmaPairFN> allPairs = new ArrayList<FrameLemmaPairFN>();

		for (String currentFileName : corporaList) {

			TigerParser tigerParser = new TigerParser();

			tigerParser.parseGraphFromFile(currentFileName);

			HashSet<FrameLemmaPairFN> toBeDeleted = new HashSet<FrameLemmaPairFN>();

			for (FrameLemmaPairFN pairfn : tigerParser.getFrameLemmaPairs()) {

				Lemma lemma = tigerParser.getIdToLemma().get(
						pairfn.getLemmaId());

				if (null == lemma) {
					toBeDeleted.add(pairfn);
				}

				pairfn.setLemma(lemma);

			}

			for (FrameLemmaPairFN pair : toBeDeleted) {
				tigerParser.getFrameLemmaPairs().remove(pair);
			}

			allPairs.addAll(tigerParser.getFrameLemmaPairs());
		}

		if (this.useTypes == true) {
			allPairs = extractTypes(allPairs);
		}

		return allPairs;
	}

	/**
	 * This class gets a list of <code>FrameLemmaPairFN</code> instances and
	 * returns a list that contains only one instance per type (-> the types).
	 * 
	 * @param frameLemmaPairs
	 * @return
	 */
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

	/**
	 * Returns a list of all <code>FrameLemmaPairFN</code> elements of the
	 * corpora that are a part of the FrameNet 1.2 release.
	 * 
	 * @return
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	private ArrayList<FrameLemmaPairFN> initializeAllFN12Pairs()
			throws SAXException, ParserConfigurationException, IOException {

		allFN12Pairs = new ArrayList<FrameLemmaPairFN>();

		FramesParser framesParser = new FramesParser();

		framesParser.parseGraphFromFile("files\\frames1.2.xml");

		lexunits = framesParser.getLexunits();

		for (FrameLemmaPairFN currentPair : allFNPairs) {

			if (lexunitExists(currentPair, lexunits)) {

				allFN12Pairs.add(currentPair);

			}
		}

		return allFN12Pairs;
	}

	/**
	 * Returns <code>true</code> if there is a <code>Lexunit</code> that has
	 * the same frame name and lemma as the 'pair'.
	 * 
	 * @param pair
	 * @param lexunits
	 * @return
	 */
	private boolean lexunitExists(FrameLemmaPairFN pair,
			ArrayList<Lexunit> lexunits) {

		String frame = pair.getFrame().getName();

		String lemma = pair.getLemma().getName();

		String pos = pair.getLemma().getPos();

		for (Lexunit lexunit : lexunits) {

			if (lexunit.getFrameName().equals(frame)
					&& lexunit.getLexemeName().equals(lemma)
					&& lexunit.getPos().equals(posMappingPennToFramenet.get(pos))) {

				fnToLexunitMapping.put(pair, lexunit.getId());

				return true;
			}
		}

		return false;

	}

	/**
	 * Returns a <code>HashMap</code> that maps the
	 * <code>FrameLemmaPairFN</code> elements out of the FrameNet 1.2 release
	 * to their corresponding <code>FrameLemmaPairVN</code> elements.
	 * 
	 * @return
	 */
	private HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairVN>> initializeFnToVnMapping()
			throws SAXException, ParserConfigurationException, IOException {

		VNToFNParser vntofnParser = new VNToFNParser();

		vntofnParser.parseGraphFromFile("files\\semlink1.1\\vn-fn\\"
				+ "VNclass-FNframeMappings.xml");

		HashSet<VNToFN> shallowMapping = vntofnParser.getMappings();

		this.shallowVnToFnMapping = shallowMapping;

		fnToVnMapping = new HashMap<FrameLemmaPairFN, ArrayList<FrameLemmaPairVN>>();

		for (FrameLemmaPairFN pairFN : allFN12Pairs) {

			String lexunitId = fnToLexunitMapping.get(pairFN);

			if (null == fnToVnMapping.get(pairFN)) {

				fnToVnMapping.put(pairFN, new ArrayList<FrameLemmaPairVN>());
			}

			for (VNToFN mapping : shallowMapping) {

				if (mapping.getFnframe().equals(pairFN.getFrame().getName())) {

					if (mapping.getFnlexent().equals(lexunitId)) {

						FrameLemmaPairVN pairVN = new FrameLemmaPairVN(mapping
								.getVnclass(), mapping.getVnmember());

						ArrayList<FrameLemmaPairVN> pairsVN = fnToVnMapping
								.get(pairFN);

						pairsVN.add(pairVN);

					}
				}
			}

		}

		return fnToVnMapping;
	}

	/**
	 * Returns a mapping from <code>FrameLemmaPairVN</code> to the list of the
	 * corresponding <code>FrameLemmaPairPB</code> elements, using Semlink.
	 */
	private HashMap<FrameLemmaPairVN, ArrayList<FrameLemmaPairPB>> initializeVnToPbMapping()
			throws SAXException, ParserConfigurationException, IOException {

		PBToVNParser pbtovnParser = new PBToVNParser();

		pbtovnParser
				.parseGraphFromFile("files\\semlink1.1\\vn-pb\\type_map.xml");

		HashSet<PBToVN> shallowMapping = pbtovnParser.getMappings();

		this.shallowPbToVnMapping = shallowMapping;

		HashMap<FrameLemmaPairVN, ArrayList<FrameLemmaPairPB>> vnToPbMapping = new HashMap<FrameLemmaPairVN, ArrayList<FrameLemmaPairPB>>();

		HashSet<FrameLemmaPairVN> seenPair = new HashSet<FrameLemmaPairVN>();

		for (ArrayList<FrameLemmaPairVN> pairsVn : fnToVnMapping.values()) {

			for (FrameLemmaPairVN pairVN : pairsVn) {

				if (!seenPair.contains(pairVN)) {

					String vnclass = pairVN.getVnclass();
					String lemma = pairVN.getVnmember();

					for (PBToVN pbtovn : shallowMapping) {

						if (pbtovn.getVn_class().equals(vnclass)
								&& pbtovn.getLemma().equals(lemma)) {

							FrameLemmaPairPB pairPB = new FrameLemmaPairPB(
									pbtovn.getPb_roleset(), pbtovn.getLemma());

							ArrayList<FrameLemmaPairPB> pairsPB = vnToPbMapping
									.get(pairVN);

							if (null == pairsPB) {
								pairsPB = new ArrayList<FrameLemmaPairPB>();
							}

							pairsPB.add(pairPB);

							vnToPbMapping.put(pairVN, pairsPB);
						}
					}
				}
			}
		}

		return vnToPbMapping;
	}

	/**
	 * Reads out all nombank files and creates a map of all rolesets out of
	 * them.
	 */
	private HashMap<String, ArrayList<NBRoleset>> initializeNombankLemmaToRolesets()
			throws ParserConfigurationException, IOException, SAXException {

		String folder = "files\\nombank\\frames\\";

		String[] allFileNames = new File(folder).list();

		nombankLemmaToRolesets = new HashMap<String, ArrayList<NBRoleset>>();

		for (String currentFile : allFileNames) {

			if (currentFile.endsWith(".xml")) {

				NombankParser parser = new NombankParser();

				parser.parseGraphFromFile(folder + currentFile);

				nombankLemmaToRolesets.putAll(parser.getLemmaToRolesets());
			}
		}
		return nombankLemmaToRolesets;

	}

	/**
	 * Reads out the propbank files and returns a list of lists of
	 * <code>PBRoleset</code>.
	 */
	private ArrayList<ArrayList<PBRoleset>> initializeRolesetLists()
			throws ParserConfigurationException, IOException, SAXException {

		String folder = "files\\propbank\\";

		String[] allFileNames = new File(folder).list();

		rolesetLists = new ArrayList<ArrayList<PBRoleset>>();

		for (String currentFile : allFileNames) {

			if (currentFile.endsWith(".xml")) {

				PBParser parser = new PBParser();

				parser.parseLEFromFile(folder + currentFile);

				rolesetLists.add(parser.getRolesets());
			}
		}
		return rolesetLists;
	}

	private HashMap<String, String> initializePosMappingPennToFramenet() {

		posMappingPennToFramenet = new HashMap<String, String>();

		posMappingPennToFramenet.put("VBD", "V");

		posMappingPennToFramenet.put("NNS", "N");

		posMappingPennToFramenet.put("VBN", "V");

		posMappingPennToFramenet.put("VBG", "V");

		posMappingPennToFramenet.put("JJR", "A");

		posMappingPennToFramenet.put("NNP", "N");

		posMappingPennToFramenet.put("VBP", "V");

		posMappingPennToFramenet.put("VBZ", "V");

		posMappingPennToFramenet.put("JJS", "A");

		posMappingPennToFramenet.put("VB", "V");

		posMappingPennToFramenet.put("NN", "N");

		posMappingPennToFramenet.put("JJ", "A");

		posMappingPennToFramenet.put("IN", "PREP");

		posMappingPennToFramenet.put("RB", "ADV");

		posMappingPennToFramenet.put("PRP", "PREP");

		posMappingPennToFramenet.put("RP", "ADV");

		posMappingPennToFramenet.put("MD", "V");

		posMappingPennToFramenet.put("CD", "NUM");

		posMappingPennToFramenet.put("WRB", "ADV");
		
		return posMappingPennToFramenet;

	}

}
