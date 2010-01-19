package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;




public class StringCell extends Cell {

	/* static fields */

	/* private fields */
	private Text text;
	

	/* ctors */

	public StringCell(Composite c, int x, int y, int style) {
		super(c, style, x, y);
		initGUI();
		advinitGUI();
	}



	/* methods */

	@Override
	public Object getValue() {
		return text.getText();
	}
	
	
	private void initGUI() {
		setLayout(new FillLayout());
		addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent ev) {
				if (ev.character == SWT.CR) changeOccured();
			}
		});
		addFocusListener(new FocusAdapter() {
			public void focusLost(final FocusEvent arg0) {
				changeOccured();
			}
		});

		text = new Text(this, SWT.NONE);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				informListenersOfFocus();
			}
		});
	}
	
	private void changeOccured() {
		informListeners(getValue());
	}

	private void advinitGUI() {
		text.setText("");
	}

	@Override
	public void setValue(Object o) {
		if (!(o instanceof String)) return;
		text.setText((String)o);
	}
	
	
	
}














