package semeval.mapping;

import java.util.ArrayList;

/**
 * This represents one lexical unit entry in a HTML list.
 * 
 * @author Fabian Shirokov
 * 
 */
public class ListEntry  {

	private String frame;

	private String lemma;

	private String pos;

	private boolean fn12;

	private ArrayList<FrameLemmaPairPB> rolesetsOverSemlink;

	private ArrayList<FrameLemmaPairPB> rolesetsDirectlyFramenetToPropbank;

	private ArrayList<FrameLemmaPairPB> rolesetsChecked;

	private ArrayList<FrameLemmaPairPB> rolesetsUnchecked;

	/**
	 * @param frame
	 * @param lemma
	 * @param pos
	 * @param fn12
	 */
	public ListEntry(String frame, String lemma, String pos, boolean fn12) {
		super();
		this.frame = frame;
		this.lemma = lemma;
		this.pos = pos;
		this.fn12 = fn12;
		this.rolesetsOverSemlink = new ArrayList<FrameLemmaPairPB>();
		this.rolesetsDirectlyFramenetToPropbank = new ArrayList<FrameLemmaPairPB>();
		this.rolesetsChecked = new ArrayList<FrameLemmaPairPB>();
		this.rolesetsUnchecked = new ArrayList<FrameLemmaPairPB>();
	}

	/**
	 * @return the frame
	 */
	public String getFrame() {
		return frame;
	}

	/**
	 * @param frame
	 *            the frame to set
	 */
	public void setFrame(String frame) {
		this.frame = frame;
	}

	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * @param lemma
	 *            the lemma to set
	 */
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	/**
	 * @return the pos
	 */
	public String getPos() {
		return pos;
	}

	/**
	 * @param pos
	 *            the pos to set
	 */
	public void setPos(String pos) {
		this.pos = pos;
	}

	/**
	 * @return the fn12
	 */
	public boolean getFn12() {
		return fn12;
	}

	/**
	 * @param fn12
	 *            the fn12 to set
	 */
	public void setFn12(boolean fn12) {
		this.fn12 = fn12;
	}

	/**
	 * @return the rolesetsOverSemlink
	 */
	public ArrayList<FrameLemmaPairPB> getRolesetsOverSemlink() {
		return rolesetsOverSemlink;
	}

	/**
	 * @param rolesetsOverSemlink
	 *            the rolesetsOverSemlink to set
	 */
	public void addRolesetOverSemlink(FrameLemmaPairPB newRolesetOverSemlink) {

		boolean alreadyExists = false;

		String newName = getRolesetName(newRolesetOverSemlink);

		for (FrameLemmaPairPB oldRoleset : this.rolesetsOverSemlink) {

			String oldName = getRolesetName(oldRoleset);

			if (oldName.equals(newName)) {
				alreadyExists = true;
				return;
			}
		}

		if (!alreadyExists) {
			this.rolesetsOverSemlink.add(newRolesetOverSemlink);
		}

	}

	/**
	 * @return the rolesetsDirectlyFramenetToPropbank
	 */
	public ArrayList<FrameLemmaPairPB> getRolesetsDirectlyFramenetToPropbank() {
		return rolesetsDirectlyFramenetToPropbank;
	}

	/**
	 * @param rolesetsDirectlyFramenetToPropbank
	 *            the rolesetsDirectlyFramenetToPropbank to set
	 */
	public void addRolesetDirectlyFramenetToPropbank(
			FrameLemmaPairPB newRolesetDirectlyFramenetToPropbank) {

		boolean alreadyExists = false;

		String newName = getRolesetName(newRolesetDirectlyFramenetToPropbank);

		for (FrameLemmaPairPB oldRoleset : this.rolesetsDirectlyFramenetToPropbank) {

			String oldName = getRolesetName(oldRoleset);

			if (oldName.equals(newName)) {
				alreadyExists = true;
				return;
			}
		}

		if (!alreadyExists) {
			this.rolesetsDirectlyFramenetToPropbank
					.add(newRolesetDirectlyFramenetToPropbank);
		}

	}

	/**
	 * @return the rolesetsChecked
	 */
	public ArrayList<FrameLemmaPairPB> getRolesetsChecked() {
		return rolesetsChecked;
	}

	/**
	 * @param rolesetsChecked
	 *            the rolesetsChecked to set
	 */
	public void addRolesetChecked(FrameLemmaPairPB newRolesetChecked) {

		boolean alreadyExists = false;

		String newName = getRolesetName(newRolesetChecked);

		for (FrameLemmaPairPB oldRoleset : this.rolesetsChecked) {

			String oldName = getRolesetName(oldRoleset);

			if (oldName.equals(newName)) {
				alreadyExists = true;
				return;
			}
		}

		if (!alreadyExists) {
			this.rolesetsChecked.add(newRolesetChecked);
		}

	}

	/**
	 * @return the rolesetsUnchecked
	 */
	public ArrayList<FrameLemmaPairPB> getRolesetsUnchecked() {
		return rolesetsUnchecked;
	}

	/**
	 * @param rolesetsUnchecked
	 *            the rolesetsUnchecked to set
	 */
	public void addRolesetUnchecked(FrameLemmaPairPB newRolesetUnchecked) {

		boolean alreadyExists = false;

		String newName = getRolesetName(newRolesetUnchecked);

		for (FrameLemmaPairPB oldRoleset : this.rolesetsUnchecked) {

			String oldName = getRolesetName(oldRoleset);

			if (oldName.equals(newName)) {
				alreadyExists = true;
				return;
			}
		}

		if (!alreadyExists) {
			this.rolesetsUnchecked.add(newRolesetUnchecked);
		}

	}

	private String getRolesetName(FrameLemmaPairPB pairPB) {

		String name = pairPB.getPb_roleset();
		

		if (name.startsWith("verb-")) {
			name = name.substring(5);
		}

		return name;
	}

	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("<tr>");

		buffer.append("<td>");

		buffer.append(this.getFrame());

		buffer.append("</td>");

		buffer.append("<td>");

		buffer.append(this.getLemma());

		buffer.append("</td>");

		buffer.append("<td>");

		buffer.append(this.getPos());

		buffer.append("</td>");

		buffer.append("<td>");

		buffer.append(this.getFn12());

		buffer.append("</td>");

		buffer.append("<td>");

		if (!getPos().equals("N")) {
			for (FrameLemmaPairPB pairPB : getRolesetsOverSemlink()) {
				buffer.append(getRolesetName(pairPB) + "<br />");
			}
		}

		buffer.append("</td>");

		buffer.append("<td>");
		if (getPos().equals("V")) {
			for (FrameLemmaPairPB pairPB : getRolesetsDirectlyFramenetToPropbank()) {
				buffer.append(getRolesetName(pairPB) + "<br />");
			}
		}
		buffer.append("</td>");

		buffer.append("<td>");
		if (getPos().equals("N")) {
			for (FrameLemmaPairPB pairPB : getRolesetsChecked()) {
				buffer.append(getRolesetName(pairPB) + "<br />");
			}
		}
		buffer.append("</td>");

		buffer.append("<td>");
		if (getPos().equals("N")) {
			for (FrameLemmaPairPB pairPB : getRolesetsUnchecked()) {
				buffer.append(getRolesetName(pairPB) + "<br />");
			}
		}
		buffer.append("</td>");

		buffer.append("</tr>");

		return buffer.toString();
	}
	

}
