package BaseEmulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import Sequential.Processor_Seq;

public class DisplayBuilder {

	public static final String HEX = "Hex";
	public static final String UNSIGNED = "Unsigned";
	public static final String SIGNED = "Signed";
	public static final String HEXLE = "Hex LE";
	public static String DISPLAY_SETTING = HEX;
	
	public static final String SEQUENTIAL = "Sequential";
	public static final String PIPELINE = "Pipeline";

	public static String displayText(LittleEndian val) {
		
		if(val.equals(new DoubleWord(-1)))
			return "BUBBLE";
		switch (DISPLAY_SETTING) {
		case SIGNED:
			return (val.calculateValueSigned() + " ");
		case UNSIGNED:
			return val.calculateValueUnSigned();
		case HEXLE:
			return val.generateHexLE();
		default:
			return "0x" + val.displayToString();
		}
	}

	/**
	 * Uses the before and after images to create the dif tree, printing it
	 * 
	 * @param before the previous image
	 * @param after  the after image
	 * @param text   the type of difference
	 * @return the string representation of the dif
	 */
	public static String memoryDifference(TreeMap<Long, DoubleWord> before, TreeMap<Long, DoubleWord> after,
			String text) {
		String output = "Memory Differences: " + text + ":\n";
		ArrayList<Long> dif = Memory.getDif(before, after);
		for (Long l : dif) {
			if (before.containsKey(l))
				output += "0x" + Long.toString(l, 16) + ": " + displayText(before.get(l)) + "====>"
						+ displayText(after.get(l)) + "\n";
			else
				output += "0x" + Long.toString(l, 16) + ": " + displayText(new BYTE()) + "====>"
						+ displayText(after.get(l)) + "\n";
		}
		return output + "\n";
	}

	/**
	 * The memory display as a string
	 * 
	 * @return represents the memory as a string
	 */
	public static String memoryDisplay() {
		String output = "Memory:\n";
		Set<Long> usedAddresses = new HashSet<Long>();
		for (long address : Memory.memory.keySet()) {
			long modifiedAddress = address - address % 8;
			if (address < 0)
				modifiedAddress = address - ((8 + address % 8) % 8);
			if (!usedAddresses.contains(modifiedAddress)) {
				usedAddresses.add(modifiedAddress);
				output += "0x" + Long.toUnsignedString(address, 16) + " = "
						+ displayText(Memory.loadDoubleWord(modifiedAddress)) + "\n";
			}
		}
		return output + "\n";
	}

	/**
	 * Uses the before and after images to create the dif tree, printing it
	 * 
	 * @param before the previous image
	 * @param after  the after image
	 * @param text   the type of difference
	 * @return the string representation of the dif
	 */
	public static String registerDifference(TreeMap<String, DoubleWord> before, TreeMap<String, DoubleWord> after,
			String text) {
		String output = "Register File Differences: " + text + ":\n";
		ArrayList<String> dif = RegisterFile.getDif(before, after);
		for (String s : dif) {
			output += String.format("%3s", s) + ": " + displayText(before.get(s)) + "====>" + displayText(after.get(s))
					+ "\n";
		}
		return output + "\n";
	}

	/**
	 * Creates a register display string
	 * 
	 * @return the register file as a string
	 */
	public static String registerDisplay(RegisterFile rf) {
		String output = "Register File:\n";
		for (String reg : rf.keySet()) {
			output += String.format("%3s", reg) + " = " + displayText(rf.get(reg)) + "\n";
		}
		return output + "\n";
	}

	/**
	 * Generates an initialize display
	 * 
	 * @param PC the current PC
	 * @return the initialize string
	 */
	public static String initializeDisplayBuilder(DoubleWord PC, RegisterFile rf) {
		String output = "";
		output += "PC: " + displayText(Processor_Seq.PC) + "\n\n";
		output += registerDisplay(rf) + "\n";
		output += memoryDisplay() + "\n";
		return output;
	}

	/**
	 * Creates a run Display text output
	 * 
	 * @param PC                  the counter Program counter
	 * @param rf                  the register file
	 * @param exceptionGenerated  flag for exception generation
	 * @param exception           the exception generated
	 * @param initialRegisterFile initial register image
	 * @param finalRegisterFile   final register image
	 * @param initialMemory       initial memory image
	 * @param finalMemory         final memory image
	 * @return the string output of run
	 */
	public static String runDisplayBuilder(DoubleWord PC, RegisterFile rf, boolean exceptionGenerated, String exception,
			TreeMap<String, DoubleWord> initialRegisterFile, TreeMap<String, DoubleWord> finalRegisterFile,
			TreeMap<Long, DoubleWord> initialMemory, TreeMap<Long, DoubleWord> finalMemory) {
		String output = "";
		output += "RUN:\n";
		output += "PC: " + displayText(PC) + "\n";
		if (exceptionGenerated)
			output += "The processor exited with: " + exception + "\n";
		output += registerDisplay(rf);
		output += memoryDisplay();
		output += registerDifference(initialRegisterFile, finalRegisterFile, "FINAL");
		output += memoryDifference(initialMemory, finalMemory, "FINAL");
		return output;
	}

	/**
	 * Step output display after one step execution, and HLT was not generated
	 * 
	 * @param address       the address of completed instruction
	 * @param completed     the completed instruction
	 * @param stepBeforeReg the image prior to step - reg
	 * @param stepAfterReg  the image after the step - reg
	 * @param stepBeforeMem the image prior to step - mem
	 * @param stepAfterMem  the image after the step - mem
	 * @return the output string of the step
	 */
	public static String stepDisplayBuilder(DoubleWord address, Instruction completed,
			TreeMap<String, DoubleWord> stepBeforeReg, TreeMap<String, DoubleWord> stepAfterReg,
			TreeMap<Long, DoubleWord> stepBeforeMem, TreeMap<Long, DoubleWord> stepAfterMem) {
		
		String output = "";
		output += "PC: " + DisplayBuilder.displayText(address) + "\n";
		output += "Completed Instruction: " + completed.buildDisplayInstruction() + "\n";
		output += DisplayBuilder.registerDifference(stepBeforeReg, stepAfterReg, "STEP");
		output += DisplayBuilder.memoryDifference(stepBeforeMem, stepAfterMem, "STEP");
		return output;
	}

	/**
	 * Creates a step completion Display text output
	 * 
	 * @param PC                  the counter Program counter
	 * @param completed           the completed instruction
	 * @param rf                  the register file
	 * @param exceptionGenerated  flag for exception generation
	 * @param exception           the exception generated
	 * @param initialRegisterFile initial register image
	 * @param finalRegisterFile   final register image
	 * @param initialMemory       initial memory image
	 * @param finalMemory         final memory image
	 * @return the string output of run
	 */
	public static String stepCompletionDisplayBuilder(DoubleWord PC, Instruction completed, RegisterFile rf,
			boolean exceptionGenerated, String exception, TreeMap<String, DoubleWord> initialRegisterFile,
			TreeMap<String, DoubleWord> finalRegisterFile, TreeMap<Long, DoubleWord> initialMemory,
			TreeMap<Long, DoubleWord> finalMemory) {
		String output = "";
		output += "The program has completed its execution:\n";
		output += "PC: " + DisplayBuilder.displayText(PC) + "\n";
		output += "Completed Instruction: " + completed.buildDisplayInstruction() + "\n";
		output += registerDisplay(rf);
		output += memoryDisplay();
		output += registerDifference(initialRegisterFile, finalRegisterFile, "FINAL");
		output += memoryDifference(initialMemory, finalMemory, "FINAL");
		return output;
	}
	
	/**
	 * @param instructions the instructions in the pipeline
	 * @param addresses the addresses of the pipeline
	 * @return The current pipeline as a string
	 */
	public static String displayPipeline(DoubleWord[] addresses, Instruction[] instructions) {
		
		String output = "";
		output += "Fetch: " + displayText(addresses[0]) + "\n";
		output += "Decode: " + displayText(addresses[1]) + "\n";
		output += "Execute: " + displayText(addresses[2]) + "\n";
		output += "Memory: " + displayText(addresses[3]) + "\n";
		output += "WriteBack: " + displayText(addresses[4]) + "\n";
		
		return output;
	}
	
	/**
	 * @param pulseBeforeReg the image prior to step - reg
	 * @param pulseAfterReg  the image after the step - reg
	 * @param pulseBeforeMem the image prior to step - mem
	 * @param pulseAfterMem  the image after the step - mem
	 * @return the output display of the pulse
	 */
	public static String pulseDisplayBuilder(
			TreeMap<String, DoubleWord> pulseBeforeReg, TreeMap<String, DoubleWord> pulseAfterReg,
			TreeMap<Long, DoubleWord> pulseBeforeMem, TreeMap<Long, DoubleWord> pulseAfterMem) {
		
		String output = "";
		output += DisplayBuilder.registerDifference(pulseBeforeReg, pulseAfterReg, "PULSE");
		output += DisplayBuilder.memoryDifference(pulseBeforeMem, pulseBeforeMem, "PULSE");
		return output;
	}
	
	/**
	 * Step output display after one step execution, and HLT was not generated
	 * 
	 * @param address       the address of completed instruction
	 * @param completed     the completed instruction
	 * @param pulseBeforeReg the image prior to step - reg
	 * @param pulseAfterReg  the image after the step - reg
	 * @param pulseBeforeMem the image prior to step - mem
	 * @param pulseAfterMem  the image after the step - mem
	 * @return the output string of the step
	 */
	public static String pulseInstructionCompletionDisplayBuilder(DoubleWord address, Instruction completed,
			TreeMap<String, DoubleWord> pulseBeforeReg, TreeMap<String, DoubleWord> pulseAfterReg,
			TreeMap<Long, DoubleWord> pulseBeforeMem, TreeMap<Long, DoubleWord> pulseAfterMem) {
		
		String output = "";
		output += "PC: " + DisplayBuilder.displayText(address) + "\n";
		output += "Completed Instruction: " + completed.buildDisplayInstruction() + "\n";
		output += DisplayBuilder.registerDifference(pulseBeforeReg, pulseAfterReg, "PULSE");
		output += DisplayBuilder.memoryDifference(pulseBeforeMem, pulseAfterMem, "PULSE");
		return output;
	}
}
