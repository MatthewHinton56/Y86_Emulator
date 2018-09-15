import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Compiler {

	public static final HashMap<Long, String[]> COMPILED_INSTRUCTIONS =  new HashMap<Long, String[]>(); 
	public static final HashMap<Long, DoubleWord> COMPILED_CONSTANTS = new HashMap<Long, DoubleWord>(); 
	public static String start_address;
	public static boolean compiled;

	public static String compile(String input) {
		preprocessor(input);
		compiled = true;
		COMPILED_INSTRUCTIONS.clear();
		COMPILED_CONSTANTS.clear();
		start_address = inputLines.get(0).address;
		String output = "";
		String offset,rB;
		String[] instruction;
		for(Line l: inputLines) {
			output += "0x" + l.address +": ";
			switch(l.splitLine[0]) {
			case ".quad":
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
				break;
			case "halt":
			case "ret":
			case "nop":
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], null, null, null);
				output += convertArrayToString(instruction);
				COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
				break;
			case "pushq":
			case "popq":
				instruction = InstructionBuilder.getInstruction(l.splitLine[0], l.splitLine[1],  "No register", null);
				output += convertArrayToString(instruction);
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
				output += convertArrayToString(instruction);
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
				output += convertArrayToString(instruction);
				COMPILED_INSTRUCTIONS.put(Long.parseLong(l.address, 16),instruction);
				break;
			case "irmovq":
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
				break;	

			case "rmmovq": 
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
				break;	

			case "mrmovq": 
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
				break;		
			}
			output+= " "+l.line+"\n";
			//System.out.println(output);
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
				//System.out.println("");
			}
		}
	}

	private static HashMap<String, String> TAG_TO_ADDRESS = new HashMap<String, String>(); 
	private static ArrayList<Line> inputLines = new ArrayList<Line>();
	public static void main(String[] args) throws FileNotFoundException {
		//String input = ".pos 0\nirmovq stack, %rsp\nrrmovq %rsp, %rbp\nirmovq src, %rdi\nirmovq dest, %rsi\nirmovq $3, %rdx\ncall copy_block\nhalt";
		Scanner scan = new Scanner(new File("copy.ys"));
		String input = "";
		while(scan.hasNextLine()) {
			input+=scan.nextLine()+"\n";
		}
		System.out.println(compile(input));
		//System.out.println(Compiler.start_address);
		//System.out.println(Compiler.COMPILED_CONSTANTS);
		//System.out.println(Compiler.COMPILED_INSTRUCTIONS);
		Processor.initialize();
		Processor.run();
		System.out.println(Processor.registerFile.get("%rax"));
		System.out.println(Processor.PC.calculateValueSigned());
	}

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
