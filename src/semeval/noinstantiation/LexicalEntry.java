package semeval.noinstantiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LexicalEntry {

	private String name;

	private String frame;

	private String pos;

	private String definition;

	private ArrayList<FERealization> feRealizations;

	public LexicalEntry(String frame, String name, String pos) {
		super();
		this.name = name;
		this.frame = frame;
		this.pos = pos;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public ArrayList<FERealization> getFeRealizations() {
		return feRealizations;
	}

	public void setFeRealizations(ArrayList<FERealization> feRealizations) {
		this.feRealizations = feRealizations;
	}

	public String getName() {
		return name;
	}

	public String getFrame() {
		return frame;
	}

	public String getPos() {
		return pos;
	}

	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("pos: " + this.pos + ", frame: " + frame + ", name: "
				+ name);

		return buffer.toString();
	}

	/*
	public ArrayList<FERealization> getOrderedFeRealizations() {

		ArrayList<FERealization> unorderedRealizations = feRealizations;

		ArrayList<FERealization> orderedRealizations = new ArrayList<FERealization>();

		for (FERealization ureal : unorderedRealizations) {

			// falls es in orderedRelations bereits eine FERealization mit dem
			// FE gibt
			boolean feAlreadyExists = false;
			// dummy
			FERealization oreal = new FERealization(0);

			for (FERealization currentOreal : orderedRealizations) {

				ArrayList<ValenceUnit> valenceUnits = currentOreal
						.getValenceUnits();

				for (ValenceUnit valenceUnit : valenceUnits) {
				
					String feOfOreal = valenceUnit.getFe();

					if (feOfOreal.equals(ureal.getValenceUnit().getFe())) {

						feAlreadyExists = true;
						oreal = currentOreal;
					}

				}

			}

			if (feAlreadyExists) {
				FERealization newReal = new FERealization(ureal.getTotal()
						+ oreal.getTotal());
				orderedRealizations.add(newReal);
			} else {
				orderedRealizations.add(ureal);
			}

		}

		return orderedRealizations;
	}*/

}
