package semeval.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

import salsa.util.MyFileWriter;

/**
 * Test class for LUChecker.
 * 
 * @author Fabian Shirokov
 * 
 */
public class LUCheckerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LUCheckerTest test = new LUCheckerTest();

		test.run();
	}

	private void run() {

		try {

			String firstCorpus = "files\\hundMarkus1.xml";

			String secondCorpus = "files\\hundMarkus2.xml";

			String thirdCorpus = "files\\tigerchen_merged.xml";

			String fourthCorpus = "files\\tiger.russell.090605.xml";

			String[] files = new String[4];

			files[0] = firstCorpus;

			files[1] = secondCorpus;

			files[2] = thirdCorpus;

			files[3] = fourthCorpus;

			LUChecker checker = new LUChecker(files, true);

			ArrayList<FrameLemmaPairFN> allPairs = checker.getAllFNPairs();

			MyFileWriter fileWriter = new MyFileWriter("output.html");

			StringBuilder buffer = new StringBuilder();

			String newline = System.getProperty("line.separator");

			buffer.append("<html><head></head><body>" + newline);

			buffer.append("<table border=\"1\">" + newline);

			buffer.append("<tr><th>Frame</th><th>Lemma</th><th>POS</th>"
					+ "<th>FN1.2</th><th>FN->SL->PB</th><th>FN->PB</th>"
					+ "<th>NB: Checked</th><th>NB: Unchecked</th></tr>");
			
			ArrayList<ListEntry> allEntries = new ArrayList<ListEntry>();

			for (FrameLemmaPairFN pairFN : allPairs) {

				ListEntry entry = new ListEntry(pairFN.getFrame().getName(),
						pairFN.getLemma().getName(), checker.getPosOf(pairFN),
						checker.isInFN12(pairFN));

				// FrameNet -> VerbNet (semlink) -> PropBank
				if (null != checker.getVNOf(pairFN)) {

					ArrayList<FrameLemmaPairVN> pairsVN = checker
							.getVNOf(pairFN);

					for (FrameLemmaPairVN pairVN : pairsVN) {

						ArrayList<FrameLemmaPairPB> pairsPB = checker
								.getPBOf(pairVN);

						if (null != pairsPB) {
							for (FrameLemmaPairPB currentPairPB : pairsPB) {

								entry.addRolesetOverSemlink(currentPairPB);
							}
						}
					}
				}

				// FN -> PB directly
				FrameLemmaPairPB pbdireclyoffn = checker
						.getPBDirectlyOf(pairFN);
				if (null != pbdireclyoffn) {
					entry.addRolesetDirectlyFramenetToPropbank(pbdireclyoffn);
				}

				// FN->NB->PB
				ArrayList<FrameLemmaPairPB> pairsPBnom = checker
						.getPBOverNombankDirectly(pairFN);

				HashSet<String> seenRolesets = new HashSet<String>();

				HashSet<String> notCorrespondingRolesets = new HashSet<String>();

				for (FrameLemmaPairPB pairPBnom : pairsPBnom) {

					if (null != pairPBnom.getPb_roleset()) {

						if (checker.correspondsToFrame(pairPBnom, pairFN
								.getFrame().getName())) {

							if (!seenRolesets.contains(getRoleset(pairPBnom))) {

								entry.addRolesetChecked(pairPBnom);

								seenRolesets.add(getRoleset(pairPBnom));

							}
						} else {
							notCorrespondingRolesets.add(pairPBnom
									.getPb_roleset());
						}

					}
				}

				ArrayList<FrameLemmaPairPB> pairsPBnomsem = checker
						.getPBOverNombankAndVerbnet(pairFN);

				for (FrameLemmaPairPB pairPBnomsem : pairsPBnomsem) {

					if (!seenRolesets.contains(getRoleset(pairPBnomsem))) {

						if (checker.correspondsToFrame(pairPBnomsem, pairFN
								.getFrame().getName())) {

							entry.addRolesetChecked(pairPBnomsem);
						} else {
							notCorrespondingRolesets.add(pairPBnomsem
									.getPb_roleset());
						}
					}
				}

				// FN->NB->SL->PB
				for (String roleset : notCorrespondingRolesets) {

					entry.addRolesetUnchecked(new FrameLemmaPairPB(roleset,
							pairFN.getLemma().getName()));
				}

				/*
				 * if (null != checker.getVNOf(pairFN)) { HashSet<String>
				 * seenRolesets = new HashSet<String>();
				 * 
				 * for (FrameLemmaPairVN pairVN : checker.getVNOf(pairFN)) {
				 * 
				 * if (null != checker.getPBOf(pairVN)) {
				 * 
				 * ArrayList<FrameLemmaPairPB> pairsPB = checker
				 * .getPBOf(pairVN);
				 * 
				 * for (FrameLemmaPairPB currentPairPB : pairsPB) {
				 * 
				 * String roleset = currentPairPB .getPb_roleset();
				 * 
				 * if (!seenRolesets.contains(roleset)) {
				 * 
				 * buffer.append(currentPairPB .getPb_roleset() + "<br />");
				 * 
				 * seenRolesets.add(roleset); } } } } }
				 */
				allEntries.add(entry);
			}
			Collections.sort(allEntries, new ListEntryComparator());
			for (ListEntry entry : allEntries) {
				
				buffer.append(entry.toString() + newline);
			}
			buffer.append("</table>" + newline);
			buffer.append("</body></html>");

			fileWriter.writeToFile(buffer.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRoleset(FrameLemmaPairPB pairPB) {

		String roleset = pairPB.getPb_roleset();

		if (null != roleset) {
			if (roleset.startsWith("verb-")) {
				roleset = roleset.substring(5);
			}
		}

		return roleset;
	}

	private String getRoleset(String roleset) {

		if (null != roleset) {
			if (roleset.startsWith("verb-")) {
				roleset = roleset.substring(5);
			}
		}

		return roleset;
	}
}
