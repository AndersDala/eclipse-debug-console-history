package se.liu.lysator.dahlberg.eclipse.history;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;

/**
 * Adds a trivial history to the Eclipse ProcessConsole view.
 * 
 * Enables the use of key up/down to select previously entered commands.
 * 
 * @author Anders Dahlberg
 * @version 1.0
 *
 */
public class HistoryConsolePage extends StyledTextConsolePage {

	private int index = 0;
	// TODO store/re-use history per launch configuration?
	private List<String> history = new ArrayList<String>();
	
	public HistoryConsolePage() {
		// add empty first line to make history easier to use
		history.add("");
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
			String text = util.getCurrentLineText();
			addHistory(text);
			util.setCaretPosition(tc.getCaretOffset());
			index = 0;
		} else {
			util.setCaretPosition(tc.getCaretOffset());
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
				int begin = util.getBegin();
				int end = util.getEnd();
				assert begin <= end;
				tc.setSelection(begin, end);
				tc.insert(text);
				util.setCaretPosition(tc.getCaretOffset());
			}
		});
	}
}
