package BaseEmulator;

public abstract class LittleEndian {

	@Override
	public boolean equals(Object obj) {
		LittleEndian le = (LittleEndian) obj;
		if (le.bitArray.length != this.bitArray.length)
			return false;
		return ALU.Equal(this.bitArray, le.bitArray);
	}

	public final boolean bitArray[];

	/**
	 * Calculates the value of the Little Endian value
	 * 
	 * @return the signed calculated value
	 */
	public long calculateValueSigned() {
		long val = (bitArray[bitArray.length - 1]) ? ((long) Math.pow(-2, bitArray.length - 1)) : 0;
		for (int pos = 0; pos < bitArray.length - 1; pos++) {
			if (bitArray[pos]) {
				val += ((long) Math.pow(2, pos));
			}
		}
		return val;
	}

	/**
	 * Calculates the unsigned value of the Little Endian value
	 * 
	 * @return the unsigned calculate value
	 */
	public String calculateValueUnSigned() {
		long val = (bitArray[bitArray.length - 1]) ? ((long) Math.pow(2, bitArray.length - 1)) : 0;
		for (int pos = 0; pos < bitArray.length - 1; pos++) {
			if (bitArray[pos]) {
				val += ((long) Math.pow(2, pos));
			}
		}
		return Long.toUnsignedString(val);
	}

	/**
	 * Generates a bit string representation of the Little Endian
	 * 
	 * @return the string representation
	 */
	public String generateBitString() {
		String s = "";
		for (int pos = bitArray.length - 1; pos >= 0; pos--)
			s += (bitArray[pos]) ? "1" : "0";
		return s;
	}

	/**
	 * Gets the underlying bit array of the Little Endian Value
	 * 
	 * @return
	 */
	public boolean[] generateBitArray() {
		return bitArray;
	}

	/**
	 * Generates a Hex string of the Little Endian
	 * 
	 * @return the hex string output
	 */
	public String generateHex() {
		String hex = "";
		for (int pos = bitArray.length - 8; pos >= 0; pos -= 8) {
			hex += generateHex(bitArray, pos);
		}
		return hex;
	}

	/**
	 * Generates a Hex string of the Little Endian value, with a little endian
	 * format
	 * 
	 * @return the hex string output, formatted in a little endian style
	 */
	public String generateHexLE() {
		String hex = "";
		for (int pos = 0; pos <= bitArray.length - 8; pos += 8) {
			hex += generateHex(bitArray, pos);
		}
		return hex;
	}

	/**
	 * Generates a little endian version of the bit string for the LE object
	 * 
	 * @return the little endian bit string
	 */
	public String generateBitStringLE() {
		String bitString = "";
		for (int pos = 7; pos <= bitArray.length - 1; pos += 8) {
			for (int x = pos; x >= pos - 7; x--) {
				bitString += (bitArray[x]) ? "1" : "0";
			}
		}
		return bitString;
	}

	/**
	 * Gets the sign of the Little endian
	 * 
	 * @return the sign value
	 */
	public boolean getSign() {
		return bitArray[bitArray.length - 1];
	}

	public String toString() {
		String s = this.generateHex();
		return s;
	}

	/**
	 * Creates a display string for the Little Endian
	 * 
	 * @return the string to display
	 */
	public String displayToString() {
		String s = this.generateHex();
		int pos = 0;
		int firstNonZero = -1;
		while (pos < s.length() && firstNonZero == -1) {
			if (s.charAt(pos) != '0')
				firstNonZero = pos;
			pos++;
		}
		if (firstNonZero == -1)
			firstNonZero = s.length();
		s = s.substring(firstNonZero);
		return (s.length() == 0) ? "0" : s;
	}

	/**
	 * Takes a hex value and extends it to the required size, fixes for an LE format
	 * 
	 * @param hex          the hex input to fix
	 * @param requiredSize the size to extend to
	 * @return the fixed Hex input
	 */
	public static String LEHexFixer(String hex, int requiredSize) {
		hex = (hex.length() % 2 == 1) ? "0" + hex : hex; 
		requiredSize = requiredSize / 4;
		char c = Character.toUpperCase(hex.charAt(hex.length() - 2));
		boolean sign = (c >= '8' && c <= 'F');
		String signExtension = (sign) ? "F" : "0";
		int pad = requiredSize - hex.length();
		for (int i = 0; i < pad; i++) {
			hex = hex + signExtension;
		}
		return hex;
	}

	/**
	 * Fixes a hex value following BE configuration
	 * 
	 * @param hex          the hex value to fix
	 * @param requiredSize the size to extend too
	 * @return the fixed hex value
	 */
	public static String hexFixer(String hex, int requiredSize) {
		requiredSize = requiredSize / 4;
		char c = Character.toUpperCase(hex.charAt(0));
		boolean sign = (c >= '8' && c <= 'F');
		String signExtension = (sign) ? "F" : "0";
		int pad = requiredSize - hex.length();
		for (int i = 0; i < pad; i++)
			hex = signExtension + hex;
		return hex;
	}

	/**
	 * Generates the Hex value for a specific Byte
	 * 
	 * @param bitArray the bit array to extract from
	 * @param start    the byte to extract
	 * @return the hex value of the byte
	 */
	public static String generateHex(boolean[] bitArray, int start) {
		int valLowerFour = 0;
		int valUpperFour = 0;
		for (int pos = 0; pos < 4; pos++) {
			if (bitArray[pos + start]) {
				valLowerFour += ((int) Math.pow(2, pos));
			}
			if (bitArray[pos + start + 4]) {
				valUpperFour += ((int) Math.pow(2, pos));
			}
		}
		String lowerFour = (valLowerFour > 9) ? ((char) (55 + valLowerFour)) + "" : "" + valLowerFour;
		String upperFour = (valUpperFour > 9) ? ((char) (55 + valUpperFour)) + "" : "" + valUpperFour;
		return upperFour + lowerFour;
	}

	/**
	 * Gets a nibble representation of a single hex value Ex: F -> 1111
	 * 
	 * @param hex the hex to parse
	 * @return the parsed nibble, reprsented as a bit array
	 */
	public static boolean[] getNibble(char hex) {
		if (!charContains(validNibbles, hex))
			throw new NumberFormatException("Invalid hex input: " + hex);
		boolean[] nibble = new boolean[4];
		int val = 0;
		if (Character.isLowerCase(hex)) {
			val = hex - 'a' + 10;
		} else if (Character.isUpperCase(hex)) {
			val = hex - 'A' + 10;
		} else {
			val = hex - '0';
		}
		for (int pos = 3; pos >= 0; pos--) {
			int pow = ((int) Math.pow(2, pos));
			if (val >= pow) {
				nibble[pos] = true;
				val -= pow;
			}
		}
		return nibble;

	}

	/**
	 * Gets a bit array representation of hex input
	 * 
	 * @param hex the input hex
	 * @return the bit array representation
	 */
	public static boolean[] getByte(String hex) {
		hex = LittleEndian.hexFixer(hex, 8);
		boolean[] byteArray = new boolean[8];
		char[] nibbles = hex.toCharArray();
		System.arraycopy(getNibble(nibbles[1]), 0, byteArray, 0, 4);
		System.arraycopy(getNibble(nibbles[0]), 0, byteArray, 4, 4);
		return byteArray;
	}

	/**
	 * Creates a default Little Endian of length size
	 * 
	 * @param size
	 */
	public LittleEndian(int size) {
		bitArray = new boolean[size];
	}

	/**
	 * Creates a Little Endian using an input bit array
	 * 
	 * @param bitArray the input bit array
	 */
	public LittleEndian(boolean[] bitArray) {
		this.bitArray = bitArray.clone();
	}
	
	/**
	 * Valid nibbles for a Hex
	 */
	public static final char[] validNibbles = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'A', 'b', 'B',
			'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F' };

	/**
	 * Scans an array for a specfic char value
	 * 
	 * @param array the array to scan
	 * @param t     the char value to look up
	 * @return if the array contains the char
	 */
	public static boolean charContains(char[] array, char t) {
		for (char c : array)
			if (c == t)
				return true;
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println(LEHexFixer("F", 64));
	}
}
