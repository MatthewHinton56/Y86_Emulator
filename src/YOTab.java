import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
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
	Button step,run,initialize;
	TextArea area;
	ScrollPane pane, displayPane;
	BorderPane border;
	GridPane registerDisplay;
	TabPane parent;
	HBox box;
	public YOTab(TabPane parent, String fileName, String inputText) {
		this.parent = parent;
		border = new BorderPane();
		area = new TextArea(inputText);
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		area.setPrefHeight(bounds.getHeight()-175);
		area.setPrefWidth(bounds.getWidth()/2);
		pane = new ScrollPane(area);
		pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		registerDisplay = new GridPane();
		refresh();
		displayPane = new ScrollPane(registerDisplay);
		displayPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		box = new HBox();
		box.setPrefHeight(100);
		initialize = new Button("Initialize");
		initialize.setPrefHeight(100);
		initialize.setPrefWidth(100);
		initialize.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.initialize();
				refresh();
			}
		});
		step = new Button("Step");
		step.setPrefHeight(100);
		step.setPrefWidth(100);
		step.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.step();
				refresh();
			}
		});
		run = new Button("Run");
		run.setPrefHeight(100);
		run.setPrefWidth(100);
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.run();
				refresh();
			}
		});
		box.getChildren().addAll(initialize,step,run);
		border.setBottom(box);
		border.setLeft(pane);
		border.setRight(displayPane);
		this.setContent(border);
		this.setText(fileName);
	}
	
	public void refresh() {
		registerDisplay.getChildren().clear();
		int row = 0;
		for(String register: Processor.registerFile.keySet()) {
			registerDisplay.add(new TextField(register), 0, row);
			registerDisplay.add(new TextField("0x"+Processor.registerFile.get(register).displayToString()), 1, row);
			row++;
		}
		registerDisplay.add(new TextField("PC"), 0, row);
		String PC = (Processor.PC != null) ? "0x"+Processor.PC.displayToString() : "N/A";
		registerDisplay.add(new TextField(PC), 1, row);
		row++;
		registerDisplay.add(new TextField("ZF"), 0, row);
		String ZF = (ALU.ZF()) ? "1" : "0";
		registerDisplay.add(new TextField("0x"+ZF), 1, row);
		row++;
		registerDisplay.add(new TextField("SF"), 0, row);
		String SF = (ALU.SF()) ? "1" : "0";
		registerDisplay.add(new TextField("0x"+SF), 1, row);
		row++;
		registerDisplay.add(new TextField("OF"), 0, row);
		String OF = (ALU.OF()) ? "1" : "0";
		registerDisplay.add(new TextField("0x"+OF), 1, row);
	}
	
	
	
}
