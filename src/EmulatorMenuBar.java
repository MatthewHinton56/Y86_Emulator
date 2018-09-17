import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class EmulatorMenuBar extends MenuBar implements EventHandler<ActionEvent> {
	
	public static final String HEX = "Hex";
	public static final String UNSIGNED = "Unsigned";
	public static final String SIGNED = "Signed";
	public static final String HEXLE = "Hex LE";

	public MainStage mainStage;
	public Menu file, options;
	public MenuItem newButton, saveButton, loadButton;
	public RadioMenuItem hex, unsigned, signed, hexLe;
	public ToggleGroup group;
	public static String displaySetting = HEX;
	//IF length is not stored, array is terminated with zero, and all other values will not be zero.
	public RadioMenuItem RDI_Input, RSI_Input, store_RDI_Length_RDX, store_RSI_Length_RCX;
	public Menu parameters, RDI_Settings, RSI_Settings, RDI_Length, RSI_Length;//Array can be between 0 and 30 elements.
	public ToggleGroup RDIGroup, RSIGroup;
	private Menu instructions;
	
	public EmulatorMenuBar(MainStage mainStage) {
		this.mainStage = mainStage;
		file = new Menu("File");
		newButton = new MenuItem("New");
		newButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        mainStage.pane.getTabs().clear();
		        mainStage.yotab = null;
		        TextInputDialog dialog = new TextInputDialog("file");
		        dialog.setTitle("File Name");
		        dialog.setHeaderText(".ys appended for you");
		        dialog.setContentText("Please enter File Name:");

		        // Traditional way to get the response value.
		        Optional<String> result = dialog.showAndWait();
		        if (result.isPresent()){
		            	mainStage.ystab = new YSTab(mainStage.pane,result.get() +".ys","",mainStage);
		            	mainStage.pane.getTabs().add(mainStage.ystab);
		        }
		    }
		});
		saveButton = new MenuItem("Save");
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	try {
		    		if(mainStage.ystab != null) {
		    		FileChooser fileChooser = new FileChooser();
		            fileChooser.setTitle("Save file");
		            fileChooser.setInitialFileName(mainStage.ystab.fileName);
		            File file = fileChooser.showSaveDialog(null);
		            FileWriter writer = new FileWriter(file);
		            writer.write(mainStage.ystab.area.getText());
		            writer.close();
		    		}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
		    }
		});
		loadButton = new MenuItem("Load");
		loadButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	FileChooser fileChooser = new FileChooser();
		    	 fileChooser.setTitle("Open Resource File");
		    	 fileChooser.getExtensionFilters().addAll(
		    	         new ExtensionFilter("Y86 - Assembler", "*.ys"));
		    	 File selectedFile = fileChooser.showOpenDialog(null);

		        // Traditional way to get the response value.
		            	try {
							Scanner scan = new Scanner(selectedFile);
							String input = "";
							while(scan.hasNextLine()) {
								input+=scan.nextLine()+"\n";
							}
							scan.close();
							mainStage.ystab = new YSTab(mainStage.pane,selectedFile.getName(),input,mainStage);
			            	mainStage.pane.getTabs().add(mainStage.ystab);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		    }
		});
		options = new Menu("Options");
		hex = new RadioMenuItem(HEX);
		unsigned = new RadioMenuItem(UNSIGNED);
		signed = new RadioMenuItem(SIGNED);
		hexLe = new RadioMenuItem(HEXLE);
		
		group = new ToggleGroup();
		hex.setToggleGroup(group);
		unsigned.setToggleGroup(group);
		signed.setToggleGroup(group);
		hexLe.setToggleGroup(group);
		
		hex.setOnAction(this);
		signed.setOnAction(this);
		unsigned.setOnAction(this);
		hexLe.setOnAction(this);
		
		constructParameterOptions();
		
		instructions = new Menu("Instructions");
		for(String instruction: Instruction.INSTRUCTION_TO_ARCHETYPE.keySet()) {
			MenuItem item = new MenuItem(instruction);
			item.setOnAction(this);
			instructions.getItems().add(item);
		}
		
		hex.setSelected(true);
		options.getItems().addAll(hex, signed, unsigned, hexLe);
		file.getItems().addAll(newButton, saveButton, loadButton);
		this.getMenus().addAll(file, options,instructions, parameters);
	}
	
	private void constructParameterOptions() {
		parameters = new Menu("Parameters");
		RDI_Settings = new Menu("RDI Settings");
		RDI_Input = new RadioMenuItem("RDI On");
		RDI_Length = new Menu("Select Length");
		RDIGroup = new ToggleGroup();
		for(int i = 0; i <= 30; i++) {
			RadioMenuItem lengthOption = new RadioMenuItem(i+"");
			lengthOption.setToggleGroup(RDIGroup);
			if(i == 1)
				lengthOption.setSelected(true);
			RDI_Length.getItems().add(lengthOption);
		}
		store_RDI_Length_RDX = new RadioMenuItem("Store length of RDI in RDX");
		store_RDI_Length_RDX.setSelected(true);
		
		RDI_Settings.getItems().addAll(RDI_Input, RDI_Length, store_RDI_Length_RDX);
		
		RSI_Settings = new Menu("RSI Settings");
		RSI_Input = new RadioMenuItem("RSI On");
		RSI_Length = new Menu("Select Length");
		RSIGroup = new ToggleGroup();
		for(int i = 0; i <= 30; i++) {
			RadioMenuItem lengthOption = new RadioMenuItem(i+"");
			lengthOption.setToggleGroup(RSIGroup);
			if(i == 1)
				lengthOption.setSelected(true);
			RSI_Length.getItems().add(lengthOption);
		}
		store_RSI_Length_RCX = new RadioMenuItem("Store length of RSI in RCX");
		store_RSI_Length_RCX.setSelected(true);
		
		RSI_Settings.getItems().addAll(RSI_Input, RSI_Length, store_RSI_Length_RCX);
		parameters.getItems().addAll(RDI_Settings, RSI_Settings);
	}

	public void handle(ActionEvent arg0) {
		displaySetting = ((RadioMenuItem)group.getSelectedToggle()).getText();
		if(mainStage.yotab != null) 
			mainStage.yotab.refresh();
		if(mainStage.ystab != null && arg0.getSource() instanceof MenuItem && !(arg0.getSource() instanceof RadioMenuItem)) {
			MenuItem item = (MenuItem)arg0.getSource();
				String archetype = Instruction.INSTRUCTION_TO_ARCHETYPE.get(item.getText());
				mainStage.ystab.area.setText(mainStage.ystab.area.getText()+archetype+"\n");
		}
	}
	
	
	
}
