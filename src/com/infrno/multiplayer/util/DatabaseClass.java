package com.infrno.multiplayer.util;

import java.sql.*;
import com.mysql.jdbc.Driver;

import com.infrno.multiplayer.Application;

public class DatabaseClass 
{
	private Application main_class;
	
	private String db_server;
	private String db_username;
	private String db_password;
	private String db_instance_name;
	
	public DatabaseClass(Application main_class_in)
	{
		main_class = (Application) main_class_in;
		
//		db_server = main_class.app_instance.getProperties().getPropertyStr("db_server");
//		db_username = main_class.app_instance.getProperties().getPropertyStr("db_username");
//		db_password = main_class.app_instance.getProperties().getPropertyStr("db_password");
//		db_instance_name = main_class.app_instance.getProperties().getPropertyStr("db_instance_name");
		
		db_server = "gold";
		db_username = "sage_rw";
		db_password = "sk00bysnack99";
		db_instance_name = "sage";
		
		setupDBConnection();
	}
	
	private void setupDBConnection()
    {
    	try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch ( ClassNotFoundException exception ){
        	main_class.error("DatabaseClass.setupDBConnection() Unable to load jdbc driver. ClassNotFoundException " + exception.getMessage( ) );
        }
    }
	
	private Connection getDBConnection() throws SQLException
	{
		return DriverManager.getConnection("jdbc:mysql://"+db_server+"/"+db_instance_name+"?user="+db_username+"&password="+db_password+"");
	}
	
	private ResultSet executeQuery(Connection conn, String sql)
	{
		ResultSet rs = null;
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		}catch(SQLException ex){
			main_class.error(sql);
			main_class.error("DatabaseClass.executeQuery() sqlexecuteException execute query: " + ex.toString());
		}
		return rs;
	}
	private int executeUpdate(String sql)
	{
		int rs=0;
		try{
			Connection conn = getDBConnection();
			Statement stmt = null;
			stmt = conn.createStatement();
			rs = stmt.executeUpdate(sql);
			conn.close();
		}catch(SQLException ex){
			main_class.error(sql);
			main_class.error("DatabaseClass.executeUpdate() sqlexecuteException execute update: " + ex.toString());
		}
		return rs;
	}
	
	/**
	 * Methods to call
	 */

	public boolean saveImage()
	{
		
		try{
			Connection conn = getDBConnection();
			Statement stmt = conn.createStatement();
			String sql = "insert into image (filename, hash) values ('test', 'foobar')";
			stmt.execute(sql);
			conn.close();
		}catch(SQLException e){
			main_class.error("DatabaseClass.saveImage() sqlexecuteException: " + e.toString());
		}
		
		return true;
	}
		
	
	public String sampleQuery(String some_val)
	{
		try{
			Connection conn = getDBConnection();
			String sql = "SELECT some_prop WHERE value = "+some_val;
			ResultSet rs = executeQuery(conn,sql);
			if(rs.next() == true){
				//iterate over result
			}
			conn.close();
		}catch(SQLException e){
			main_class.error("sqlexecuteException: " + e.toString());
		}
		return "some value";
	}
	
	public void sampleUpdate(String event_id)
	{
		String sql = "INSERT INTO log_event VALUES (LAST_INSERT_ID(),"+event_id+")";
		int rs = executeUpdate(sql);
//		main_class.log("log success: "+rs);
	}
	
}