package Pipeline;

import java.util.Scanner;

import BaseEmulator.DisplayBuilder;
import BaseEmulator.DoubleWord;
import Terminal.TextInterface;

public class TextInterface_Pipeline extends TextInterface {

	/**
	 * Initializes the processor
	 */
	public void initialize() {
		if (this.RDI_Length > 0 || this.RSI_Length > 0)
			Processor_Pipeline.initializeInputs(this.RDI_Length > 0, "" + RDI_Length, !this.RDI_Zero_Terminator,
					this.RSI_Length > 0, "" + RSI_Length, !this.RSI_Zero_Terminator);
		else
			Processor_Pipeline.initialize();
		System.out.println("Processor output:\n\nInitialize:\n");
		if (Processor_Pipeline.status.equals("HLT")) {
			System.out.println("Program failed to initialize, check that all memory locations are valid");
		} else {
			System.out.println(DisplayBuilder.initializeDisplayBuilder(Processor_Pipeline.PC, Processor_Pipeline.registerFile));
		}
	}

	/**2
	 * Shows the compiler output
	 */
	public void disas(String inputText) {
		String output = "";
		Scanner scan = new Scanner(inputText);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String addressString = line.substring(line.indexOf("x")+1, line.indexOf(":"));
			DoubleWord address = new DoubleWord(Long.parseLong(addressString, 16));
			String restOfLine = line.substring(line.indexOf(":")+1);
			if(!restOfLine.contains(":") && !restOfLine.contains(".")) {
				if(Processor_Pipeline.addresses[Processor_Pipeline.FETCH_ADDRESS] != null && Processor_Pipeline.addresses[Processor_Pipeline.FETCH_ADDRESS].equals(address))
					output += "F";
				else 
					output += "\u2002";
				if(Processor_Pipeline.addresses[Processor_Pipeline.DECODE_ADDRESS] != null && Processor_Pipeline.addresses[Processor_Pipeline.DECODE_ADDRESS].equals(address))
					output += "D";
				else 
					output += "\u2002";
				if(Processor_Pipeline.addresses[Processor_Pipeline.EXECUTE_ADDRESS] != null && Processor_Pipeline.addresses[Processor_Pipeline.EXECUTE_ADDRESS].equals(address))
					output += "E";
				else 
					output += "\u2002";
				if(Processor_Pipeline.addresses[Processor_Pipeline.MEMORY_ADDRESS] != null && Processor_Pipeline.addresses[Processor_Pipeline.MEMORY_ADDRESS].equals(address))
					output += "M";
				else 
					output += "\u2002";
				if(Processor_Pipeline.addresses[Processor_Pipeline.WRITEBACK_ADDRESS] != null && Processor_Pipeline.addresses[Processor_Pipeline.WRITEBACK_ADDRESS].equals(address))
					output+= "W";
				else 
					output += "\u2002";
			} else {
				output += "\u2002\u2002\u2002\u2002\u2002";
			}

			output+=" "+line+"\n";
		}
		scan.close();
		System.out.println(output);
	}

	/**
	 * Displays one step execution
	 */
	public void step() {
		if (Processor_Pipeline.initialized) {
			Processor_Pipeline.step();
			System.out.println("STEP:");
			if (Processor_Pipeline.status.equals("HLT")) {
				if (Processor_Pipeline.exceptionGenerated)
					System.out.println("The processor exited with:\n" + Processor_Pipeline.exception);
				else {
					String output = DisplayBuilder.displayPipeline(Processor_Pipeline.addresses, Processor_Pipeline.instructions) + "\n";
					output = DisplayBuilder.stepCompletionDisplayBuilder(Processor_Pipeline.PC,
							Processor_Pipeline.completedInstruction, Processor_Pipeline.registerFile,
							Processor_Pipeline.exceptionGenerated, Processor_Pipeline.exception,
							Processor_Pipeline.initialRegisterFile, Processor_Pipeline.finalRegisterFile,
							Processor_Pipeline.initialMemory, Processor_Pipeline.finalMemory);

					System.out.println(output);
				}
			} else {
				String output = DisplayBuilder.displayPipeline(Processor_Pipeline.addresses, Processor_Pipeline.instructions) + "\n";
				output = DisplayBuilder.stepDisplayBuilder(Processor_Pipeline.completedInstruction.address,
						Processor_Pipeline.completedInstruction, Processor_Pipeline.stepBeforeReg, Processor_Pipeline.stepAfterReg,
						Processor_Pipeline.stepBeforeMem, Processor_Pipeline.stepAfterMem);
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
		if (Processor_Pipeline.initialized) {
			Processor_Pipeline.pulse();
			System.out.println("PULSE:");
			if (Processor_Pipeline.status.equals("HLT")) {
				if (Processor_Pipeline.exceptionGenerated)
					System.out.println("The processor exited with:\n" + Processor_Pipeline.exception);
				else {
						String output = DisplayBuilder.displayPipeline(Processor_Pipeline.addresses, Processor_Pipeline.instructions) + "\n";
						output += DisplayBuilder.stepCompletionDisplayBuilder(Processor_Pipeline.PC,
							Processor_Pipeline.completedInstruction, Processor_Pipeline.registerFile,
							Processor_Pipeline.exceptionGenerated, Processor_Pipeline.exception,
							Processor_Pipeline.initialRegisterFile, Processor_Pipeline.finalRegisterFile,
							Processor_Pipeline.initialMemory, Processor_Pipeline.finalMemory);

						System.out.println(output);
				}
			} else {
				String output = DisplayBuilder.displayPipeline(Processor_Pipeline.addresses, Processor_Pipeline.instructions) + "\n";
				if(Processor_Pipeline.cp_finished)
				{
					output += DisplayBuilder.pulseInstructionCompletionDisplayBuilder(Processor_Pipeline.completedInstruction.address,
							Processor_Pipeline.completedInstruction, Processor_Pipeline.pulseBeforeReg, Processor_Pipeline.pulseBeforeReg,
							Processor_Pipeline.pulseBeforeMem, Processor_Pipeline.pulseAfterMem);	
				}
				else
				{
					output += DisplayBuilder.pulseDisplayBuilder(Processor_Pipeline.pulseBeforeReg, 
							Processor_Pipeline.pulseAfterReg, Processor_Pipeline.pulseBeforeMem, Processor_Pipeline.pulseAfterMem);
				}
				System.out.println(output);
			}
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	/**
	 * Creates a run display entry
	 */
	public void run() {
		if (Processor_Pipeline.initialized) {
			Processor_Pipeline.run();
			String output = DisplayBuilder.displayPipeline(Processor_Pipeline.addresses, Processor_Pipeline.instructions) + "\n";
			output = DisplayBuilder.runDisplayBuilder(Processor_Pipeline.PC, Processor_Pipeline.registerFile,
					Processor_Pipeline.exceptionGenerated, Processor_Pipeline.exception, Processor_Pipeline.initialRegisterFile,
					Processor_Pipeline.finalRegisterFile, Processor_Pipeline.initialMemory, Processor_Pipeline.finalMemory);
			System.out.println(output);
		}
	}

	/**
	 * Creates a register output
	 */
	public void register() {
		if (Processor_Pipeline.initialized) {
			System.out.println(DisplayBuilder.registerDisplay(Processor_Pipeline.registerFile));
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	/**
	 * Pipeline: should never be called on this object
	 */
	@Override
	public void pipeline(String compiledText) {
		if (Processor_Pipeline.initialized) {
			System.out.println(DisplayBuilder.displayPipeline(Processor_Pipeline.addresses, Processor_Pipeline.instructions));
		} else {
			System.out.println("Processor is not initialized");
		}
	}

}
