package GUI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import Sequential.ALU;
import Sequential.BYTE;
import Sequential.DoubleWord;
import Sequential.LittleEndian;
import Sequential.Memory;
import Sequential.Processor;
import Sequential.RegisterFile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;

public class YOTab extends Tab {

	String fileName;
	Button step, run, initialize;
	TextArea area;
	ScrollPane pane, displayPane;
	BorderPane border;
	GridPane registerDisplay;
	TabPane parent;
	HBox box;
	private BorderPane textBorder;
	private String inputText;
	private TextArea outputDisplay;
	private ScrollPane outputDisplayPane;
	private GridPane memDisplay;
	private ScrollPane memDisplayScrollPane;

	/**
	 * Creates a YO Tab to control processor running
	 * 
	 * @param parent    the holder of the tab
	 * @param fileName  the name of the file to run
	 * @param inputText the text to be displayed
	 * @param emb       the menu bar to be read from
	 */
	public YOTab(TabPane parent, String fileName, String inputText, EmulatorMenuBar emb) {
		this.parent = parent;
		border = new BorderPane();
		textBorder = new BorderPane();
		area = new TextArea(inputText);
		this.inputText = inputText;
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		area.setEditable(false);
		area.setPrefHeight((bounds.getHeight() - 175) / 2);
		area.setPrefWidth(bounds.getWidth() / 2);
		pane = new ScrollPane(area);
		pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		outputDisplay = new TextArea("Processor output:\n");
		outputDisplay.setEditable(false);
		outputDisplay.setPrefHeight((bounds.getHeight() - 175) / 2);
		outputDisplay.setPrefWidth(bounds.getWidth() / 2);
		outputDisplayPane = new ScrollPane(outputDisplay);
		outputDisplayPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		textBorder.setBottom(outputDisplayPane);
		textBorder.setTop(pane);

		registerDisplay = new GridPane();
		memDisplay = new GridPane();
		displayPane = new ScrollPane(registerDisplay);
		displayPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		box = new HBox();
		box.setPrefHeight(100);
		initialize = new Button("Initialize");
		initialize.setPrefHeight(100);
		initialize.setPrefWidth(100);
		initialize.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				if (emb.RDI_Input.isSelected() || emb.RSI_Input.isSelected())
					Processor.initializeInputs(emb.RDI_Input.isSelected(),
							((RadioMenuItem) emb.RDIGroup.getSelectedToggle()).getText(),
							emb.store_RDI_Length_RDX.isSelected(), emb.RSI_Input.isSelected(),
							((RadioMenuItem) emb.RSIGroup.getSelectedToggle()).getText(),
							emb.store_RSI_Length_RCX.isSelected());
				else
					Processor.initialize();
				refresh();
				initializeDisplay();
			}
		});
		step = new Button("Step");
		step.setPrefHeight(100);
		step.setPrefWidth(100);
		step.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.step();
				refresh();
				stepDisplay();
			}
		});
		run = new Button("Run");
		run.setPrefHeight(100);
		run.setPrefWidth(100);
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.run();
				refresh();
				runDisplay();
			}
		});

		memDisplayScrollPane = new ScrollPane(memDisplay);
		memDisplayScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		refresh();
		displayPane = new ScrollPane(registerDisplay);
		displayPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		box = new HBox();
		box.setPrefHeight(100);

		box.getChildren().addAll(initialize, step, run);
		border.setBottom(box);
		border.setLeft(textBorder);
		border.setRight(displayPane);
		border.setCenter(memDisplayScrollPane);
		this.setContent(border);
		this.setText(fileName);
	}

	/**
	 * Refreshes the GUI screen at each execution of the processor
	 */
	public void refresh() {
		registerDisplay.getChildren().clear();
		int row = 0;
		registerDisplay.add(new TextField("Processor info"), 0, row);
		row++;
		registerDisplay.add(new TextField("Status"), 0, row);
		registerDisplay.add(new TextField(Processor.status), 1, row);
		row++;
		for (String register : Processor.registerFile.keySet()) {
			TextField tf1 = new TextField(register);
			tf1.setEditable(false);
			registerDisplay.add(tf1, 0, row);
			TextField tf2 = new TextField("0x" + Processor.registerFile.get(register).displayToString());
			tf2.setEditable(false);
			registerDisplay.add(tf2, 1, row);
			row++;
		}
		TextField tx1 = new TextField("PC");
		tx1.setEditable(false);
		registerDisplay.add(tx1, 0, row);
		String PC = (Processor.PC != null) ? "0x" + Processor.PC.displayToString() : "N/A";
		TextField tx2 = new TextField(PC);
		tx2.setEditable(false);
		registerDisplay.add(tx2, 1, row);
		row++;

		TextField zx1 = new TextField("ZF");
		zx1.setEditable(false);
		registerDisplay.add(zx1, 0, row);
		String ZF = (ALU.ZF()) ? "1" : "0";
		TextField zx2 = new TextField("0x" + ZF);
		zx2.setEditable(false);
		registerDisplay.add(zx2, 1, row);
		row++;

		TextField sx1 = new TextField("SF");
		sx1.setEditable(false);
		registerDisplay.add(sx1, 0, row);
		String SF = (ALU.SF()) ? "1" : "0";
		TextField sx2 = new TextField("0x" + SF);
		zx2.setEditable(false);
		registerDisplay.add(sx2, 1, row);
		row++;

		TextField ox1 = new TextField("OF");
		ox1.setEditable(false);
		registerDisplay.add(ox1, 0, row);
		String OF = (ALU.OF()) ? "1" : "0";
		TextField ox2 = new TextField("0x" + OF);
		ox2.setEditable(false);
		registerDisplay.add(ox2, 1, row);

		String outputDisplay = modifiedDisplay();
		area.setText(outputDisplay);
		memDisplay.getChildren().clear();
		row = 2;
		Set<Long> usedAddresses = new HashSet<Long>();
		for (long address : Memory.memory.keySet()) {
			long modifiedAddress = address - address % 8;
			if (address < 0)
				modifiedAddress = address - ((8 + address % 8) % 8);
			if (!usedAddresses.contains(modifiedAddress)) {
				usedAddresses.add(modifiedAddress);
				LittleEndian value = Memory.loadDoubleWord(modifiedAddress);
				TextField tf1 = new TextField("0x" + Long.toHexString(modifiedAddress));
				tf1.setEditable(false);
				memDisplay.add(tf1, 0, row);
				TextField tf2 = new TextField(displayText(value));
				memDisplay.add(tf2, 1, row);
				row++;
			}
		}

	}

	/**
	 * Creates a display entry for step
	 */
	protected void stepDisplay() {
		if (Processor.initialized) {
			outputDisplay.setText(outputDisplay.getText() + "STEP:\n");
			if (!Processor.status.equals("AOK")) {
				if (Processor.exceptionGenerated)
					outputDisplay.setText(
							outputDisplay.getText() + "The processor exited with:\n" + Processor.exception + "\n");
				else {
					outputDisplay.setText(outputDisplay.getText() + "The program has completed its execution:\n");
					outputDisplay.setText(outputDisplay.getText() + "PC: " + displayText(Processor.PC) + "\n");
					outputDisplay.setText(outputDisplay.getText() + "Completed Instruction: "
							+ Processor.completedInstruction.buildDisplayInstruction() + "\n");
					outputDisplay.setText(outputDisplay.getText() + registerDisplay());
					outputDisplay.setText(outputDisplay.getText() + memoryDisplay());
					outputDisplay.setText(outputDisplay.getText()
							+ registerDifference(Processor.initialRegisterFile, Processor.finalRegisterFile, "FINAL"));
					outputDisplay.setText(outputDisplay.getText()
							+ memoryDifference(Processor.initialMemory, Processor.finalMemory, "FINAL"));
				}
			} else {
				outputDisplay.setText(
						outputDisplay.getText() + "PC: " + displayText(Processor.completedInstruction.address) + "\n");
				outputDisplay.setText(outputDisplay.getText() + "Completed Instruction: "
						+ Processor.completedInstruction.buildDisplayInstruction() + "\n");
				outputDisplay.setText(outputDisplay.getText()
						+ registerDifference(Processor.stepBeforeReg, Processor.stepAfterReg, "STEP"));
				outputDisplay.setText(outputDisplay.getText()
						+ memoryDifference(Processor.stepBeforeMem, Processor.stepAfterMem, "STEP"));
			}
		}
	}

	/**
	 * Creates a run display entry
	 */
	protected void runDisplay() {
		if (Processor.initialized) {
			outputDisplay.setText(outputDisplay.getText() + "RUN:\n");
			outputDisplay.setText(outputDisplay.getText() + "PC: " + displayText(Processor.PC) + "\n");
			if (Processor.exceptionGenerated)
				outputDisplay
						.setText(outputDisplay.getText() + "The processor exited with: " + Processor.exception + "\n");
			outputDisplay.setText(outputDisplay.getText() + registerDisplay());
			outputDisplay.setText(outputDisplay.getText() + memoryDisplay());
			outputDisplay.setText(outputDisplay.getText()
					+ registerDifference(Processor.initialRegisterFile, Processor.finalRegisterFile, "FINAL"));
			outputDisplay.setText(outputDisplay.getText()
					+ memoryDifference(Processor.initialMemory, Processor.finalMemory, "FINAL"));
		}
	}

	/**
	 * Creates an initialize display entry
	 */
	public void initializeDisplay() {
		outputDisplay.setText("Processor output:\n\n Initialize:\n");
		if (Processor.status.equals("HLT")) {
			outputDisplay.setText(outputDisplay.getText()
					+ "Program failed to initialize, check that all memory locations are valid");
		} else {
			outputDisplay.setText(outputDisplay.getText() + "PC: " + displayText(Processor.PC) + "\n\n");
			outputDisplay.setText(outputDisplay.getText() + registerDisplay() + "\n");
			outputDisplay.setText(outputDisplay.getText() + memoryDisplay() + "\n");
		}
	}

	/**
	 * Creates a register display string
	 * 
	 * @return the register file as a string
	 */
	private String registerDisplay() {
		String output = "Register File:\n";
		for (String reg : Processor.registerFile.keySet()) {
			output += String.format("%3s", reg) + " = " + displayText(Processor.registerFile.get(reg)) + "\n";
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
	private String registerDifference(TreeMap<String, DoubleWord> before, TreeMap<String, DoubleWord> after,
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
	 * The memory display as a string
	 * 
	 * @return represents the memory as a string
	 */
	private String memoryDisplay() {
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
	private String memoryDifference(TreeMap<Long, DoubleWord> before, TreeMap<Long, DoubleWord> after, String text) {
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
	 * Used to show which instruction is next in the sequential machine to be
	 * executed
	 * 
	 * @return the display output
	 */
	private String modifiedDisplay() {
		String output = "";
		Scanner scan = new Scanner(inputText);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String addressString = line.substring(line.indexOf("x") + 1, line.indexOf(":"));
			DoubleWord address = new DoubleWord(Long.parseLong(addressString, 16));
			String restOfLine = line.substring(line.indexOf(":") + 1);
			if (!restOfLine.contains(":") && !restOfLine.contains(".")) {
				if (Processor.PC.equals(address))
					output += ">";
				else
					output += "\u2002";
			} else {
				output += "\u2002";
			}

			output += " " + line + "\n";
		}
		scan.close();
		return output;
	}

	public static String displayText(LittleEndian val) {
		switch (EmulatorMenuBar.displaySetting) {
		case EmulatorMenuBar.SIGNED:
			return (val.calculateValueSigned() + " ");
		case EmulatorMenuBar.UNSIGNED:
			return val.calculateValueUnSigned();
		case EmulatorMenuBar.HEXLE:
			return val.generateHexLE();
		default:
			return "0x" + val.displayToString();
		}
	}

}
