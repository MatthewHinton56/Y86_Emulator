package BaseEmulator;

public class Word extends LittleEndian {

	public static final int WORDSIZE = 32;

	/**
	 * Creates a default word
	 */
	public Word() {
		super(WORDSIZE);
	}

	/**
	 * Creates a word using a hex value, using the LE flag to parse the hex
	 * correctly
	 * 
	 * @param hex the hex value to process
	 * @param LE  Little Endian flag for the input type
	 */
	public Word(String hex, boolean LE) {
		super(WORDSIZE);
		if (LE)
			hex = LEHexFixer(hex, WORDSIZE);
		else
			hex = hexFixer(hex, WORDSIZE);
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
	 * Creates a word using a bit array input
	 * 
	 * @param bitArray the input bit array
	 */
	public Word(boolean bitArray[]) {
		super(bitArray);
	}

	/**
	 * Extends a word to a double word
	 * 
	 * @param signed to extend sign or unsigned
	 * @return the extended word
	 */
	public DoubleWord extendToDoubleWord(boolean signed) {
		String hex = this.generateHex();
		if (signed) {
			boolean neg = this.getSign();
			hex = (neg) ? "FFFFFFFF" + hex : "00000000" + hex;
			return new DoubleWord(hex, false);
		}
		return new DoubleWord("00000000" + hex, false);
	}

	/**
	 * Extracts a specific byte from the word. 0 - Lowest ordered byte 3 - Highest
	 * ordered byte
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

	/**
	 * Extracts a specific half word from the word. 0 - Lowest ordered half word
	 * 
	 * @param pos the half word to extract
	 * @return the extracted Half Word
	 */
	public HalfWord getHalfWord(int pos) {
		pos = pos * HalfWord.HALFWORDSIZE;
		boolean[] bitArray = new boolean[HalfWord.HALFWORDSIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, HalfWord.HALFWORDSIZE);
		return new HalfWord(bitArray);
	}

}
