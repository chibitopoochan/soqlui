package com.gmail.chibitopoochan.soqlui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class ResultSet {
	private String searchText;
	private ObservableList<SObjectRecord> records;
	private ObservableList<TableColumn<SObjectRecord,String>> columns;

	public ResultSet() {
		searchText = "";
		records = FXCollections.observableArrayList();
		columns = FXCollections.observableArrayList();
	}

	public ResultSet(String searchText,
			ObservableList<SObjectRecord> records,
			ObservableList<TableColumn<SObjectRecord,String>> columns) {
		this.searchText = searchText;
		this.records = records;
		this.columns = columns;
	}

	/**
	 * @return searchText
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * @param searchText セットする searchText
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	/**
	 * @return records
	 */
	public ObservableList<SObjectRecord> getRecords() {
		return records;
	}

	/**
	 * @param records セットする records
	 */
	public void setRecords(ObservableList<SObjectRecord> records) {
		this.records = records;
	}

	/**
	 * @return columns
	 */
	public ObservableList<TableColumn<SObjectRecord, String>> getColumns() {
		return columns;
	}

	/**
	 * @param columns セットする columns
	 */
	public void setColumns(ObservableList<TableColumn<SObjectRecord, String>> columns) {
		this.columns = columns;
	}

}
