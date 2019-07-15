/**
 * 
 */
package JUnitTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import BaseEmulator.DoubleWord;
import BaseEmulator.RegisterFile;

/**
 * @author Matth
 *
 */
class RegisterFileTest {

	/**
	 * Test method for {@link BaseEmulator.RegisterFile#RegisterFile()}.
	 */
	@Test
	void testRegisterFile() {
		Set<String> keySet = new HashSet<String>();
		RegisterFile file = new RegisterFile();
		keySet.add("%rax");
		keySet.add("%rcx");
		keySet.add("%rdx");
		keySet.add("%rbx");
		keySet.add("%rsp");
		keySet.add("%rbp");
		keySet.add("%rsi");
		keySet.add("%rdi");
		keySet.add("%r8");
		keySet.add("%r9");
		keySet.add("%r10");
		keySet.add("%r11");
		keySet.add("%r12");
		keySet.add("%r13");
		keySet.add("%r14");
		Assertions.assertEquals(keySet, file.keySet());
	}

	/**
	 * Test method for {@link BaseEmulator.RegisterFile#set(java.lang.String, BaseEmulator.DoubleWord)}.
	 */
	@Test
	void testSet() {
		RegisterFile file = new RegisterFile();
		file.set("%rax", new DoubleWord(1));
		file.set("fake register", new DoubleWord(4));
		Set<String> keySet = new HashSet<String>();
		keySet.add("%rax");
		keySet.add("%rcx");
		keySet.add("%rdx");
		keySet.add("%rbx");
		keySet.add("%rsp");
		keySet.add("%rbp");
		keySet.add("%rsi");
		keySet.add("%rdi");
		keySet.add("%r8");
		keySet.add("%r9");
		keySet.add("%r10");
		keySet.add("%r11");
		keySet.add("%r12");
		keySet.add("%r13");
		keySet.add("%r14");
		Assertions.assertEquals(keySet, file.keySet());
		assertEquals(new DoubleWord(1), file.get("%rax"));
		
		
	}

	/**
	 * Test method for {@link BaseEmulator.RegisterFile#reset()}.
	 */
	@Test
	void testReset() {
		RegisterFile file = new RegisterFile();
		file.set("%rax", new DoubleWord(1));
		file.set("%r14", new DoubleWord(1213));
		file.set("%rbx", new DoubleWord(1441));
		file.set("%rdx", new DoubleWord("42", false));
		file.set("%rcx", new DoubleWord(0x54));
		file.reset();
		Set<String> keySet = new HashSet<String>();
		keySet.add("%rax");
		keySet.add("%rcx");
		keySet.add("%rdx");
		keySet.add("%rbx");
		keySet.add("%rsp");
		keySet.add("%rbp");
		keySet.add("%rsi");
		keySet.add("%rdi");
		keySet.add("%r8");
		keySet.add("%r9");
		keySet.add("%r10");
		keySet.add("%r11");
		keySet.add("%r12");
		keySet.add("%r13");
		keySet.add("%r14");
		Assertions.assertEquals(keySet, file.keySet());
		for(String register: file.keySet()) {
			assertEquals(new DoubleWord(), file.get(register));
		}
	}

	/**
	 * Test method for {@link BaseEmulator.RegisterFile#createImage()}.
	 */
	@Test
	void testCreateImage() {
		RegisterFile file = new RegisterFile();
		TreeMap<String, DoubleWord> imageNull = file.createImage();
		
		TreeMap<String, DoubleWord> expectedImage = new TreeMap<String, DoubleWord>();
		expectedImage.put("%rax", new DoubleWord());
		expectedImage.put("%rcx", new DoubleWord());
		expectedImage.put("%rdx", new DoubleWord());
		expectedImage.put("%rbx", new DoubleWord());
		expectedImage.put("%rsp", new DoubleWord());
		expectedImage.put("%rbp", new DoubleWord());
		expectedImage.put("%rsi", new DoubleWord());
		expectedImage.put("%rdi", new DoubleWord());
		expectedImage.put("%r8", new DoubleWord());
		expectedImage.put("%r9", new DoubleWord());
		expectedImage.put("%r10", new DoubleWord());
		expectedImage.put("%r11", new DoubleWord());
		expectedImage.put("%r12", new DoubleWord());
		expectedImage.put("%r13", new DoubleWord());
		expectedImage.put("%r14", new DoubleWord());
		
		assertEquals(imageNull, expectedImage);
		
		file.set("%rax", new DoubleWord(1));
		file.set("%r14", new DoubleWord(1213));
		file.set("%rbx", new DoubleWord(1441));
		file.set("%rdx", new DoubleWord("42", false));
		file.set("%rcx", new DoubleWord(0x54));
		
		expectedImage.put("%rax", new DoubleWord(1));
		expectedImage.put("%r14", new DoubleWord(1213));
		expectedImage.put("%rbx", new DoubleWord(1441));
		expectedImage.put("%rdx", new DoubleWord("42", false));
		expectedImage.put("%rcx", new DoubleWord(0x54));
		
		TreeMap<String, DoubleWord> imageAfter = file.createImage();
		assertEquals(imageAfter, expectedImage);
		
	}

	/**
	 * Test method for {@link BaseEmulator.RegisterFile#getDif(java.util.TreeMap, java.util.TreeMap)}.
	 */
	@Test
	void testGetDif() {
		RegisterFile file = new RegisterFile();
		TreeMap<String, DoubleWord> imageNull = file.createImage();
	
		
		file.set("%rax", new DoubleWord(1));
		file.set("%r14", new DoubleWord(1213));
		file.set("%rbx", new DoubleWord(1441));
		file.set("%rdx", new DoubleWord("42", false));
		file.set("%rcx", new DoubleWord(0x54));
		
		TreeMap<String, DoubleWord> imageAfter = file.createImage();
		ArrayList<String> diffOne = RegisterFile.getDif(imageNull, imageAfter);
		ArrayList<String> diffOneExpected = new ArrayList<String>();
		diffOneExpected.add("%rax");
		diffOneExpected.add("%r14");
		diffOneExpected.add("%rbx");
		diffOneExpected.add("%rdx");
		diffOneExpected.add("%rcx");
		diffOneExpected.sort(null);
		assertEquals(diffOneExpected, diffOne);
		file.set("%rax", new DoubleWord());
		file.set("%r13", new DoubleWord(1214));
		
		TreeMap<String, DoubleWord> imageAfterTwo = file.createImage();
		
		ArrayList<String> diffTwoExpected = new ArrayList<String>();
		diffTwoExpected.add("%rax");
		diffTwoExpected.add("%r13");
		diffTwoExpected.sort(null);
		ArrayList<String> diffTwo = RegisterFile.getDif(imageAfter, imageAfterTwo);	
		assertEquals(diffTwoExpected, diffTwo);
		
	}

}
