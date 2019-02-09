import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainStage extends Application implements EventHandler<ActionEvent>{
	
	public YSTab ystab;
	public YOTab yotab;
	public TabPane pane;
	public EmulatorMenuBar emb;
	@Override
	public void start(Stage primaryStage) throws Exception {

		pane = new TabPane();
		//ystab = new YSTab(pane,"test","",this);
		//pane.getTabs().add(ystab);
		//Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		emb = new EmulatorMenuBar(this);
		BorderPane border = new BorderPane();
		border.setTop(emb);
		border.setCenter(pane);
		Scene scene = new Scene(border);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setMinHeight(500);
		primaryStage.setMinWidth(750);
		primaryStage.setTitle("Y86 Emulator");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("y86icon.png")));
		//primaryStage.setResizable(false);
		primaryStage.show();
	}

	@Override
	public void handle(ActionEvent arg0) {
		String input = ystab.area.getText();
		pane.getTabs().remove(yotab);
		ystab.output.setText("Compiler Output:\n");
		String output;
		try {
		output = Compiler.compile(input, ystab.output);
		if(output.length() == 0) {
			ystab.output.setText(ystab.output.getText() + "\n Nothing provided to compile");
		} else {
			
		ystab.output.setText(ystab.output.getText() + "\n Assembly compiled and ready for emulation in yotab" );
		
		Processor.clear();
		yotab = new YOTab(pane,ystab.fileName.substring(0,ystab.fileName.indexOf(".")) +".yo", output, emb);
		yotab.refresh();
		pane.getTabs().add(yotab);
		}
		}
		catch(IllegalArgumentException e) {
			ystab.output.setText(ystab.output.getText() + "Compiler Error Output:\n" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
