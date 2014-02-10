package se.liu.lysator.dahlberg.eclipse.history;

import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * 
 * Super class introduced to simplify implementation of History and GdBCompletion consoles.
 * 
 * Note: currently only the gdb console is supported.
 * 
 * @author Anders Dahlberg
 * @version 1.0
 * 
 */
public abstract class StyledTextConsolePage implements IConsolePageParticipant,
		VerifyKeyListener, MouseListener, LineStyleListener {

	protected StyledText tc;
	protected TextUtil util;

	public StyledTextConsolePage() {
		super();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class cls) {
		return null;
	}

	@Override
	public void mouseDoubleClick(MouseEvent me) {
		util.setCaretPosition(tc.getCaretOffset());
	}

	@Override
	public void mouseDown(MouseEvent me) {
		util.setCaretPosition(tc.getCaretOffset());
	}

	@Override
	public void mouseUp(MouseEvent me) {
		util.setCaretPosition(tc.getCaretOffset());
	}

	@Override
	public void activated() {
		util.setCaretPosition(tc.getCaretOffset());
	}

	@Override
	public void deactivated() {
	}

	@Override
	public void dispose() {
		if (!tc.isDisposed()) {
			tc.removeVerifyKeyListener(this);
			tc.removeMouseListener(this);
		}
	}

	@Override
	public void init(IPageBookViewPage viewPage, IConsole console) {
		Control control = viewPage.getControl();
		String consoleName = console.getName();

		if (control instanceof StyledText) {
			this.tc = (StyledText) control;
			util = new TextUtil(tc);
			
			boolean gdbConsole = consoleName.endsWith(" gdb");
			
			// For now, only support the gdb console
			if (gdbConsole) {
				tc.addVerifyKeyListener(this);
				tc.addMouseListener(this);
				tc.addLineStyleListener(this);
			}
		} else {
			String exception = "This plugin requires that the ProcessConsole "
					+ "is using a StyledText control";
			throw new IllegalStateException(exception);
		}
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		util.setCaretPosition(tc.getCaretOffset());
	}

	@Override
	public void lineGetStyle(LineStyleEvent e) {
		// Set the line number
		int line = tc.getLineAtOffset(e.lineOffset);
		int lastLine = tc.getLineCount() - 1;

		e.bulletIndex = line;

		String prompt = "gdb>";

		// Set the style, 12 pixles wide for each digit
		StyleRange style = new StyleRange();
		style.metrics = new GlyphMetrics(0, 0, prompt.length() * 12);

		// Create and set the bullet
		e.bullet = new Bullet(ST.BULLET_TEXT, style);
		if (line == lastLine) {
			e.bullet.text = prompt;
		} else {
			e.bullet.text = "";
		}
	}

}