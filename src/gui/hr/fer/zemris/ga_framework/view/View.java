package hr.fer.zemris.ga_framework.view;

import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.IView;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;



/**
 * Abstract base class for all views.
 * Performs operations common to all views,
 * such as registering to controller, and
 * unregistering on dispose.
 * 
 * @author Axel
 *
 */
public abstract class View extends Composite implements IView {
	
	
	
	/* protected */
	protected long id;
	protected IController ctrl;
	
	
	public View(Composite c, int style, IController controller, long elemid, Events[] evtypes) {
		super(c, style);
		
		ctrl = controller;
		id = elemid;
		
		ctrl.registerView(evtypes, this);
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				ctrl.unregisterView(View.this);
			}
		});
	}
	
	public long getId() {
		return id;
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * Returns an image used for the representation
	 * of the view. Typically, this should be a reference
	 * to a static object shared by all instances of
	 * the implementing class.
	 * 
	 * @return
	 */
	public abstract Image getImage(Display d);
	
	/**
	 * Returns the control to be placed on top
	 * of ctabitem.
	 * 
	 * @return
	 * Null if no control is provided.
	 */
	public abstract Control createTopControl(CTabFolder parent);
	
	
}














