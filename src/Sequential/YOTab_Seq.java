package Sequential;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import BaseEmulator.ALU;
import BaseEmulator.DisplayBuilder;
import BaseEmulator.DoubleWord;
import BaseEmulator.LittleEndian;
import BaseEmulator.Memory;
import GUI.EmulatorMenuBar;
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

public class YOTab_Seq extends Tab {

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
	public YOTab_Seq(TabPane parent, String fileName, String inputText, EmulatorMenuBar emb) {
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
					Processor_Seq.initializeInputs(emb.RDI_Input.isSelected(),
							((RadioMenuItem) emb.RDIGroup.getSelectedToggle()).getText(),
							emb.store_RDI_Length_RDX.isSelected(), emb.RSI_Input.isSelected(),
							((RadioMenuItem) emb.RSIGroup.getSelectedToggle()).getText(),
							emb.store_RSI_Length_RCX.isSelected());
				else
					Processor_Seq.initialize();
				refresh();
				initializeDisplay();
			}
		});
		step = new Button("Step");
		step.setPrefHeight(100);
		step.setPrefWidth(100);
		step.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor_Seq.step();
				refresh();
				stepDisplay();
			}
		});
		run = new Button("Run");
		run.setPrefHeight(100);
		run.setPrefWidth(100);
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor_Seq.run();
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
		registerDisplay.add(new TextField(Processor_Seq.status), 1, row);
		row++;
		for (String register : Processor_Seq.registerFile.keySet()) {
			TextField tf1 = new TextField(register);
			tf1.setEditable(false);
			registerDisplay.add(tf1, 0, row);
			TextField tf2 = new TextField("0x" + Processor_Seq.registerFile.get(register).displayToString());
			tf2.setEditable(false);
			registerDisplay.add(tf2, 1, row);
			row++;
		}
		TextField tx1 = new TextField("PC");
		tx1.setEditable(false);
		registerDisplay.add(tx1, 0, row);
		String PC = "0x" + Processor_Seq.PC.displayToString();
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
				TextField tf2 = new TextField(DisplayBuilder.displayText(value));
				memDisplay.add(tf2, 1, row);
				row++;
			}
		}

	}
	
	
	
	
	

	/**
	 * Creates a display entry for step
	 */
	protected void stepDisplay() {
		if (Processor_Seq.initialized) {
			outputDisplay.setText(outputDisplay.getText() + "STEP:\n");
			if (!Processor_Seq.status.equals("AOK")) {
				if (Processor_Seq.exceptionGenerated)
					outputDisplay.setText(
							outputDisplay.getText() + "The processor exited with:\n" + Processor_Seq.exception + "\n");
				else {
					
					String output = DisplayBuilder.stepCompletionDisplayBuilder(Processor_Seq.PC, Processor_Seq.completedInstruction,
							Processor_Seq.registerFile, Processor_Seq.exceptionGenerated, Processor_Seq.exception, 
							Processor_Seq.initialRegisterFile, Processor_Seq.finalRegisterFile, 
							Processor_Seq.initialMemory, Processor_Seq.finalMemory);
					
					outputDisplay.setText(outputDisplay.getText() + output);
				}
			} else {
				String output = DisplayBuilder.stepDisplayBuilder(Processor_Seq.completedInstruction.address, Processor_Seq.completedInstruction, 
						Processor_Seq.stepBeforeReg, Processor_Seq.stepAfterReg, Processor_Seq.stepBeforeMem, Processor_Seq.stepAfterMem);
				outputDisplay.setText(outputDisplay.getText() + output);
			}
		}
	}
	
	/**
	 * Creates a run display entry
	 */
	protected void runDisplay() {
		if (Processor_Seq.initialized) {
			String output = DisplayBuilder.runDisplayBuilder(Processor_Seq.PC, Processor_Seq.registerFile, 
					Processor_Seq.exceptionGenerated, Processor_Seq.exception, 
					Processor_Seq.initialRegisterFile, Processor_Seq.finalRegisterFile, 
					Processor_Seq.initialMemory, Processor_Seq.finalMemory);
			outputDisplay.setText(outputDisplay.getText() + output);
		}
	}

	
	
	/**
	 * Creates an initialize display entry
	 */
	public void initializeDisplay() {
		outputDisplay.setText("Processor output:\n\n Initialize:\n");
		if (Processor_Seq.status.equals("HLT")) {
			outputDisplay.setText(outputDisplay.getText()
					+ "Program failed to initialize, check that all memory locations are valid");
		} else {
			outputDisplay.setText(outputDisplay.getText() + "PC: " + DisplayBuilder.initializeDisplayBuilder(Processor_Seq.PC, Processor_Seq.registerFile));
		}
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
		return output;
	}

}
