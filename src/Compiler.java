import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import javafx.scene.control.TextArea;

public class Compiler {

	public static final HashMap<Long, String[]> COMPILED_INSTRUCTIONS =  new HashMap<Long, String[]>(); 
	public static final HashMap<Long, DoubleWord> COMPILED_CONSTANTS = new HashMap<Long, DoubleWord>(); 
	public static String start_address;
	public static boolean compiled;

	public static String compile(String input, TextArea outputWindow) {
		preprocessor(input);
		compiled = true;
		COMPILED_INSTRUCTIONS.clear();
		COMPILED_CONSTANTS.clear();
		if(inputLines.size() == 0)
			return "";
		start_address = inputLines.get(0).address;
		String output = "";
		String[] instruction;
		for(Line l: inputLines) {
			String outputLine = "";
			outputLine += "0x" + l.address +": ";
			switch(l.splitLine[0]) {
			case ".quad":
				outputLine = quadDirective(outputLine, l);
				break;
			case "halt":
			case "ret":
			case "nop":
				if(l.splitLine.length > 1)
					throw new IllegalArgumentException("Invalid instruction argument - " + l.splitLine[0] +" does not take an argument\n"
							+ "Error occured on the line: "+ l.line);
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], null, null, null, l.line);
				outputLine += convertArrayToString(instruction);
				COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
				break;
			case "pushq":
			case "popq":
				if(l.splitLine.length != 2)
					throw new IllegalArgumentException("Invalid instruction argument - " + l.splitLine[0] +" requires only 1 register argument\n"
							+ "Error occured on the line: "+ l.line);
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[1],  "No register", null, l.line);
				outputLine += convertArrayToString(instruction);
				COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
				break;
			case "rrmovq":
			case "cmovle":
			case "cmovg":
			case "cmovge":
			case "cmovne":
			case "cmove":
			case "cmovl":
			case "addq":
			case "subq":
			case "xorq":
			case "andq":
				if(l.splitLine.length != 3)
					throw new IllegalArgumentException("Invalid instruction argument - " + l.splitLine[0] +" requires 2 register arguments\n"
							+ "Error occured on the line: "+ l.line);
				if(!l.line.contains(","))
					throw new IllegalArgumentException("Invalid instruction argument - Comma is required\n"
							+ "Error occured on the line: "+ l.line);
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[1],   l.splitLine[2], null, l.line);
				outputLine += convertArrayToString(instruction);
				COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
				break;
			case "call":
			case "jmp":
			case "jle":
			case "jg":
			case "jge":
			case "jne":
			case "je":
			case "jl":
				if(l.splitLine.length != 2)
					throw new IllegalArgumentException("Invalid instruction argument - " + l.splitLine[0] +" requires 1 Tag argument\n"
							+ "Error occured on the line: "+ l.line);
				if(!TAG_TO_ADDRESS.containsKey(l.splitLine[1]))
					throw new IllegalArgumentException("Invalid instruction argument - Invalid Tag: " + l.splitLine[1] + "\n"
							+ "Error occured on the line: "+ l.line);
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], null,   null, new DoubleWord(TAG_TO_ADDRESS.get(l.splitLine[1]),false), l.line);
				outputLine += convertArrayToString(instruction);
				COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
				break;
			case "irmovq":
				if(!l.line.contains(","))
					throw new IllegalArgumentException("Invalid instruction argument - Comma is required\n"
							+ "Error occured on the line: "+ l.line);
				outputLine = irmovq(outputLine, l);
				break;	
			case "rmmovq":
				if(!l.line.contains(","))
					throw new IllegalArgumentException("Invalid instruction argument - Comma is required\n"
							+ "Error occured on the line: "+ l.line);
				outputLine = rmmovq(outputLine, l);
				break;
			case "mrmovq":
				if(!l.line.contains(","))
					throw new IllegalArgumentException("Invalid instruction argument - Comma is required\n"
							+ "Error occured on the line: "+ l.line);
				outputLine = mrmovq(outputLine, l);
				break;		
			case ".pos":
			case ".align":
				break;
			default:
				if(l.splitLine[0].contains(":")) {

				}
				else if(l.splitLine[0].contains("."))
					throw new IllegalArgumentException("Invalid assembler directive: " + l.splitLine[0] + "\n"
							+ "Error occured on the line: "+ l.line);
				else 
					throw new IllegalArgumentException("Invalid instruction: " + l.splitLine[0] + "\n"
							+ "Error occured on the line: "+ l.line);
			}
			outputLine+= " "+l.line+"\n";
			outputWindow.setText(outputWindow.getText() + l.line + " ==> " + outputLine+"\n");
			output+=outputLine;
		}
		return output;
	}


	private static String irmovq(String output, Line l) {
		DoubleWord dw;
		if(l.splitLine.length !=3)
			throw new IllegalArgumentException("Invalid instruction argument - irmovq requires 1 register and 1 immediate\n"
					+ "Error occured on the line: "+ l.line);
		String[] instruction;
		if(TAG_TO_ADDRESS.containsKey(l.splitLine[1])) {
			dw = new DoubleWord(TAG_TO_ADDRESS.get(l.splitLine[1]),false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], "No register", l.splitLine[2], dw, l.line);
			output += convertArrayToString(instruction);
		} else if(l.splitLine[1].contains("0x")) {
			try {
				dw = new DoubleWord(l.splitLine[1].substring(2), false);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Hex argument: "+ l.splitLine[1]+"\n"
						+ "Error occured on the line: "+ l.line);
			}
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], "No register", l.splitLine[2], dw, l.line);
			output += convertArrayToString(instruction);
		} else {
			try {
				dw = new DoubleWord(Long.parseLong(l.splitLine[1].substring(1)));
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Integer argument: "+ l.splitLine[1]+"\n"
						+ "Error occured on the line: "+ l.line);
			}
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], "No register", l.splitLine[2], dw, l.line);
			output += convertArrayToString(instruction);
		}
		COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
		return output;
	}


	private static String rmmovq(String output, Line l) {
		if(l.splitLine.length != 3)
			throw new IllegalArgumentException("Invalid instruction argument - rmmovq requires 1 register and 1 memory function\n"
					+ "Error occured on the line: "+ l.line);
		if(!l.splitLine[2].contains("(") || !l.splitLine[2].contains(")"))
			throw new IllegalArgumentException("Invalid instruction argument - The second argument of mrmovq must be the memory function\n"
					+ "Error occured on the line: "+ l.line);
		String offset;
		String rB;
		String[] instruction;
		DoubleWord dw;
		offset = l.splitLine[2].substring(0,l.splitLine[2].indexOf("("));
		rB = l.splitLine[2].substring(l.splitLine[2].indexOf("(")+1,l.splitLine[2].indexOf(")"));
		if(TAG_TO_ADDRESS.containsKey(offset)) {
			dw = new DoubleWord(TAG_TO_ADDRESS.get(offset),false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[1], rB, dw, l.line);
			output += convertArrayToString(instruction);
		} else if(offset.contains("0x")) {
			try {
				dw = new DoubleWord(offset.substring(2), false);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Hex argument: "+ l.splitLine[1]+"\n"
						+ "Error occured on the line: "+ l.line);
			}
			instruction = InstructionBuilder.getInstruction(l.splitLine[0],  l.splitLine[1], rB, dw, l.line);
			output += convertArrayToString(instruction);
		} else {
			try {
				dw = (offset.length() > 0) ? new DoubleWord(Long.parseLong(offset)) : new DoubleWord(0);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Integer argument: "+ l.splitLine[1]+"\n"
						+ "Error occured on the line: "+ l.line);
			}
			instruction = InstructionBuilder.getInstruction(l.splitLine[0],  l.splitLine[1], rB, dw, l.line);
			output += convertArrayToString(instruction);
		}
		COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
		return output;
	}


	private static String mrmovq(String output, Line l) {
		if(l.splitLine.length != 3)
			throw new IllegalArgumentException("Invalid instruction argument - mrmovq requires 1 register and 1 memory function\n"
					+ "Error occured on the line: "+ l.line);
		if(!l.splitLine[1].contains("(") || !l.splitLine[1].contains(")"))
			throw new IllegalArgumentException("Invalid instruction argument - The first argument of mrmovq must be the memory function\n"
					+ "Error occured on the line: "+ l.line);
		String offset;
		String rB;
		String[] instruction;
		DoubleWord dw;
		offset = l.splitLine[1].substring(0,l.splitLine[1].indexOf("("));
		rB = l.splitLine[1].substring(l.splitLine[1].indexOf("(")+1,l.splitLine[1].indexOf(")"));
		if(TAG_TO_ADDRESS.containsKey(offset)) {
			dw = new DoubleWord(TAG_TO_ADDRESS.get(offset),false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[2], rB, dw, l.line);
			output += convertArrayToString(instruction);
		} else if(offset.contains("0x")) {
			try {
				dw = new DoubleWord(offset.substring(2), false);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Hex argument: "+ l.splitLine[1]+"\n"
						+ "Error occured on the line: "+ l.line);
			}
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[2], rB, dw, l.line);
			output += convertArrayToString(instruction);
		} else {
			try {
				dw = (offset.length() > 0) ? new DoubleWord(Long.parseLong(offset)) : new DoubleWord(0);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Integer argument: "+ l.splitLine[1]+"\n"
						+ "Error occured on the line: "+ l.line);
			}
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[2], rB, dw, l.line);
			output += convertArrayToString(instruction);
		}
		COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
		return output;
	}


	private static String quadDirective(String output, Line l) {
		DoubleWord dw;
		if(l.splitLine.length != 2)
			throw new IllegalArgumentException("Invalid instruction argument - mrmovq requires 1 register and 1 memory function\n"
					+ "Error occured on the line: "+ l.line);
		if(TAG_TO_ADDRESS.containsKey(l.splitLine[1])) {
			if(!TAG_TO_ADDRESS.containsKey(l.splitLine[1]))
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Tag: " + l.splitLine[1] + "\n"
						+ "Error occured on the line: "+ l.line);
			dw = new DoubleWord(TAG_TO_ADDRESS.get(l.splitLine[1]),false);
			output += dw.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), dw);
		} else if(l.splitLine[1].contains("0x")) {
			try {
				dw = new DoubleWord(l.splitLine[1].substring(2), false);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Hex argument: "+ l.splitLine[1]+"\n"
						+ "Error occured on the line: "+ l.line);
			}
			output += dw.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), dw);
		} else {
			try {
				dw = new DoubleWord(Long.parseLong(l.splitLine[1]));
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid instruction argument - Invalid Integer argument: "+ l.splitLine[1]+"\n"
						+ "Error occured on the line: "+ l.line);
			}
			output += dw.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), dw);
		}
		return output;
	}


	//creates a map of tags assigned with an address
	public static void preprocessor(String input) {
		long address = 0;
		TAG_TO_ADDRESS.clear();
		inputLines.clear();
		Scanner scan = new Scanner(input);
		while(scan.hasNextLine()) {
			String sLine;
			String line = sLine = scan.nextLine();
			line = line.replace(",", "");
			if(line.contains("#")) {
				line = line.substring(0, line.indexOf("#"));
				sLine = sLine.substring(0, sLine.indexOf("#"));
			}
			int nonWhiteSpace = -1;
			int index = 0;
			while(nonWhiteSpace ==-1 && index < line.length()) {
				if(line.charAt(index) > 32)
					nonWhiteSpace = index;
				index++;
			}
			line = (nonWhiteSpace != -1) ? line.substring(nonWhiteSpace) : "";
			String[] splitLine = line.split(" ");
			String instruction = splitLine[0];
			if(!line.contains("#") && line.length() > 0) {
				if(instruction.startsWith(".")) {
					switch(instruction) {
					case ".pos":
						address = posPreProcess(address, sLine, splitLine);
						break;
					case ".quad":
						inputLines.add(new Line(Long.toHexString(address),splitLine,sLine));
						address+=8;
						break;
					case ".align":
						address = alignPreProcess(address, sLine, splitLine);
						break;
					}
				} else if(instruction.contains(":")) {
					TAG_TO_ADDRESS.put(splitLine[0].substring(0, splitLine[0].length()-1), Long.toHexString(address));
					inputLines.add(new Line(Long.toHexString(address),splitLine,sLine));
				} else {
					inputLines.add(new Line(Long.toHexString(address),splitLine,sLine));
					if(Instruction.inArray(InstructionBuilder.ONE_BYTE, instruction))
						address += 1;
					else if(Instruction.inArray(InstructionBuilder.TEN_BYTE, instruction))
						address += 10;
					else if(Instruction.inArray(InstructionBuilder.NINE_BYTE, instruction))
						address += 9;
					else
						address += 2;
				}
			} else {

			}
		}
		scan.close();
	}


	private static long posPreProcess(long address, String sLine, String[] splitLine) {
		if(splitLine.length < 2)
			throw new IllegalArgumentException("Invalid assembler directive argument - .pos requires an address argument.\n"
					+ "Error occured on the line: "+ sLine);
		if(splitLine[1].startsWith("0x")) {
			try {
				address = Integer.parseInt(splitLine[1].substring(2),16);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid assembler directive argument - Invalid Hex argument: "+ splitLine[1]+" for .pos.\n"
						+ "Error occured on the line: "+ sLine);
			}
		} else {
			try {
				address = Integer.parseInt(splitLine[1]);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid assembler directive argument - Invalid Integer argument: "+ splitLine[1]+" for .pos.\n"
						+ "Error occured on the line: "+ sLine);
			}
		}
		inputLines.add(new Line(Long.toHexString(address),splitLine,sLine));
		return address;
	}


	private static long alignPreProcess(long address, String sLine, String[] splitLine) {
		if(splitLine.length < 2)
			throw new IllegalArgumentException("Invalid assembler directive argument - .align requires an address argument.\n"
					+ "Error occured on the line: "+ sLine);
		try {
			long offset = Integer.parseInt(splitLine[1]);
			address = address + (offset - address%offset);
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("Invalid assembler directive argument - Invalid Integer argument: "+ splitLine[1]+" for .align.\n"
					+ "Error occured on the line: "+ sLine);
		}
		inputLines.add(new Line(Long.toHexString(address),splitLine,sLine));
		return address;
	}

	private static HashMap<String, String> TAG_TO_ADDRESS = new HashMap<String, String>(); 
	private static ArrayList<Line> inputLines = new ArrayList<Line>();
	private static class Line {
		String address;
		String[] splitLine;
		String line;
		public Line(String address, String[] splitLine, String line) {
			this.address = address;
			this.splitLine = splitLine;
			this.line = line;
		}

		public String toString() {
			return address + " " + Arrays.toString(splitLine);
		}
	}

	public static String convertArrayToString(String[] array) {
		String output = "";
		for(String s: array)
			output += s;
		return output;
	}
}
