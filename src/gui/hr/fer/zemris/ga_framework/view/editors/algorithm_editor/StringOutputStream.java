package hr.fer.zemris.ga_framework.view.editors.algorithm_editor;

import java.io.IOException;
import java.io.OutputStream;

@Deprecated
public class StringOutputStream extends OutputStream {
	
	/* static fields */

	/* private fields */
	private StringBuilder sb;

	/* ctors */
	
	public StringOutputStream() {
		sb = new StringBuilder();
	}

	/* methods */

	@Override
	public void write(int b) throws IOException {
		sb.append((char)b);
	}
	
	public String getString() {
		return sb.toString();
	}
	
}














