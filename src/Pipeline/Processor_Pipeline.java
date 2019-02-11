package Pipeline;

import java.util.TreeMap;

import BaseEmulator.ALU;
import BaseEmulator.BYTE;
import BaseEmulator.DoubleWord;
import BaseEmulator.Instruction;
import BaseEmulator.Memory;
import BaseEmulator.MemoryException;
import BaseEmulator.RegisterFile;
import Compilation.Compiler;
import Compilation.InstructionBuilder;

public class Processor_Pipeline {

	public static final RegisterFile registerFile = new RegisterFile();
	public static Instruction currentInstruction;
	public static DoubleWord PC = new DoubleWord(0);
	public static String status = "HLT";
	public static TreeMap<String, DoubleWord> initialRegisterFile, stepBeforeReg, stepAfterReg, finalRegisterFile;
	public static TreeMap<Long, DoubleWord> initialMemory, stepBeforeMem, stepAfterMem, finalMemory;
	public static boolean initialized;
	public static Instruction completedInstruction;
	public static String exception;
	public static boolean exceptionGenerated;

	/**
	 * Fetches the next instruction to process
	 */
	public static void fetch() {
		int pcInt = ((int) PC.calculateValueSigned());
		BYTE[] instructionArray = Memory.getInstruction(pcInt);
		currentInstruction = new Instruction(instructionArray, PC);
		currentInstruction.valP = new DoubleWord(
				ALU.IADD(PC.bitArray, currentInstruction.standardValPIncrement.bitArray));
		if (InstructionBuilder.getKey(Instruction.BYTE_TO_FUNCTION, currentInstruction.instruction) == null)
			status = "INV";
	}

	/**
	 * Decodes and assigns register values into current instruction
	 */
	public static void decode() {
		currentInstruction.valA = registerFile.get(currentInstruction.rA);
		currentInstruction.valB = registerFile.get(currentInstruction.rB);
		String instruction = currentInstruction.instruction;
		if (instruction.equals("ret") || instruction.equals("pushq") || instruction.equals("popq")
				|| instruction.equals("call")) {
			currentInstruction.valB = registerFile.get("%rsp");
		}
		if (instruction.equals("ret"))
			currentInstruction.valA = registerFile.get("%rsp");

	}

	/**
	 * Executes the current instruction
	 */
	public static void execute() {
		switch (currentInstruction.instruction) {
		case "halt":
			status = "HLT";
			break;
		case "mrmovq":
		case "rmmovq":
			currentInstruction.valE = new DoubleWord(
					ALU.IADD(currentInstruction.immediate.bitArray, currentInstruction.valB.bitArray));
			break;
		case "addq":
			currentInstruction.valE = new DoubleWord(
					ALU.ADD(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "subq":
			currentInstruction.valE = new DoubleWord(
					ALU.SUB(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "andq":
			currentInstruction.valE = new DoubleWord(
					ALU.AND(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "xorq":
			currentInstruction.valE = new DoubleWord(
					ALU.XOR(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
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

	/**
	 * Performs memory operation if necessary for current instruction
	 */
	public static void memory() {
		if (currentInstruction.memory) {
			long address;
			switch (currentInstruction.instruction) {
			case "pushq":
			case "rmmovq":
				address = currentInstruction.valE.calculateValueSigned();// since map is used, negative are allowed,
																			// rather than dealing with signed/unsigned,
				// as it essentially the same value.
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
				address = currentInstruction.valB.calculateValueSigned();// since map is used, negative are allowed,
																			// rather than dealing with signed/unsigned,
				// as it essentially the same value.
				currentInstruction.valM = Memory.loadDoubleWord(address);
				break;
			}
		}
	}

	/**
	 * Writes the output value back into the register file
	 */
	public static void writeBack() {
		switch (currentInstruction.instruction) {
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

	/**
	 * Updates the program counter to the next instruction
	 */
	public static void pc() {
		switch (currentInstruction.instruction) {
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
			currentInstruction.valP = (currentInstruction.conditionMet) ? currentInstruction.immediate
					: currentInstruction.valP;
			break;
		case "halt":
			currentInstruction.valP = PC;
		}
		PC = currentInstruction.valP;
		completedInstruction = currentInstruction;
	}

	/**
	 * Initializes the processor and sets memory to have compiled instructions and
	 * data
	 */
	public static void initialize() {
		if (Compiler.compiled) {
			Memory.memory.clear();
			Processor_Pipeline.PC = new DoubleWord(Long.parseLong(Compiler.start_address, 16));
			for (long l : Compiler.COMPILED_CONSTANTS.keySet())
				Memory.storeDoubleWord(l, Compiler.COMPILED_CONSTANTS.get(l));
			for (long l : Compiler.COMPILED_INSTRUCTIONS.keySet())
				Memory.storeInstruction(l, Compiler.COMPILED_INSTRUCTIONS.get(l));
			status = "AOK";
			registerFile.reset();
			ALU.resetCC();
			Processor_Pipeline.initialMemory = Memory.createImage();
			Processor_Pipeline.initialRegisterFile = Processor_Pipeline.registerFile.createImage();
			finalMemory = stepBeforeMem = stepAfterMem = null;
			finalRegisterFile = stepBeforeReg = stepAfterReg = null;
			initialized = true;
			exceptionGenerated = false;
			Memory.accessibleMemory.clear();
		} else {
			status = "HLT";
		}
	}

	/**
	 * Steps one instruction through the processor Preconditon: Processor status is
	 * AOK
	 */
	public static void step() {
		if (status.equals("AOK")) {
			Processor_Pipeline.stepBeforeMem = Memory.createImage();
			Processor_Pipeline.stepBeforeReg = Processor_Pipeline.registerFile.createImage();
			try {
				fetch();
			} catch (MemoryException e) {
				exception = e.getMessage();
				exceptionGenerated = true;
				status = "ADR";
			}
			if (status.equals("AOK")) {
				decode();
				execute();
				try {
					memory();
				} catch (MemoryException e) {
					exception = e.getMessage();
					exceptionGenerated = true;
					status = "ADR";
				}
				if (status.equals("AOK")) {
					writeBack();
					pc();
				}
			}
		}
		if (status.equals("AOK")) {
			Processor_Pipeline.stepAfterMem = Memory.createImage();
			Processor_Pipeline.stepAfterReg = Processor_Pipeline.registerFile.createImage();
		} else {
			Processor_Pipeline.stepBeforeMem = Processor_Pipeline.finalMemory = Memory.createImage();
			Processor_Pipeline.stepBeforeReg = Processor_Pipeline.finalRegisterFile = Processor_Pipeline.registerFile
					.createImage();
		}
	}

	/**
	 * Runs the processor until status is no longer AOK
	 */
	public static void run() {
		while (status.equals("AOK")) {
			try {
				fetch();
			} catch (MemoryException e) {
				exception = e.getMessage();
				exceptionGenerated = true;
				status = "ADR";
			}
			if (status.equals("AOK")) {
				decode();
				execute();
				try {
					memory();
				} catch (MemoryException e) {
					exception = e.getMessage();
					exceptionGenerated = true;
					status = "ADR";
				}
				if (status.equals("AOK")) {
					writeBack();
					pc();
				}
			}
		}
		Processor_Pipeline.finalMemory = Memory.createImage();
		Processor_Pipeline.finalRegisterFile = Processor_Pipeline.registerFile.createImage();
	}

	/**
	 * Clears the processor and memory, as well as ALU values
	 */
	public static void clear() {
		Memory.memory.clear();
		status = "HLT";
		registerFile.reset();
		ALU.resetCC();
		Processor_Pipeline.PC = new DoubleWord(0);
		Memory.accessibleMemory.clear();
	}

	/**
	 * Initializes the processor using compiled instruction and parameters
	 * 
	 * @param RDI_Selected            If RDI parameter is enabled
	 * @param RDI_Length              the length of the RDI parameter
	 * @param place_RDI_length_in_RDX flag for if the length is to be stored in RDX
	 * @param RSI_Selected            If RSI parameter is enabled
	 * @param RSI_Length              the length of the RSI parameter
	 * @param place_RSI_length_in_RCX flag for if the length is to be stored in RCX
	 */
	public static void initializeInputs(boolean RDI_Selected, String RDI_Length, boolean place_RDI_length_in_RDX,
			boolean RSI_Selected, String RSI_Length, boolean place_RSI_length_in_RCX) {
		initialize();

		if (Processor_Pipeline.initialized) {
			long rdiLength = Long.parseLong(RDI_Length);
			if (RDI_Selected)
				Memory.priorityStore(Memory.RDI_POSITION, rdiLength, !place_RDI_length_in_RDX);
			if (RDI_Selected && place_RDI_length_in_RDX)
				Processor_Pipeline.registerFile.set("%rdx", new DoubleWord(rdiLength));
			if (RDI_Selected)
				Processor_Pipeline.registerFile.set("%rdi", new DoubleWord(Memory.RDI_POSITION));
			long rsiLength = Long.parseLong(RSI_Length);
			if (RSI_Selected)
				Memory.priorityStore(Memory.RSI_POSITION, rsiLength, !place_RSI_length_in_RCX);
			if (RSI_Selected && place_RSI_length_in_RCX)
				Processor_Pipeline.registerFile.set("%rcx", new DoubleWord(rsiLength));
			if (RSI_Selected)
				Processor_Pipeline.registerFile.set("%rsi", new DoubleWord(Memory.RSI_POSITION));
			Processor_Pipeline.initialMemory = Memory.createImage();
			Processor_Pipeline.initialRegisterFile = Processor_Pipeline.registerFile.createImage();
			finalMemory = stepBeforeMem = stepAfterMem = null;
			finalRegisterFile = stepBeforeReg = stepAfterReg = null;
		}

	}

}
