package com.gmail.chibitopoochan.soqlui.model;

import java.util.List;

public class ApplicationSetting {
	private int tabCount;
	private int historySize;
	private int recordSize;
	private boolean advanceQuery;
	private String connectionURL;
	private String loginURL;
	private String objectURL;
	private String recordURL;
	private String proxyLoginURL;
	private String proxyBackURL;
	private String proxyTargetURL;
	private List<String> formatColumns;
	private boolean useCSS;
	private boolean useEditor;
	private boolean useDecorator;
	private boolean debugMode;

	/**
	 * @return tabCount
	 */
	public int getTabCount() {
		return tabCount;
	}
	/**
	 * @param tabCount セットする tabCount
	 */
	public void setTabCount(int tabCount) {
		this.tabCount = tabCount;
	}
	/**
	 * @return historySize
	 */
	public int getHistorySize() {
		return historySize;
	}
	/**
	 * @param historySize セットする historySize
	 */
	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}
	/**
	 * @return advanceQuery
	 */
	public boolean isAdvanceQuery() {
		return advanceQuery;
	}
	/**
	 * @param advanceQuery セットする advanceQuery
	 */
	public void setAdvanceQuery(boolean advanceQuery) {
		this.advanceQuery = advanceQuery;
	}
	/**
	 * @return connectionURL
	 */
	public String getConnectionURL() {
		return connectionURL;
	}
	/**
	 * @param connectionURL セットする connectionURL
	 */
	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}
	/**
	 * @return loginURL
	 */
	public String getLoginURL() {
		return loginURL;
	}
	/**
	 * @param loginURL セットする loginURL
	 */
	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}
	/**
	 * @return objectURL
	 */
	public String getObjectURL() {
		return objectURL;
	}
	/**
	 * @param objectURL セットする objectURL
	 */
	public void setObjectURL(String objectURL) {
		this.objectURL = objectURL;
	}
	/**
	 * @return recordURL
	 */
	public String getRecordURL() {
		return recordURL;
	}
	/**
	 * @param recordURL セットする recordURL
	 */
	public void setRecordURL(String recordURL) {
		this.recordURL = recordURL;
	}
	/**
	 * @return proxyLoginURL
	 */
	public String getProxyLoginURL() {
		return proxyLoginURL;
	}
	/**
	 * @param proxyLoginURL セットする proxyLoginURL
	 */
	public void setProxyLoginURL(String proxyLoginURL) {
		this.proxyLoginURL = proxyLoginURL;
	}
	/**
	 * @return proxyBackURL
	 */
	public String getProxyBackURL() {
		return proxyBackURL;
	}
	/**
	 * @param proxyBackURL セットする proxyBackURL
	 */
	public void setProxyBackURL(String proxyBackURL) {
		this.proxyBackURL = proxyBackURL;
	}
	/**
	 * @return proxyTargetURL
	 */
	public String getProxyTargetURL() {
		return proxyTargetURL;
	}
	/**
	 * @param proxyTargetURL セットする proxyTargetURL
	 */
	public void setProxyTargetURL(String proxyTargetURL) {
		this.proxyTargetURL = proxyTargetURL;
	}
	/**
	 * @return formatColumns
	 */
	public List<String> getFormatColumns() {
		return formatColumns;
	}
	/**
	 * @param formatColumns セットする formatColumns
	 */
	public void setFormatColumns(List<String> formatColumns) {
		this.formatColumns = formatColumns;
	}
	/**
	 * @return useCSS
	 */
	public boolean isUseCSS() {
		return useCSS;
	}
	/**
	 * @param useCSS セットする useCSS
	 */
	public void setUseCSS(boolean useCSS) {
		this.useCSS = useCSS;
	}
	/**
	 * @return useEditor
	 */
	public boolean isUseEditor() {
		return useEditor;
	}
	/**
	 * @param useEditor セットする useEditor
	 */
	public void setUseEditor(boolean useEditor) {
		this.useEditor = useEditor;
	}
	/**
	 * @return useDecorator
	 */
	public boolean isUseDecorator() {
		return useDecorator;
	}
	/**
	 * @param useDecorator セットする useDecorator
	 */
	public void setUseDecorator(boolean useDecorator) {
		this.useDecorator = useDecorator;
	}
	/**
	 * @return recordSize
	 */
	public int getRecordSize() {
		return recordSize;
	}
	/**
	 * @param recordSize セットする recordSize
	 */
	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}
	/**
	 * @return debugMode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}
	/**
	 * @param debugMode セットする debugMode
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

}
