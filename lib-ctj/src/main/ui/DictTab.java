package main.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.library.Book;
import main.library.Library;
import main.library.Dictionary;
import main.library.DictElement;
public class DictTab {
	private String name;
	private int tabCount;
	private Tab tab = new Tab();
	Dictionary dictObj;
	private TableView<DictElement> dictData = new TableView<DictElement>();
	private ObservableList<DictElement> data =
	        FXCollections.observableArrayList(
	        	
	            
	        );
	public DictTab(Dictionary dict, TabPane pane){
		tab.setOnCloseRequest(new EventHandler<Event>() {
			@Override
		    public void handle(Event e) 
		    {
		        
		    }
		});
		dictObj = dict;
		dictData.setEditable(true);
		for(int i = 0; i < dict.getElements().size(); i++) {
			data.add(dict.getElements().get(i));
		}
        TableColumn<DictElement, String> spellCol = new TableColumn<>("Spelling");
        spellCol.setCellValueFactory(new PropertyValueFactory<>("spelling"));
		TableColumn<DictElement, String> funcCol = new TableColumn<>("Function");
        funcCol.setCellValueFactory(new PropertyValueFactory<>("function"));
        TableColumn<DictElement, Integer> freqCol = new TableColumn<>("Frequency");
        freqCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        TableColumn<DictElement, Integer> syllableCol = new TableColumn<>("Syllables");
        syllableCol.setCellValueFactory(new PropertyValueFactory<>("syllables"));
        TableColumn<DictElement, String> arpabetCol = new TableColumn<>("Arpabet");
        arpabetCol.setCellValueFactory(new PropertyValueFactory<>("arpabet"));
        TableColumn<DictElement, String> morphemeCol = new TableColumn<>("Morphemes");
        morphemeCol.setCellValueFactory(new PropertyValueFactory<>("morphemes"));
        TableColumn<DictElement, String> cognateCol = new TableColumn<>("Cognate");
        cognateCol.setCellValueFactory(new PropertyValueFactory<>("cognate"));
        TableColumn<DictElement, Double> biphAveCol = new TableColumn<>("BiphAve");
        biphAveCol.setCellValueFactory(new PropertyValueFactory<>("biphAve"));
        TableColumn<DictElement, Double> pSegAveCol = new TableColumn<>("P Seg Ave");
        pSegAveCol.setCellValueFactory(new PropertyValueFactory<>("pSegAve"));
        TableColumn<DictElement, String[]> neighborhoodCol = new TableColumn<>("Neighborhood");
        neighborhoodCol.setCellValueFactory(new PropertyValueFactory<>("neighborhoodDisplay"));
        dictData.setItems(data);
        dictData.getColumns().addAll(
        		spellCol, funcCol, 
        		freqCol, syllableCol, 
        		arpabetCol, morphemeCol, 
        		cognateCol, biphAveCol, 
        		pSegAveCol, neighborhoodCol);
        tab.setContent(dictData);
        pane.getTabs().add(tab);
	}
	public TableView<DictElement> getDictTable(){
		return dictData;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String newName) {
		name = newName;
		tab.setText(name);
	}
	public int getDictCount() {
		return tabCount;
	}
	public Tab getTab() {
		return tab;
	}
	public Dictionary getDict() {
		return dictObj;
	}
	public void setDict(Dictionary newDict) {
		dictObj = newDict;
	}
}
