package semeval.mapping;

public class PBToVN {
	
	String lemma;
	
	String pb_roleset;
	
	String vn_class;

	public PBToVN(String lemma, String pb_roleset, String vn_class) {

		this.lemma = lemma;
		this.pb_roleset = pb_roleset;
		this.vn_class = vn_class;
	}

	public String getLemma() {
		return lemma;
	}

	public String getPb_roleset() {
		return pb_roleset;
	}

	public String getVn_class() {
		return vn_class;
	}
	
	

}
