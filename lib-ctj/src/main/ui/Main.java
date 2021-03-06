package main.ui;
import main.library.*;
import main.library.Library.FilterFuncs;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.application.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.*;
import javafx.event.*;

public class Main extends Application implements EventHandler<ActionEvent>{
	private TabIdentifier tabID = new TabIdentifier();
	private LibTabs libTabs = new LibTabs();
	private DictTabs dictTabs = new DictTabs();
    Button newLib = new Button("New Library");
	Button open = new Button("Open");
	Button save = new Button("Save");
	Button saveCSV = new Button("Save as CSV");
	Button addB = new Button("Add Book");
	Button delB = new Button("Delete Books");
	Button filter = new Button("Filter Books");
	Button merge = new Button("Merge Libraries");
	Button genD = new Button("Generate Dictionary");
	FileChooser openChooser = new FileChooser();
	FileChooser saveChooser = new FileChooser();
	FileChooser csvChooser = new FileChooser();
	FileChooser bookChooser = new FileChooser();
	FileChooser cmuChooser = new FileChooser();
	FileChooser masterDictChooser = new FileChooser();
	FileChooser buildDictExeChooser = new FileChooser();
	FileChooser newDictChooser = new FileChooser();
	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception{
		primaryStage.setTitle("LIB-CTJ");
		ToolBar toolbar = new ToolBar(newLib, open, save, saveCSV, addB, delB, filter, merge, genD);
		
		//File Chooser options, necessary to remember previously used directory
		openChooser.setTitle("Open Library or Dictionary");
    	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
    	openChooser.setInitialDirectory(new File(currentPath));
    	ExtensionFilter xml = new ExtensionFilter("XML Files", "*.xml");
    	openChooser.getExtensionFilters().add(xml);
    	
    	saveChooser.setTitle("Save Library");
    	saveChooser.setInitialDirectory(new File(currentPath));
    	saveChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
    	
     	csvChooser.setTitle("Save Dictionary CSV");
     	ExtensionFilter csv = new ExtensionFilter("CSV Files", "*.csv");
    	csvChooser.getExtensionFilters().add(csv);
     	csvChooser.setInitialDirectory(new File(currentPath));
     	
     	bookChooser.setTitle("Select Book Text File");
     	ExtensionFilter txt = new ExtensionFilter("Text Files", "*.txt");
    	bookChooser.getExtensionFilters().add(txt);
     	bookChooser.setInitialDirectory(new File(currentPath));
     	
     	cmuChooser.setTitle("Select CMU");
    	cmuChooser.getExtensionFilters().add(txt);
     	cmuChooser.setInitialDirectory(new File(currentPath));
     	
     	masterDictChooser.setTitle("Select Master Dictionary");
    	masterDictChooser.getExtensionFilters().add(xml);
     	masterDictChooser.setInitialDirectory(new File(currentPath));
     	
     	buildDictExeChooser.setTitle("Select build_dictionary Executable");
     	buildDictExeChooser.setInitialDirectory(new File(currentPath));
     	
     	newDictChooser.setTitle("Select Dictionary Directory");
    	newDictChooser.getExtensionFilters().add(xml);
     	newDictChooser.setInitialDirectory(new File(currentPath));
     	
		//Tabs
		TabPane tabPane = new TabPane();
		
		//Open/Save File Stuff
		Stage stage = new Stage(); //For Open/Save File, and Lib and Dict Tabs
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				if(!libTabs.isSaved()) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
	            	alert.setTitle("Unsaved Libraries");
	            	alert.setHeaderText("You have unsaved libraries. Do you want to leave without saving?");
	            	ButtonType yes = new ButtonType("Yes");
	            	ButtonType no = new ButtonType("No");
	            	alert.getButtonTypes().setAll(yes, no);
	            	Optional <ButtonType> result = alert.showAndWait();
	            	if(result.get() == no) {
	            		e.consume();
	            	}
				}
				
			}
		});
		filter.disableProperty().set(libTabs.isEmpty());
		merge.disableProperty().set(libTabs.isEmpty());
		saveCSV.setDisable(true);
		genD.setDisable(true);
		addB.setDisable(true);
		delB.setDisable(true);
		save.setDisable(true);
		//Button Actions
		newLib.setOnAction( 
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						
						Library libObj = new Library();
						LibTab libTab = new LibTab(libObj, tabPane, stage);
				        //Tabs
						tabID.addID(libTab.getTab(), "Lib");
						libTabs.addLibTab(libTab.getTab(), libTab);
						libTab.getTab().setOnClosed(new EventHandler<Event>() {
							@Override
						    public void handle(Event e) 
						    {
								if(libTabs.getTabCount()==1) {
									genD.setDisable(true);
									filter.setDisable(true);
									addB.setDisable(true);
									delB.setDisable(true);
									merge.setDisable(true);
									save.setDisable(true);
								}
								libTabs.deleteLibTab(libTab.getTab(), libTab);
								System.out.println(libTabs.getTabCount());
								
						    }
						});
						Tab tab = libTab.getTab();
						SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
						selectionModel.select(tab);
						saveCSV.setDisable(true);
                        addB.setDisable(false);
                        delB.setDisable(false);
                        save.setDisable(false);
                        filter.setDisable(false);
                        merge.setDisable(false);
                        genD.setDisable(false);
						tab.setOnSelectionChanged(event -> {
                            if (tab.isSelected()) {
                                saveCSV.setDisable(true);
                                addB.setDisable(false);
                                delB.setDisable(false);
                                save.setDisable(false);
                            	merge.setDisable(false);
                            	genD.setDisable(false);
                                if(libTabs.getTabCount()>=1)
                                	filter.setDisable(false);
                               
                            }
                        });
						libTab.setName("Lib" + libTabs.getTabCount());
						libObj.setName(libTab.getName());
					}
				});
		
		open.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                	
	                    File file = openChooser.showOpenDialog(stage);
	                    if (file != null) {
	                    	openChooser.setInitialDirectory(file.getParentFile().getAbsoluteFile());
	                    	Document doc;
	                    	Element root;
	                    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	                		DocumentBuilder dBuilder;
	                    	try {
	                    		dBuilder = dbFactory.newDocumentBuilder();
	                			doc = dBuilder.parse(file.getAbsoluteFile());
	                			doc.getDocumentElement().normalize();
	                			
	                			root = doc.getDocumentElement();
	                			if(root.getTagName().equals("Literature")) {
	                				Library libObj = new Library(file.toPath().toAbsolutePath());
	    	                        LibTab libTab = new LibTab(libObj, tabPane, stage);
	    	                        libTab.setName(libObj.getName());
	    	                        libTabs.addLibTab(libTab.getTab(), libTab);
	    	                        Tab tab = libTab.getTab();
	    	                        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	    							selectionModel.select(tab);
	    							saveCSV.setDisable(true);
	                                addB.setDisable(false);
	                                delB.setDisable(false);
	                                genD.setDisable(false);
	                                save.setDisable(false);
	                                filter.setDisable(false);
                                	merge.setDisable(false);
	    	                        tab.setOnSelectionChanged(event -> {
	    	                            if (tab.isSelected()) {
	    	                                saveCSV.setDisable(true);
	    	                                addB.setDisable(false);
	    	                                delB.setDisable(false);
	    	                                genD.setDisable(false);
	    	                                save.setDisable(false);
	    	                                filter.setDisable(false);
	    	                                merge.setDisable(false);
	    	                            }
	    	                        });
	    	                        tab.setOnClosed(new EventHandler<Event>() {
	    								@Override
	    							    public void handle(Event e) 
	    							    {
	    									if(libTabs.getTabCount()==1) {
	    										save.setDisable(true);
	    										genD.setDisable(true);
	    										filter.setDisable(true);
	    										addB.setDisable(true);
	    										delB.setDisable(true);
	    										merge.setDisable(true);
	    									}
	    									libTabs.deleteLibTab(libTab.getTab(), libTab);
	    							    }
	    							});
	    	                        libTab.setIsSaved(true);
	    	                        tabID.addID(libTab.getTab(), "Lib");
	                			}
	                			if(root.getTagName().equals("Dictionary")) {
	                				Dictionary dictObj = new Dictionary(file.toPath());
	    	                    	DictTab dictTab = new DictTab(dictObj, tabPane);
	    	                    	dictTab.setName(dictObj.getPath().getFileName().toString());
	    	                    	dictTabs.addDictTab(dictTab.getTab(), dictTab);
	    	                    	Tab tab = dictTab.getTab();
	    	                    	SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	    							selectionModel.select(tab);
	    							saveCSV.setDisable(false);
	                                genD.setDisable(true);
	                                addB.setDisable(true);
	                                delB.setDisable(true);
	                                filter.setDisable(true);
	                                merge.setDisable(true);
	                                save.setDisable(true);
	    	                    	tab.setOnSelectionChanged(event -> {
	    	                            if (tab.isSelected()) {
	    	                                saveCSV.setDisable(false);
	    	                                genD.setDisable(true);
	    	                                addB.setDisable(true);
	    	                                delB.setDisable(true);
	    	                                filter.setDisable(true);
	    	                                merge.setDisable(true);
	    	                                save.setDisable(true);
	    	                            }
	    	                        });
	    	                    	tab.setOnClosed(event -> {
	    	                    		if(dictTabs.getTabCount()==1) {
	    	                    			saveCSV.setDisable(true);
	    	                    		}
	    	                    	});
	    	                    	tabID.addID(dictTab.getTab(), "Dict");
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
	                	
	                	Tab tab = tabPane.getSelectionModel().getSelectedItem();
	                    LibTab libTab = libTabs.getLibTab(tab);
	                    Library libObj = libTab.getLib();
	                    saveChooser.setInitialFileName(libObj.getName());
	                	File file = saveChooser.showSaveDialog(stage);
	                	if (file != null) {
	                		saveChooser.setInitialDirectory(file.getParentFile().getAbsoluteFile());
		                    libObj.save(file.toPath());
		                    libTab.setName(file.getName());
		                    libObj.setPath(file.toPath());
		                    libTab.setIsSaved(true);
		                    genD.setDisable(false);
	                	}
	                }
	            });
		saveCSV.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						ArrayList<Dictionary.DictField> fields = new ArrayList<Dictionary.DictField>();
						Stage stageCSV = new Stage();
	                	stageCSV.setTitle("Save Dictionary to .CSV");
	                	GridPane grid = new GridPane();
	                	grid.setPadding(new Insets(10, 10, 10, 10));
	                	grid.setVgap(8);
	                	grid.setHgap(10);
	                	
	                	Label title = new Label("Select the dictionary fields you want to save from: ");
	                	GridPane.setConstraints(title, 0, 0);
	                	
	                	CheckBox function = new CheckBox();
	                	function.setText("Function");
	                	GridPane.setConstraints(function, 0, 1);
	                	
	                	CheckBox frequency = new CheckBox();
	                	frequency.setText("Frequency");
	                	GridPane.setConstraints(frequency, 0, 2);
	                	
	                	CheckBox syllables = new CheckBox();
	                	syllables.setText("Syllables");
	                	GridPane.setConstraints(syllables, 0, 3);
	                	
	                	CheckBox arpabet = new CheckBox();
	                	arpabet.setText("Arpabet");
	                	GridPane.setConstraints(arpabet, 0, 4);
	                	
	                	CheckBox morphemes = new CheckBox();
	                	morphemes.setText("Morphemes");
	                	GridPane.setConstraints(morphemes, 0, 5);
	                	
	                	CheckBox cognate = new CheckBox();
	                	cognate.setText("Cognate");
	                	GridPane.setConstraints(cognate, 0, 6);
	                	
	                	CheckBox biphAve = new CheckBox();
	                	biphAve.setText("BiphAve");
	                	GridPane.setConstraints(biphAve, 0, 7);
	                	
	                	CheckBox pSegAve = new CheckBox();
	                	pSegAve.setText("P Seg Ave");
	                	GridPane.setConstraints(pSegAve, 0, 8);
	                	
	                	CheckBox neighborhood = new CheckBox();
	                	neighborhood.setText("Neighborhood");
	                	GridPane.setConstraints(neighborhood, 0, 9);
	                	
	                	Button dictPath = new Button("Browse");
						TextField dictPathText = new TextField();
	                	dictPathText.setPromptText("Select Location of .csv (Required)");
	                	dictPathText.setEditable(false);
	                	
	                	dictPathText.setPrefWidth(250);
						
	                	dictPath.setOnAction(new EventHandler<ActionEvent>() {
	                		 public void handle(final ActionEvent e) {
	     	                	
	     	                    File file = csvChooser.showSaveDialog(stage);
	     	                    if (file != null) {
	     	                    	csvChooser.setInitialDirectory(file.getParentFile().getAbsoluteFile());
	     	                        dictPathText.setText(file.getPath());
	     	                    }
	     	                }
	                	});
	                	
	                	HBox dict = new HBox(4,  dictPath, dictPathText);
	                	GridPane.setConstraints(dict, 0, 10);
	                	
	                	Button submit = new Button("Submit");
	                	submit.setOnAction(new EventHandler<ActionEvent>() {
	    	                @Override
	    	                public void handle(final ActionEvent a) {
	    	                	if(dictPathText.getText().isEmpty()) {
	    	                		Alert alert = new Alert(Alert.AlertType.ERROR);
	    	                        alert.setHeaderText("Please select the location of the .csv file");
	    	                        alert.showAndWait();
	    	                	}
	    	                	else {
	    	                		Path newFilePath = Paths.get(dictPathText.getText());
	    	                		Dictionary dict = dictTabs.getDictTab(tabPane.getSelectionModel().getSelectedItem()).getDict();
	    	                		if(function.isSelected()) fields.add(Dictionary.DictField.FUNCTION);
	    	                		if(frequency.isSelected()) fields.add(Dictionary.DictField.FREQUENCY);
	    	                		if(syllables.isSelected()) fields.add(Dictionary.DictField.SYLLABLES);
	    	                		if(arpabet.isSelected()) fields.add(Dictionary.DictField.ARPABET);
	    	                		if(morphemes.isSelected()) fields.add(Dictionary.DictField.MORPHEMES);
	    	                		if(cognate.isSelected()) fields.add(Dictionary.DictField.COGNATE);
	    	                		if(biphAve.isSelected()) fields.add(Dictionary.DictField.BIPHAVE);
	    	                		if(pSegAve.isSelected()) fields.add(Dictionary.DictField.PSEGAVE);
	    	                		if(neighborhood.isSelected()) fields.add(Dictionary.DictField.NEIGHBORHOOD);
	    	                		dict.saveAsCSV(dict.getPath(), newFilePath, fields);
	    	                		stageCSV.close();
	    	                	}
	    	                }
	    	            });
	                	GridPane.setConstraints(submit, 0, 11);
	                	
	                	grid.getChildren().addAll(title, function, frequency, syllables, arpabet, morphemes, cognate, biphAve, pSegAve, neighborhood, dict, submit);
	                	Scene scene = new Scene(grid, 350, 400);
	                    stageCSV.setScene(scene);
	                    stageCSV.show();
					}
				});
		addB.setOnAction(
				new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                	ToggleGroup completeGroup = new ToggleGroup();
	                	Stage stageAdd = new Stage();
	                	stageAdd.setTitle("Add Book");
	                	GridPane grid = new GridPane();
	                	grid.setPadding(new Insets(10, 10, 10, 10));
	                	grid.setVgap(8);
	                	grid.setHgap(10);
	                	
	                	Text asterisk = new Text("*");
	                	asterisk.setFill(Color.RED);
	                	
	                	Text titleText = new Text("Title");
	                	HBox titleLab = new HBox(titleText, asterisk);
	                	GridPane.setConstraints(titleLab, 0, 0);
	                	
	                	TextField titleInput = new TextField();
	                	titleInput.setPromptText("Title (Required)");
	                	GridPane.setConstraints(titleInput, 1, 0);
	                	
	                	Text authText = new Text("Author");
	                	Text asterisk2 = new Text("*");
	                	asterisk2.setFill(Color.RED);
	                	HBox authLab = new HBox(authText, asterisk2);
	                	GridPane.setConstraints(authLab, 0, 1);
	                	
	                	TextField authInput = new TextField();
	                	authInput.setPromptText("Author (Required)");
	                	GridPane.setConstraints(authInput, 1, 1);
	                	
	                	
	                	Label ageLab = new Label("Age");
	                	GridPane.setConstraints(ageLab, 0, 2);
	                	
	                	TextField ageInput = new TextField();
	                	ageInput.setPromptText("Age");
	                	GridPane.setConstraints(ageInput, 1, 2);
	                	
	                	Label isbnLab = new Label("ISBN");
	                	GridPane.setConstraints(isbnLab, 0, 3);
	                	
	                	TextField isbnInput = new TextField();
	                	isbnInput.setPromptText("ISBN");
	                	GridPane.setConstraints(isbnInput, 1, 3);
	                	
	                	Label genreLab = new Label("Genre");
	                	GridPane.setConstraints(genreLab, 0, 4);
	                	
	                	TextField genreInput = new TextField();
	                	genreInput.setPromptText("Genre");
	                	GridPane.setConstraints(genreInput, 1, 4);
	                	
	                	Label completenessLab = new Label("Text Completeness");
	                	GridPane.setConstraints(completenessLab, 0, 5);
	                	
	                	RadioButton completeButton = new RadioButton();
	                	completeButton.setText("Complete");
	                	completeButton.setSelected(true);
	                	completeButton.setToggleGroup(completeGroup);
	                	RadioButton incompleteButton = new RadioButton();
	                	incompleteButton.setText("Incomplete");
	                	incompleteButton.setToggleGroup(completeGroup);
	                	
	                	HBox completeButtons = new HBox(4, completeButton, incompleteButton);
	                	GridPane.setConstraints(completeButtons, 1, 5);
	                	
	                	Text fileText = new Text("Text File");
	                	Text asterisk3 = new Text("*");
	                	asterisk3.setFill(Color.RED);
	                	HBox fileLab = new HBox(fileText, asterisk3);
	                	GridPane.setConstraints(fileLab, 0, 6);
	                	
	                	TextField filePath = new TextField();
	                	filePath.setPromptText("Select Book (Required)");
	                	filePath.setEditable(false);
	                	
	                	filePath.setPrefWidth(250);
	                	
	                	Button browse = new Button("Browse");
	                	browse.setOnAction(new EventHandler<ActionEvent>() {
	                		 public void handle(final ActionEvent e) {
	     	                	
	     	                    File file = bookChooser.showOpenDialog(stage);
	     	                    if (file != null) {
	     	                    	bookChooser.setInitialDirectory(file.getParentFile().getAbsoluteFile());
	     	                        filePath.setText(file.getPath());
	     	                    }
	     	                }
	                	});
	                	
	                	Button submit = new Button("Submit");
	                	submit.setOnAction(new EventHandler<ActionEvent>() {
	    	                @Override
	    	                public void handle(final ActionEvent a) {
	    	                	if(libTabs.getTabCount() == 0) {
	    	                		stageAdd.close();
	    	                		Alert alert = new Alert(Alert.AlertType.ERROR);
	    	                        alert.setHeaderText("Error: There is no library to add a book to");
	    	                        alert.showAndWait();
	    	                	}
	    	                	if(titleInput.getText().isEmpty() || authInput.getText().isEmpty() || filePath.getText().isEmpty()) {
	    	                		Alert alert = new Alert(Alert.AlertType.ERROR);
	    	                        alert.setHeaderText("Please fill all required fields");
	    	                        alert.showAndWait();
	    	                	}
	    	                	
	    	                	if(!titleInput.getText().isEmpty() && !authInput.getText().isEmpty() && !filePath.getText().isEmpty()) {
	    	                		final boolean comp;
		    	                	if(!tabPane.getTabs().isEmpty()) {
		    	                		if(completeButton.isSelected()) {
		    	                			comp = true;
		    	                		}
		    	                		else comp = false;
		    	                		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		    	                		LibTab libTab = libTabs.getLibTab(tab);
		    	                		//When Null Pointer Exception Fixed, move .close() to next line after .addNewBook()
		    	                		stageAdd.close();
		    	                		libTab.addNewBook(new Book(titleInput.getText(), authInput.getText(), ageInput.getText(), isbnInput.getText(), comp, genreInput.getText(), filePath.getText()));
			    	                    libTab.setIsSaved(false);
		    	                	}
	    	                	}
	    	                	
	    	                }
	    	            });
	                	HBox browser = new HBox(4, browse, filePath);
	                	GridPane.setConstraints(browser, 1, 6);
	                	GridPane.setConstraints(submit, 1, 8);
	                	
	                	grid.getChildren().addAll(titleLab, titleInput, authLab, authInput, ageLab, ageInput, isbnLab, isbnInput, genreLab, genreInput, completenessLab, completeButtons, fileLab, browser, submit);
	                	Scene scene = new Scene(grid, 500, 500);
	                    stageAdd.setScene(scene);
	                    stageAdd.show();
	                }
	            });
		delB.setOnAction(
			new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					if(!tabPane.getSelectionModel().isEmpty()) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
		            	alert.setTitle("Delete Books");
		            	alert.setHeaderText("Are you sure you want to delete selected books?");
		            	ButtonType yes = new ButtonType("Yes");
		            	ButtonType no = new ButtonType("No");
		            	alert.getButtonTypes().setAll(yes, no);
		            	Optional <ButtonType> result = alert.showAndWait();
		            	if(result.get() == no) {
		            		e.consume();
		            	}
						if(result.get()==yes) {
							Tab tab = tabPane.getSelectionModel().getSelectedItem();
							LibTab libTab = libTabs.getLibTab(tab);
							Library lib = libTab.getLib();
							libTab.setIsSaved(false);
							List<Book> selected = libTab.getLibTable().getSelectionModel().getSelectedItems();
							lib.delete(selected);
							libTab.getData().removeAll(selected); 
							libTab.getLibTable().getSelectionModel().clearSelection(); //Makes sure nothing is selected after deletion.
							e.consume();
						}
					}
				}
			});
		filter.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						if(libTabs.getTabCount() > 0) {
							Tab tab = tabPane.getSelectionModel().getSelectedItem();
							LibTab libTab = libTabs.getLibTab(tab);
							Library lib = libTab.getLib();
							Stage stageFilter = new Stage();
							stageFilter.setTitle("Filter Books");
							GridPane grid = new GridPane();
							grid.setPadding(new Insets(10, 10, 10, 10));
							grid.setVgap(8);
							grid.setHgap(10);
	                	
							Button submit = new Button("Filter");

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
							
							Label genreLab = new Label("Genre: ");
							GridPane.setConstraints(genreLab, 0, 4);
							TextField genreInput = new TextField();
							GridPane.setConstraints(genreInput, 1, 4);
							
							Label completeLab = new Label("Complete: ");
							GridPane.setConstraints(completeLab, 0, 5);
							ToggleGroup completeGroup = new ToggleGroup();
							RadioButton completeButton = new RadioButton();
							completeButton.setText("Complete");
							completeButton.setSelected(false);
							completeButton.setToggleGroup(completeGroup);
							RadioButton incompleteButton = new RadioButton();
							incompleteButton.setText("Incomplete");
							incompleteButton.setToggleGroup(completeGroup);
							
							HBox completeButtons = new HBox(4, completeButton, incompleteButton);
							GridPane.setConstraints(completeButtons, 1, 5);
							
							Label uniqueLab = new Label("Unique Words: ");
							GridPane.setConstraints(uniqueLab, 0, 6);
							
							Label uniqueGreaterLab = new Label(">=");
							TextField uniqueGreaterInput = new TextField();
							HBox uniqueGreater = new HBox(4, uniqueGreaterLab, uniqueGreaterInput);
							GridPane.setConstraints(uniqueGreater, 1, 6);
							
							Label uniqueLessLab = new Label("<=");
							TextField uniqueLessInput = new TextField();
							HBox uniqueLess = new HBox(4, uniqueLessLab, uniqueLessInput);
							GridPane.setConstraints(uniqueLess, 1, 7);
							
							Label totalLab = new Label("Total Words: ");
							GridPane.setConstraints(totalLab, 0, 8);
							
							Label totalGreaterLab = new Label(">=");
							TextField totalGreaterInput = new TextField();
							HBox totalGreater = new HBox(4, totalGreaterLab, totalGreaterInput);
							GridPane.setConstraints(totalGreater, 1, 8);
							
							Label totalLessLab = new Label("<=");
							TextField totalLessInput = new TextField();
							HBox totalLess = new HBox(4, totalLessLab, totalLessInput);
							GridPane.setConstraints(totalLess, 1, 9);
							
							GridPane.setConstraints(submit, 1, 10);
							isbnInput.setPrefWidth(200);
							
							submit.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(final ActionEvent e) {
									HashMap<Library.BookFields, Object> filterMap = new HashMap<>();

									String titleInputTxt = titleInput.getText().trim();
									String[] fTitles = titleInputTxt.split(";");
									if (fTitles.length > 0 && !fTitles[0].isEmpty()) {
										// ensure inputs are all lowercase and trimmed of whitespace
										for (int i = 0; i < fTitles.length; ++i) 
											fTitles[i] = fTitles[i].toLowerCase().trim();
										filterMap.put(Library.BookFields.TITLE, 
												new Object[] {Library.FilterFuncs.CONTAINS, fTitles});	
									}

									String authorInputTxt = authInput.getText().trim();
									String[] fAuthors = authorInputTxt.split(";");
									if (fAuthors.length > 0 && !fAuthors[0].isEmpty()) {
										for (int i = 0; i < fAuthors.length; ++i)
											fAuthors[i] = fAuthors[i].toLowerCase().trim();
										filterMap.put(Library.BookFields.AUTHOR, 
												new Object[] {Library.FilterFuncs.CONTAINS, fAuthors});
									}

									String ageInputTxt = ageInput.getText().trim();
									String[] fAges = ageInputTxt.split(";");
									if (fAges.length > 0 && !fAges[0].isEmpty()) {
										for (int i = 0; i < fAges.length; ++i)
											fAges[i] = fAges[i].toLowerCase().trim();
										filterMap.put(Library.BookFields.AGE,
												new Object[] {Library.FilterFuncs.EQUALS, fAges});
									}

									String isbnInputTxt = isbnInput.getText().trim();
									String[] fIsbns = isbnInputTxt.split(";");
									if (fIsbns.length > 0 && !fIsbns[0].isEmpty()) {
										for (int i = 0; i < fIsbns.length; ++i)
											fIsbns[i] = fIsbns[i].toLowerCase().trim();
										filterMap.put(Library.BookFields.ISBN,
												new Object[] {Library.FilterFuncs.EQUALS, fIsbns});
									}

									String genreInputTxt = genreInput.getText().trim();
									String[] fGenres = genreInputTxt.split(";");
									if (fGenres.length > 0 && !fGenres[0].isEmpty()) {
										for (int i = 0; i < fGenres.length; ++i)
											fGenres[i] = fGenres[i].toLowerCase().trim();
										filterMap.put(Library.BookFields.GENRE,
												new Object[] {Library.FilterFuncs.EQUALS, fGenres});
									}
									
									if (completeButton.isSelected()) {
										filterMap.put(Library.BookFields.COMPLETE, 
			    								new Object[] {Library.FilterFuncs.EQUALS, true});
									}
									if (incompleteButton.isSelected()) {
										filterMap.put(Library.BookFields.COMPLETE, 
			    								new Object[] {Library.FilterFuncs.EQUALS, false});
									}
									
									HashMap<FilterFuncs, Integer> uCountFilterMap = new HashMap<>();
									String uWordLowerLimit = uniqueGreaterInput.getText().trim();
									if (!uWordLowerLimit.isEmpty()) {
										uCountFilterMap.put(FilterFuncs.GREATER_THAN, Integer.parseInt(uWordLowerLimit));
									}
									String uWordUpperLimit = uniqueLessInput.getText().trim();
									if (!uWordUpperLimit.isEmpty()) {
										uCountFilterMap.put(FilterFuncs.LESS_THAN, Integer.parseInt(uWordUpperLimit));
									}
									if (!uCountFilterMap.isEmpty()) {
										filterMap.put(Library.BookFields.UNIQUE_WORD_COUNT, uCountFilterMap);
									}
									
									HashMap<FilterFuncs, Integer> tCountFilterMap = new HashMap<>();
									String tWordLowerLimit = totalGreaterInput.getText().trim();
									if (!tWordLowerLimit.isEmpty()) {
										tCountFilterMap.put(FilterFuncs.GREATER_THAN, Integer.parseInt(tWordLowerLimit));
									}
									String tWordUpperLimit = totalLessInput.getText().trim();
									if (!tWordUpperLimit.isEmpty()) {
										tCountFilterMap.put(FilterFuncs.LESS_THAN, Integer.parseInt(tWordUpperLimit));
									}
									if (!tCountFilterMap.isEmpty()) {
										filterMap.put(Library.BookFields.TOTAL_WORD_COUNT, tCountFilterMap);
									}

									Library fLib = lib.filter(filterMap);
		    						LibTab fLibTab = new LibTab(fLib, tabPane, stage);
		    						Tab fTab = fLibTab.getTab();
		    						fLibTab.setIsSaved(false);
		    						SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	    							selectionModel.select(fTab);
	    							saveCSV.setDisable(true);
	                                addB.setDisable(false);
	                                delB.setDisable(false);
	                                save.setDisable(false);
	                                filter.setDisable(false);
                                	merge.setDisable(false);
                                	genD.setDisable(false);
	    	                        fTab.setOnSelectionChanged(event -> {
	    	                            if (fTab.isSelected()) {
	    	                                saveCSV.setDisable(true);
	    	                                addB.setDisable(false);
	    	                                genD.setDisable(false);
	    	                                delB.setDisable(false);
	    	                                save.setDisable(false);
	    	                                filter.setDisable(false);
	    	                                merge.setDisable(false);
	    	                            }
	    	                        });
	    	                        fTab.setOnClosed(new EventHandler<Event>() {
	    								@Override
	    							    public void handle(Event e) 
	    							    {
	    									if(libTabs.getTabCount()==1) {
	    										save.setDisable(true);
	    										filter.setDisable(true);
	    										addB.setDisable(true);
	    										delB.setDisable(true);
	    										merge.setDisable(true);
	    										genD.setDisable(true);
	    									}
	    									libTabs.deleteLibTab(fTab, fLibTab);
	    							    }
	    							});
		    						fLibTab.setName(fLib.getPath().getFileName().toString());
		    						fLib.setName(fLibTab.getName());
		    						libTabs.addLibTab(fTab, fLibTab);
		    						tabID.addID(fTab, "Lib");
		    						stageFilter.close();
								}
							});
						
							grid.getChildren().addAll(titleLab, titleInput, authLab, authInput, ageLab, ageInput, isbnLab, isbnInput, uniqueLab, uniqueGreater, uniqueLess, totalLab, totalGreater, totalLess, genreLab, genreInput, completeLab, completeButtons, submit);
							Scene scene = new Scene(grid, 350, 500);
							stageFilter.setScene(scene);
							stageFilter.show();
						}

					}});
	    						
	                	
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
		            	
		            	
		            	
		            	ObservableList<Tab> tabs = tabPane.getTabs();
		            	ObservableList<Library> data =
		            	        FXCollections.observableArrayList(
		            	        	
		            	            
		            	        );

		            	TableView<Library> libs = new MultiSelectTableView<Library>();
		        		TableColumn<Library, String> libCol = new TableColumn<>("Library");
		                libCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		            	for(int i = 0; i < tabs.size(); i++) {
		            		if(tabID.getID(tabs.get(i)).equals("Lib")) {
		            			data.add(libTabs.getLibTab(tabs.get(i)).getLib());
		            		}
		            	}
		            	libs.setItems(data);
		            	libs.getColumns().add(libCol);
		            	Label libLab1 = new Label("Select Libraries to Merge:");
	                	GridPane.setConstraints(libLab1, 0, 0);
		            	
		            	GridPane.setConstraints(libs, 0, 1);
		            	
		            	Button submit = new Button("Merge");
		            	GridPane.setConstraints(submit, 0, 2);
		            	submit.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(final ActionEvent e) {
								if(libs.getSelectionModel().isEmpty()) {
									Alert alert = new Alert(Alert.AlertType.ERROR);
	    	                        alert.setHeaderText("Libraries not selected");
	    	                        alert.showAndWait();
								}
								else {
									Library temp = new Library();
									Library newLib = temp.merge(libs.getSelectionModel().getSelectedItems());
									LibTab libTab = new LibTab(newLib, tabPane, stage);
									//Tabs
									libTabs.addLibTab(libTab.getTab(), libTab);
									Tab tab = libTab.getTab();
									SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	    							selectionModel.select(tab);
	    							saveCSV.setDisable(true);
	                                addB.setDisable(false);
	                                delB.setDisable(false);
	                                genD.setDisable(false);
	                                save.setDisable(false);
	                                filter.setDisable(false);
                                	merge.setDisable(false);
	    	                        tab.setOnSelectionChanged(event -> {
	    	                            if (tab.isSelected()) {
	    	                                saveCSV.setDisable(true);
	    	                                addB.setDisable(false);
	    	                                delB.setDisable(false);
	    	                                genD.setDisable(false);
	    	                                save.setDisable(false);
	    	                                filter.setDisable(false);
	    	                                merge.setDisable(false);
	    	                            }
	    	                        });
	    	                        tab.setOnClosed(new EventHandler<Event>() {
	    								@Override
	    							    public void handle(Event e) 
	    							    {
	    									if(libTabs.getTabCount()==1) {
	    										save.setDisable(true);
	    										filter.setDisable(true);
	    										addB.setDisable(true);
	    										delB.setDisable(true);
	    										merge.setDisable(true);
	    										genD.setDisable(true);
	    									}
	    									libTabs.deleteLibTab(libTab.getTab(), libTab);
	    							    }
	    							});
									libTab.setName("Lib" + libTabs.getTabCount());
									//libTab.getLib().setName(libTab.getName());
									libTab.setIsSaved(false);
									tabID.addID(libTab.getTab(), "Lib");
									stageMerge.close();
								}
								
								
							}
		            	});
		            	grid.getChildren().addAll(libLab1, libs, submit);
		            	Scene scene = new Scene(grid, 300, 400);
		                stageMerge.setScene(scene);
		                stageMerge.show();
		            }
		        });
		
		genD.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					String osName = System.getProperty("os.name").toLowerCase();
                	boolean isWindows = osName.startsWith("windows");
                	boolean isMac = osName.startsWith("mac");
                	boolean isSaved = libTabs.getLibTab(tabPane.getSelectionModel().getSelectedItem()).getIsSaved();
                	if(!isSaved) {
                		Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("This Library must be saved before you may generate a Dictionary");
                        alert.showAndWait();
                	}
                	/* if(!isWindows && !isMac){
                			Alert alert = new Alert(Alert.AlertType.WARNING);
                        	alert.setHeaderText("This function only works on Windows and Mac machines");
                        	alert.showAndWait();
                	 */
					if (libTabs.getTabCount() > 0 && isSaved) {
						Stage genDStage = new Stage();
						GridPane grid = new GridPane();
	                	grid.setPadding(new Insets(10, 10, 10, 10));
	                	grid.setVgap(8);
	                	grid.setHgap(10);
	                	
	                	
						Button browseCMU = new Button("Browse");
						TextField cmuPath = new TextField();
	                	cmuPath.setPromptText("Select CMU file (Required)");
	                	cmuPath.setEditable(false);
	                	
	                	cmuPath.setPrefWidth(300);
						
	                	browseCMU.setOnAction(new EventHandler<ActionEvent>() {
	                		 public void handle(final ActionEvent e) {
	     	                	
	     	                    File file = cmuChooser.showOpenDialog(stage);
	     	                    if (file != null) {
	     	                    	cmuChooser.setInitialDirectory(file.getParentFile().getAbsoluteFile());
	     	                        cmuPath.setText(file.getPath());
	     	                    }
	     	                }
	                	});
	                	Text cmuText = new Text("CMU: ");
	                	GridPane.setConstraints(cmuText, 0, 0);
	                	HBox cmu = new HBox(4, browseCMU, cmuPath);
	                	GridPane.setConstraints(cmu, 0, 1);
	                	
	                	
	                	Button browseMaster = new Button("Browse");
						TextField masterPath = new TextField();
	                	masterPath.setPromptText("Select Master Dictionary (Optional)");
	                	masterPath.setEditable(false);
	                	
	                	masterPath.setPrefWidth(300);
						
	                	browseMaster.setOnAction(new EventHandler<ActionEvent>() {
	                		 public void handle(final ActionEvent e) {
	     	                	
	     	                    File file = masterDictChooser.showOpenDialog(stage);
	     	                    if (file != null) {
	     	                    	masterDictChooser.setInitialDirectory(file.getParentFile().getAbsoluteFile());
	     	                        masterPath.setText(file.getPath());
	     	                    }
	     	                }
	                	});
	                	Text masterText = new Text("Master Dictionary: ");
	                	GridPane.setConstraints(masterText, 0, 2);
	                	HBox master = new HBox(4, browseMaster, masterPath);
	                	GridPane.setConstraints(master, 0, 3);
	                	
	                	Button exePath = new Button("Browse");
						TextField exeText = new TextField();
	                	exeText.setPromptText("Select Location of build_dictionary.exe (Required)");
	                	exeText.setEditable(false);
	                	
	                	exeText.setPrefWidth(300);
						
	                	exePath.setOnAction(new EventHandler<ActionEvent>() {
	                		 public void handle(final ActionEvent e) {
	     	                	
	     	                	if(isWindows) {
	     	                		ExtensionFilter exe = new ExtensionFilter("EXE Files", "*.exe");
	     	                		buildDictExeChooser.getExtensionFilters().add(exe);
	     	                	}
	     	                	
	     	                    File file = buildDictExeChooser.showOpenDialog(stage);
	     	                    if (file != null) {
	     	                    	buildDictExeChooser.setInitialDirectory(file.getParentFile().getAbsoluteFile());
	     	                        exeText.setText(file.getAbsolutePath());
	     	                    }
	     	                }
	                	});
	                	Text exePromptText = new Text("Build_dictionary Executable: ");
	                	GridPane.setConstraints(exePromptText, 0, 4);
	                	HBox exe= new HBox(4, exePath, exeText);
	                	GridPane.setConstraints(exe, 0, 5);
	                	
	                	Button dictPath = new Button("Browse");
						TextField dictPathText = new TextField();
	                	dictPathText.setPromptText("Select Location of Generated Dictionary (Required)");
	                	dictPathText.setEditable(false);
	                	
	                	dictPathText.setPrefWidth(300);
						
	                	dictPath.setOnAction(new EventHandler<ActionEvent>() {
	                		 public void handle(final ActionEvent e) {
	     	                	
	     	                    File file = newDictChooser.showSaveDialog(stage);
	     	                    
	     	                    if (file != null) {
	     	                    	newDictChooser.setInitialDirectory(file.getParentFile().getAbsoluteFile());
	     	                        dictPathText.setText(file.getPath());
	     	                    }
	     	                }
	                	});
	                	Text dictText = new Text("New Dictionary: ");
	                	GridPane.setConstraints(dictText, 0, 6);
	                	HBox dict = new HBox(4, dictPath, dictPathText);
	                	GridPane.setConstraints(dict, 0, 7);
	                	
	                	
	                	
						
	                	Button submit = new Button("Generate Dictionary");
	                	submit.setOnAction(new EventHandler<ActionEvent>() {
	    	                @Override
	    	                public void handle(final ActionEvent a) {
	    	                	if(dictPath.getText().isEmpty() || cmuPath.getText().isEmpty() || exeText.getText().isEmpty()) {
	    	                		Alert alert = new Alert(Alert.AlertType.ERROR);
	    	                        alert.setHeaderText("Please select the CMU, build_dictionary executable, and new dictionary file");
	    	                        alert.showAndWait();
	    	                	}
	    	                	else {
	    	                		genDStage.close();
	    	                		Tab tab = tabPane.getSelectionModel().getSelectedItem();
	    	                		LibTab libTab = libTabs.getLibTab(tab);
	    	                		Library lib = libTab.getLib();
	    	                		Path newDictPath = Paths.get(dictPathText.getText());
	    	                		System.out.println(dictPathText.getText());
	    	                		Path cmuPath1 = Paths.get(cmuPath.getText());
	    	                		System.out.println(cmuPath.getText());
	    	                		Path masterPath1 = Paths.get(masterPath.getText());
	    	                		System.out.println(masterPath.getText());
	    	                		Path exePath1 = Paths.get(exeText.getText());
	    	                		System.out.print(exeText.getText());
	    	                		Dictionary newDict = Dictionary.Dictionary(lib, exePath1, newDictPath, cmuPath1, masterPath1);
	    	                		DictTab newDictTab = new DictTab(newDict, tabPane);
	    	                		newDictTab.setName(newDict.getPath().getFileName().toString());
	    	                		dictTabs.addDictTab(newDictTab.getTab(), newDictTab);
	    	                		Tab newTab = newDictTab.getTab();
	    	                		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	    	                		selectionModel.select(newTab);
	    	                		saveCSV.setDisable(false);
	    	                		genD.setDisable(true);
	    	                		addB.setDisable(true);
	    	                		delB.setDisable(true);
	    	                		filter.setDisable(true);
	    	                		merge.setDisable(true);
	    	                		save.setDisable(true);
	    	                		newTab.setOnSelectionChanged(event -> {
	    	                			if (newTab.isSelected()) {
	    	                				saveCSV.setDisable(false);
	    	                				genD.setDisable(true);
	    	                				addB.setDisable(true);
	    	                				delB.setDisable(true);
	    	                				filter.setDisable(true);
	    	                				merge.setDisable(true);
	    	                				save.setDisable(true);
	    	                			}	
	    	                		});	
	    	                		newTab.setOnClosed(event -> {
	    	                			if(dictTabs.getTabCount()==1) {
	    	                				saveCSV.setDisable(true);
	    	                			}
	    	                		});
	    	                		tabID.addID(newDictTab.getTab(), "Dict");
	    	                	}
	    	                }
	                	});
	                	GridPane.setConstraints(submit, 0, 8);
						grid.getChildren().addAll(cmuText, cmu, masterText, master,exePromptText, exe, dictText, dict,  submit);
						Scene scene = new Scene(grid, 400,350);
		                genDStage.setScene(scene);
		                genDStage.show();
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
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
	public class LibChoiceConverter extends StringConverter<LibChoice> {

		  public LibChoice fromString(String string) {
		    	return new LibChoice(new Library(), string);
		  }

		  public String toString(LibChoice myClassinstance) {
		    	return myClassinstance.getName();
		  }
	}
}
