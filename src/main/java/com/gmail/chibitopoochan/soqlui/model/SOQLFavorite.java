package com.gmail.chibitopoochan.soqlui.model;

public class SOQLFavorite {
	private static final String SUBJECT_FORMAT = "%1$s";
	private static final int SUBJECT_SIZE = 20;

	private String name;
	private String query;

	/**
	 * 空のSOQLお気に入りを作成
	 */
	public SOQLFavorite() {

	}

	/**
	 * SOQLお気に入りを作成
	 * @param name
	 * @param query
	 */
	public SOQLFavorite(String name, String query) {
		this.name = name;
		this.query = query;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name セットする name
	 */
	public void setName(String name) {
		this.name = name;
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

	/**
	 * 比較メソッドの上書き
	 * @param o 比較対象
	 * @return 同一ならtrue
	 */
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(this == o) return true;
		if(this.toString().equals(o.toString())) return true;
		return false;
	}

	/**
	 * hashCodeの上書き
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return String.format(SUBJECT_FORMAT
				,name
				 .substring(0,Math.min(name.length()-1,SUBJECT_SIZE)));
	}

}
