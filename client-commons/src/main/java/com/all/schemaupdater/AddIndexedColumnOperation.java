package com.all.schemaupdater;

public class AddIndexedColumnOperation extends AddColumnOperation {

	private String indexName;

	public AddIndexedColumnOperation(String dataBasePath, String tableName, String columnName, String columnType,
			String indexName) {
		super(dataBasePath, tableName, columnName, columnType);
		this.indexName = indexName;
		this.add(new StringBuilder().append("CREATE INDEX ").append(this.indexName).append(" ON ").append(getTableName()).append("(")
				.append(getColumnName()).append(");").toString());
	}

	public final String getIndexName() {
		return indexName;
	}
}
