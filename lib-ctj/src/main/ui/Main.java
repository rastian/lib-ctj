package main.ui;
import main.library.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.application.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
	
	
	public static void main(String[] args) {
		launch(args);

	}
	public void start(Stage primaryStage) throws Exception{
		primaryStage.setTitle("LIB-CTJ");
		ToolBar toolbar = new ToolBar(newLib, open, save, saveCSV, addB, delB, filter, merge, genD);
		
		//Tabs
		TabPane tabPane = new TabPane();
		
		//Open/Save File Stuff
		final FileChooser fileChooser = new FileChooser();
		Stage stage = new Stage(); //For Open/Save File
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
		FileChooser openLD = new FileChooser(); 
		openLD.setTitle("Select Library or Dictionary XML File to Open");
		filter.disableProperty().set(libTabs.isEmpty());
		merge.disableProperty().set(libTabs.isEmpty());
		saveCSV.setDisable(true);
		//Button Actions
		newLib.setOnAction( 
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						//Table Stuff
						Library libObj = new Library();
						LibTab libTab = new LibTab(libObj, tabPane, stage);
				        //Tabs
						tabID.addID(libTab.getTab(), "Lib");
						libTabs.addLibTab(libTab.getTab(), libTab);
						libTab.getTab().setOnClosed(new EventHandler<Event>() {
							@Override
						    public void handle(Event e) 
						    {
								if(libTabs.getTabCount()==1)
									filter.setDisable(true);
									merge.setDisable(true);
								libTabs.deleteLibTab(libTab.getTab(), libTab);
						    }
						});
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
                            	merge.setDisable(false);
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
	                	FileChooser chooser = new FileChooser();
	                	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
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
	    	                        LibTab libTab = new LibTab(libObj, tabPane, stage);
	    	                        libTab.setName(libObj.getPath().getFileName().toString());
	    	                        libObj.setName(libTab.getName());
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
	    									if(libTabs.getTabCount()==1)
	    										filter.setDisable(true);
	    										merge.setDisable(true);
	    									libTabs.deleteLibTab(libTab.getTab(), libTab);
	    							    }
	    							});
	    	                        filter.setDisable(false);
	    	                        merge.setDisable(false);
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
	    	                    		if(dictTabs.getTabCount()==0) {
	    	                    			save.setDisable(false);
	    	                    			addB.setDisable(false);
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
	                	FileChooser chooser = new FileChooser();
	                	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
	                	chooser.setInitialDirectory(new File(currentPath));
	                	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
	                    File file = chooser.showSaveDialog(stage);
	                    Tab tab = tabPane.getSelectionModel().getSelectedItem();
	                    LibTab libTab = libTabs.getLibTab(tab);
	                    Library libObj = libTab.getLib();
	                    libObj.save(file.toPath());
	                    libTab.setName(file.getName());
	                    libObj.setName(libTab.getName());
	                    libTab.setIsSaved(true);
	                    
	                    
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
	                	
	                	CheckBox spelling = new CheckBox();
	                	spelling.setText("Spelling");
	                	GridPane.setConstraints(spelling, 0, 4);
	                	
	                	CheckBox arpabet = new CheckBox();
	                	arpabet.setText("Arpabet");
	                	GridPane.setConstraints(arpabet, 0, 5);
	                	
	                	CheckBox morphemes = new CheckBox();
	                	morphemes.setText("Morphemes");
	                	GridPane.setConstraints(morphemes, 0, 6);
	                	
	                	CheckBox cognate = new CheckBox();
	                	cognate.setText("Cognate");
	                	GridPane.setConstraints(cognate, 0, 7);
	                	
	                	CheckBox biphAve = new CheckBox();
	                	biphAve.setText("BiphAve");
	                	GridPane.setConstraints(biphAve, 0, 8);
	                	
	                	CheckBox pSegAve = new CheckBox();
	                	pSegAve.setText("P Seg Ave");
	                	GridPane.setConstraints(pSegAve, 0, 9);
	                	
	                	CheckBox neighborhood = new CheckBox();
	                	neighborhood.setText("Neighborhood");
	                	GridPane.setConstraints(neighborhood, 0, 10);
	                	
	                	TextField filePath = new TextField();
	                	filePath.setPrefWidth(250);
	                	
	                	
	                	Button submit = new Button("Submit");
	                	submit.setOnAction(new EventHandler<ActionEvent>() {
	    	                @Override
	    	                public void handle(final ActionEvent a) {
	    	                	Dictionary dict = dictTabs.getDictTab(tabPane.getSelectionModel().getSelectedItem()).getDict();
	    	                	if(function.isSelected()) fields.add(Dictionary.DictField.FUNCTION);
	    	                	if(frequency.isSelected()) fields.add(Dictionary.DictField.FREQUENCY);
	    	                	if(syllables.isSelected()) fields.add(Dictionary.DictField.SYLLABLES);
	    	                	if(spelling.isSelected()) fields.add(Dictionary.DictField.SPELLING);
	    	                	if(arpabet.isSelected()) fields.add(Dictionary.DictField.ARPABET);
	    	                	if(morphemes.isSelected()) fields.add(Dictionary.DictField.MORPHEMES);
	    	                	if(cognate.isSelected()) fields.add(Dictionary.DictField.COGNATE);
	    	                	if(biphAve.isSelected()) fields.add(Dictionary.DictField.BIPHAVE);
	    	                	if(pSegAve.isSelected()) fields.add(Dictionary.DictField.PSEGAVE);
	    	                	if(neighborhood.isSelected()) fields.add(Dictionary.DictField.NEIGHBORHOOD);
	    	                	dict.saveAsCSV(dict.getPath(),fields);
	    	                	stageCSV.close();
	    	                	
	    	                }
	    	            });
	                	GridPane.setConstraints(submit, 0, 11);
	                	
	                	grid.getChildren().addAll(title, function, frequency, syllables, spelling, arpabet, morphemes, cognate, biphAve, pSegAve, neighborhood, submit);
	                	Scene scene = new Scene(grid, 500, 500);
	                    stageCSV.setScene(scene);
	                    stageCSV.show();
					}
				});
		addB.setOnAction(
				new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                	ToggleGroup completeGroup = new ToggleGroup();
	                	Boolean comp;
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
	     	                	FileChooser chooser = new FileChooser();
	     	                	ExtensionFilter txt = new ExtensionFilter("Text Files", "*.txt");
	    	                	chooser.getExtensionFilters().add(txt);
	     	                	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
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
	    	                	if(titleInput.getText().isEmpty() || authInput.getText().isEmpty() || filePath.getText().isEmpty()) {
	    	                		Alert alert = new Alert(Alert.AlertType.ERROR);
	    	                        alert.setHeaderText("Please fill all required fields");
	    	                        alert.showAndWait();
	    	                	}
	    	                	
	    	                	if(!titleInput.getText().isEmpty() && !authInput.getText().isEmpty() && !filePath.getText().isEmpty()) {
	    	                		final boolean comp;
	    	                		if(libTabs.getTabCount() == 0) {
	    	                			Library libObj = new Library();
		    							LibTab libTab = new LibTab(libObj, tabPane, stage);
		    					        //Tabs
		    							libTabs.addLibTab(libTab.getTab(), libTab);
		    							libTab.getTab().setOnClosed(new EventHandler<Event>() {
		    								@Override
		    							    public void handle(Event e) 
		    							    {
		    									libTabs.deleteLibTab(libTab.getTab(), libTab);
		    							    }
		    							});
		    							libTab.setName("Lib" + libTabs.getTabCount());
		    							libObj.setName(libTab.getName());
	    	                		}
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
							ObservableList<Book> selected = libTab.getLibTable().getSelectionModel().getSelectedItems();
							lib.delete(selected);
							libTab.getData().removeAll(selected);
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
							HashMap<Library.BookFields, Object> filterMap = new HashMap<>();
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
							
							Label uniqueEqualLab = new Label("= ");
							TextField uniqueEqualInput = new TextField();
							HBox uniqueEqual = new HBox(4, uniqueEqualLab, uniqueEqualInput);
							GridPane.setConstraints(uniqueEqual, 1, 6);
							
							Label uniqueGreaterLab = new Label("> ");
							TextField uniqueGreaterInput = new TextField();
							HBox uniqueGreater = new HBox(4, uniqueGreaterLab, uniqueGreaterInput);
							GridPane.setConstraints(uniqueGreater, 1, 7);
							
							Label uniqueLessLab = new Label("< ");
							TextField uniqueLessInput = new TextField();
							HBox uniqueLess = new HBox(4, uniqueLessLab, uniqueLessInput);
							GridPane.setConstraints(uniqueLess, 1, 8);
							
							Label totalLab = new Label("Total Words: ");
							GridPane.setConstraints(totalLab, 0, 9);
							
							Label totalEqualLab = new Label("= ");
							TextField totalEqualInput = new TextField();
							HBox totalEqual = new HBox(4, totalEqualLab, totalEqualInput);
							GridPane.setConstraints(totalEqual, 1, 9);
							
							Label totalGreaterLab = new Label("> ");
							TextField totalGreaterInput = new TextField();
							HBox totalGreater = new HBox(4, totalGreaterLab, totalGreaterInput);
							GridPane.setConstraints(totalGreater, 1, 10);
							
							Label totalLessLab = new Label("< ");
							TextField totalLessInput = new TextField();
							HBox totalLess = new HBox(4, totalLessLab, totalLessInput);
							GridPane.setConstraints(totalLess, 1, 11);
							
							GridPane.setConstraints(submit, 1, 12);
							isbnInput.setPrefWidth(200);
							
							submit.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(final ActionEvent e) {
									Library filteredLib = new Library();
									String titleInputTxt = titleInput.getText().trim();
									if (!titleInputTxt.isEmpty()) {
										filterMap.put(Library.BookFields.TITLE,
											new Object[] {Library.FilterFuncs.CONTAINS, titleInputTxt});
									}
									String authInputTxt = authInput.getText().trim();
									if (!authInputTxt.isEmpty()) {
										filterMap.put(Library.BookFields.AUTHOR, 
											new Object[] {Library.FilterFuncs.CONTAINS, authInputTxt});
									}
									String ageInputTxt = ageInput.getText().trim();
									if (!ageInputTxt.isEmpty()) {
										filterMap.put(Library.BookFields.AGE, 
											new Object[] {Library.FilterFuncs.EQUALS, ageInputTxt});
									}
									String isbnInputTxt = isbnInput.getText().trim();
									if (!isbnInputTxt.isEmpty()) {
										filterMap.put(Library.BookFields.ISBN, 
		    								new Object[] {Library.FilterFuncs.EQUALS, isbnInputTxt});
									}
									String genreInputTxt = genreInput.getText().trim();
									if (!genreInputTxt.isEmpty()) {
										filterMap.put(Library.BookFields.GENRE, 
		    								new Object[] {Library.FilterFuncs.EQUALS, genreInputTxt});	
									}
									if (completeButton.isSelected()) {
										filterMap.put(Library.BookFields.COMPLETE, 
		    								new Object[] {Library.FilterFuncs.EQUALS, true});
									}
									if (incompleteButton.isSelected()) {
										filterMap.put(Library.BookFields.COMPLETE, 
		    								new Object[] {Library.FilterFuncs.EQUALS, false});
									}
									Library filteredLibrary = lib.filter(filterMap);
		    						LibTab fLibTab = new LibTab(filteredLibrary, tabPane, stage);
		    						fLibTab.setName("filtered-" + lib.getPath().getFileName());
		    						fLibTab.getLib().setName(fLibTab.getName());
		    						libTabs.addLibTab(fLibTab.getTab(), fLibTab);
		    						fLibTab.setIsSaved(false);
		    						tabID.addID(fLibTab.getTab(), "Lib");
		    						stageFilter.close();
								}
							});
						
							grid.getChildren().addAll(titleLab, titleInput, authLab, authInput, ageLab, ageInput, isbnLab, isbnInput, uniqueLab, uniqueEqual, uniqueGreater, uniqueLess, totalLab, totalEqual, totalGreater, totalLess, genreLab, genreInput, completeLab, completeButtons, submit);
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

		            	MultiSelectTableView<Library> libs = new MultiSelectTableView<Library>();
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
		            	
		            	GridPane.setConstraints(libs, 1, 1);
		            	
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
									libTab.setName("Lib" + libTabs.getTabCount());
									libTab.getLib().setName(libTab.getName());
									libTab.setIsSaved(false);
									tabID.addID(libTab.getTab(), "Lib");
									stageMerge.close();
								}
								
								
							}
		            	});
		            	grid.getChildren().addAll(libLab1, libs, submit);
		            	Scene scene = new Scene(grid, 500, 400);
		                stageMerge.setScene(scene);
		                stageMerge.show();
		            }
		        });
		
		genD.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					if(libTabs.getTabCount()!=0) {
						String o = " -old old.xml ";
						LibTab libTab = libTabs.getLibTab(tabPane.getSelectionModel().getSelectedItem());
						
						String l = " -lit " + libTab.getLib().getPath().toString() +" ";
						String c = " -cmu cmu.txt ";
						Process p = Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"build_dictionary.exe"+ o + c + l);
						
					}
					
					
					
				}
				catch(Exception ex){
					System.out.println(ex);
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
	private Library getChoice(ChoiceBox<LibChoice> c) {
		Library lib = c.getValue().getLibrary();
		return lib;
	}
	public class LibChoiceConverter extends StringConverter<LibChoice> {

		  public LibChoice fromString(String string) {
		    	return new LibChoice(new Library(), string);
		  }

		  public String toString(LibChoice myClassinstance) {
		    	return myClassinstance.getName();
		  }
	}
}
