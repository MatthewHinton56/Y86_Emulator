import javafx.event.ActionEvent;
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
	ScrollPane pane;
	BorderPane border;
	TabPane parent;
	HBox box;
	
	public YSTab(TabPane parent, String fileName, String inputText, EventHandler<ActionEvent> handler) {
		this.fileName = fileName;
		this.parent = parent;
		border = new BorderPane();
		area = new TextArea(inputText);
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		area.setPrefHeight(bounds.getHeight()-175);
		area.setPrefWidth(bounds.getWidth());
		pane = new ScrollPane(area);
		pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		box = new HBox();
		box.setPrefHeight(100);
		compile = new Button("Compile");
		compile.setOnAction(handler);
		compile.setPrefHeight(100);
		compile.setPrefWidth(100);
		box.getChildren().add(compile);
		border.setBottom(box);
		border.setCenter(pane);
		this.setContent(border);
		this.setText(fileName);
	}
	
}
