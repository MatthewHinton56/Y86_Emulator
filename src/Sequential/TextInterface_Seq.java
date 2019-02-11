package Sequential;

import java.util.Scanner;

import BaseEmulator.DisplayBuilder;
import BaseEmulator.DoubleWord;
import Terminal.TextInterface;

public class TextInterface_Seq extends TextInterface {

	/**
	 * Initializes the processor
	 */
	public void initialize() {
		Processor_Seq.initialize();
		System.out.println("Processor output:\n\nInitialize:\n");
		if (Processor_Seq.status.equals("HLT")) {
			System.out.println("Program failed to initialize, check that all memory locations are valid");
		} else {
			System.out.println(DisplayBuilder.initializeDisplayBuilder(Processor_Seq.PC, Processor_Seq.registerFile));
		}
	}

	/**
	 * Shows the compiler output
	 */
	public void disas(String inputText) {
		String output = "";
		Scanner scan = new Scanner(inputText);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String addressString = line.substring(line.indexOf("x") + 1, line.indexOf(":"));
			DoubleWord address = new DoubleWord(Long.parseLong(addressString, 16));
			String restOfLine = line.substring(line.indexOf(":") + 1);
			if (!restOfLine.contains(":") && !restOfLine.contains(".")) {
				if (Processor_Seq.PC.equals(address))
					output += ">";
				else
					output += "\u2002";
			} else {
				output += "\u2002";
			}

			output += " " + line + "\n";
		}
		scan.close();
		System.out.println(output);
	}

	/**
	 * Displays one step execution
	 */
	public void step() {
		if (Processor_Seq.initialized) {
			Processor_Seq.step();
			System.out.println("STEP:");
			if (Processor_Seq.status.equals("HLT")) {
				if (Processor_Seq.exceptionGenerated)
					System.out.println("The processor exited with:\n" + Processor_Seq.exception);
				else {
					String output = DisplayBuilder.stepCompletionDisplayBuilder(Processor_Seq.PC,
							Processor_Seq.completedInstruction, Processor_Seq.registerFile,
							Processor_Seq.exceptionGenerated, Processor_Seq.exception,
							Processor_Seq.initialRegisterFile, Processor_Seq.finalRegisterFile,
							Processor_Seq.initialMemory, Processor_Seq.finalMemory);

					System.out.println(output);
				}
			} else {
				String output = DisplayBuilder.stepDisplayBuilder(Processor_Seq.completedInstruction.address,
						Processor_Seq.completedInstruction, Processor_Seq.stepBeforeReg, Processor_Seq.stepAfterReg,
						Processor_Seq.stepBeforeMem, Processor_Seq.stepAfterMem);
				System.out.println(output);
			}
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	/**
	 * clockPulse: should never be called on this object
	 */
	@Override
	public void clockPulse() {
	}

	/**
	 * Creates a run display entry
	 */
	public void run() {
		if (Processor_Seq.initialized) {
			Processor_Seq.run();
			String output = DisplayBuilder.runDisplayBuilder(Processor_Seq.PC, Processor_Seq.registerFile,
					Processor_Seq.exceptionGenerated, Processor_Seq.exception, Processor_Seq.initialRegisterFile,
					Processor_Seq.finalRegisterFile, Processor_Seq.initialMemory, Processor_Seq.finalMemory);
			System.out.println(output);
		}
	}

	/**
	 * Creates a register output
	 */
	public void register() {
		if (Processor_Seq.initialized) {
			System.out.println(DisplayBuilder.registerDisplay(Processor_Seq.registerFile));
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	/**
	 * Pipeline: should never be called on this object
	 */
	@Override
	public void pipeline(String compiledText) {
	}

}
