
public class MemoryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MemoryException(long address, String type) {
		super("The address: 0x"+ Long.toString(address, 16) + " is an invlalid address of type "+ type);
	}

}
