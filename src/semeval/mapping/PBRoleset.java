package semeval.mapping;

/**
 * Represents the lemma and the name of a roleset
 * @author Fabian Shirokov
 *
 */
public class PBRoleset {
	
	private String id;
	
	private String name;
	
	private String lemma;

	/**
	 * @param id
	 * @param name
	 * @param lemma
	 */
	public PBRoleset(String id, String name, String lemma) {
		super();
		this.id = id;
		this.name = name;
		this.lemma = lemma;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * @param lemma the lemma to set
	 */
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	

}
