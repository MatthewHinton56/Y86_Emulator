package BaseEmulator;

public class DoubleWord extends LittleEndian {

	public static final int DOUBLEWORDSIZE = 64;

	/**
	 * Creates a default double word
	 */
	public DoubleWord() {
		super(DOUBLEWORDSIZE);
	}

	/**
	 * Creates a double word using a hex value, using the LE flag to parse the hex
	 * correctly
	 * 
	 * @param hex the hex value to process
	 * @param LE  Little Endian flag for the input type
	 */
	public DoubleWord(String hex, boolean LE) {
		super(DOUBLEWORDSIZE);
		if (LE)
			hex = LEHexFixer(hex, DOUBLEWORDSIZE);
		else
			hex = hexFixer(hex, DOUBLEWORDSIZE);
		for (int pos = 0; pos < hex.length(); pos += 2) {
			boolean[] hexBYTE = getByte(hex.substring(pos, pos + 2));

			if (LE) {
				System.arraycopy(hexBYTE, 0, bitArray, pos * BYTE.BYTESIZE / 2, BYTE.BYTESIZE);
			} else {
				System.arraycopy(hexBYTE, 0, bitArray, DoubleWord.DOUBLEWORDSIZE - (pos / 2 + 1) * BYTE.BYTESIZE,
						BYTE.BYTESIZE);
			}

		}
	}

	/**
	 * Creates a double word using a bit array input
	 * 
	 * @param bitArray the input bit array
	 */
	public DoubleWord(boolean bitArray[]) {
		super(bitArray);
	}

	/**
	 * Extracts a specific byte from the double word. 0 - Lowest ordered byte 7 -
	 * Highest ordered byte
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
	 * Extracts a specific half word from the double word. 0 - Lowest ordered half
	 * word 3 - Highest ordered half word
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

	/**
	 * Extracts a specific word from the double word. 0 - Lowest ordered word
	 * 
	 * @param pos the word to extract
	 * @return the extracted Word
	 */
	public Word getWord(int pos) {
		pos = pos * Word.WORDSIZE;
		boolean[] bitArray = new boolean[Word.WORDSIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, Word.WORDSIZE);
		return new Word(bitArray);
	}

	public DoubleWord(long l) {
		super(ALU.longToBitArray(l, DOUBLEWORDSIZE));
	}

}
