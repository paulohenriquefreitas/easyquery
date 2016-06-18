package com.b2winc.solr.modeljson;

import java.util.List;

import com.b2w.catalogbackendcommons.index.IndexedItem;

public class QueryResult {

	private List<IndexedItem> items;
	private Integer numFounds;
	private String baseLink;
	
	public QueryResult(List<IndexedItem> items, String baseLink) {
		this.items = items;
		this.baseLink = baseLink;
	}
	
	public List<IndexedItem> getItems() {
		return items;
	}
	
	public void setItems(List<IndexedItem> items) {
		this.items = items;
	}
	
	public Integer getNumFounds() {
		return numFounds;
	}
	
	public void setNumFounds(Integer numFounds) {
		this.numFounds = numFounds;
	}
	
	public String getBaseLink() {
		return baseLink;
	}
	
	public void setBaseLink(String baseLink) {
		this.baseLink = baseLink;
	}
}
