package semeval.mapping;

/**
 * Represents a nombank roleset. Contains an Id, and optionally 'source' and
 * 'vnclass'.
 * 
 * @author Fabian Shirokov
 * 
 */
public class NBRoleset {

	private String id;

	private String name;

	private String vnclass;

	private String source;

	private String lemma;

	/**
	 * Default constructor that takes the value of the 'id' attribute as an
	 * argument.
	 * 
	 * @param id
	 */
	public NBRoleset(String lemma, String id) {
		super();
		this.id = id;
		this.lemma = lemma;
	}

	/**
	 * Returns the value of the 'id' attribute, e. g. 'accessory.01'.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the 'id' attribute, e. g. 'accessory.01'.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the value of the 'name' attribute, e. g. 'nomlike'.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the 'name' attribute, e. g. 'nomlike'.
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the value of 'vnclass', e. g. '22.1-2'.
	 * 
	 * @return the vnclass
	 */
	public String getVnclass() {
		return vnclass;
	}

	/**
	 * Sets the value of 'vnclass', e. g. '22.1-2'.
	 * 
	 * @param vnclass
	 *            the vnclass to set
	 */
	public void setVnclass(String vnclass) {
		this.vnclass = vnclass;
	}

	/**
	 * Returns the value of the 'source', e. g. 'verb-add.02'.
	 * 
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the value of the 'source', e. g. 'verb-add.02'.
	 * 
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Returns the lemma this roleset belongs to.
	 * 
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * Sets the lemma this roleset belongs to (out of the 'lemma' attribute in
	 * the 'predicate' element).
	 * 
	 * @param lemma
	 *            the lemma to set
	 */
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

}
