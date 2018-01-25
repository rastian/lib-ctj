package ui;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.event.*;

public class Main extends Application implements EventHandler<ActionEvent>{

	Button newLib = new Button("New Library");
	Button open = new Button("Open");
	Button save = new Button("Save");
	Button delete = new Button("Delete");
	Button addB = new Button("Add Book");
	Button delB = new Button("Delete Books");
	Button filter = new Button("Filter Books");
	Button merge = new Button("Merge Libraries");
	Button genD = new Button("Generate Dictionaries");
	
	
	public static void main(String[] args) {
		launch(args);

	}
	public void start(Stage primaryStage) throws Exception{
		primaryStage.setTitle("libctj");
		ToolBar toolbar = new ToolBar(newLib, open, save, delete, addB, delB, filter, merge, genD);
		
		newLib.setOnAction(e -> System.out.println("Thanks Obama"));
		open.setOnAction(e -> System.out.println("Opened"));
		save.setOnAction(e -> System.out.println("Saved"));
		delete.setOnAction(e -> System.out.println("Deleted"));
		addB.setOnAction(e -> System.out.println("Added Book"));
		delB.setOnAction(e -> System.out.println("Books Deleted"));
		filter.setOnAction(e -> System.out.println("Libraries Filtered"));
		merge.setOnAction(e -> System.out.println("Libraries Merged"));
		genD.setOnAction(e -> System.out.println("Generate Dictionary"));
		BorderPane pane = new BorderPane();
		pane.setTop(toolbar);
		Scene scene = new Scene(pane, 1000, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	@Override
	public void handle(ActionEvent event) {}

}
