package ui;
import library.Book;
import java.io.File;
import javafx.application.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import library.Book;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.*;
import javafx.event.*;

public class Main extends Application implements EventHandler<ActionEvent>{
	//Sample Books for Testing Table
	private TableView<Book> table = new TableView<Book>();
    private final ObservableList<Book> data =
        FXCollections.observableArrayList(
            new Book("Cat in the Hat", "Dr. Seuss", "K", "978-0394800011", "cat.txt")
            
        );
	
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
		//Table
		TableView lib1 = new TableView();
		lib1.setEditable(true);
        TableColumn titleCol = new TableColumn("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        TableColumn authorCol = new TableColumn("Author");
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        TableColumn isbnCol = new TableColumn("ISBN-13");
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("isbn"));
        TableColumn ageCol = new TableColumn("Age");
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("age"));
        TableColumn uniqueCol = new TableColumn("Unique Words");
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("uniqueWordCount"));
        TableColumn totalCol = new TableColumn("Total Words");
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("totalWordCount"));
        lib1.getColumns().addAll(titleCol, authorCol, isbnCol, ageCol, uniqueCol, totalCol);
        lib1.setItems(FXCollections.observableArrayList(data));
		//Tabs
		TabPane tabPane = new TabPane();
		Tab tab = new Tab();
		tab.setText("new lib");
		tab.setContent(lib1);
		tabPane.getTabs().add(tab);
		
		//Open/Save File Stuff
		final FileChooser fileChooser = new FileChooser();
		Stage stage = new Stage(); //For Open/Save File
		FileChooser openLD = new FileChooser(); 
		openLD.setTitle("Select Library or Dictionary XML File to Open");
		
		//Button Actions
		newLib.setOnAction( 
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						Scene newScene = new Scene(new Group());
						//Table Stuff
						TableView lib1 = new TableView();
						lib1.setEditable(true);
				        TableColumn titleCol = new TableColumn("Title");
				        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
				        TableColumn authorCol = new TableColumn("Author");
				        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
				        TableColumn isbnCol = new TableColumn("ISBN-13");
				        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("isbn"));
				        TableColumn ageCol = new TableColumn("Age");
				        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("age"));
				        TableColumn uniqueCol = new TableColumn("Unique Words");
				        titleCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("uniqueWordCount"));
				        TableColumn totalCol = new TableColumn("Total Words");
				        titleCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("totalWordCount"));
				        lib1.getColumns().addAll(titleCol, authorCol, isbnCol, ageCol, uniqueCol, totalCol);
				        lib1.setItems(FXCollections.observableArrayList(data));
				        //Tabs
						Tab tab = new Tab();
						tab.setText("new lib");
						tab.setContent(lib1);
						tabPane.getTabs().add(tab);
						
					}
				});
		
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
	                	submit.setOnAction(a-> System.out.println("Book Added"));
	                	
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
		filter.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						Stage stageFilter = new Stage();
	                	stageFilter.setTitle("Filter Books");
	                	GridPane grid = new GridPane();
	                	grid.setPadding(new Insets(10, 10, 10, 10));
	                	grid.setVgap(8);
	                	grid.setHgap(10);
	                	
	                	Button submit = new Button("Filter");
	                	submit.setOnAction(a-> System.out.println("Library Filtered"));
	                	
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
	                	
	                	Label uniqueLab = new Label("Unique Words: ");
	                	GridPane.setConstraints(uniqueLab, 0, 4);
	                	
	                	Label uniqueEqualLab = new Label("=");
	                	GridPane.setConstraints(uniqueEqualLab, 1,4);
	                	TextField uniqueEqualInput = new TextField();
	                	GridPane.setConstraints(uniqueEqualInput, 1, 5);
	                	
	                	Label uniqueGreaterLab = new Label(">");
	                	GridPane.setConstraints(uniqueGreaterLab, 1, 6);
	                	TextField uniqueGreaterInput = new TextField();
	                	GridPane.setConstraints(uniqueGreaterInput, 1, 7);
	                	
	                	Label uniqueLessLab = new Label("<");
	                	GridPane.setConstraints(uniqueLessLab, 1, 8);
	                	TextField uniqueLessInput = new TextField();
	                	GridPane.setConstraints(uniqueLessInput, 1, 9);
	                	
	                	Label totalLab = new Label("Total Words: ");
	                	GridPane.setConstraints(totalLab, 0, 10);
	                	
	                	Label totalEqualLab = new Label("=");
	                	GridPane.setConstraints(totalEqualLab, 1,10);
	                	TextField totalEqualInput = new TextField();
	                	GridPane.setConstraints(totalEqualInput, 1, 11);
	                	
	                	Label totalGreaterLab = new Label(">");
	                	GridPane.setConstraints(totalGreaterLab, 1, 12);
	                	TextField totalGreaterInput = new TextField();
	                	GridPane.setConstraints(totalGreaterInput, 1, 13);
	                	
	                	Label totalLessLab = new Label("<");
	                	GridPane.setConstraints(totalLessLab, 1, 14);
	                	TextField totalLessInput = new TextField();
	                	GridPane.setConstraints(totalLessInput, 1, 15);
	                	
	                	GridPane.setConstraints(submit, 1, 16);
	                	
	                	grid.getChildren().addAll(titleLab, titleInput, authLab, authInput, ageLab, ageInput, isbnLab, isbnInput, uniqueLab, uniqueEqualLab, uniqueEqualInput, uniqueGreaterLab, uniqueGreaterInput, uniqueLessLab, uniqueLessInput, totalLab, totalEqualLab, totalEqualInput, totalGreaterLab, totalGreaterInput, totalLessLab, totalLessInput, submit);
	                	Scene scene = new Scene(grid, 1000, 600);
	                    stageFilter.setScene(scene);
	                    stageFilter.show();
					}
				});;
		merge.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						Stage stageMerge = new Stage();
		            	stageMerge.setTitle("Merge Libraries");
		            	GridPane grid = new GridPane();
		            	grid.setPadding(new Insets(10, 10, 10, 10));
		            	grid.setVgap(8);
		            	grid.setHgap(10);
		            	
		            	ObservableList<String> options = 
		            		    FXCollections.observableArrayList(
		            		        "Library 1",
		            		        "Library 2",
		            		        "Library 3"
		            		    );
		            	ComboBox comboBox = new ComboBox(options);
		            	ComboBox comboBox2 = new ComboBox(options);
		            	Label lib1 = new Label("First Library: ");
	                	GridPane.setConstraints(lib1, 0, 0);
		            	GridPane.setConstraints(comboBox, 1, 0);
		            	
		            	Label lib2 = new Label("Library to Merge with: ");
		            	GridPane.setConstraints(lib2, 0,1);
		            	GridPane.setConstraints(comboBox2, 1, 1);
		            	
		            	Button submit = new Button("Merge");
		            	GridPane.setConstraints(submit, 0, 2);
		            	submit.setOnAction(a-> System.out.println("Libraries Merged"));
		            	grid.getChildren().addAll(lib1, comboBox, lib2, comboBox2, submit);
		            	Scene scene = new Scene(grid, 500, 500);
		                stageMerge.setScene(scene);
		                stageMerge.show();
		            }
		        });
		
		genD.setOnAction(e -> System.out.println("Generate Dictionary"));
		
		//Main Panes/Show Stage
		BorderPane mainPane = new BorderPane();
		mainPane.setTop(toolbar);
		mainPane.setCenter(tabPane);
		Scene mainScene = new Scene(mainPane, 1000, 500);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}
	@Override
	public void handle(ActionEvent event) {}

}
