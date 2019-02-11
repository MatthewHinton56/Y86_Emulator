import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;

public class YSTab extends Tab {

	String fileName;
	Button compile;
	TextArea area;
	TextArea output;
	ScrollPane pane, outputDisplayPane;
	BorderPane border;
	TabPane parent;
	HBox box;

	/**
	 * Creates a YS text editor tab with compilation functionality
	 * 
	 * @param parent    the holder of this tab
	 * @param fileName  the name of the file to be read
	 * @param inputText the text to be placed into the text editor
	 * @param handler   the handler to control functionality
	 */
	public YSTab(TabPane parent, String fileName, String inputText, EventHandler<ActionEvent> handler) {
		this.fileName = fileName;
		this.parent = parent;
		border = new BorderPane();
		area = new TextArea(inputText);
		output = new TextArea("Compiler Output:\n");
		output.setEditable(false);
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		area.setPrefHeight(bounds.getHeight() - 175);
		area.setPrefWidth(bounds.getWidth());
		output.setPrefWidth(bounds.getWidth() / 4);
		pane = new ScrollPane(area);
		pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		outputDisplayPane = new ScrollPane(output);
		outputDisplayPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		box = new HBox();
		box.setPrefHeight(100);
		compile = new Button("Compile");
		compile.setOnAction(handler);
		compile.setPrefHeight(100);
		compile.setPrefWidth(100);
		box.getChildren().addAll(compile, output);
		border.setBottom(box);
		border.setCenter(pane);
		border.setRight(outputDisplayPane);
		this.setContent(border);
		this.setText(fileName);
		this.setOnCloseRequest(new EventHandler<Event>() {
			@Override
			public void handle(Event arg0) {
				((MainStage) handler).ystab = null;
			}
		});

	}

}
