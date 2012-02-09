package xdi2.io;

/**
 * This abstract class relieves subclasses from the following:
 * - Writing a subject
 * - Writing a statement
 * 
 * In both cases, a temporary graph is created which is then written by the subclass.
 * 
 * @author markus
 */
public abstract class AbstractXDIWriter implements XDIWriter {

	private static final long serialVersionUID = -4120729667091454408L;
}
