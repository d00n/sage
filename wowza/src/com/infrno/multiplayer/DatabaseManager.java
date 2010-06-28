package com.infrno.multiplayer;

import java.sql.*;

import com.mysql.jdbc.Driver;
import com.wowza.wms.amf.AMFDataObj;

import com.infrno.multiplayer.Application;

public class DatabaseManager 
{
	private Application main_app;
	
	private String db_server;
	private String db_username;
	private String db_password;
	private String db_instance_name;
	
	private Connection _conn;
	private PreparedStatement _sessionPreparedStatment;
	private PreparedStatement _sessionReportPreparedStatment;
	
	private int _session_id = 0;
	
	public DatabaseManager(Application app)
	{
		main_app = app;
		
//		db_server = main_class.app_instance.getProperties().getPropertyStr("db_server");
//		db_username = main_class.app_instance.getProperties().getPropertyStr("db_username");
//		db_password = main_class.app_instance.getProperties().getPropertyStr("db_password");
//		db_instance_name = main_class.app_instance.getProperties().getPropertyStr("db_instance_name");
		
		db_server = "gold";
		db_username = "sage_rw";
		db_password = "sk00bysnack99";
		db_instance_name = "sage";
		
		setupDBConnection();
		setupPreparedStatements();
	}
	
	private void setupDBConnection()
    {
    	try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch ( ClassNotFoundException e ){
        	main_app.error("DatabaseManager.setupDBConnection() Unable to load jdbc driver. ClassNotFoundException " + e.getMessage( ) );
        }
        
    	try{
            _conn = DriverManager.getConnection("jdbc:mysql://"+db_server+"/"+db_instance_name+"?user="+db_username+"&password="+db_password+"");
        } catch (SQLException e) {
        	main_app.error("DatabaseManager.setupDBConnection() get DB connection " + e.getMessage( ) );
		}
    }
	
	private void setupPreparedStatements(){
		
		String sessionSql = "insert into session "+
		"(room_id, " +
		"room_name, " +
		"wowza_client_id, "+
		"application_name) values (?,?,?,?)";
	
		String sessionReportSql = "insert into session_report "+
		"(session_id, " +
		"room_id, " +
		"room_name, " +
		"user_name, " +
		"audio_bytes_per_second, " +
		"video_bytes_per_second, " +
		"data_bytes_per_second, " +
		"current_bytes_per_second, " +
		"max_bytes_per_second, " +
		"byte_count, " +
		"data_byte_count, " +
		"video_byte_count, " +
		"audio_loss_rate, " +
		"srtt, " +
		"wowza_protocol, " +
		"dropped_frames) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
		try {
			_sessionReportPreparedStatment = _conn.prepareStatement(sessionReportSql);
			_sessionPreparedStatment = _conn.prepareStatement(sessionSql, PreparedStatement.RETURN_GENERATED_KEYS);			
		} catch (SQLException e) {
        	main_app.error("DatabaseManager.setupPreparedStatements() " + e.getMessage( ) );
		}
	}
	
	public void close() {
		try {
			_conn.close();
		} catch (SQLException e) {
        	main_app.error("DatabaseManager.close() " + e.getMessage( ) );
		}
	}
	
	public Boolean saveSessionStartReport(AMFDataObj amfDataObj, int client_id)
	{
		main_app.log("DatabaseManager.saveSessionStartReport() session_id=" + _session_id);
		
		if (_session_id > 0) {
			main_app.log("DatabaseManager.saveSessionStartReport() aborting because session_id has already been set");
			return true;
		}
		
		String room_name 				= amfDataObj.getString("room_name");		

		try{
			_sessionPreparedStatment.clearParameters();
			
			_sessionPreparedStatment.setString(1, main_app.app_instance.getName());
			_sessionPreparedStatment.setString(2, room_name);
			_sessionPreparedStatment.setInt(3, client_id);			
			_sessionPreparedStatment.setString(4, main_app.app_instance.getApplication().getName());
			
						
			_sessionPreparedStatment.execute();
		}catch(SQLException e){
			main_app.error("saveSessionStartReport(): execute(): " + e.toString());
		}
		
		ResultSet rs;
		try {
			rs = _sessionPreparedStatment.getGeneratedKeys();
			if (rs.next()) {
			    _session_id = rs.getInt(1);
			}
		} catch (SQLException e) {
			main_app.error("saveSessionStartReport(): getGeneratedKeys() or next() failed: " + e.toString());
		}
		
		return true;
	}


	public boolean saveSessionReport(AMFDataObj amfDataObj)
	{
		main_app.log("DatabaseManager.saveSessionReport()");
		
		String uname 					= amfDataObj.getString("uname");
		String wowza_protocol 			= amfDataObj.getString("wowza_protocol");		
		int currentBytesPerSecond 		= amfDataObj.getInt("currentBytesPerSecond");
		int dataBytesPerSecond 			= amfDataObj.getInt("dataBytesPerSecond");
		int videoBytesPerSecond 		= amfDataObj.getInt("videoBytesPerSecond"); 
		int audioBytesPerSecond 		= amfDataObj.getInt("audioBytesPerSecond");
		int maxBytesPerSecond 			= amfDataObj.getInt("maxBytesPerSecond");
		int byteCount 					= amfDataObj.getInt("byteCount"); 
		int dataByteCount 				= amfDataObj.getInt("dataByteCount"); 
		int audioLossRate 				= amfDataObj.getInt("audioLossRate"); 
		int droppedFrames 				= amfDataObj.getInt("droppedFrames"); 
		int videoByteCount 				= amfDataObj.getInt("videoByteCount"); 
		int srtt 						= amfDataObj.getInt("SRTT");
		String room_id 					= amfDataObj.getString("room_id");
		String user_name 				= amfDataObj.getString("user_name");
		String room_name 				= amfDataObj.getString("room_name");		
		
		
		// This belongs in a client header record, created on user connect
		//String capabilities  			= amfDataObj.getString("capabilities");
		// "A=t&SA=t&SV=t&EV=t&MP3=t&AE=t&VE=t&ACC=t&PR=t&SP=f&SB=f&DEB=t&V=WIN%2010%2C0%2C45%2C2&M=Adobe%20Windows&R=1920x1200&DP=72&COL=color&AR=1.0&OS=Windows%207&ARCH=x86&L=en&IME=t&PR32=t&PR64=t&PT=PlugIn&AVD=f&LFD=f&WD=f&TLS=t&ML=5.1", 

		try{
			_sessionReportPreparedStatment.clearParameters();
			
			_sessionReportPreparedStatment.setInt(1, _session_id);
			_sessionReportPreparedStatment.setString(2, room_id);
			_sessionReportPreparedStatment.setString(3, room_name);
			_sessionReportPreparedStatment.setString(4, user_name);
			_sessionReportPreparedStatment.setInt(5, audioBytesPerSecond);
			_sessionReportPreparedStatment.setInt(6, videoBytesPerSecond);
			_sessionReportPreparedStatment.setInt(7, dataBytesPerSecond);
			_sessionReportPreparedStatment.setInt(8, currentBytesPerSecond);
			_sessionReportPreparedStatment.setInt(9, maxBytesPerSecond);
			_sessionReportPreparedStatment.setInt(10, byteCount);
			_sessionReportPreparedStatment.setInt(11, dataByteCount);
			_sessionReportPreparedStatment.setInt(12, videoByteCount);
			_sessionReportPreparedStatment.setInt(13, audioLossRate);
			_sessionReportPreparedStatment.setInt(14, srtt);
			_sessionReportPreparedStatment.setString(15, wowza_protocol);
			_sessionReportPreparedStatment.setInt(16, droppedFrames);
						
			_sessionReportPreparedStatment.execute();
		}catch(SQLException e){
			main_app.error("saveSessionReport() sqlexecuteException: " + e.toString());
		}
		
		return true;
	}
		
//	
//	public String sampleQuery(String some_val)
//	{
//		try{
//			String sql = "SELECT some_prop WHERE value = "+some_val;
//			ResultSet rs = executeQuery(_conn,sql);
//			if(rs.next() == true){
//				//iterate over result
//			}
//			conn.close();
//		}catch(SQLException e){
//			main_app.error("sqlexecuteException: " + e.toString());
//		}
//		return "some value";
//	}
//	
//	public void sampleUpdate(String event_id)
//	{
//		String sql = "INSERT INTO log_event VALUES (LAST_INSERT_ID(),"+event_id+")";
//		int rs = executeUpdate(sql);
//	}
//	
//	private ResultSet executeQuery(Connection conn, String sql)
//	{
//		ResultSet rs = null;
//		Statement stmt = null;
//		try{
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery(sql);
//		}catch(SQLException ex){
//			main_app.error(sql);
//			main_app.error("DatabaseManager.executeQuery() sqlexecuteException execute query: " + ex.toString());
//		}
//		return rs;
//	}
//	private int executeUpdate(String sql)
//	{
//		int rs=0;
//		try{
//			Statement stmt = null;
//			stmt = conn.createStatement();
//			rs = stmt.executeUpdate(sql);
//			conn.close();
//		}catch(SQLException ex){
//			main_app.error(sql);
//			main_app.error("DatabaseManager.executeUpdate() sqlexecuteException execute update: " + ex.toString());
//		}
//		return rs;
//	}
	
	
}