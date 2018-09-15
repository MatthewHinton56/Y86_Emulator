import java.io.File;
import java.io.FileNotFoundException;
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
		start_address = inputLines.get(0).address;
		String output = "";
		String offset,rB;
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
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], null, null, null);
				outputLine += convertArrayToString(instruction);
				COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
				break;
			case "pushq":
			case "popq":
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[1],  "No register", null);
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
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[1],   l.splitLine[2], null);
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
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], null,   null, new DoubleWord(TAG_TO_ADDRESS.get(l.splitLine[1]),false));
				outputLine += convertArrayToString(instruction);
				COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
				break;
			case "irmovq":
				outputLine = irmovq(outputLine, l);
				break;	

			case "rmmovq": 
				outputLine = rmmovq(outputLine, l);
				break;	

			case "mrmovq": 
				outputLine = mrmovq(outputLine, l);
				break;		
			}
			outputLine+= " "+l.line+"\n";
			outputWindow.setText(outputWindow.getText() + l.line + " ==> " + outputLine+"\n");
			output+=outputLine;
		}
		return output;
	}


	private static String irmovq(String output, Line l) {
		String[] instruction;
		if(TAG_TO_ADDRESS.containsKey(l.splitLine[1])) {
			DoubleWord dw = new DoubleWord(TAG_TO_ADDRESS.get(l.splitLine[1]),false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], "No register", l.splitLine[2], dw);
			output += convertArrayToString(instruction);
		} else if(l.splitLine[1].contains("0x")) {
			DoubleWord dw = new DoubleWord(l.splitLine[1].substring(3), false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], "No register", l.splitLine[2], dw);
			output += convertArrayToString(instruction);
		} else {
			DoubleWord dw = new DoubleWord(Long.parseLong(l.splitLine[1].substring(1)));
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], "No register", l.splitLine[2], dw);
			output += convertArrayToString(instruction);
		}
		COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
		return output;
	}


	private static String rmmovq(String output, Line l) {
		String offset;
		String rB;
		String[] instruction;
		offset = l.splitLine[2].substring(0,l.splitLine[2].indexOf("("));
		rB = l.splitLine[2].substring(l.splitLine[2].indexOf("(")+1,l.splitLine[2].indexOf(")"));
		if(TAG_TO_ADDRESS.containsKey(offset)) {
			DoubleWord dw = new DoubleWord(TAG_TO_ADDRESS.get(offset),false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[1], rB, dw);
			output += convertArrayToString(instruction);
		} else if(offset.contains("0x")) {
			DoubleWord dw = new DoubleWord(offset.substring(2), false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0],  l.splitLine[1], rB, dw);
			output += convertArrayToString(instruction);
		} else {
			DoubleWord dw = (offset.length() > 0) ? new DoubleWord(Long.parseLong(offset)) : new DoubleWord(0);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0],  l.splitLine[1], rB, dw);
			output += convertArrayToString(instruction);
		}
		COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
		return output;
	}


	private static String mrmovq(String output, Line l) {
		String offset;
		String rB;
		String[] instruction;
		offset = l.splitLine[1].substring(0,l.splitLine[1].indexOf("("));
		rB = l.splitLine[1].substring(l.splitLine[1].indexOf("(")+1,l.splitLine[1].indexOf(")"));
		if(TAG_TO_ADDRESS.containsKey(offset)) {
			DoubleWord dw = new DoubleWord(TAG_TO_ADDRESS.get(offset),false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[2], rB, dw);
			output += convertArrayToString(instruction);
		} else if(offset.contains("0x")) {
			DoubleWord dw = new DoubleWord(offset.substring(2), false);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[2], rB, dw);
			output += convertArrayToString(instruction);
		} else {
			DoubleWord dw = (offset.length() > 0) ? new DoubleWord(Long.parseLong(offset)) : new DoubleWord(0);
			instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[2], rB, dw);
			output += convertArrayToString(instruction);
		}
		COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
		return output;
	}


	private static String quadDirective(String output, Line l) {
		if(TAG_TO_ADDRESS.containsKey(l.splitLine[1])) {
			DoubleWord dw = new DoubleWord(TAG_TO_ADDRESS.get(l.splitLine[1]),false);
			output += dw.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), dw);
		} else if(l.splitLine[1].contains("0x")) {
			DoubleWord dw = new DoubleWord(l.splitLine[1].substring(2), false);
			output += dw.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), dw);
		} else {
			DoubleWord dw = new DoubleWord(Long.parseLong(l.splitLine[1]));
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
			if(line.contains("#"))
				line = line.substring(0, line.indexOf("#"));
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
						if(splitLine[1].startsWith("0x")) {
							address = Integer.parseInt(splitLine[1].substring(2),16);
						} else {
							address = Integer.parseInt(splitLine[1]);
						}
						inputLines.add(new Line(Long.toHexString(address),splitLine,sLine));
						break;
					case ".quad":
						inputLines.add(new Line(Long.toHexString(address),splitLine,sLine));
						address+=8;
						break;
					case ".align":
						address = address + address%Integer.parseInt(splitLine[1]);
						inputLines.add(new Line(Long.toHexString(address),splitLine,sLine));
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
