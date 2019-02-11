package Sequential;

public class MemoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a Memory exception for an invalid address
	 * 
	 * @param address the invalid address
	 * @param type    the of invalid address
	 */
	public MemoryException(long address, String type) {
		super("The address: 0x" + Long.toUnsignedString(address, 16) + " is an invalid address of type " + type);
	}

	/**
	 * Creates a Memory exception for an invalid portion of memory
	 * 
	 * @param address the invalid address
	 */
	public MemoryException(long address) {
		super("The address: 0x" + Long.toUnsignedString(address, 16) + " is accessing an invalid portion of memory");
	}
}
