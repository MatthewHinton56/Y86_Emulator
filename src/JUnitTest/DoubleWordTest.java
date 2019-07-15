/**
 * 
 */
package JUnitTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import BaseEmulator.DoubleWord;
import BaseEmulator.LittleEndian;
import BaseEmulator.Word;;

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
		Assertions.assertArrayEquals(array, dw.bitArray);
	}

	/**
	 * Test method for {@link BaseEmulator.DoubleWord#DoubleWord(java.lang.String, boolean)}.
	 */
	@Test
	void testDoubleWordStringBoolean() {
		DoubleWord dwLE = new DoubleWord("1032547698BADCFE", true);
		DoubleWord dw = new DoubleWord("FEDCBA9876543210", false);
		Assertions.assertArrayEquals(dwLE.bitArray, dw.bitArray);
	}

	/**
	 * Test method for {@link BaseEmulator.DoubleWord#DoubleWord(boolean[])}.
	 */
	@Test
	void testDoubleWordBooleanArray() {
		boolean[] test = new boolean[64];
		for(int i = 0; i < 64; i++)
			test[i] = Math.random() > .5;
		DoubleWord dw = new DoubleWord(test);
		Assertions.assertArrayEquals(test, dw.bitArray);
	}

	/**
	 * Test method for {@link BaseEmulator.DoubleWord#DoubleWord(long)}.
	 */
	@Test
	void testDoubleWordLong() {
		DoubleWord negativeOne = new DoubleWord(-1);
		DoubleWord zero = new DoubleWord(0);
		DoubleWord longMax = new DoubleWord(Long.MAX_VALUE);
		DoubleWord longMin = new DoubleWord(Long.MIN_VALUE);
		
		Assertions.assertEquals(-1, negativeOne.calculateValueSigned());
		Assertions.assertEquals(0, zero.calculateValueSigned());
		Assertions.assertEquals(Long.MAX_VALUE, longMax.calculateValueSigned());
		Assertions.assertEquals(Long.MIN_VALUE, longMin.calculateValueSigned());
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		DoubleWord negativeOne = new DoubleWord(-1);
		DoubleWord zero = new DoubleWord(0);
		Assertions.assertEquals(negativeOne, negativeOne);
		Assertions.assertNotEquals(negativeOne, zero);
		
		Word wZero = new Word();
		Assertions.assertNotEquals(wZero, zero);
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#calculateValueSigned()}.
	 */
	@Test
	void testCalculateValueSigned() {
		DoubleWord negativeOne = new DoubleWord("FFFFFFFFFFFFFFFF", false);
		DoubleWord zero = new DoubleWord("0000000000000000", false);
		DoubleWord longMax = new DoubleWord("7FFFFFFFFFFFFFFF", false);
		DoubleWord longMin = new DoubleWord("8000000000000000", false);
		
		Assertions.assertEquals(-1, negativeOne.calculateValueSigned());
		Assertions.assertEquals(0, zero.calculateValueSigned());
		Assertions.assertEquals(Long.MAX_VALUE, longMax.calculateValueSigned());
		Assertions.assertEquals(Long.MIN_VALUE, longMin.calculateValueSigned());
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#calculateValueUnSigned()}.
	 */
	@Test
	void testCalculateValueUnSigned() {
		DoubleWord negativeOne = new DoubleWord("FFFFFFFFFFFFFFFF", false);
		DoubleWord zero = new DoubleWord("0000000000000000", false);
		DoubleWord longMax = new DoubleWord("7FFFFFFFFFFFFFFF", false);
		DoubleWord longMin = new DoubleWord("8000000000000000", false);
		assertEquals(negativeOne.calculateValueUnSigned(), "18446744073709551615");
		assertEquals(zero.calculateValueUnSigned(), "0");
		assertEquals(longMax.calculateValueUnSigned(), "9223372036854775807");
		assertEquals(longMin.calculateValueUnSigned(), "9223372036854775808");
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateBitString()}.
	 */
	@Test
	void testGenerateBitString() {
		DoubleWord negativeOne = new DoubleWord("FFFFFFFFFFFFFFFF", false);
		DoubleWord zero = new DoubleWord("0000000000000000", false);
		DoubleWord longMax = new DoubleWord("7FFFFFFFFFFFFFFF", false);
		DoubleWord longMin = new DoubleWord("8000000000000000", false);
		DoubleWord dw = new DoubleWord("FEDCBA9876543210", false);
		assertEquals("1111111111111111111111111111111111111111111111111111111111111111", negativeOne.generateBitString());
		assertEquals("0000000000000000000000000000000000000000000000000000000000000000", zero.generateBitString());
		assertEquals("0111111111111111111111111111111111111111111111111111111111111111", longMax.generateBitString());
		assertEquals("1000000000000000000000000000000000000000000000000000000000000000", longMin.generateBitString());
		assertEquals("1111111011011100101110101001100001110110010101000011001000010000", dw.generateBitString());
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateBitArray()}.
	 */
	@Test
	void testGenerateBitArray() {
		boolean[] test = new boolean[64];
		for(int i = 0; i < 64; i++)
			test[i] = Math.random() > .5;
		DoubleWord dw = new DoubleWord(test);
		Assertions.assertArrayEquals(test, dw.generateBitArray());
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateHex()}.
	 */
	@Test
	void testGenerateHex() {
		DoubleWord negativeOne = new DoubleWord(-1);
		DoubleWord zero = new DoubleWord(0);
		DoubleWord longMax = new DoubleWord(Long.MAX_VALUE);
		DoubleWord longMin = new DoubleWord(Long.MIN_VALUE);
		DoubleWord dw = new DoubleWord(-81985529216486896l);
		
		assertEquals("FFFFFFFFFFFFFFFF", negativeOne.generateHex());
		assertEquals("0000000000000000", zero.generateHex());
		assertEquals("7FFFFFFFFFFFFFFF", longMax.generateHex());
		assertEquals("8000000000000000", longMin.generateHex());
		assertEquals("FEDCBA9876543210", dw.generateHex());
		
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateHexLE()}.
	 */
	@Test
	void testGenerateHexLE() {
		DoubleWord negativeOne = new DoubleWord(-1);
		DoubleWord zero = new DoubleWord(0);
		DoubleWord longMax = new DoubleWord(Long.MAX_VALUE);
		DoubleWord longMin = new DoubleWord(Long.MIN_VALUE);
		DoubleWord dw = new DoubleWord(-81985529216486896l);
		
		assertEquals("FFFFFFFFFFFFFFFF", negativeOne.generateHexLE());
		assertEquals("0000000000000000", zero.generateHexLE());
		assertEquals("FFFFFFFFFFFFFF7F", longMax.generateHexLE());
		assertEquals("0000000000000080", longMin.generateHexLE());
		assertEquals("1032547698BADCFE", dw.generateHexLE());
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#generateBitStringLE()}.
	 */
	@Test
	void testGenerateBitStringLE() {
		DoubleWord negativeOne = new DoubleWord("FFFFFFFFFFFFFFFF", false);
		DoubleWord zero = new DoubleWord("0000000000000000", false);
		DoubleWord longMax = new DoubleWord("7FFFFFFFFFFFFFFF", false);
		DoubleWord longMin = new DoubleWord("8000000000000000", false);
		DoubleWord dw = new DoubleWord("FEDCBA9876543210", false);
		
		assertEquals("1111111111111111111111111111111111111111111111111111111111111111", negativeOne.generateBitStringLE());
		assertEquals("0000000000000000000000000000000000000000000000000000000000000000", zero.generateBitStringLE());
		assertEquals("1111111111111111111111111111111111111111111111111111111101111111", longMax.generateBitStringLE());
		assertEquals("0000000000000000000000000000000000000000000000000000000010000000", longMin.generateBitStringLE());
		assertEquals("0001000000110010010101000111011010011000101110101101110011111110", dw.generateBitStringLE());
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#getSign()}.
	 */
	@Test
	void testGetSign() {
		DoubleWord negativeOne = new DoubleWord("FFFFFFFFFFFFFFFF", false);
		DoubleWord zero = new DoubleWord("0000000000000000", false);
		DoubleWord longMax = new DoubleWord("7FFFFFFFFFFFFFFF", false);
		assertEquals(true, negativeOne.getSign());
		assertEquals(false, zero.getSign());
		assertEquals(false, longMax.getSign());
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#toString()}.
	 */
	@Test
	void testToString() {
		DoubleWord negativeOne = new DoubleWord(-1);
		DoubleWord zero = new DoubleWord(0);
		DoubleWord longMax = new DoubleWord(Long.MAX_VALUE);
		DoubleWord longMin = new DoubleWord(Long.MIN_VALUE);
		DoubleWord dw = new DoubleWord(-81985529216486896l);
		
		assertEquals("FFFFFFFFFFFFFFFF", negativeOne.toString());
		assertEquals("0000000000000000", zero.toString());
		assertEquals("7FFFFFFFFFFFFFFF", longMax.toString());
		assertEquals("8000000000000000", longMin.toString());
		assertEquals("FEDCBA9876543210", dw.toString());
	}

	/**
	 * Test method for {@link BaseEmulator.LittleEndian#displayToString()}.
	 */
	@Test
	void testDisplayToString() {
		DoubleWord negativeOne = new DoubleWord(-1);
		DoubleWord zero = new DoubleWord(0);
		DoubleWord longMax = new DoubleWord(Long.MAX_VALUE);
		DoubleWord longMin = new DoubleWord(Long.MIN_VALUE);
		DoubleWord dw = new DoubleWord(-81985529216486896l);
		DoubleWord half = new DoubleWord("00000000FFFFFFFF", false);
		
		assertEquals("FFFFFFFFFFFFFFFF", negativeOne.displayToString());
		assertEquals("0", zero.displayToString());
		assertEquals("7FFFFFFFFFFFFFFF", longMax.displayToString());
		assertEquals("8000000000000000", longMin.displayToString());
		assertEquals("FEDCBA9876543210", dw.displayToString());
		assertEquals("FFFFFFFF", half.displayToString());
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
		DoubleWord dw = new DoubleWord("FEDCBA9876543210", false);
		assertEquals("10", LittleEndian.generateHex(dw.bitArray, 0));
		assertEquals("21", LittleEndian.generateHex(dw.bitArray, 4));
		assertEquals("32", LittleEndian.generateHex(dw.bitArray, 8));
		assertEquals("54", LittleEndian.generateHex(dw.bitArray, 16));
		assertEquals("76", LittleEndian.generateHex(dw.bitArray, 24));
		assertEquals("98", LittleEndian.generateHex(dw.bitArray, 32));
		assertEquals("BA", LittleEndian.generateHex(dw.bitArray, 40));
		assertEquals("DC", LittleEndian.generateHex(dw.bitArray, 48));
		assertEquals("FE", LittleEndian.generateHex(dw.bitArray, 56));
	}

}
