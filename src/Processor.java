import java.util.TreeMap;

public class Processor {

	public static final RegisterFile registerFile = new RegisterFile();
	public static Instruction currentInstruction;
	public static DoubleWord PC;
	public static String status = "HLT";
	public static TreeMap<String, DoubleWord> initialRegisterFile, stepBeforeReg, stepAfterReg, finalRegisterFile;
	public static TreeMap<Long, DoubleWord> initialMemory, stepBeforeMem, stepAfterMem, finalMemory;
	public static boolean initialized;
	public static Instruction completedInstruction;
	public static String exception;
	public static boolean exceptionGenerated;

	public static void fetch() {
		int pcInt = ((int)PC.calculateValueSigned());
		BYTE[] instructionArray = Memory.getInstruction(pcInt);
		currentInstruction = new Instruction(instructionArray, PC);
		currentInstruction.valP = new DoubleWord(ALU.IADD(PC.bitArray, currentInstruction.standardValPIncrement.bitArray));
		if(InstructionBuilder.getKey(Instruction.BYTE_TO_FUNCTION, currentInstruction.instruction) == null)
			status = "INV";
	}

	public static void decode() {
		currentInstruction.valA = registerFile.get(currentInstruction.rA);
		currentInstruction.valB = registerFile.get(currentInstruction.rB);
		String instruction = currentInstruction.instruction;
		if(instruction.equals("ret") || instruction.equals("pushq") || instruction.equals("popq") || instruction.equals("call")) {
			currentInstruction.valB = registerFile.get("%rsp");
		}
		if(instruction.equals("ret"))
			currentInstruction.valA = registerFile.get("%rsp");

	}

	public static void execute() {
		switch(currentInstruction.instruction) {
		case "halt":
			status = "HLT";
			break;
		case "mrmovq":	
		case "rmmovq":
			currentInstruction.valE = new DoubleWord(ALU.IADD(currentInstruction.immediate.bitArray, currentInstruction.valB.bitArray));
			break;
		case "addq":
			currentInstruction.valE = new DoubleWord(ALU.ADD(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "subq":
			currentInstruction.valE = new DoubleWord(ALU.SUB(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "andq":
			currentInstruction.valE = new DoubleWord(ALU.AND(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "xorq":
			currentInstruction.valE = new DoubleWord(ALU.XOR(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "pushq":	
		case "call":
			currentInstruction.valE = new DoubleWord(ALU.DECREMENTEIGHT(currentInstruction.valB.bitArray));
			break;
		case "ret":	
		case "popq":
			currentInstruction.valE = new DoubleWord(ALU.INCREMENTEIGHT(currentInstruction.valB.bitArray));
			break;
		case "jle":
		case "cmovle":
			currentInstruction.conditionMet = (ALU.SF() ^ ALU.OF()) || ALU.ZF();
			break;
		case "jl":
		case "cmovl":
			currentInstruction.conditionMet = ALU.SF() ^ ALU.OF();
			break;	
		case "je":
		case "cmove":
			currentInstruction.conditionMet = ALU.ZF();
			break;		
		case "jne":
		case "cmovne":
			currentInstruction.conditionMet = !ALU.ZF();
			break;	
		case "jge":
		case "cmovge":
			currentInstruction.conditionMet = !(ALU.SF() ^ ALU.OF());
			break;	
		case "jg":
		case "cmovg":
			currentInstruction.conditionMet = !(ALU.SF() ^ ALU.OF()) && !ALU.ZF();
			break;		
		}
	}

	public static void memory() {
		if(currentInstruction.memory) {
			long address;
			switch(currentInstruction.instruction) {
			case "pushq":
			case "rmmovq":
				address = currentInstruction.valE.calculateValueSigned();//since map is used, negative are allowed, rather than dealing with signed/unsigned, 
				//as it essentially the same value.
				Memory.storeDoubleWord(address, currentInstruction.valA);
				break;
			case "mrmovq":
				address = currentInstruction.valE.calculateValueSigned();
				currentInstruction.valM = Memory.loadDoubleWord(address);
				break;
			case "popq":
				address = currentInstruction.valB.calculateValueSigned();
				currentInstruction.valM = Memory.loadDoubleWord(address);
				break;
			case "call":
				address = currentInstruction.valE.calculateValueSigned();
				Memory.storeDoubleWord(address, currentInstruction.valP);
				break;
			case "ret":
				address = currentInstruction.valB.calculateValueSigned();//since map is used, negative are allowed, rather than dealing with signed/unsigned, 
				//as it essentially the same value.
				currentInstruction.valM = Memory.loadDoubleWord(address);
				break;
			}
		}
	}

	public static void writeBack() {
		switch(currentInstruction.instruction) {
		case "addq":
		case "subq":
		case "xorq":
		case "andq":
			registerFile.set(currentInstruction.rB, currentInstruction.valE);
			break;
		case "mrmovq":
			registerFile.set(currentInstruction.rA, currentInstruction.valM);
			break;
		case "irmovq":
			registerFile.set(currentInstruction.rB, currentInstruction.immediate);
			break;
		case "rrmovq":
		case "cmovl":
		case "cmovle":
		case "cmove":
		case "cmovne":
		case "cmovg":
		case "cmovge":
			DoubleWord dw = (currentInstruction.conditionMet) ? currentInstruction.valA : currentInstruction.valB;
			registerFile.set(currentInstruction.rB, dw);
			break;
		case "popq":
		case "ret":
			registerFile.set(currentInstruction.rA, currentInstruction.valM);
		case "call":
		case "pushq": 
			registerFile.set("%rsp", currentInstruction.valE);
			break;
		}
	}

	public static void pc() {
		switch(currentInstruction.instruction) {
		case "call":
			currentInstruction.valP = currentInstruction.immediate;
			break;
		case "ret":
			currentInstruction.valP = currentInstruction.valM;
			break;
		case "jmp":
		case "jle":
		case "jl":
		case "je":
		case "jne":
		case "jge":
		case "jg":
			currentInstruction.valP = (currentInstruction.conditionMet) ? currentInstruction.immediate : currentInstruction.valP;
			break;
		}
		PC = currentInstruction.valP;
		completedInstruction = currentInstruction;
	}

	public static void initialize() {
		if(Compiler.compiled) {
			Processor.PC = new DoubleWord(Long.parseLong(Compiler.start_address,16));
			for(long l: Compiler.COMPILED_CONSTANTS.keySet())
				Memory.storeDoubleWord(l, Compiler.COMPILED_CONSTANTS.get(l));
			for(long l: Compiler.COMPILED_INSTRUCTIONS.keySet())
				Memory.storeInstruction(l, Compiler.COMPILED_INSTRUCTIONS.get(l));
			status = "AOK";
			registerFile.reset();
			Processor.initialMemory = Memory.createImage();
			Processor.initialRegisterFile = Processor.registerFile.createImage();
			finalMemory = stepBeforeMem = stepAfterMem = null;
			finalRegisterFile = stepBeforeReg = stepAfterReg = null;
			initialized = true;
			exceptionGenerated = false;
		} else {
			status = "HLT";
		}
	}

	public static void step() {
		if(status.equals("AOK")) {
			fetch();
			if(status.equals("AOK")) {
				decode();
				execute();
				memory();
				writeBack();
				pc();
			}
		}
		if(status.equals("AOK")) {
			Processor.stepAfterMem = Memory.createImage();
			Processor.stepAfterReg = Processor.registerFile.createImage();
		} else {
			Processor.stepBeforeMem = Processor.finalMemory = Memory.createImage();
			Processor.stepBeforeReg = Processor.finalRegisterFile = Processor.registerFile.createImage();
		}
	}

	public static void run() {
		while(status.equals("AOK")) {
			fetch();
			if(status.equals("AOK")) {
				decode();
				execute();
				try 
				{
					memory();
				} catch(MemoryException e) {
					exception = e.getMessage();
					exceptionGenerated = true;
					status = "ADR";
				}
				if(status.equals("AOK")) {
					writeBack();
					pc();
				}
			}
		}
		Processor.finalMemory = Memory.createImage();
		Processor.finalRegisterFile = Processor.registerFile.createImage();
	}

	public static void clear() {
		Memory.memory.clear();
		status = "HLT";
		registerFile.reset();
		Processor.PC = new DoubleWord(0);
	}

}
