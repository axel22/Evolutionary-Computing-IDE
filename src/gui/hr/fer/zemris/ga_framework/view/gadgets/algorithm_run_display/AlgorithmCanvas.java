package hr.fer.zemris.ga_framework.view.gadgets.algorithm_run_display;

import hr.fer.zemris.ga_framework.model.ICanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


public class AlgorithmCanvas extends Canvas implements ICanvas {
	
	/* static fields */

	/* private fields */
	private Image img;
	private boolean isImageAvailable;
	private volatile Color drawcol, fillcol;

	/* ctors */
	
	public AlgorithmCanvas(Composite c, int style) {
		super(c, style);
		
		img = new Image(this.getDisplay(), this.getSize().x + 1, this.getSize().y + 1);
		isImageAvailable = false;
		drawcol = this.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		fillcol = this.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		
		initGUI();
		advinitGUI();
	}

	/* methods */
	
	@Override
	protected void checkSubclass() {
	}
	
	public Image getImage() {
		return img;
	}
	
	private void initGUI() {
		setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void advinitGUI() {
		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent ev) {
				GC gc = ev.gc;
				gc.drawImage(img, 0, 0);
			}
		});
		
		this.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent ev) {
				Point sz = AlgorithmCanvas.this.getSize();
				setCanvasSize(sz.x, sz.y);
			}
		});
	}

	private void innerClearCanvas() {
		GC gc = new GC(img);
		ImageData data = img.getImageData();
		gc.fillRectangle(0, 0, data.width, data.height);
		gc.dispose();
	}
	
	public void clearCanvas() {
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (AlgorithmCanvas.this.isDisposed()) return;
				innerClearCanvas();
			}
		});
	}

	public void showNotAvailable(final boolean notAvailable) {
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (AlgorithmCanvas.this.isDisposed()) return;
				innerClearCanvas();
				if (notAvailable) {
					isImageAvailable = false;
				} else {
					isImageAvailable = true;
				}
			}
		});
	}

	public void setCanvasSize(final int width, final int height) {
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (AlgorithmCanvas.this.isDisposed()) return;
				
				// adjust buffer image size
				Image old = img;
				Image imnew = new Image(AlgorithmCanvas.this.getDisplay(), width, height);
				GC gc = new GC(imnew);
				gc.drawImage(old, 0, 0);
				gc.dispose();
				img = imnew;
				old.dispose();
				
				// adjust canvas size
				Point parsz = AlgorithmCanvas.this.getParent().getSize();
				int w = width, h = height;
				if (parsz.x > w) w = parsz.x;
				if (parsz.y > h) h = parsz.y;
				AlgorithmCanvas.this.setSize(w, h);
				
				flip();
			}
		});
	}

	public void flip() {
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (AlgorithmCanvas.this.isDisposed()) return;
				if (!isImageAvailable) {
					GC gc = new GC(img);
					gc.setForeground(new Color(AlgorithmCanvas.this.getDisplay(),
							180, 180, 180));
					gc.drawText("Image not available", 2, 2);
					gc.dispose();
				}
				
				redraw();
			}
		});
	}

	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				GC gc = new GC(img);
				gc.setAntialias(SWT.ON);
				gc.setForeground(drawcol);
				gc.drawLine(x1, y1, x2, y2);
				gc.dispose();
			}
		});
	}
	
	public void drawOval(final int x, final int y, final int a, final int b) {
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				GC gc = new GC(img);
				gc.setAntialias(SWT.ON);
				gc.setForeground(drawcol);
				gc.drawOval(x, y, a, b);
				gc.dispose();
			}
		});
	}

	public void drawText(final String text, final int x, final int y, final boolean center) {
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				GC gc = new GC(img);
				int xp = x, yp = y;
				if (center) {
					yp -= gc.getFontMetrics().getHeight() / 2;
					xp -= text.length() * gc.getFontMetrics().getAverageCharWidth() / 2;
				}
				gc.setForeground(drawcol);
				gc.drawText(text, xp, yp, true);
				gc.dispose();
			}
		});
	}

	public void fillOval(final int x, final int y, final int r1, final int r2) {
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				GC gc = new GC(img);
				gc.setAntialias(SWT.ON);
				gc.setBackground(fillcol);
				gc.fillOval(x, y, r1, r2);
				gc.dispose();
			}
		});
	}

	public void setDrawColor(int r, int g, int b) {
		drawcol = new Color(this.getDisplay(), r, g, b);
	}

	public void setFillColor(int r, int g, int b) {
		fillcol = new Color(this.getDisplay(), r, g, b);
	}

}














