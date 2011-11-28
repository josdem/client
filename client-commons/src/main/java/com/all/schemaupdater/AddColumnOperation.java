package com.all.schemaupdater;

import java.util.ArrayList;
import java.util.List;

public class AddColumnOperation {

	private final List<String> queryList;
	private final String dataBasePath;
	private final String tableName;
	private final String columnName;

	public AddColumnOperation(String dataBasePath, String tableName, String columnName, String columnType) {
		this.tableName = tableName;
		this.columnName = columnName;
		this.dataBasePath = dataBasePath;
		this.queryList = new ArrayList<String>();
		this.queryList.add(new StringBuilder().append("ALTER TABLE ").append(tableName).append(" ADD ").append(columnName)
				.append(" ").append(columnType).append(";").toString());
	}

	public final boolean add(String query) {
		return queryList.add(query);
	}

	public final List<String> getQueryList() {
		return new ArrayList<String>(queryList);
	}

	public final String getTableName() {
		return tableName;
	}

	public final String getColumnName() {
		return columnName;
	}

	public final String getDataBasePath() {
		return dataBasePath;
	}

}
