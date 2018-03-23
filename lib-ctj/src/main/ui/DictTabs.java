package main.ui;

import java.util.HashMap;

import javafx.scene.control.Tab;

public class DictTabs {
	private HashMap<Tab, DictTab> dictMap = new HashMap<Tab, DictTab>();
	int tabCount;
	public DictTabs() {
		tabCount = 0;
	}
	public void addDictTab(Tab tab, DictTab dictTab) {
		dictMap.put(tab, dictTab);
		tabCount++;
	}
	public DictTab getDictTab(Tab tab) {
		return dictMap.get(tab);
	}
	public int getTabCount() {
		return tabCount;
	}
}

