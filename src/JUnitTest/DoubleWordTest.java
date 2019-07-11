/**
 * 
 */
package JUnitTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import BaseEmulator.DoubleWord;
import BaseEmulator.LittleEndian;;

/**
 * @author Matth
 *
 */
class DoubleWordTest {

	/**
	 * Test method for {@link BaseEmulator.DoubleWord#DoubleWord()}.
	 */
	@Test
	void testDoubleWord() {
		DoubleWord dw = new DoubleWord();
		boolean[] array = new boolean[64];
		System.out.println(Arrays.toString(array));
		Assertions.assertArrayEquals(array, dw.bitArray);
	}

	/**
	 * Test method for {@link BaseEmulator.DoubleWord#DoubleWord(java.lang.String, boolean)}.
	 */
	@Test
	void testDoubleWordStringBoolean() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.DoubleWord#DoubleWord(boolean[])}.
	 */
	@Test
	void testDoubleWordBooleanArray() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.DoubleWord#DoubleWord(long)}.
	 */
	@Test
	void testDoubleWordLong() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#calculateValueSigned()}.
	 */
	@Test
	void testCalculateValueSigned() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#calculateValueUnSigned()}.
	 */
	@Test
	void testCalculateValueUnSigned() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateBitString()}.
	 */
	@Test
	void testGenerateBitString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateBitArray()}.
	 */
	@Test
	void testGenerateBitArray() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateHex()}.
	 */
	@Test
	void testGenerateHex() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateHexLE()}.
	 */
	@Test
	void testGenerateHexLE() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateBitStringLE()}.
	 */
	@Test
	void testGenerateBitStringLE() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#getSign()}.
	 */
	@Test
	void testGetSign() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#toString()}.
	 */
	@Test
	void testToString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#displayToString()}.
	 */
	@Test
	void testDisplayToString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#LEHexFixer(java.lang.String, int)}.
	 */
	@Test
	void testLEHexFixer() {
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				String hex = Integer.toHexString(j);
				String input;
				String expectedOutput;
				if( i > 0) {
					int leftPadCount = i - 1;
					char[] leftPad = new char[leftPadCount];
					Arrays.fill(leftPad, '0');
					String leftPadString = new String(leftPad);
					char[] rightPad = new char[16 - i - 1];
					input = leftPadString + hex + "0";
					char padChar = (j > 7) ? 'F' : '0';
					Arrays.fill(rightPad, padChar);
					String rigthPadString = new String(rightPad);
					expectedOutput = input + rigthPadString;
					if(i % 2 == 0)
						expectedOutput = "0" + expectedOutput.substring(0, expectedOutput.length() - 1);
				} else {
					input = hex;
					expectedOutput = "0" + hex + "00000000000000";
				}
				String actualOutput = LittleEndian.LEHexFixer(input, 64);
				Assertions.assertEquals(expectedOutput, actualOutput);
				
			}
		}
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#hexFixer(java.lang.String, int)}.
	 */
	@Test
	void testHexFixer() {
		for(int i = 0; i < 16; i++) {
			char[] rightPad = new char[i];
			Arrays.fill(rightPad, '0');
			char[] leftPad = new char[16 - i - 1];
			String rightPadString = new String(rightPad);
			for(int j = 0; j < 16; j++) {
				String hex = Integer.toHexString(j);
				
				char padChar = (j > 7) ? 'F' : '0';
				Arrays.fill(leftPad, padChar);
				String leftPadString = new String(leftPad);
				String input = hex + rightPadString;
				String expectedOutput = leftPadString + hex + rightPadString;
				String actualOutput = LittleEndian.hexFixer(input, 64);
				Assertions.assertEquals(expectedOutput, actualOutput);
			}
		}
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateHex(boolean[], int)}.
	 */
	@Test
	void testGenerateHexBooleanArrayInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#getNibble(char)}.
	 */
	@Test
	void testGetNibble() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#getByte(java.lang.String)}.
	 */
	@Test
	void testGetByte() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#charContains(char[], char)}.
	 */
	@Test
	void testCharContains() {
		fail("Not yet implemented"); // TODO
	}

}
