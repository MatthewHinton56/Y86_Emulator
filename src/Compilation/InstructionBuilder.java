package Compilation;
import java.util.HashMap;

import BaseEmulator.DoubleWord;
import BaseEmulator.Instruction;

public class InstructionBuilder {

	/**
	 * gets an instruction based on a specific compile line
	 * 
	 * @param instruction the instruction to be compiled
	 * @param rA          input register A
	 * @param rB          input register B
	 * @param immediate   the immedaite value of the instruction
	 * @param line        the line that is being process
	 * @return the compiled instruction
	 */
	public static String[] getInstruction(String instruction, String rA, String rB, DoubleWord immediate, String line) {
		if (Instruction.inArray(ONE_BYTE, instruction))
			return instructionOneByte(instruction);
		if (Instruction.inArray(TEN_BYTE, instruction))
			return instructionTenByte(instruction, rA, rB, immediate, line);
		if (Instruction.inArray(NINE_BYTE, instruction))
			return instructionNineByte(instruction, immediate);
		return instructionTwoByte(instruction, rA, rB, line);
	}

	/**
	 * Gets a key out of a key - val pair
	 * 
	 * @param map the map to be examined
	 * @param val the val to look for
	 * @return the key associated with val
	 */
	public static String getKey(HashMap<String, String> map, String val) {
		for (String key : map.keySet())
			if (map.get(key).equals(val))
				return key;
		return null;
	}

	/**
	 * Creates a one Byte instruction string
	 * 
	 * @param instruction the instruction to be processed
	 * @return the instruction to be returned
	 */
	public static String[] instructionOneByte(String instruction) {
		String instructionArray[] = new String[1];
		instructionArray[0] = getKey(Instruction.BYTE_TO_FUNCTION, instruction);
		return instructionArray;
	}

	/**
	 * Creates a two byte instruction using a two register input
	 * 
	 * @param instruction the instruction to be processed
	 * @param rA          register input A
	 * @param rB          register input B
	 * @param line        the line being processed
	 * @return the instruction to be returned
	 */
	public static String[] instructionTwoByte(String instruction, String rA, String rB, String line) {
		String instructionArray[] = new String[2];
		instructionArray[0] = getKey(Instruction.BYTE_TO_FUNCTION, instruction);
		String registerHex = getKey(Instruction.NIBBLE_TO_REGISTER, rA) + getKey(Instruction.NIBBLE_TO_REGISTER, rB);
		if (getKey(Instruction.NIBBLE_TO_REGISTER, rA) == null)
			throw new IllegalArgumentException("Invalid instruction argument - Invalid register:" + rA + "\n"
					+ "Error occured on the line: " + line);
		if (getKey(Instruction.NIBBLE_TO_REGISTER, rB) == null)
			throw new IllegalArgumentException("Invalid instruction argument - Invalid register:" + rB + "\n"
					+ "Error occured on the line: " + line);
		instructionArray[1] = registerHex;
		return instructionArray;
	}

	/**
	 * Creates a nine byte instruction using an immediate
	 * 
	 * @param instruction the instruction being processed
	 * @param immediate   immediate input
	 * @return the instruction to be returned
	 */
	public static String[] instructionNineByte(String instruction, DoubleWord immediate) {
		String instructionArray[] = new String[9];
		instructionArray[0] = getKey(Instruction.BYTE_TO_FUNCTION, instruction);
		for (int pos = 1; pos < 9; pos++) {
			instructionArray[pos] = immediate.getBYTE(pos - 1).generateHex();
		}
		return instructionArray;
	}

	/**
	 * Creates a ten byte instruction using an immediate and registers
	 * 
	 * @param instruction
	 * @param rA          register input A
	 * @param rB          register input B
	 * @param immediate   immediate input
	 * @param line        the line being processed
	 * @return the instruction to be returned
	 */
	public static String[] instructionTenByte(String instruction, String rA, String rB, DoubleWord immediate,
			String line) {
		String instructionArray[] = new String[10];
		instructionArray[0] = getKey(Instruction.BYTE_TO_FUNCTION, instruction);
		if (getKey(Instruction.NIBBLE_TO_REGISTER, rA) == null)
			throw new IllegalArgumentException("Invalid instruction argument - Invalid register:" + rA + "\n"
					+ "Error occured on the line: " + line);
		if (getKey(Instruction.NIBBLE_TO_REGISTER, rB) == null)
			throw new IllegalArgumentException("Invalid instruction argument - Invalid register:" + rB + "\n"
					+ "Error occured on the line: " + line);
		String registerHex = getKey(Instruction.NIBBLE_TO_REGISTER, rA) + getKey(Instruction.NIBBLE_TO_REGISTER, rB);
		instructionArray[1] = registerHex;
		for (int pos = 2; pos <= 9; pos++) {
			instructionArray[pos] = immediate.getBYTE(pos - 2).generateHex();
		}
		return instructionArray;
	}

	public static final String[] ONE_BYTE = { "halt", "nop", "ret" };
	public static final String[] NINE_BYTE = { "call", "jmp", "jl", "jg", "je", "jne", "jle", "jge" };
	public static final String[] TEN_BYTE = { "irmovq", "mrmovq", "rmmovq" };

}
