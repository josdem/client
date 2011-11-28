package com.all.schemaupdater;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class TestAddColumnOperation {

	@Test
	public void shouldCreateAddColumnOperation() throws Exception {
		AddColumnOperation operation = new AddColumnOperation("/Users/user/ALL/DB/all_all", "LOCALUSER", "idLocation",
				"VARCHAR(255)");
		String defaultValueQuery = "UPDATE LOCALUSER SET idLocation=''";
		operation.add(defaultValueQuery);

		List<String> queryList = operation.getQueryList();
		assertEquals(2, queryList.size());
		assertEquals("ALTER TABLE LOCALUSER ADD idLocation VARCHAR(255);", queryList.get(0));
		assertEquals(defaultValueQuery, queryList.get(1));
	}

	@Test
	public void shouldCreateAddIndexedColumnOperation() throws Exception {
		AddIndexedColumnOperation operation = new AddIndexedColumnOperation("/Users/user/ALL/DB/all_all", "LOCALUSER",
				"idLocation", "VARCHAR(255)", "indexName");

		List<String> queryList = operation.getQueryList();
		assertEquals(2, queryList.size());
		String expectedIndexOp = "CREATE INDEX " + operation.getIndexName() + " ON " + operation.getTableName() + "("
				+ operation.getColumnName() + ");";
		assertEquals("ALTER TABLE LOCALUSER ADD idLocation VARCHAR(255);", queryList.get(0));
		assertEquals(expectedIndexOp, queryList.get(1));
	}

}
