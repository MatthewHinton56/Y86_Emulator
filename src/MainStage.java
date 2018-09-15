import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainStage extends Application implements EventHandler<ActionEvent>{
	
	public YSTab ystab;
	public YOTab yotab;
	public TabPane pane;
	@Override
	public void start(Stage primaryStage) throws Exception {

		pane = new TabPane();
		//ystab = new YSTab(pane,"test","",this);
		//pane.getTabs().add(ystab);
		//Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		EmulatorMenuBar emb = new EmulatorMenuBar(this);
		BorderPane border = new BorderPane();
		border.setTop(emb);
		border.setCenter(pane);
		Scene scene = new Scene(border);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setMinHeight(500);
		primaryStage.setMinWidth(500);
		primaryStage.show();
	}

	@Override
	public void handle(ActionEvent arg0) {
		String input = ystab.area.getText();
		String output = Compiler.compile(input);
		pane.getTabs().remove(yotab);
		yotab = new YOTab(pane,ystab.fileName.substring(0,ystab.fileName.indexOf(".")) +".yo",output);
		pane.getTabs().add(yotab);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
