package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;


public abstract class Cell extends Composite {

	
	/* static fields */

	
	/* private fields */
	private List<ICellListener> listeners;
	protected int xcell, ycell;

	
	/* ctors */
	
	public Cell(Composite c, int style, int x, int y) {
		super(c, style);
		listeners = new ArrayList<ICellListener>();
		xcell = x;
		ycell = y;
	}

	
	/* methods */
	
	@Override
	protected void checkSubclass() {
		// disable subclass prevention
	}
	
	public void addCellListener(ICellListener l) {
		listeners.add(l);
	}
	
	public void removeCellListener(ICellListener l) {
		listeners.remove(l);
	}
	
	protected void informListeners(Object nval) {
		for (ICellListener l : listeners) {
			l.onCellValueChange(xcell, ycell, nval);
		}
	}
	
	protected void informListenersOfFocus() {
		for (ICellListener l : listeners) {
			l.onCellFocus(xcell, ycell);
		}
	}
	
	public int getYPos() {
		return ycell;
	}
	
	public abstract Object getValue();
	
	/**
	 * Sets the value to cell. If the value
	 * is inappropriate, nothing is done.
	 * 
	 * @param o
	 */
	public abstract void setValue(Object o);

}














