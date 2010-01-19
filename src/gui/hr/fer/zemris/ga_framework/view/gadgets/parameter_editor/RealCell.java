package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;




public class RealCell extends Cell {

	/* static fields */

	/* private fields */
	private Text text;
	private double oldnum;
	

	/* ctors */

	public RealCell(Composite c, int x, int y, int style) {
		super(c, style, x, y);
		initGUI();
		advinitGUI();
	}



	/* methods */

	@Override
	public Object getValue() {
		double num = 0;
		try {
			num = Double.parseDouble(text.getText());
		} catch (NumberFormatException e) {
			return null;
		}
		
		return num;
	}
	
	
	private void initGUI() {
		setLayout(new FillLayout());

		text = new Text(this, SWT.NONE);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ev) {
				if (ev.character == SWT.CR) changeOccured();
			}
		});
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				changeOccured();
			}
			@Override
			public void focusGained(FocusEvent arg0) {
				informListenersOfFocus();
			}
		});
	}
	
	private void changeOccured() {
		Double num = (Double)getValue();
		
		if (num == null) {
			text.setText(String.valueOf(oldnum));
			return;
		}
		
		oldnum = num;
		informListeners(num);
	}

	private void advinitGUI() {
		oldnum = 0;
		text.setText("0.0");
	}

	@Override
	public void setValue(Object o) {
		if (!(o instanceof Double)) return;
		text.setText(String.valueOf(o));
	}
	
	
	
}














