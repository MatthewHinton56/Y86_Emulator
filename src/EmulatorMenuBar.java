import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class EmulatorMenuBar extends MenuBar {
	
	public MainStage mainStage;
	public Menu file;
	public MenuItem newButton, saveButton, loadButton;
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
		            	mainStage.ystab = new YSTab(mainStage.pane,result.get(),"",mainStage);
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
							mainStage.ystab = new YSTab(mainStage.pane,selectedFile.getName(),input,mainStage);
			            	mainStage.pane.getTabs().add(mainStage.ystab);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		    }
		});
		
		
		file.getItems().addAll(newButton, saveButton, loadButton);
		this.getMenus().add(file);
	}
	
	
	
}
