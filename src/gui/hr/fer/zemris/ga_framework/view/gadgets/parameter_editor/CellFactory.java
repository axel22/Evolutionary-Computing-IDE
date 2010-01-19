package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.ParameterTypes;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

public class CellFactory {

	public static Cell createCell(Composite parent, IParameter parameter, int x, int y, int style) {
		if (parameter.getAllowed() != null) {
			Cell cell = new ComboCell(parent, x, y, parameter.getAllowed(), style);
			return cell;
		}
		ParameterTypes tp = parameter.getParamType();
		Cell cell = null;
		switch (tp) {
		case BOOLEAN:
			cell = new BooleanCell(parent, x, y, style);
			return cell;
		case INTEGER:
			cell = new IntegerCell(parent, x, y, style);
			return cell;
		case REAL:
			cell = new RealCell(parent, x, y, style);
			return cell;
		case STRING:
			cell = new StringCell(parent, x, y, style);
			return cell;
		case TIME:
			cell = new TimeCell(parent, x, y, style);
			return cell;
		case ISERIALIZABLE:
			Class<ISerializable> cls = parameter.getValueClass();
			cell = new SerializableCell(parent, x, y, cls, style);
			return cell;
		default:
			throw new IllegalArgumentException("Parameter type unknown: " + tp);
		}
	}
	
	/**
	 * 
	 * @param parent
	 * @param pt
	 * May never be null.
	 * @param allowed
	 * May be null.
	 * @param sercls
	 * May be null if pt is different than ISERIALIZABLE.
	 * @param x
	 * @param y
	 * @param style
	 * @return
	 */
	public static Cell createCell(Composite parent, ParameterTypes pt, List<Object> allowed, Class<ISerializable> sercls, int x, int y, int style) {
		if (allowed != null) {
			Cell cell = new ComboCell(parent, x, y, allowed, style);
			return cell;
		}
		ParameterTypes tp = pt;
		Cell cell = null;
		switch (tp) {
		case BOOLEAN:
			cell = new BooleanCell(parent, x, y, style);
			return cell;
		case INTEGER:
			cell = new IntegerCell(parent, x, y, style);
			return cell;
		case REAL:
			cell = new RealCell(parent, x, y, style);
			return cell;
		case STRING:
			cell = new StringCell(parent, x, y, style);
			return cell;
		case TIME:
			cell = new TimeCell(parent, x, y, style);
			return cell;
		case ISERIALIZABLE:
			Class<ISerializable> cls = sercls;
			cell = new SerializableCell(parent, x, y, cls, style);
			return cell;
		default:
			throw new IllegalArgumentException("Parameter type unknown: " + tp);
		}
	}

	
}














