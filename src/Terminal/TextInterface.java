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
}
