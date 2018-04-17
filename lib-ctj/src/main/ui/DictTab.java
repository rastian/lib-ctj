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
	private MultiSelectTableView<DictElement> dictData = new MultiSelectTableView<DictElement>();
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
		dictData.setEditable(true);
		for(int i = 0; i < dict.getElements().size(); i++) {
			data.add(dict.getElements().get(i));
		}
        TableColumn spellCol = new TableColumn("Spelling");
        spellCol.setCellValueFactory(new PropertyValueFactory<DictElement, String>("spelling"));
		TableColumn funcCol = new TableColumn("Function");
        funcCol.setCellValueFactory(new PropertyValueFactory<DictElement, String>("function"));
        TableColumn freqCol = new TableColumn("Frequency");
        freqCol.setCellValueFactory(new PropertyValueFactory<DictElement, Integer>("frequency"));
        TableColumn syllableCol = new TableColumn("Syllables");
        syllableCol.setCellValueFactory(new PropertyValueFactory<DictElement, Integer>("syllables"));
        TableColumn arpabetCol = new TableColumn("Arpabet");
        arpabetCol.setCellValueFactory(new PropertyValueFactory<DictElement, String>("arpabet"));
        TableColumn morphemeCol = new TableColumn("Morphemes");
        morphemeCol.setCellValueFactory(new PropertyValueFactory<DictElement, String>("morphemes"));
        TableColumn cognateCol = new TableColumn("Cognate");
        cognateCol.setCellValueFactory(new PropertyValueFactory<DictElement, String>("cognate"));
        TableColumn biphAveCol = new TableColumn("BiphAve");
        biphAveCol.setCellValueFactory(new PropertyValueFactory<DictElement, Double>("biphAve"));
        TableColumn pSegAveCol = new TableColumn("P Seg Ave");
        pSegAveCol.setCellValueFactory(new PropertyValueFactory<DictElement, Double>("pSegAve"));
        TableColumn neighborhoodCol = new TableColumn("Neighborhood");
        neighborhoodCol.setCellValueFactory(new PropertyValueFactory<DictElement, String[]>("neighborhoodDisplay"));
        dictData.setItems(data);
        dictData.getColumns().addAll(spellCol, funcCol, freqCol, syllableCol, arpabetCol, morphemeCol, cognateCol, biphAveCol, pSegAveCol, neighborhoodCol);
        tab.setContent(dictData);
        pane.getTabs().add(tab);
	}
	public MultiSelectTableView<DictElement> getDictTable(){
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
