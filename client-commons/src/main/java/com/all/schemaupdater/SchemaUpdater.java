package com.all.schemaupdater;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SchemaUpdater {

	private static final String ACCESS_TABLE_QUERY = "SELECT * FROM ";

	private static final String JDBC_HSQLDB = "jdbc:hsqldb:";

	private static final String HSQLDB_JDBC_DRIVER = "org.hsqldb.jdbcDriver";

	private static final String SHUTDOWN_COMMAND = "SHUTDOWN";

	private static final String PASSWORD = "";

	private static final String USER = "sa";

	private static final Log log = LogFactory.getLog(SchemaUpdater.class);

	private final List<AddColumnOperation> operations = new ArrayList<AddColumnOperation>();

	private SchemaUpdater() {
	}

	private boolean addOperation(AddColumnOperation operation) {
		return operations.add(operation);
	}

	public void execute() {
		if (operations == null || operations.isEmpty()) {
			log.info("There are no schema updates.");
			return;
		}
		log.info("Updating schema...");
		for (AddColumnOperation addColumnOperation : operations) {
			executeOperation(addColumnOperation);
		}
	}

	private void executeOperation(AddColumnOperation operation) {
		Connection conn = null;
		Statement statement = null;
		try {
			Class.forName(HSQLDB_JDBC_DRIVER);
			conn = DriverManager.getConnection(JDBC_HSQLDB + operation.getDataBasePath(), USER, PASSWORD);
			statement = conn.createStatement();
			ResultSet table = getTableAccess(statement, operation.getTableName());
			if (!isColumnInTable(table, operation.getColumnName())) {
				log.info("Will create " + operation.getColumnName() + " column on table " + operation.getTableName());
				executeQueries(statement, operation.getQueryList());
			}

		} catch (Throwable e) {
			log.error("Unexpected exception updating DB schema.", e);
		} finally {
			if (conn != null) {
				try {
					if (statement != null) {
						statement.execute(SHUTDOWN_COMMAND);
						statement.close();
					}
					conn.close();
				} catch (SQLException e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	private void executeQueries(Statement statement, List<String> queryList) throws SQLException {
		for (String currentQuery : queryList) {
			log.info("Executing " + currentQuery);
			statement.executeQuery(currentQuery);
		}
	}

	private ResultSet getTableAccess(Statement statement, String tableName) throws SQLException {
		return statement.executeQuery(ACCESS_TABLE_QUERY + tableName);
	}

	private boolean isColumnInTable(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			if (metaData.getColumnLabel(i).equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	public static void updateUserDatabaseSchema(String userDatabasePath) {
		SchemaUpdater schemaUpdater = new SchemaUpdater();
		// Adding dateDownloaded indexed column to Track table on V 0.0.24
		AddIndexedColumnOperation addDateDownloadedColumnOp = new AddIndexedColumnOperation(userDatabasePath, "Track",
				"dateDownloaded", "TIMESTAMP", "DATE_DOWNLOADED_IDX");
		schemaUpdater.addOperation(addDateDownloadedColumnOp);
		schemaUpdater.execute();
	}
}
