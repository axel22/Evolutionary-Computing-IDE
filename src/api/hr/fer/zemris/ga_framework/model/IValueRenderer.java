package hr.fer.zemris.ga_framework.model;




/**
 * This interface describes the value renderer
 * dialog used to render those return values of
 * the dialog that have the type ISerializable.
 * 
 * Note that you do not have to implement the renderers
 * for your return types if they are ISerializable - this
 * is optional. However, if you do, make sure you create
 * only one per class. Also, the renderer must implement
 * this interface and in addition extend the type Composite found
 * in the SWT (you will need the SWT library to develop
 * return value renderers). As such, they need two parameters
 * in their ctors - parent Composite and style (int).
 * 
 * @author Axel
 *
 */
public interface IValueRenderer {
	
	/**
	 * By invoking this method, the value renderer Composite
	 * shall proceed by drawing (or displaying) a representation
	 * of the ISerializable object.
	 * The object, of course, must have the correct type. This
	 * method shall typically cast the object into the correct
	 * type.
	 * 
	 * @param iser
	 * The value to be rendered.
	 */
	void setValueToRender(ISerializable iser);
	
}














