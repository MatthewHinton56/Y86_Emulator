import java.util.HashMap;

public class InstructionBuilder {

	public static String[] getInstruction(String instruction, String rA, String rB, DoubleWord immediate) {
		if(Instruction.inArray(ONE_BYTE, instruction))
			return instructionOneByte(instruction);
		if(Instruction.inArray(TEN_BYTE, instruction))
			return instructionTenByte(instruction, rA, rB, immediate);
		if(Instruction.inArray(NINE_BYTE, instruction))
			return instructionNineByte(instruction, immediate);
		return instructionTwoByte(instruction, rA, rB);
	}

	public static String getKey(HashMap<String, String> map, String val) {
		for(String key: map.keySet())
			if(map.get(key).equals(val))
				return key;
		return null;
	}

	public static String[] instructionOneByte(String instruction) {
		String instructionArray[] = new String[1];
		instructionArray[0] = getKey(Instruction.BYTE_TO_FUNCTION, instruction);
		return instructionArray;
	}

	public static String[] instructionTwoByte(String instruction, String rA, String rB) {
		String instructionArray[] = new String[2];
		instructionArray[0] = getKey(Instruction.BYTE_TO_FUNCTION, instruction);
		String registerHex =  getKey(Instruction.NIBBLE_TO_REGISTER, rA) + getKey(Instruction.NIBBLE_TO_REGISTER, rB);
		instructionArray[1] =  registerHex;
		return instructionArray;
	}

	public static String[] instructionNineByte(String instruction, DoubleWord immediate) {
		String instructionArray[] = new String[9];
		instructionArray[0] = getKey(Instruction.BYTE_TO_FUNCTION, instruction);
		for(int pos = 1; pos < 9; pos++) {
			instructionArray[pos] = immediate.getBYTE(pos-1).generateHex(); 
		}
		return instructionArray;
	}

	public static String[] instructionTenByte(String instruction, String rA, String rB, DoubleWord immediate) {
		String instructionArray[] = new String[10];
		instructionArray[0] = getKey(Instruction.BYTE_TO_FUNCTION, instruction);
		String registerHex =  getKey(Instruction.NIBBLE_TO_REGISTER, rA) + getKey(Instruction.NIBBLE_TO_REGISTER, rB);
		instructionArray[1] =  registerHex;
		for(int pos = 2; pos <= 9; pos++) {
			instructionArray[pos] = immediate.getBYTE(pos-2).generateHex(); 
		}
		return instructionArray;
	}

	public static final String[] ONE_BYTE = {"halt", "nop", "ret"};
	public static final String[] NINE_BYTE = {"call", "jmp", "jl", "jg", "je", "jne", "jle", "jge"};
	public static final String[] TEN_BYTE = {"irmovq", "mrmovq", "rmmovq"};

}
