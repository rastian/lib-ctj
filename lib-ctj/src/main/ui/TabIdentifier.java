package main.ui;

import java.util.HashMap;

import javafx.scene.control.Tab;

public class TabIdentifier {
	private HashMap<Tab, String> tabIDs;
	public TabIdentifier() {
		tabIDs = new HashMap<Tab, String>();
	}
	public String getID(Tab tab) {
		return tabIDs.get(tab);
	}
	public void addID(Tab tab, String id) {
		tabIDs.put(tab, id);
	}
}
