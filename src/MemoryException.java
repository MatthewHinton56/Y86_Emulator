
public class MemoryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MemoryException(long address, String type) {
		super("The address: 0x"+ Long.toUnsignedString(address, 16) + " is an invalid address of type "+ type);
	}

	public MemoryException(long address) {
		super("The address: 0x"+ Long.toUnsignedString(address, 16) + " is accessing an invalid portion of memory");
	}
}
