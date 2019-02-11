package Terminal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import BaseEmulator.DisplayBuilder;
import Compilation.Compiler;
import Sequential.Processor_Seq;
import Sequential.TextInterface_Seq;

public class Terminal {
	public static final String HEX = "H";
	public static final String UNSIGNED = "U";
	public static final String SIGNED = "S";
	public static final String HEXLE = "HL";

	private static TextInterface control;
	private static boolean pipelined;

	public static void main(String[] args) {
		runInterface();
	}

	/**
	 * Runs the text interface
	 */
	private static void runInterface() {
		boolean running = true;
		String fileText = "";
		boolean fileLoad = false;
		boolean programCompiled = false;
		boolean programInitialized = false;
		String compiledText = "";
		Scanner inputScanner = new Scanner(System.in);
		control = new TextInterface_Seq();
		pipelined = false;
		while (running) {
			System.out.print("(Y86): ");
			String input = inputScanner.nextLine();
			String[] parsedInput = input.split("\\s+");
			if (parsedInput.length > 0) {
				switch (parsedInput[0].toLowerCase()) {
				case "q":
				case "quit":
					System.out.println("Program Exitting");
					running = false;
					break;
				case "load":
				case "l":
					if (parsedInput.length >= 2) {
						fileText = loadFile(parsedInput[1]);
						fileLoad = (fileText != null);
						compiledText = "";
						programCompiled = false;
						programInitialized = false;
					} else {
						System.out.println("No file given");
					}
					break;
				case "compile":
					compiledText = compile(fileLoad, fileText);
					programCompiled = (compiledText != null);
					programInitialized = false;
					break;
				case "initialize":
				case "i":
					if (programCompiled) {
						processFlags(parsedInput);
						control.initialize();
						programInitialized = true;
					} else {
						System.out.println("No Program is currently compiled");
					}
					break;
				case "disas":
					if (programCompiled) {
						control.disas(compiledText);
					} else {
						System.out.println("No code currently compiled");
					}
					break;
				case "step":
				case "s":
				case "next":
					if (programInitialized) {
						processFlags(parsedInput);
						control.step();
					} else {
						System.out.println("Program is not initialized");
					}
					break;
				case "r":
				case "run":
					if (programInitialized) {
						processFlags(parsedInput);
						control.run();
					} else {
						System.out.println("Program is not initialized");
					}
					break;
				case "clockpulse":
				case "cp":
				case "c":
					if (pipelined) {
						if (programInitialized) {
							processFlags(parsedInput);
							control.clockPulse();
						} else {
							System.out.println("Program is not initialized");
						}
					}
					break;
				case "reg":
				case "register":
					if (programInitialized) {
						processFlags(parsedInput);
						control.register();
					} else {
						System.out.println("Program is not initialized");
					}
					break;
				case "mem":
				case "memory":
					if (programInitialized) {
						processFlags(parsedInput);
						control.memory();
					} else {
						System.out.println("Program is not initialized");
					}
					break;
				case "pipeline":
				case "p":
					if (pipelined) {

						if (programInitialized)
							control.pipeline(compiledText);
					}
					break;

				default:
					if (input.length() > 0) {
						System.out.println("Invalid command: " + input);
					}
				}
			}
		}
		inputScanner.close();
	}

	private static void processFlags(String[] parsedInput) {
		if (parsedInput.length > 1) {
			if (parsedInput[1].equals("-t")) {
				switch (parsedInput[2]) {
				case HEX:
					DisplayBuilder.DISPLAY_SETTING = DisplayBuilder.HEX;
					break;
				case HEXLE:
					DisplayBuilder.DISPLAY_SETTING = DisplayBuilder.HEXLE;
					break;
				case SIGNED:
					DisplayBuilder.DISPLAY_SETTING = DisplayBuilder.SIGNED;
					break;
				case UNSIGNED:
					DisplayBuilder.DISPLAY_SETTING = DisplayBuilder.UNSIGNED;
					break;
				default:
					System.out.println("Invalid modifier: " + parsedInput[2] + ". Default setting used");
				}
			}
		}

	}

	private static String loadFile(String filePath) {
		String fileText = "";
		// Traditional way to get the response value.
		try {
			Scanner scan = new Scanner(new File(filePath));
			while (scan.hasNextLine()) {
				fileText += scan.nextLine() + "\n";
			}
			scan.close();
		} catch (FileNotFoundException e1) {
			System.out.println("Not a valid File Path. No File loaded");
			return null;
		}
		System.out.println("File: testFile was successfully loaded");
		return fileText;
	}

	private static String compile(boolean fileLoad, String fileText) {
		if (!fileLoad) {
			System.out.println("No File is currently loaded");
			return null;
		}
		System.out.println();
		String output;
		try {
			String[] compiler_output = new String[1];
			compiler_output[0] = "";
			output = Compiler.compile(fileText, compiler_output);
			System.out.println("Assembly compiled and ready for emulation");
			System.out.println(output);
			Processor_Seq.clear();
		} catch (IllegalArgumentException e) {
			System.out.println("Compiler Output:\n" + e.getMessage());
			return null;
		}
		return output;
	}

}