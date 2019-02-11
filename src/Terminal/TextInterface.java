package Terminal;

import BaseEmulator.DisplayBuilder;
import Sequential.Processor_Seq;

public abstract class TextInterface {

	public abstract void initialize();

	public abstract void disas(String inputText);

	public abstract void step();

	public abstract void clockPulse();

	public abstract void run();

	public abstract void register();

	/**
	 * Creates a memory output
	 */
	public void memory() {
		if (Processor_Seq.initialized) {
			System.out.println(DisplayBuilder.memoryDisplay());
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	public abstract void pipeline(String compiledText);

	protected int RDI_Length;
	protected boolean RDI_Zero_Terminator;
	protected int RSI_Length;
	protected boolean RSI_Zero_Terminator;

	public void setRDI(int RDI_Length, boolean RDI_Zero_Terminator) {
		this.RDI_Length = RDI_Length;
		this.RDI_Zero_Terminator = RDI_Zero_Terminator;
	}

	public void setRSI(int RSI_Length, boolean RSI_Zero_Terminator) {
		this.RSI_Length = RSI_Length;
		this.RSI_Zero_Terminator = RSI_Zero_Terminator;
	}

	public String getRDI_Info() {
		return "Length: " + RDI_Length + " is zero terminated: " + RDI_Zero_Terminator;
	}

	public String getRSI_Info() {
		return "Length: " + RSI_Length + " is zero terminated: " + RSI_Zero_Terminator;
	}

}
