package Pipeline;

import java.util.Arrays;
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
import Sequential.Processor_Seq;

public class Processor_Pipeline {

	public static final RegisterFile registerFile = new RegisterFile();
	public static DoubleWord PC = new DoubleWord(0);
	public static String status = "HLT";
	public static TreeMap<String, DoubleWord> initialRegisterFile, stepBeforeReg, stepAfterReg, finalRegisterFile, pulseBeforeRg, pulseAfterReg;
	public static TreeMap<Long, DoubleWord> initialMemory, stepBeforeMem, stepAfterMem, finalMemory, pulseBeforeMem, pulseAfterMem;
	public static boolean initialized;
	public static String exception;
	public static boolean exceptionGenerated;
	public static Instruction completedInstruction;
	public static boolean instruction_finished;
	
	public static DoubleWord[] addresses = new DoubleWord[5];
	public static Instruction[] instructions = new Instruction[4];
	
	public static final int FETCH_ADDRESS = 0;
	public static final int DECODE_ADDRESS = 1;
	public static final int EXECUTE_ADDRESS = 2;	
	public static final int MEMORY_ADDRESS = 3;
	public static final int WRITEBACK_ADDRESS = 4;
	
	public static final int NEXT_DECODE = 0;
	public static final int NEXT_EXECUTE = 1;
	public static final int NEXT_MEMORY = 2;	
	public static final int NEXT_WRITEBACK = 3;
	
	public static boolean ret_stall; //triggered on ret
	public static boolean misprediction; //jump misprediction
	public static boolean mem_stall;
	public static boolean temp_stall;

	/**
	 * Fetches the next instruction to process
	 */
	public static void fetch() {
		
		long pcInt = addresses[FETCH_ADDRESS].calculateValueSigned();
		BYTE[] instructionArray = Memory.getInstruction(pcInt);
		instructions[NEXT_DECODE] = new Instruction(instructionArray, addresses[FETCH_ADDRESS]);
		generate_next_pc();
		if (InstructionBuilder.getKey(Instruction.BYTE_TO_FUNCTION, instructions[NEXT_DECODE].instruction) == null)
			status = "INV";
	}
	
	public static void fetch_control() {
		if(!mem_stall && !misprediction && !ret_stall && !temp_stall)
		{
			fetch();
			
			addresses[DECODE_ADDRESS] = instructions[NEXT_DECODE].address;
			addresses[FETCH_ADDRESS] = instructions[NEXT_DECODE].valP;
			if(instructions[NEXT_DECODE].instruction.equals("ret"))
				ret_stall = true;
		}
		else if(ret_stall || misprediction)
			instructions[NEXT_DECODE] = new Instruction();
		misprediction = false;
		temp_stall = false;
	}
	
	public static void generate_next_pc() {
		if(Instruction.inArray(Instruction.IMMEDIATE_SPECIAL_CASE, instructions[NEXT_DECODE].instruction))
		{
			instructions[NEXT_DECODE].valP =  instructions[NEXT_DECODE].immediate;
		}
		else
		{
			instructions[NEXT_DECODE].valP = new DoubleWord(
					ALU.IADD(addresses[FETCH_ADDRESS].bitArray, instructions[NEXT_DECODE].standardValPIncrement.bitArray));
		}
	}
	

	/**
	 * Decodes and assigns register values into current instruction
	 */
	public static void decode() {
		 mem_stall = !handleDependencies();
	}
	
	public static boolean handleDependencies()
	{
		//rA
		boolean rAStatisfied = false;
		if(!instructions[NEXT_DECODE].rA.equals("")) {	
			for(int i = NEXT_MEMORY; i <= NEXT_WRITEBACK; i++) {
				if(!instructions[i].bubble && instructions[i].dependencyLoaded.containsKey(instructions[NEXT_DECODE].rA))
				{
					if(!instructions[i].dependencyLoaded.get(instructions[NEXT_DECODE].rA))
						return false;
					instructions[NEXT_DECODE].valA = instructions[i].dependencyVal.get(instructions[NEXT_DECODE].rA);
					rAStatisfied = true;
					break;
				}
			}
			if(!rAStatisfied)
				instructions[NEXT_DECODE].valA = registerFile.get(instructions[NEXT_DECODE].rA);
		}
		boolean rBStatisfied = false;
		if(!instructions[NEXT_DECODE].rB.equals("")) {	
			for(int i = NEXT_MEMORY; i <= NEXT_WRITEBACK; i++) {
				if(!instructions[i].bubble && instructions[i].dependencyLoaded.containsKey(instructions[NEXT_DECODE].rB))
				{
					if(!instructions[i].dependencyLoaded.get(instructions[NEXT_DECODE].rB))
						return false;
					instructions[NEXT_DECODE].valB = instructions[i].dependencyVal.get(instructions[NEXT_DECODE].rB);
					rBStatisfied = true;
					break;
				}
			}
			if(!rBStatisfied)
				instructions[NEXT_DECODE].valB = registerFile.get(instructions[NEXT_DECODE].rB);
		}
		return true;
	}
	
	public static void decode_control() {
			if(!instructions[NEXT_DECODE].bubble)
				decode();
			if(!mem_stall) {
				addresses[EXECUTE_ADDRESS] = addresses[DECODE_ADDRESS];
				instructions[NEXT_EXECUTE] = instructions[NEXT_DECODE];				
			}
			else
			{
				instructions[NEXT_EXECUTE] = new Instruction();
				addresses[EXECUTE_ADDRESS] = new DoubleWord(-1);
			}
	}	


	/**
	 * Executes the current instruction
	 */
	public static void execute() {
		switch (instructions[NEXT_EXECUTE].instruction) {
		case "mrmovq":
		case "rmmovq":
			instructions[NEXT_EXECUTE].valE = new DoubleWord(
					ALU.IADD(instructions[NEXT_EXECUTE].immediate.bitArray, instructions[NEXT_EXECUTE].valB.bitArray));
			break;
		case "irmovq":
			instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].immediate);
			break;
		case "addq":
			instructions[NEXT_EXECUTE].valE = new DoubleWord(
					ALU.ADD(instructions[NEXT_EXECUTE].valA.bitArray, instructions[NEXT_EXECUTE].valB.bitArray));
			instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].valE);
			break;
		case "subq":
			instructions[NEXT_EXECUTE].valE = new DoubleWord(
					ALU.SUB(instructions[NEXT_EXECUTE].valA.bitArray, instructions[NEXT_EXECUTE].valB.bitArray));
			instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].valE);
			break;
		case "andq":
			instructions[NEXT_EXECUTE].valE = new DoubleWord(
					ALU.AND(instructions[NEXT_EXECUTE].valA.bitArray, instructions[NEXT_EXECUTE].valB.bitArray));
			instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].valE);
			break;
		case "xorq":
			instructions[NEXT_EXECUTE].valE = new DoubleWord(
					ALU.XOR(instructions[NEXT_EXECUTE].valA.bitArray, instructions[NEXT_EXECUTE].valB.bitArray));
			instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].valE);
			break;
		case "pushq":
		case "call":
			instructions[NEXT_EXECUTE].valE = new DoubleWord(ALU.DECREMENTEIGHT(instructions[NEXT_EXECUTE].valB.bitArray));
			instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].valE);
			break;
		case "ret":
		case "popq":
			instructions[NEXT_EXECUTE].valE = new DoubleWord(ALU.INCREMENTEIGHT(instructions[NEXT_EXECUTE].valB.bitArray));
			instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].valE);
			break;
		case "jle":
		case "cmovle":
			instructions[NEXT_EXECUTE].conditionMet = (ALU.SF() ^ ALU.OF()) || ALU.ZF();
			break;
		case "jl":
		case "cmovl":
			instructions[NEXT_EXECUTE].conditionMet = ALU.SF() ^ ALU.OF();
			break;
		case "je":
		case "cmove":
			instructions[NEXT_EXECUTE].conditionMet = ALU.ZF();
			break;
		case "jne":
		case "cmovne":
			instructions[NEXT_EXECUTE].conditionMet = !ALU.ZF();
			break;
		case "jge":
		case "cmovge":
			instructions[NEXT_EXECUTE].conditionMet = !(ALU.SF() ^ ALU.OF());
			break;
		case "jg":
		case "cmovg":
			instructions[NEXT_EXECUTE].conditionMet = !(ALU.SF() ^ ALU.OF()) && !ALU.ZF();
			break;
		}
		if(Instruction.inArray(Instruction.CONDITIONAL_JUMP, instructions[NEXT_EXECUTE].instruction))
		{
			if(!instructions[NEXT_EXECUTE].conditionMet)
				bubbleStages();
		}
		if (Instruction.inArray(Instruction.CONDITIONAL_MOVE, instructions[NEXT_EXECUTE].instruction))
		{
			if(instructions[NEXT_EXECUTE].conditionMet)
				instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].valA);
			else
				instructions[NEXT_EXECUTE].dependencyVal.put(instructions[NEXT_EXECUTE].rB, instructions[NEXT_EXECUTE].valB);
		}
	}

	private static void bubbleStages() {
		instructions[NEXT_DECODE] = new Instruction();
		addresses[DECODE_ADDRESS] = new DoubleWord(-1);
		addresses[FETCH_ADDRESS] = new DoubleWord(
				ALU.IADD(addresses[EXECUTE_ADDRESS].bitArray, instructions[NEXT_EXECUTE].standardValPIncrement.bitArray));
		misprediction = true;
		ret_stall = mem_stall = temp_stall = false;
	}

	public static void execute_control() {
		if(!instructions[NEXT_EXECUTE].bubble) {
			execute();
		}
		addresses[MEMORY_ADDRESS] = addresses[EXECUTE_ADDRESS];
		instructions[NEXT_MEMORY] = instructions[NEXT_EXECUTE];	
	}
	
	/**
	 * Performs memory operation if necessary for current instruction
	 */
	public static void memory() {
		if (instructions[NEXT_MEMORY].memory) {
			long address;
			switch (instructions[NEXT_MEMORY].instruction) {
			case "pushq":
			case "rmmovq":
				address = instructions[NEXT_MEMORY].valE.calculateValueSigned();// since map is used, negative are allowed,
																			// rather than dealing with signed/unsigned,
				// as it essentially the same value.
				Memory.storeDoubleWord(address, instructions[NEXT_MEMORY].valA);
				break;
			case "mrmovq":
				address = instructions[NEXT_MEMORY].valE.calculateValueSigned();
				instructions[NEXT_MEMORY].valM = Memory.loadDoubleWord(address);
				instructions[NEXT_MEMORY].dependencyVal.put(instructions[NEXT_MEMORY].rA, instructions[NEXT_MEMORY].valM);
				instructions[NEXT_MEMORY].dependencyLoaded.put(instructions[NEXT_MEMORY].rA, true);
				break;
			case "popq":
				address = instructions[NEXT_MEMORY].valB.calculateValueSigned();
				instructions[NEXT_MEMORY].valM = Memory.loadDoubleWord(address);
				instructions[NEXT_MEMORY].dependencyVal.put(instructions[NEXT_MEMORY].rA, instructions[NEXT_MEMORY].valM);
				instructions[NEXT_MEMORY].dependencyLoaded.put(instructions[NEXT_MEMORY].rA, true);
				break;
			case "call":
				address = instructions[NEXT_MEMORY].valE.calculateValueSigned();
				DoubleWord callReturn = new DoubleWord(
						ALU.IADD(addresses[MEMORY_ADDRESS].bitArray, instructions[NEXT_MEMORY].standardValPIncrement.bitArray));
				Memory.storeDoubleWord(address, callReturn);
				break;
			case "ret":
				address = instructions[NEXT_MEMORY].valB.calculateValueSigned();// since map is used, negative are allowed,												// rather than dealing with signed/unsigned,
				// as it essentially the same value.
				instructions[NEXT_MEMORY].valM = Memory.loadDoubleWord(address);
				instructions[NEXT_MEMORY].valP = instructions[NEXT_MEMORY].valM;
				addresses[FETCH_ADDRESS] = 	instructions[NEXT_MEMORY].valP;
				temp_stall = true;
				ret_stall = false;
				break;
			}
		}
	}

	public static void memory_control() {
		if(!instructions[NEXT_MEMORY].bubble) {
			memory();
		}
		addresses[WRITEBACK_ADDRESS] = addresses[MEMORY_ADDRESS];
		instructions[NEXT_WRITEBACK] = instructions[NEXT_MEMORY];
	}	
	
	/**
	 * Writes the output value back into the register file
	 */
	public static void writeBack() {
		switch (instructions[NEXT_WRITEBACK].instruction) {
		case "addq":
		case "subq":
		case "xorq":
		case "andq":
			registerFile.set(instructions[NEXT_WRITEBACK].rB, instructions[NEXT_WRITEBACK].valE);
			break;
		case "mrmovq":
			registerFile.set(instructions[NEXT_WRITEBACK].rA, instructions[NEXT_WRITEBACK].valM);
			break;
		case "irmovq":
			registerFile.set(instructions[NEXT_WRITEBACK].rB, instructions[NEXT_WRITEBACK].immediate);
			break;
		case "rrmovq":
		case "cmovl":
		case "cmovle":
		case "cmove":
		case "cmovne":
		case "cmovg":
		case "cmovge":
			DoubleWord dw = (instructions[NEXT_WRITEBACK].conditionMet) ? instructions[NEXT_WRITEBACK].valA : instructions[NEXT_WRITEBACK].valB;
			registerFile.set(instructions[NEXT_WRITEBACK].rB, dw);
			break;
		case "popq":
			registerFile.set(instructions[NEXT_WRITEBACK].rA, instructions[NEXT_WRITEBACK].valM);
		case "ret":
		case "call":
		case "pushq":
			registerFile.set(instructions[NEXT_WRITEBACK].rB, instructions[NEXT_WRITEBACK].valE);
			break;
		}
	}

	/**
	 * Updates the program counter to the next instruction
	 */
	
	public static void writeBack_control() {
		if(!instructions[NEXT_WRITEBACK].bubble) {
			writeBack();
			if(instructions[NEXT_WRITEBACK].instruction.equals("halt"))
				status = "HLT";
			instruction_finished = true;
		}
		PC = addresses[MEMORY_ADDRESS];
		int index = addresses.length - 2;
		while(PC.equals(new DoubleWord(-1))) {
			PC = addresses[index];
			index--;
		}
		if(!instructions[NEXT_WRITEBACK].bubble && instructions[NEXT_WRITEBACK].instruction.equals("ret")) 
			PC = instructions[NEXT_WRITEBACK].valP;
		completedInstruction = instructions[NEXT_WRITEBACK];
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
			for(int i = 0; i < instructions.length; i++) {
				instructions[i] = new Instruction();
			}
			for(int i = 0; i < addresses.length; i++) {
				addresses[i] = new DoubleWord(-1);
			}	
			ret_stall = false;
			misprediction = false;
			mem_stall = false;
			addresses[FETCH_ADDRESS] = new DoubleWord(Long.parseLong(Compiler.start_address, 16));
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

	public static void pulse() {
		if(status.equals("AOK")) {
			Processor_Seq.stepBeforeMem = Memory.createImage();
			Processor_Seq.stepBeforeReg = Processor_Seq.registerFile.createImage();
			writeBack_control();
			try {
				memory_control();
			} catch (MemoryException e) {
				exception = e.getMessage();
				exceptionGenerated = true;
				status = "ADR";
			}
			if (status.equals("AOK")) {
				execute_control();
				decode_control();
				try {
					fetch_control();
				} catch (MemoryException e) {
					exception = e.getMessage();
					exceptionGenerated = true;
					status = "ADR";
				}
			}
		}
		if (status.equals("AOK")) {
			Processor_Pipeline.pulseAfterMem = Memory.createImage();
			Processor_Pipeline.pulseAfterReg = Processor_Pipeline.registerFile.createImage();
			if(instruction_finished) {
				Processor_Pipeline.stepAfterMem = Processor_Pipeline.pulseAfterMem;
				Processor_Pipeline.stepAfterReg = Processor_Pipeline.pulseAfterReg;
			}
		} else {
			Processor_Pipeline.pulseAfterMem = Processor_Pipeline.finalMemory = Memory.createImage();
			Processor_Pipeline.pulseAfterReg = Processor_Pipeline.finalRegisterFile = Processor_Pipeline.registerFile
					.createImage();
			if(instruction_finished) {
				Processor_Pipeline.stepAfterMem = Processor_Pipeline.finalMemory;
				Processor_Pipeline.stepAfterReg = Processor_Pipeline.finalRegisterFile;
			}
				
		}
		instruction_finished = false;
	}	
	
	
	
	/**
	 * Steps one instruction through the processor Preconditon: Processor status is
	 * AOK
	 */
	public static void step() {
		if(status.equals("AOK")) {
			Processor_Pipeline.stepBeforeMem = Memory.createImage();
			Processor_Pipeline.stepBeforeReg = Processor_Pipeline.registerFile.createImage();
		}
		while (status.equals("AOK") && !instruction_finished) {
			writeBack_control();
			try {
				memory_control();
			} catch (MemoryException e) {
				exception = e.getMessage();
				exceptionGenerated = true;
				status = "ADR";
			}
			if (status.equals("AOK")) {
				execute_control();
				decode_control();
				try {
					fetch_control();
				} catch (MemoryException e) {
					exception = e.getMessage();
					exceptionGenerated = true;
					status = "ADR";
				}
			}
		}
		instruction_finished = false;
		if (status.equals("AOK")) {
			Processor_Pipeline.stepAfterMem = Memory.createImage();
			Processor_Pipeline.stepAfterReg = Processor_Pipeline.registerFile.createImage();
		} else {
			Processor_Pipeline.stepBeforeMem = Processor_Pipeline.finalMemory = Memory.createImage();
			Processor_Pipeline.stepAfterReg = Processor_Pipeline.finalRegisterFile = Processor_Pipeline.registerFile
					.createImage();
		}
	}

	/**
	 * Runs the processor until status is no longer AOK
	 */
	public static void run() {
		while (status.equals("AOK")) {
			writeBack_control();
			try {
				memory_control();
			} catch (MemoryException e) {
				exception = e.getMessage();
				exceptionGenerated = true;
				status = "ADR";
			}
			if (status.equals("AOK")) {
				execute_control();
				decode_control();
				try {
					fetch_control();
				} catch (MemoryException e) {
					exception = e.getMessage();
					exceptionGenerated = true;
					status = "ADR";
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
		for(int i = 0; i < instructions.length; i++) {
			instructions[i] = new Instruction();
			addresses[i] = new DoubleWord(i);
		}
		ret_stall = false;
		misprediction = false;
		mem_stall = false;
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
