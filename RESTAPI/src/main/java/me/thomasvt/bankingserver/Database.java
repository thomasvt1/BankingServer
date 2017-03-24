package me.thomasvt.bankingserver;

import java.sql.*;

import com.mysql.jdbc.CommunicationsException;


public class Database {
	
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://bank.ccx9175trsi7.eu-west-2.rds.amazonaws.com:3306/bank";
	   
	private final String USER = "bank";
	private final String PASS = "gpkdN29HZ4fvjtS2";

	private Connection conn = null;
	private Statement stmt = null;
		
	public Database() {
		App.databaseCalled++;
		if (App.databaseCalled > 1) {
			System.out.println("!!WARNING!! DATABASE CONSTRUCTOR CALLED!");
			System.out.println("POSSIBLE MULTIPLE CONNECTIONS TO DB!");
			System.out.println("TIMES CONSTRUCTOR CALLED: " + App.databaseCalled);
		}
	}

	public void startConnection() {
		System.out.println("Registrating jbdc Driver");
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		System.out.println("Connecting to database...");
		
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	Connection getConn() {
		try { //First try method to make sure there is a connection and it is't closed.
			if (conn == null)
				startConnection();
			if (conn.isClosed())
				startConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		executeDummyQuery();
		return conn;
	}

	/*
	This method executes a dummy query on the database to make sure the connection is still alive.
	 */
	private void executeDummyQuery() {
		if (!shouldRunDummy())
			return;
		try {
			stmt = getConn().createStatement();
			stmt.executeUpdate("SELECT 'pinHash' FROM xxx WHERE 'user' = 22");
		} catch (CommunicationsException e) {}
		catch (SQLException e) {}
	}

	private long lastRunTime = 60000; //1 minute
	private boolean shouldRunDummy() {
		long currentTime = System.currentTimeMillis();
		boolean shouldRun = true;

		if ((currentTime - lastRunTime) < 60000)
			shouldRun = false;

		lastRunTime = currentTime;

		return shouldRun;
	}
	
	Statement getStmt() {
		try {
			if (getConn().isClosed())
				startConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (stmt.isClosed())
				System.out.println("stmt CLOSED");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}
	
	void createStatement(String sql) {
		try {
			stmt = getConn().createStatement();
			stmt.executeUpdate(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	String selectStatement(String sql) {
		String reply = null;
		
		try {
			stmt = getConn().createStatement();
	        ResultSet rs = getStmt().executeQuery(sql);
	        
	        while (rs.next())
	        	reply = rs.getString(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return reply;
	}
	
	void printStatusOfConnection() {
		try {
			if (conn == null) {
				System.out.println("Connection was null!");
			}
			else if (conn.isClosed())
				System.out.println("Connection is closed!");
			else
				System.out.println("Connection is open");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}