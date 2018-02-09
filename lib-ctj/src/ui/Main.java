package ui;
import java.io.File;
import javafx.application.*;
import javafx.stage.*;
import library.Book;
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
		final FileChooser fileChooser = new FileChooser();
		primaryStage.setTitle("libctj");
		Stage stage = new Stage();
		FileChooser openLD = new FileChooser();
		openLD.setTitle("Select Library or Dictionary XML File to Open");
		ToolBar toolbar = new ToolBar(newLib, open, save, delete, addB, delB, filter, merge, genD);
	
		
		newLib.setOnAction(e -> System.out.println("Thanks Obama"));
		open.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    File file = fileChooser.showOpenDialog(stage);
	                }
	            });
		save.setOnAction(
				new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    File file = fileChooser.showSaveDialog(stage);
	                }
	            });
		delete.setOnAction(e -> System.out.println("Deleted Library"));
		addB.setOnAction(
				new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                	Stage stageAdd = new Stage();
	                	stageAdd.setTitle("Add Book");
	                	GridPane grid = new GridPane();
	                	grid.setPadding(new Insets(10, 10, 10, 10));
	                	grid.setVgap(8);
	                	grid.setHgap(10);
	                	
	                	Button browse = new Button("Browse");
	                	browse.setOnAction(a -> fileChooser.showOpenDialog(stage));
	                	
	                	Button submit = new Button("Submit");
	                	submit.setOnAction(a-> System.out.println("Bok Added"));
	                	
	                	Label titleLab = new Label("Title: ");
	                	GridPane.setConstraints(titleLab, 0, 0);
	                	
	                	TextField titleInput = new TextField();
	                	GridPane.setConstraints(titleInput, 1, 0);
	                	
	                	Label authLab = new Label("Author: ");
	                	GridPane.setConstraints(authLab, 0, 1);
	                	
	                	TextField authInput = new TextField();
	                	GridPane.setConstraints(authInput, 1, 1);
	                	
	                	
	                	Label ageLab = new Label("Age: ");
	                	GridPane.setConstraints(ageLab, 0, 2);
	                	
	                	TextField ageInput = new TextField();
	                	GridPane.setConstraints(ageInput, 1, 2);
	                	
	                	Label isbnLab = new Label("ISBN: ");
	                	GridPane.setConstraints(isbnLab, 0, 3);
	                	
	                	TextField isbnInput = new TextField();
	                	GridPane.setConstraints(isbnInput, 1, 3);
	                	
	                	Label fileLab = new Label("Text File: ");
	                	GridPane.setConstraints(fileLab, 0, 4);
	                	
	                	GridPane.setConstraints(browse, 1, 4);
	                	GridPane.setConstraints(submit, 1, 5);
	                	
	                	grid.getChildren().addAll(titleLab, titleInput, authLab, authInput, ageLab, ageInput, isbnLab, isbnInput, fileLab, browse, submit);
	                	Scene scene = new Scene(grid, 500, 500);
	                    stageAdd.setScene(scene);
	                    stageAdd.show();
	                }
	            });
		delB.setOnAction(e -> System.out.println("Books Deleted"));
		filter.setOnAction(e -> System.out.println("Libraries Filtered"));
		merge.setOnAction(e -> System.out.println("Libraries Merged"));
		genD.setOnAction(e -> System.out.println("Generate Dictionary"));
		BorderPane mainPane = new BorderPane();
		mainPane.setTop(toolbar);
		Scene mainScene = new Scene(mainPane, 1000, 500);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}
	@Override
	public void handle(ActionEvent event) {}

}
