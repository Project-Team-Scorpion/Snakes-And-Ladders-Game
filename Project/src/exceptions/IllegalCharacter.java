package exceptions;

public class IllegalCharacter extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalCharacter() {
		super("Only english letters and numbers allowed, Maximum 10 Characters");
	}
}