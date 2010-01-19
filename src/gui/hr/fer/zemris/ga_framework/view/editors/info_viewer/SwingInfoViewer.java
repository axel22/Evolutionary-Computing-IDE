package hr.fer.zemris.ga_framework.view.editors.info_viewer;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.view.Editor;
import hr.fer.zemris.ga_framework.view.ImageLoader;

import java.awt.Color;
import java.awt.Frame;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


/**
 * JEditorPane == BS for html, sorry boys
 * 
 * @author Axel
 *
 */
@Deprecated
public class SwingInfoViewer extends Editor {
	
	/* static fields */
	private static final ImageData IMAGE_ICON = ImageLoader.loadImage("icons", "book.png");
	private static final Events[] EVENTS = new Events[] {};

	/* private fields */
	private IAlgorithm alg;
	private JEditorPane infoDisplay;

	/* ctors */
	
	public SwingInfoViewer(Composite parent, IController c, long editorId, CommandResult res) {
		super(parent, SWT.NONE, c, editorId, EVENTS);
		
		initModel(res);
		initGUI();
		advinitGUI();
	}

	/* methods */
	
	private void initModel(CommandResult res) {
		alg = (IAlgorithm) res.msg(Events.KEY.ALGORITHM_OBJECT);
	}

	private void advinitGUI() {
		// set color
		RGB rgb = this.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND).getRGB();
		infoDisplay.setBackground(new Color(rgb.red, rgb.green, rgb.blue));
		
		// create text for the pane
		String txt = createPaneText();
		infoDisplay.setText(txt);
	}
	
	private String createPaneText() {
		StringBuilder sb = new StringBuilder("<html>");
		
		// head
		sb.append("<head>");
		sb.append("<style type='text/css'>" +
			"table.alg_values {" +
				"border-spacing: 0px;" +
				"border-style: none none none none;" +
				"border-collapse: separate;" +
				"background-color: rgb(220,220,220);" + 
			"}" +
			"table.alg_values th {" +
				"border-width: 1px 1px 0px 0px;" +
				"padding: 2px 2px 2px 2px;" +
				"border-style: solid solid solid solid;" +
				"border-color: white white white white;" +
			"}" +
			"table.alg_values td {" +
				"border-width: 1px 1px 0px 0px;" +
				"padding: 2px 2px 2px 2px;" +
				"border-style: solid solid solid solid;" +
				"border-color: white white white white;" +
			"}" +
		"</style>");
		sb.append("</head>");
		
		// header
		sb.append("<body>");
		sb.append("<div style='font-family:tahoma; font-size:45;'>");
		sb.append(alg.getName());
		sb.append("</div>");
		sb.append("<p style='font-family:tahoma; font-size:12'>");
		sb.append("&nbsp; &nbsp; by ").append(alg.getAuthors());
		sb.append("</p>");
		sb.append("<div style='background-color:rgb(220,220,220); height:5px'></div>");
		sb.append("<br/>");
		
		// short description
		sb.append("<div style='font-family:tahoma; font-size:11'>");
		sb.append(alg.getDescription());
		sb.append("</div>");
		sb.append("<br/>");
		
		// parameters
		List<IParameter> params = alg.getParameters();
		Map<String, IValue> defvals = alg.getDefaultValues();
		sb.append("<div style='font-family:tahoma; font-weight:normal; font-size:20; padding: 0px 0px 10px 0px'>")
			.append("Parameters</div>");
		sb.append("<table class='alg_values' style='font-family:tahoma; font-size:11;'>");
		sb.append("<tr style='font-weight:bold'><td>Name</td><td>Type</td><td>Default value</td><td>Description</td></tr>");
		for (IParameter p : params) {
			sb.append("<tr>");
			sb.append("<td>").append(p.getName()).append("</td>");
			sb.append("<td>").append(p.getParamType().niceName()).append("</td>");
			sb.append("<td>").append(defvals.get(p.getName()).value().toString()).append("</td>");
			sb.append("<td>").append(p.getDescription()).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("<br/>");
		
		// return values
		List<IParameter> retvals = alg.getReturnValues();
		sb.append("<div style='font-family:tahoma; font-weight:normal; font-size:20; padding: 0px 0px 10px 0px'>")
			.append("Return values</div>");
		sb.append("<table class='alg_values' style='font-family:tahoma; font-size:11;'>");
		sb.append("<tr style='font-weight:bold'><td>Name</td><td>Type</td><td>Description</td></tr>");
		for (IParameter p : retvals) {
			sb.append("<tr>");
			sb.append("<td>").append(p.getName()).append("</td>");
			sb.append("<td>").append(p.getParamType().niceName()).append("</td>");
			sb.append("<td>").append(p.getDescription()).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("<br/>");
		
		// notes
		sb.append("<div style='font-family:tahoma; font-weight:normal; font-size:11'>Notes: ");
		if (alg.doesReturnInfoDuringRun()) {
			sb.append("This implementation can return info about the state of the run, during the run. ");
		} else {
			sb.append("This implementation does not return info about the state of the run until the " +
					"algorithm ends. ");
		}
		if (alg.isPausable()) {
			sb.append("It can be paused while running. ");
		} else {
			sb.append("It cannot be paused while running. ");
		}
		if (alg.isSaveable()) {
			sb.append("It's state cannot be saved for later retrieval. ");
		} else {
			sb.append("It's state cannot be saved. ");
		}
		if (alg.isNative()) {
			sb.append("It performs native calls. ");
		} else {
			sb.append("It does not perform any native calls. ");
		}
 		sb.append("</div>");
 		sb.append("<br/>");
		
		// the info itself
 		sb.append("<div style='font-family:tahoma; font-weight:normal; font-size:20; padding: 0px 0px 10px 0px'>Description</div>");
 		sb.append("<div style='font-family:tahoma; font-size:11'>");
 		sb.append(alg.getExtensiveInfo());
 		sb.append("</div>");
 		sb.append("<br/>");
		
		// footer
		sb.append("</body>");
		
		sb.append("</html>");
		
		return sb.toString();
	}

	private void initGUI() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 2;
		setLayout(gridLayout);

		final Composite composite = new Composite(this, SWT.EMBEDDED);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLocation(0, 0);

		final Frame frame = SWT_AWT.new_Frame(composite);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		frame.add(scrollPane);

		infoDisplay = new JEditorPane();
		scrollPane.setViewportView(infoDisplay);
		infoDisplay.setEditorKit(new HTMLEditorKit());
		composite.setSize(525, 275);
	}

	@Override
	public Image getImage(Display d) {
		return new Image(d, IMAGE_ICON);
	}

	public boolean canRedo() {
		return false;
	}

	public boolean canUndo() {
		return false;
	}

	public String getEditorName() {
		return "Info Viewer: " + alg.getName();
	}

	public IInfoListener getInfoListener() {
		return null;
	}

	public void redo() {
		throw new UnsupportedOperationException("Does not support undo or redo.");
	}

	public void undo() {
		throw new UnsupportedOperationException("Does not support undo or redo.");
	}

	public void onEvent(Events evtype, CommandResult messages) {
	}

	@Override
	public Map<String, String> getSaveTypes() {
		return new HashMap<String, String>();
	}

	@Override
	public void save(String extension, OutputStream os) {
		throw new IllegalArgumentException("Cannot save to any extension.");
	}

	@Override
	public String getLoadExtension() {
		return null;
	}

	@Override
	public boolean isLoadable() {
		return false;
	}

	@Override
	public void load(InputStream is) {
		throw new UnsupportedOperationException("Cannot load state.");
	}
	
}














