
/**
 * Provides ALU functionality to the Y86 processor emulator Uses logical
 * operations to simulate the ALU
 * 
 * @author Matthew Hinton
 *
 */
public class ALU {
	/**
	 * Serves as the condition codes for the ALU
	 */
	private static boolean ZF, SF, OF;
	private static boolean BASE_VALUE = false;

	/**
	 * Performs the binary AND operation on bit strings a and b Precondition:
	 * length(a) == length(b)
	 * 
	 * @param a bit string input a
	 * @param b bit string input b
	 * @return c the output bit string
	 */
	public static boolean[] AND(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		for (int pos = 0; pos < a.length; pos++)
			c[pos] = a[pos] && b[pos];
		ZF = ALU.Equal(c, new boolean[a.length]);
		SF = c[c.length - 1];
		OF = false;
		return c;
	}

	/**
	 * Performs the binary XOR operation on bit strings a and b Precondition:
	 * length(a) == length(b)
	 * 
	 * @param a bit string input a
	 * @param b bit string input b
	 * @return c the output bit string
	 */
	public static boolean[] XOR(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		for (int pos = 0; pos < a.length; pos++)
			c[pos] = a[pos] ^ b[pos];
		ZF = ALU.Equal(c, new boolean[a.length]);
		SF = c[c.length - 1];
		OF = false;
		return c;
	}

	/**
	 * Performs the Unary NOT operation on bit string a
	 * 
	 * @param a bit string input a
	 * @return c the output bit string
	 */
	public static boolean[] NOT(boolean[] a) {
		boolean[] c = new boolean[a.length];
		for (int pos = 0; pos < a.length; pos++)
			c[pos] = !a[pos];
		return c;
	}

	/**
	 * Performs the binary XOR operation on bit strings a and b Precondition:
	 * length(a) == length(b)
	 * 
	 * @param a bit string input a
	 * @param b bit string input b
	 * @return true if the two strings are equal
	 */
	public static boolean Equal(boolean[] a, boolean[] b) {
		for (int pos = 0; pos < a.length; pos++)
			if (a[pos] != b[pos])
				return false;
		return true;
	}

	/**
	 * Performs the binary ADD operation on bit strings a and b Precondition:
	 * length(a) == length(b) Postcondition: ALU CC are set properly based on
	 * operation
	 * 
	 * @param a bit string input a
	 * @param b bit string input b
	 * @return true if the two strings are equal
	 */
	public static boolean[] ADD(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		boolean carry = false;
		for (int pos = 0; pos < a.length; pos++) {
			c[pos] = a[pos] ^ b[pos] ^ carry;
			boolean carryTemp = (a[pos] && b[pos]) || (a[pos] && carry) || (b[pos] && carry);
			if (pos == c.length - 1) {
				OF = carry ^ carryTemp;
			}
			carry = carryTemp;
		}
		ZF = ALU.Equal(c, new boolean[a.length]);
		SF = c[c.length - 1];
		return c;
	}

	/**
	 * Performs the binary ADD operation on bit strings a and b Precondition:
	 * length(a) == length(b) Postcondition: ALU CC are not set based on operation
	 * 
	 * @param a bit string input a
	 * @param b bit string input b
	 * @return true if the two strings are equal
	 */
	public static boolean[] IADD(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		boolean carry = false;
		for (int pos = 0; pos < a.length; pos++) {
			c[pos] = a[pos] ^ b[pos] ^ carry;
			boolean carryTemp = (a[pos] && b[pos]) || (a[pos] && carry) || (b[pos] && carry);
			carry = carryTemp;
		}
		return c;
	}

	/**
	 * Performs the binary SUB operation on bit strings a and b Precondition:
	 * length(a) == length(b) Postcondition: ALU CC are set properly based on
	 * operation
	 * 
	 * @param a bit string input a
	 * @param b bit string input b
	 * @return true if the two strings are equal
	 */
	public static boolean[] SUB(boolean[] a, boolean[] b) {
		a = NEG(a);
		boolean[] c = ADD(a, b);
		return c;
	}

	/**
	 * Helper function to increment a by 8
	 * 
	 * @param a bit string input a
	 * @return c the incremented a bit string
	 */
	public static boolean[] INCREMENTEIGHT(boolean[] a) {
		boolean[] b = new boolean[a.length];
		b[3] = true;
		return IADD(a, b);
	}

	/**
	 * Helper function to decrement a by 8
	 * 
	 * @param a bit string input a
	 * @return c the decremented a bit string
	 */
	public static boolean[] DECREMENTEIGHT(boolean[] a) {
		boolean[] b = NEGATIVE_EIGHT;
		return IADD(a, b);
	}

	/**
	 * Helper function to increment a by 1
	 * 
	 * @param a bit string input a
	 * @return c the incremented a bit string
	 */
	private static boolean[] INCREMENTONE(boolean[] a) {
		boolean[] b = new boolean[a.length];
		b[0] = true;
		return IADD(a, b);
	}

	/**
	 * Extends the sign of a to length target size. Sign bit is extended based on
	 * Unsigned flag
	 * 
	 * @param a          the bit string input a
	 * @param U          unsigned flag
	 * @param targetSize the output size
	 * @return c the bit string a sign extended to target size
	 */
	public static boolean[] signExtension(boolean[] a, boolean U, int targetSize) {
		boolean[] c = new boolean[targetSize];
		boolean sign = a[a.length - 1];
		System.arraycopy(a, 0, c, 0, a.length);
		if (U && !sign) {
			return c;
		}
		for (int pos = a.length; pos < targetSize; pos++)
			c[pos] = true;
		return c;
	}

	// Test Bed

	// arraySize <= 64
	// -2^(arraySize-1) <= l <= 2^(arraySize-1) -1

	/**
	 * Converts an input long to array size Precondition: arraySize <= 64
	 * Precondition: -2^(arraySize-1) <= l <= 2^(arraySize-1) -1
	 * 
	 * @param l         the long to convert
	 * @param arraySize output array size
	 * @return c the output array with bits set to two's complement of l
	 */
	public static boolean[] longToBitArray(long l, int arraySize) {
		long T_MIN = (long) (-1 * Math.pow(2, arraySize - 1));
		boolean[] c = new boolean[arraySize];
		if (l == T_MIN) {
			c[c.length - 1] = true;
			return c;
		}
		boolean neg = l < 0;
		l = Math.abs(l);
		for (int pos = arraySize - 2; pos >= 0; pos--) {
			long val = ((long) Math.pow(2, pos));
			if (val <= l) {
				c[pos] = true;
				l -= val;
			}
		}
		if (neg)
			c = INCREMENTONE(NOT(c));
		return c;
	}
	/**
	 * Finds the negation of a
	 * @param a the bit string input a
	 * @return c the negation of a
	 */
	private static boolean[] NEG(boolean[] a) {
		return INCREMENTONE(NOT(a));
	}
	/**
	 * Gets the Zero Flag value
	 * @return the ZF value
	 */
	public static boolean ZF() {
		return ZF;
	}
	
	/**
	 * Gets the Sign Flag value
	 * @return the SF value
	 */
	public static boolean SF() {
		return SF;
	}
	
	/**
	 * Gets the Overflow Flag value
	 * @return the OF value
	 */
	public static boolean OF() {
		return OF;
	}
	/**
	 * Constant bit array
	 */
	private static boolean[] NEGATIVE_EIGHT = longToBitArray(-8, 64);
	/**
	 * Resets Condition Codes prior to each execution cycles
	 */
	public static void resetCC() {
		ZF = BASE_VALUE;
		SF = BASE_VALUE;
		OF = BASE_VALUE;
	}

}
