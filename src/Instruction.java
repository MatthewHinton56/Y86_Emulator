import java.util.Arrays;
import java.util.HashMap;

public class Instruction {

	public static final HashMap<String, String> BYTE_TO_FUNCTION = new HashMap<String, String>();
	public static final HashMap<String, String> NIBBLE_TO_REGISTER = new HashMap<String, String>();
	static {
		generateMaps();
	}

	DoubleWord immediate;
	String rA, rB;
	DoubleWord valA, valB, valE, valM, valP, standardValPIncrement;
	//0 - RS1Val, 1 - RS2Val, 2 - EVal, 3 - MVal
	String instruction;
	boolean memory, conditionMet;
	public boolean stop;


	public Instruction(BYTE[] instructionArray) {
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
}
