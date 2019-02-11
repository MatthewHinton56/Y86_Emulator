package Sequential;
import java.util.Arrays;
import java.util.HashMap;

import Compilation.InstructionBuilder;

public class Instruction {

	public static final HashMap<String, String> BYTE_TO_FUNCTION = new HashMap<String, String>();
	public static final HashMap<String, String> NIBBLE_TO_REGISTER = new HashMap<String, String>();
	public static final HashMap<String, String> INSTRUCTION_TO_ARCHETYPE = new HashMap<String, String>();

	/**
	 * sets up maps and instruction archetypes
	 */
	static {
		generateMaps();
		generateInstructionArchetypes();
	}

	DoubleWord immediate;
	String rA, rB;
	DoubleWord valA, valB, valE, valM, valP, standardValPIncrement;
	// 0 - RS1Val, 1 - RS2Val, 2 - EVal, 3 - MVal
	String instruction;
	boolean memory, conditionMet;
	public boolean stop;
	public DoubleWord address;

	/**
	 * Creates an instruction using the instruction Array
	 * 
	 * @param instructionArray the array to process
	 * @param address          the address it was read from
	 */
	public Instruction(BYTE[] instructionArray, DoubleWord address) {
		this.address = address;
		String hexFunction = instructionArray[0].generateHex();
		instruction = BYTE_TO_FUNCTION.get(hexFunction);
		int immediateStart = (inArray(IMMEDIATE_SPECIAL_CASE, instruction)) ? 1 : 2;
		String hexRegister = null;
		try {
			hexRegister = instructionArray[1].generateHex();
		} catch (NullPointerException e) {
			System.out.println(Arrays.toString(instructionArray));
		}
		rA = NIBBLE_TO_REGISTER.get(hexRegister.substring(0, 1));
		rB = NIBBLE_TO_REGISTER.get(hexRegister.substring(1));
		String imm = "";
		for (int i = immediateStart; i <= immediateStart + 7; i++)
			imm += instructionArray[i];
		immediate = new DoubleWord(imm, true);
		memory = inArray(MEMORY_FUNCTIONS, instruction);
		if (inArray(InstructionBuilder.ONE_BYTE, instruction))
			standardValPIncrement = new DoubleWord(1);
		else if (inArray(InstructionBuilder.NINE_BYTE, instruction))
			standardValPIncrement = new DoubleWord(9);
		else if (inArray(InstructionBuilder.TEN_BYTE, instruction))
			standardValPIncrement = new DoubleWord(10);
		else
			standardValPIncrement = new DoubleWord(2);
		conditionMet = true;
	}

	/**
	 * Creates the archetypal form of each instruction
	 */
	private static void generateInstructionArchetypes() {
		INSTRUCTION_TO_ARCHETYPE.put("halt", "halt");
		INSTRUCTION_TO_ARCHETYPE.put("ret", "ret");
		INSTRUCTION_TO_ARCHETYPE.put("call", "call Dest");
		INSTRUCTION_TO_ARCHETYPE.put("ret", "ret");
		INSTRUCTION_TO_ARCHETYPE.put("nop", "nop");
		INSTRUCTION_TO_ARCHETYPE.put("rrmovq", "rrmovq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("cmovle", "cmovle rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("cmovl", "cmovl rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("cmove", "cmove rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("cmovne", "cmovne rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("cmovge", "cmovge rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("cmovg", "cmovg rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("irmovq", "irmovq V, rB");
		INSTRUCTION_TO_ARCHETYPE.put("rmmovq", "rmmovq rA, D(rB)");
		INSTRUCTION_TO_ARCHETYPE.put("rrmovq", "rmmovq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("mrmovq", "mrmovq D(rB), rA");
		INSTRUCTION_TO_ARCHETYPE.put("addq", "addq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("subq", "subq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("xorq", "xorq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("andq", "andq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("jle", "jle Dest");
		INSTRUCTION_TO_ARCHETYPE.put("jl", "jl Dest");
		INSTRUCTION_TO_ARCHETYPE.put("je", "je Dest");
		INSTRUCTION_TO_ARCHETYPE.put("jne", "jne Dest");
		INSTRUCTION_TO_ARCHETYPE.put("jge", "jge Dest");
		INSTRUCTION_TO_ARCHETYPE.put("jg", "jg Dest");
		INSTRUCTION_TO_ARCHETYPE.put("pushq", "pushq rA");
		INSTRUCTION_TO_ARCHETYPE.put("popq", "cmovge rA");
	}

	/**
	 * The various functions used to create instructions
	 */
	private static void generateMaps() {
		BYTE_TO_FUNCTION.put("00", "halt");
		BYTE_TO_FUNCTION.put("10", "nop");
		BYTE_TO_FUNCTION.put("20", "rrmovq");
		BYTE_TO_FUNCTION.put("21", "cmovle");
		BYTE_TO_FUNCTION.put("22", "cmovl");
		BYTE_TO_FUNCTION.put("23", "cmove");
		BYTE_TO_FUNCTION.put("24", "cmovne");
		BYTE_TO_FUNCTION.put("25", "cmovge");
		BYTE_TO_FUNCTION.put("26", "cmovg");
		BYTE_TO_FUNCTION.put("30", "irmovq");
		BYTE_TO_FUNCTION.put("40", "rmmovq");
		BYTE_TO_FUNCTION.put("50", "mrmovq");
		BYTE_TO_FUNCTION.put("60", "addq");
		BYTE_TO_FUNCTION.put("61", "subq");
		BYTE_TO_FUNCTION.put("62", "andq");
		BYTE_TO_FUNCTION.put("63", "xorq");
		BYTE_TO_FUNCTION.put("70", "jmp");
		BYTE_TO_FUNCTION.put("71", "jle");
		BYTE_TO_FUNCTION.put("72", "jl");
		BYTE_TO_FUNCTION.put("73", "je");
		BYTE_TO_FUNCTION.put("74", "jne");
		BYTE_TO_FUNCTION.put("75", "jge");
		BYTE_TO_FUNCTION.put("76", "jg");
		BYTE_TO_FUNCTION.put("80", "call");
		BYTE_TO_FUNCTION.put("90", "ret");
		BYTE_TO_FUNCTION.put("A0", "pushq");
		BYTE_TO_FUNCTION.put("B0", "popq");

		NIBBLE_TO_REGISTER.put("0", "%rax");
		NIBBLE_TO_REGISTER.put("1", "%rcx");
		NIBBLE_TO_REGISTER.put("2", "%rdx");
		NIBBLE_TO_REGISTER.put("3", "%rbx");
		NIBBLE_TO_REGISTER.put("4", "%rsp");
		NIBBLE_TO_REGISTER.put("5", "%rbp");
		NIBBLE_TO_REGISTER.put("6", "%rsi");
		NIBBLE_TO_REGISTER.put("7", "%rdi");
		NIBBLE_TO_REGISTER.put("8", "%r8");
		NIBBLE_TO_REGISTER.put("9", "%r9");
		NIBBLE_TO_REGISTER.put("A", "%r10");
		NIBBLE_TO_REGISTER.put("B", "%r11");
		NIBBLE_TO_REGISTER.put("C", "%r12");
		NIBBLE_TO_REGISTER.put("D", "%r13");
		NIBBLE_TO_REGISTER.put("E", "%r14");
		NIBBLE_TO_REGISTER.put("F", "No register");
	}

	/**
	 * Checks if a value is in an array
	 * 
	 * @param array the array to scan
	 * @param val   the value to search for
	 * @return if the val is in the array
	 */
	public static boolean inArray(String[] array, String val) {
		for (String s : array)
			if (s.equals(val))
				return true;
		return false;
	}

	public static final String[] IMMEDIATE_SPECIAL_CASE = { "call", "jmp", "jge", "jg", "je", "jne", "jl", "jle" };
	public static final String[] MEMORY_FUNCTIONS = { "rmmovq", "mrmovq", "call", "ret", "popq", "pushq" };

	/**
	 * Creates a display of the instruction, for printing
	 * 
	 * @return the string representation of the instruction
	 */
	public String buildDisplayInstruction() {
		String archetype = Instruction.INSTRUCTION_TO_ARCHETYPE.get(instruction);
		if (this.rA != null)
			archetype = archetype.replace("rA", this.rA);
		if (this.rB != null)
			archetype = archetype.replace("rB", this.rB);
		if (this.immediate != null)
			archetype = archetype.replace("Dest", "0x" + this.immediate.displayToString());
		if (this.immediate != null)
			archetype = archetype.replace("D", "0x" + this.immediate.displayToString());
		if (this.immediate != null)
			archetype = archetype.replace("V", "0x" + this.immediate.displayToString());
		return archetype;
	}
}
