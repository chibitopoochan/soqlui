package com.gmail.chibitopoochan.soqlui.model;

import java.util.Date;

public class SOQLHistory {
	private static final String SUBJECT_FORMAT = "%1$tm/%1$td %1$tH:%1$tM - %2$s";
	private static final int SUBJECT_SIZE = 20;
	private Date createdDate;
	private String query;

	/**
	 * 空のSOQL履歴を作成
	 */
	public SOQLHistory() {

	}

	/**
	 * SOQLの履歴を作成
	 * @param createdDate
	 * @param query
	 */
	public SOQLHistory(Date createdDate, String query) {
		this.createdDate = createdDate;
		this.query = query;
	}

	/**
	 * @return createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate セットする createdDate
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query セットする query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String toString() {
		return String.format(SUBJECT_FORMAT
				,createdDate
				,query.replaceAll("[\n\t\r]", " ")
				 .substring(0,Math.min(query.length(),SUBJECT_SIZE)));
	}

}
