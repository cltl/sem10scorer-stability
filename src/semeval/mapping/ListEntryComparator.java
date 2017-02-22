package semeval.mapping;

import java.util.Comparator;

public class ListEntryComparator implements Comparator<ListEntry>{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ListEntry firstEntry, ListEntry secondEntry) {

		String firstPos = firstEntry.getPos();
		
		String secondPos = secondEntry.getPos();
		
		String firstFrame = firstEntry.getFrame();
		
		String secondFrame = secondEntry.getFrame();
		
		String firstLemma = firstEntry.getLemma();
		
		String secondLemma = secondEntry.getLemma();
		
		int comparePos = firstPos.compareToIgnoreCase(secondPos);
		
		int compareFrames = firstFrame.compareToIgnoreCase(secondFrame);
		
		int compareLemma = firstLemma.compareToIgnoreCase(secondLemma);

		if (comparePos != 0) {
			return comparePos;
		} else {
			
			if (compareFrames != 0) {
				return compareFrames;
			} else {
				return compareLemma;
			}
		}
		
		
		
	}
	
	

}
