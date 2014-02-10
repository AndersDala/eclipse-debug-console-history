package se.liu.lysator.dahlberg.eclipse.history;

import org.eclipse.swt.custom.StyledText;

/**
 * 
 * Simple utility class to keep track of last line in a StyledText widget
 * 
 * @author Anders Dahlberg
 * @version 1.0
 * 
 */
public class TextUtil {
	
	private StyledText tc;
	private int caretPosition;

	public TextUtil(StyledText tc) {
		this.tc = tc;
		this.caretPosition = tc.getCaretOffset();
	}
	
	public void setCaretPosition(int position)  {
		caretPosition = position;
	}
	
	public String getCurrentLineText() {
		int begin = getBegin();
		int end = getEnd();
		assert begin <= end;
		// check for empty or single character lines 
		String text = end > begin + 1 ? tc.getText(begin, end - 1) : "";
		return text;
	}

	public int getCurrentLine() {
		caretPosition = caretPosition > tc.getCharCount() ? tc.getCharCount() : caretPosition;		
		int line = tc.getLineAtOffset(caretPosition);
		return line;
	}

	public int getBegin() {
		int line = getCurrentLine();
		int begin = tc.getOffsetAtLine(line);
		return begin;
	}

	public int getEnd() {
		int line = getCurrentLine();
		int nextLine = line + 1;
		int end = nextLine < tc.getLineCount() ? tc.getOffsetAtLine(nextLine) : tc.getCharCount();
		return end;
	}

	public int getCaretPosition() {
		return caretPosition;
	}
}
