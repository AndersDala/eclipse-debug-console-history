package se.liu.lysator.dahlberg.eclipse.history;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.IControlContentAdapter2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * Based on public EPL work from below.
 * 
 * @see http://rcp-company-uibindings.googlecode.com/svn/trunk/com.rcpcompany.uibindings/src/com/rcpcompany/uibindings/internal/uiAttributeFactories/contentAdapters/StyledTextContentAdapter.java
 *
 * @author Anders Dahlberg
 * @version 1.0
 * 
 */
public class StyledTextContentAdapter implements IControlContentAdapter, IControlContentAdapter2  {

	/*
	 * Set to <code>true</code> if we should compute the text vertical bounds rather than just use the field size.
	 * Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=164748 The corresponding SWT bug is
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=44072
	 */
	private static final boolean COMPUTE_TEXT_USING_CLIENTAREA = !"carbon".equals(SWT.getPlatform()); //$NON-NLS-1$
	private TextUtil util;
	
	public StyledTextContentAdapter(TextUtil util) {
		this.util = util;
	}

	@Override
	public String getControlContents(Control control) {
		return util.getCurrentLineText();
	}

	@Override
	public int getCursorPosition(Control control) {
		int position = util.getCaretPosition();
		return position;
	}

	// TODO this method looks broken... Not used, so don't care?
	@Override
	public Rectangle getInsertionBounds(Control control) {
		// This doesn't take horizontal scrolling into affect.
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=204599
		final StyledText tc = (StyledText) control;
		final int position = tc.getSelection().y;
		final String contents = tc.getText();
		final GC gc = new GC(tc);
		gc.setFont(tc.getFont());
		final Point extent = gc.textExtent(contents.substring(0, Math.min(position, contents.length())));
		gc.dispose();
		if (COMPUTE_TEXT_USING_CLIENTAREA) {
			return new Rectangle(tc.getClientArea().x + extent.x, tc.getClientArea().y, 1,
					tc.getClientArea().height);
		}
		return new Rectangle(extent.x, 0, 1, tc.getSize().y);
	}

	// TODO this method looks broken... Not used, so don't care?
	@Override
	public void insertControlContents(Control control, String text, int cursorPosition) {
		final StyledText tc = (StyledText) control;
		final String contents = tc.getText();
		final Point selection = tc.getSelection();
		final StringBuffer sb = new StringBuffer();
		sb.append(contents.substring(0, selection.x));
		sb.append(text);
		if (selection.y < contents.length()) {
			sb.append(contents.substring(selection.y, contents.length()));
		}
		tc.setText(sb.toString());
		selection.x = selection.x + cursorPosition;
		selection.y = selection.x;
		tc.setSelection(selection);

	}

	@Override
	public void setControlContents(Control control, String text, int cursorPosition) {
		// Ignore cursor position, use text util to find beginning and end of current line
		StyledText tc = (StyledText) control;
		int begin = util.getBegin();
		int end = util.getEnd();
		tc.setSelection(begin, end);
		// Add trailing space to make life easier for user (e.g. "set print pretty <caret>"
		tc.insert(text + " ");
		util.setCaretPosition(tc.getCaretOffset());
	}

	@Override
	public void setCursorPosition(Control control, int index) {
		((StyledText) control).setSelection(new Point(index, index));
	}

	@Override
	public Point getSelection(Control control) {
		int begin = util.getBegin();
		int end = util.getEnd();
		
		Point selection = new Point(begin, end);
		
		return selection;
	}

	@Override
	public void setSelection(Control control, Point range) {
		((StyledText) control).setSelection(range);
	}
}
