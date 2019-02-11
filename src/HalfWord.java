
public class HalfWord extends LittleEndian {

	public static final int HALFWORDSIZE = 32;

	/**
	 * Creates a default half word
	 */
	public HalfWord() {
		super(HALFWORDSIZE);
	}

	/**
	 * Creates a half word using a hex value, using the LE flag to parse the hex
	 * correctly
	 * 
	 * @param hex the hex value to process
	 * @param LE  Little Endian flag for the input type
	 */
	public HalfWord(String hex, boolean LE) {
		super(HALFWORDSIZE);
		if (LE)
			hex = LEHexFixer(hex, HALFWORDSIZE);
		else
			hex = hexFixer(hex, HALFWORDSIZE);
		for (int pos = 0; pos < hex.length(); pos += 2) {
			boolean[] hexBYTE = getByte(hex.substring(pos, pos + 2));
			if (LE) {
				System.arraycopy(hexBYTE, 0, bitArray, pos * BYTE.BYTESIZE / 2, BYTE.BYTESIZE);
			} else {
				System.arraycopy(hexBYTE, 0, bitArray, BYTE.BYTESIZE - (pos / 2 + 1) * BYTE.BYTESIZE, BYTE.BYTESIZE);
			}

		}
	}

	/**
	 * Creates a half word using a bit array input
	 * 
	 * @param bitArray the input bit array
	 */
	public HalfWord(boolean bitArray[]) {
		super(bitArray);

	}

	/**
	 * Extends a half word to a word
	 * 
	 * @param signed to extend sign or unsigned
	 * @return the extended half word
	 */
	public Word extendToWord(boolean signed) {
		String hex = this.generateHex();
		if (signed) {
			boolean neg = this.getSign();
			hex = (neg) ? "FFFF" + hex : "0000" + hex;
			return new Word(hex, false);
		}
		return new Word("0000" + hex, false);
	}

	/**
	 * Extends a half word to a double word
	 * 
	 * @param signed to extend sign or unsigned
	 * @return the extended half word
	 */
	public DoubleWord extendToDoubleWord(boolean signed) {
		String hex = this.generateHex();
		if (signed) {
			boolean neg = this.getSign();
			hex = (neg) ? "FFFFFFFFFFFF" + hex : "000000000000" + hex;
			return new DoubleWord(hex, false);
		}
		return new DoubleWord("000000000000" + hex, false);
	}

	/**
	 * Extracts a specific byte from the half word. 0 - Lowest ordered byte
	 * 
	 * @param pos the byte to extract
	 * @return the extracted BYTE
	 */
	public BYTE getBYTE(int pos) {
		pos = pos * BYTE.BYTESIZE;
		boolean[] bitArray = new boolean[BYTE.BYTESIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, BYTE.BYTESIZE);
		return new BYTE(bitArray);
	}
}
