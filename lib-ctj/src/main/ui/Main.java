package main.ui;
import main.library.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.application.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.*;
import javafx.event.*;

public class Main extends Application implements EventHandler<ActionEvent>{
	private LibTabs libTabs = new LibTabs();
	private DictTabs dictTabs = new DictTabs();
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
		
		//Tabs
		TabPane tabPane = new TabPane();
		
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
						//Table Stuff
						Library libObj = new Library();
						LibTab libTab = new LibTab(libObj, tabPane);
				        //Tabs
						libTabs.addLibTab(libTab.getTab(), libTab);
						libTab.setName("Lib" + libTabs.getTabCount());
					}
				});
		
		open.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                	FileChooser chooser = new FileChooser();
	                	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString()+"/test_files";
	                	chooser.setInitialDirectory(new File(currentPath));
	                	ExtensionFilter xml = new ExtensionFilter("XML Files", "*.xml");
	                	chooser.getExtensionFilters().add(xml);
	                    File file = chooser.showOpenDialog(stage);
	                    if (file != null) {
	                    	Document doc;
	                    	Element root;
	                    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	                		DocumentBuilder dBuilder;
	                    	try {
	                    		dBuilder = dbFactory.newDocumentBuilder();
	                			doc = dBuilder.parse(file.getPath());
	                			doc.getDocumentElement().normalize();
	                			
	                			root = doc.getDocumentElement();
	                			if(root.getTagName().equals("Literature")) {
	                				Library libObj = new Library(file.toPath());
	    	                        LibTab libTab = new LibTab(libObj, tabPane);
	    	                        libTab.setName(libObj.getPath().getFileName().toString());
	    	                        libTabs.addLibTab(libTab.getTab(), libTab);
	                			}
	                			if(root.getTagName().equals("Dictionary")) {
	                				Dictionary dictObj = new Dictionary(file.toPath());
	    	                    	DictTab dictTab = new DictTab(dictObj, tabPane);
	    	                    	dictTab.setName(dictObj.getPath().getFileName().toString());
	    	                    	dictTabs.addDictTab(dictTab.getTab(), dictTab);
	                			}
	                        } 
	                    	catch (Exception ex) {
	                            
	                        }
	                    }
	                }
	            });
		save.setOnAction(
				new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                	FileChooser chooser = new FileChooser();
	                	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString()+"/test_files";
	                	chooser.setInitialDirectory(new File(currentPath));
	                    File file = chooser.showSaveDialog(stage);
	                    Tab tab = tabPane.getSelectionModel().getSelectedItem();
	                    LibTab libTab = libTabs.getLibTab(tab);
	                    Library libObj = libTab.getLib();
	                    libObj.save(file.toPath());
	                    
	                    
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
	                	
	                	TextField filePath = new TextField();
	                	GridPane.setConstraints(filePath, 2, 4);
	                	
	                	Button browse = new Button("Browse");
	                	browse.setOnAction(new EventHandler<ActionEvent>() {
	                		 public void handle(final ActionEvent e) {
	     	                	FileChooser chooser = new FileChooser();
	     	                	ExtensionFilter txt = new ExtensionFilter("Text Files", "*.txt");
	    	                	chooser.getExtensionFilters().add(txt);
	     	                	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() +"/test_files";
	     	                	chooser.setInitialDirectory(new File(currentPath));
	     	                    File file = chooser.showOpenDialog(stage);
	     	                    if (file != null) {
	     	                        filePath.setText(file.getPath());
	     	                    }
	     	                }
	                	});
	                	
	                	Button submit = new Button("Submit");
	                	submit.setOnAction(new EventHandler<ActionEvent>() {
	    	                @Override
	    	                public void handle(final ActionEvent a) {
	    	                	if(!tabPane.getTabs().isEmpty()) {
	    	                		Tab tab = tabPane.getSelectionModel().getSelectedItem();
	    	                		LibTab libTab = libTabs.getLibTab(tab);
	    	                		//When Null Pointer Exception Fixed, move .close() to next line after .addNewBook()
	    	                		stageAdd.close();
	    	                		libTab.addNewBook(new Book(titleInput.getText(), authInput.getText(), ageInput.getText(), isbnInput.getText(), filePath.getText()));
		    	                    
	    	                	}
	    	                }
	    	            });
	                	GridPane.setConstraints(browse, 1, 4);
	                	GridPane.setConstraints(submit, 1, 5);
	                	
	                	grid.getChildren().addAll(titleLab, titleInput, authLab, authInput, ageLab, ageInput, isbnLab, isbnInput, fileLab, browse, submit);
	                	Scene scene = new Scene(grid, 500, 500);
	                    stageAdd.setScene(scene);
	                    stageAdd.show();
	                }
	            });
		delB.setOnAction(
			new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					Tab tab = tabPane.getSelectionModel().getSelectedItem();
					LibTab libTab = libTabs.getLibTab(tab);
					Library lib = libTab.getLib();
					for(int i = 0; i < lib.size(); i++) {
						if(libTab.getLibTable().getSelectionModel().getSelectedItems().contains(lib.getBook(i))) {
							libTab.delBook(i);
						}
					}
					
					
				}
			});
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
				});
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
		            	
		            	ObservableList<Library> options = 
		            		    FXCollections.observableArrayList(
		            		        
		            		    );
		            	ObservableList<Tab> tabs = tabPane.getTabs();
		            	for(int i = 0; i < tabs.size(); i++) {
		            		options.add(libTabs.getLibTab(tabs.get(i)).getLib());
		            	}
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
