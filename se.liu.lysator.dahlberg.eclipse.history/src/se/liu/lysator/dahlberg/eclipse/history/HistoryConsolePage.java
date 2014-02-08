package se.liu.lysator.dahlberg.eclipse.history;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * Adds a trivial history to the Eclipse ProcessConsole view.
 * 
 * Enables the use of key up/down to select previously entered commands.
 * 
 * @author Anders Dahlberg
 * @version 1.0
 *
 */
public class HistoryConsolePage implements IConsolePageParticipant, 
		VerifyKeyListener, MouseListener {

	private StyledText tc;
	private int caretPosition = 0;
	private int index = 0;
	// TODO store/re-use history per launch configuration?
	private List<String> history = new ArrayList<String>();
	
	public HistoryConsolePage() {
		// add empty first line to make history easier to use
		history.add("");
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class cls) {
		return null;
	}

	@Override
	public void mouseDoubleClick(MouseEvent me) {
		caretPosition = tc.getCaretOffset();
	}

	@Override
	public void mouseDown(MouseEvent me) {		
		caretPosition = tc.getCaretOffset();
	}

	@Override
	public void mouseUp(MouseEvent me) {
		caretPosition = tc.getCaretOffset();
	}
	@Override
	public void activated() {
		caretPosition = tc.getCaretOffset();
	}

	@Override
	public void deactivated() {}

	@Override
	public void dispose() {
		tc.removeVerifyKeyListener(this);
		tc.removeMouseListener(this);
	}

	@Override
	public void init(IPageBookViewPage viewPage, IConsole console) {
		Control control = viewPage.getControl();
		
		if (control instanceof StyledText) {
			this.tc = (StyledText) control;
			tc.addVerifyKeyListener(this);
			tc.addMouseListener(this);
			caretPosition = tc.getCaretOffset();
		} else {
			String exception = "This plugin requires that the ProcessConsole "
					+ "is using a StyledText control";
			throw new IllegalStateException(exception);
		}
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		int key = event.keyCode;
		// TODO replace with key code constants
		if (key == 0x1000001) {
			// key up
			int size = history.size();
			index = ++index < size ? index : index - 1;
			String line = getHistory(index);
			addText(line);
		} else if (key == 0x1000002) {
			// key down
			index = --index < 0 ? 0 : index ;
			String line = getHistory(index);
			addText(line);
		} else if (key == 0x10000) {
			// Ignore, strange key? Modifier? Move?
		} else if (key == 0xd) {
			// key enter => save to history
			String text = getCurrentLineText();
			addHistory(text);
			caretPosition = tc.getCaretOffset();
			index = 0;
		} else {
			caretPosition = tc.getCaretOffset();
		}
	}

	private void addHistory(String text) {
		if (text.length() < 2) {
			// don't add trivial commands to history
			return;
		}
		if (history.contains(text))  {
			// don't add duplicates 
			// but move it to top of list
			history.remove(text);
			history.add(1, text);
			return;
		}
		// push to after inital first line
		history.add(1, text);
	}

	private String getHistory(int index) {
		String line = "";
		if (index < history.size()) {
			line = history.get(index);
		}
		return line;
	}

	private void addText(final String text) {
		// Not sure if this is necessary
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				int line = tc.getLineAtOffset(caretPosition);
				int begin = tc.getOffsetAtLine(line);
				int nextLine = line + 1;
				int end = nextLine < tc.getLineCount() ? tc.getOffsetAtLine(nextLine) : tc.getCharCount();
				tc.setSelection(begin, end);
				tc.insert(text);
				caretPosition = tc.getCaretOffset();
			}
		});
	}
	
	private String getCurrentLineText() {
		int line = tc.getLineAtOffset(caretPosition);
		int begin = tc.getOffsetAtLine(line);
		int nextLine = line + 1;
		int end = nextLine < tc.getLineCount() ? tc.getOffsetAtLine(nextLine) : tc.getCharCount();
		// check for empty or single character lines 
		String text = end > begin + 1 ? tc.getText(begin, end - 1) : "";
		return text;
	}
}
