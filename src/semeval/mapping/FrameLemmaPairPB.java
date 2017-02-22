package semeval.mapping;

public class FrameLemmaPairPB {

	String pb_roleset;

	String lemma;

	public FrameLemmaPairPB(String pb_roleset, String lemma) {
		super();
		this.pb_roleset = pb_roleset;
		this.lemma = lemma;
	}

	public String getPb_roleset() {
		return pb_roleset;
	}

	public String getLemma() {
		return lemma;
	}

}
