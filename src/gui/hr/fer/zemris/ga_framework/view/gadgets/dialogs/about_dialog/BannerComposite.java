package hr.fer.zemris.ga_framework.view.gadgets.dialogs.about_dialog;

import java.util.Random;
import java.util.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class BannerComposite extends Canvas {
	
	/* static fields */
	private static final int LIMIT = 300;
	private static final int SIERLIMIT = 200;
	private static final int[] XTOPS = new int[] {0, 200, 100};
	private static final int[] YTOPS = new int[] {0, 0, 172};
	
	/* private fields */
	private Color colrule110, colsierp;
	private boolean[] field;
	private int xoff, yoff, row;
	private boolean[] sierp;
	private int xscurr, yscurr;
	private int sierxoff, sieryoff;
	private int siercount;
	
	/* ctors */

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public BannerComposite(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		
		field = new boolean[LIMIT * LIMIT];
		sierp = new boolean[SIERLIMIT * SIERLIMIT];
		xscurr = (int) (Math.random() * SIERLIMIT);
		yscurr = (int) (Math.random() * SIERLIMIT);
		colrule110 = new Color(this.getDisplay(), 180, 200, 250);
		colsierp = new Color(this.getDisplay(), 180, 230, 190);
		
		initGUI();
		advinitGUI();
	}
	
	/* methods */
	
	private void advinitGUI() {
		// set repaint method
		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setAntialias(SWT.ON);
				
				e.gc.setForeground(colrule110);
				for (int x = 0; x < LIMIT; x++) {
					for (int y = 0; y < LIMIT; y++) {
						if (field[y * LIMIT + x]) e.gc.drawPoint(xoff + x, yoff + y);
					}
				}
				
				e.gc.setForeground(colsierp);
				for (int x = 0; x < SIERLIMIT; x++) {
					for (int y = 0; y < SIERLIMIT; y++) {
						if (sierp[y * SIERLIMIT + x]) e.gc.drawPoint(sierxoff + x, sieryoff + y);
					}
				}
			}
		});
		
		// create timer
		final Timer t = new Timer(false);
		getDisplay().timerExec(40, new Runnable() {
			public void run() {
				if (BannerComposite.this.isDisposed()) return;
				
				// model, model, model
				handle110Rule();
				handleSierpinski();
				
				// invoke repaint
				redraw();
				
				BannerComposite.this.getDisplay().timerExec(60, this);
			}
		});
		
		// ensure time termination
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				t.cancel();
				colrule110.dispose();
				colsierp.dispose();
			}
		});
	}
	
	protected void handleSierpinski() {
		Random rand = new Random();
		int w = getSize().x;
		int h = getSize().y;
		
		if (w <= 0 || h <= 0) return;
		
		// restart sometimes
		if (siercount > 15000 || rand.nextDouble() < 0.002) {
			sierp = new boolean[SIERLIMIT * SIERLIMIT];
			sierxoff = rand.nextInt(w) - SIERLIMIT / 2;
			sieryoff = rand.nextInt(h) - SIERLIMIT / 2;
			siercount = 0;
		}
		
		// add points
		for (int i = 0; i < 100; i++) {
			int sel = rand.nextInt(3);
			xscurr = (xscurr + XTOPS[sel]) / 2;
			yscurr = (yscurr + YTOPS[sel]) / 2;
			sierp[yscurr * SIERLIMIT + xscurr] = true;
			siercount++;
		}
	}

	protected void handle110Rule() {
		Random rand = new Random();
		int w = getSize().x;
		int h = getSize().y;
		
		if (w <= 0 || h <= 0) return;
		
		// restart sometimes
		if (xoff == 0 || (yoff + row) > h || row >= (LIMIT - 1) || rand.nextDouble() < 0.002) {
			xoff = rand.nextInt(w);
			if (xoff > (w - LIMIT)) xoff = w - LIMIT;
			yoff = rand.nextInt(h);
			row = 0;
			field = new boolean[LIMIT * LIMIT];
			field[LIMIT - 1] = true;
		}
		
		// go to next state
		int prev = row++;
		for (int x = 1; x < (LIMIT - 1); x++) {
			boolean p1 = field[prev * LIMIT + x - 1];
			boolean p2 = field[prev * LIMIT + x];
			boolean p3 = field[prev * LIMIT + x + 1];
			if (p1 && p2 && p3) field[row * LIMIT + x] = false;
			else if (p1 && !p2 && !p3) field[row * LIMIT + x] = false;
			else if (!p1 && !p2 && !p3) field[row * LIMIT + x] = false;
			else field[row * LIMIT + x] = true;
		}
	}

	private void initGUI() {
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}














