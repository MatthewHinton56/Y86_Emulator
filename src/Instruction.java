import java.util.Arrays;
import java.util.HashMap;

public class Instruction {

	public static final HashMap<String, String> BYTE_TO_FUNCTION = new HashMap<String, String>();
	public static final HashMap<String, String> NIBBLE_TO_REGISTER = new HashMap<String, String>();
	public static final HashMap<String, String> INSTRUCTION_TO_ARCHETYPE = new HashMap<String, String>();
	static {
		generateMaps();
		generateInstructionArchetypes();
	}

	DoubleWord immediate;
	String rA, rB;
	DoubleWord valA, valB, valE, valM, valP, standardValPIncrement;
	//0 - RS1Val, 1 - RS2Val, 2 - EVal, 3 - MVal
	String instruction;
	boolean memory, conditionMet;
	public boolean stop;
	DoubleWord address;


	public Instruction(BYTE[] instructionArray, DoubleWord address) {
		this.address = address;
		String hexFunction = instructionArray[0].generateHex();
		instruction = BYTE_TO_FUNCTION.get(hexFunction);
		int immediateStart = (inArray(IMMEDIATE_SPECIAL_CASE, instruction)) ? 1 : 2;
		String hexRegister = null;
		try {
		 hexRegister = instructionArray[1].generateHex();
		}
		catch(NullPointerException e) {
			System.out.println(Arrays.toString(instructionArray));
		}
		rA = NIBBLE_TO_REGISTER.get(hexRegister.substring(0,1));
		rB = NIBBLE_TO_REGISTER.get(hexRegister.substring(1));
		String imm = "";
		for(int i = immediateStart; i <= immediateStart+7; i++)
			imm+= instructionArray[i];
		immediate = new DoubleWord(imm, true);
		memory = inArray(MEMORY_FUNCTIONS, instruction);
		if(inArray(InstructionBuilder.ONE_BYTE, instruction))
			standardValPIncrement = new DoubleWord(1);
		else if(inArray(InstructionBuilder.NINE_BYTE, instruction))
			standardValPIncrement = new DoubleWord(9);
		else if(inArray(InstructionBuilder.TEN_BYTE, instruction))
			standardValPIncrement = new DoubleWord(10);
		else 
			standardValPIncrement = new DoubleWord(2);
		conditionMet = true;
	}




	private static void generateInstructionArchetypes() {
		INSTRUCTION_TO_ARCHETYPE.put("HALT", "halt");
		INSTRUCTION_TO_ARCHETYPE.put("RET", "ret");
		INSTRUCTION_TO_ARCHETYPE.put("CALL", "call Dest");
		INSTRUCTION_TO_ARCHETYPE.put("RET", "ret");
		INSTRUCTION_TO_ARCHETYPE.put("NOP", "nop");
		INSTRUCTION_TO_ARCHETYPE.put("RRMOVQ", "rrmovq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("CMOVLE", "cmovle rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("CMOVL", "cmovl rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("CMOVE", "cmove rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("CMOVNE", "cmovne rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("CMOVGE", "cmovge rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("CMOVG", "cmovg rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("IRMOVQ", "irmovq V, rB");
		INSTRUCTION_TO_ARCHETYPE.put("RMMOVQ", "rmmovq rA, D(rB)");
		INSTRUCTION_TO_ARCHETYPE.put("MRMOVQ", "mrmovq D(rB), rA");
		INSTRUCTION_TO_ARCHETYPE.put("ADDQ", "addq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("SUBQ", "subq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("XORQ", "xorq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("ANDQ", "andq rA, rB");
		INSTRUCTION_TO_ARCHETYPE.put("RET", "ret");
		INSTRUCTION_TO_ARCHETYPE.put("JLE", "jle Dest");
		INSTRUCTION_TO_ARCHETYPE.put("JL", "jl Dest");
		INSTRUCTION_TO_ARCHETYPE.put("JE", "je Dest");
		INSTRUCTION_TO_ARCHETYPE.put("JNE", "jne Dest");
		INSTRUCTION_TO_ARCHETYPE.put("JGE", "jge Dest");
		INSTRUCTION_TO_ARCHETYPE.put("JG", "jg Dest");
		INSTRUCTION_TO_ARCHETYPE.put("PUSHQ", "pushq rA");
		INSTRUCTION_TO_ARCHETYPE.put("POPQ", "cmovge rA");
	}




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

	public static boolean inArray(String[] array, String val) {
		for(String s: array)
			if(s.equals(val))
				return true;
		return false;
	}

	public static final String[] IMMEDIATE_SPECIAL_CASE = {"call", "jmp", "jge", "jg", "je", "jne", "jl", "jle"}; 
	public static final String[] MEMORY_FUNCTIONS = {"rmmovq","mrmovq", "call", "ret", "popq","pushq"};


	public String buildDisplayInstruction() {
		String archetype = Instruction.INSTRUCTION_TO_ARCHETYPE.get(instruction);
		archetype = archetype.replace("rA", this.rA);
		archetype = archetype.replace("rB", this.rB);
		archetype = archetype.replace("Dest", this.immediate.displayToString());
		archetype = archetype.replace("D", this.immediate.displayToString());
		archetype = archetype.replace("V", this.immediate.displayToString());
		return archetype;
	}
}
