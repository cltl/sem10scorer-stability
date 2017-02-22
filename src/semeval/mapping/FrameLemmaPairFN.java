package semeval.mapping;


public class FrameLemmaPairFN {

	Lemma lemma;
	Frame frame;
	String lemmaId;
	int id;

	public FrameLemmaPairFN(int id, Frame frame, String lemmaId) {

		this.id = id;
		this.frame = frame;
		this.lemmaId = lemmaId;
	}

	public boolean lemmaExists() {

		if (null == this.lemma) { return false; }
		else { return true; }
	}

	public void setLemma(Lemma lemma) {

		this.lemma = lemma;
	}

	public Lemma getLemma() {

		return this.lemma;
	}

	public Frame getFrame() {

		return this.frame;
	}

	public int getId() {

		return this.id;
	}

	public String getLemmaId() {

		return this.lemmaId;
	}

	public String toString() {
		
		return frame.getName() + " - " + getLemma().getName();
	}


}
