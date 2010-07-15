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
	private PreparedStatement _sessionReportPreparedStatment;
	private PreparedStatement _sessionStartPreparedStatment;
	private PreparedStatement _sessionEndPreparedStatment;
	
	private int _session_id = 0;
	
	public DatabaseManager(Application app)
	{
		main_app = app;
		
//		db_server = main_class.app_instance.getProperties().getPropertyStr("db_server");
//		db_username = main_class.app_instance.getProperties().getPropertyStr("db_username");
//		db_password = main_class.app_instance.getProperties().getPropertyStr("db_password");
//		db_instance_name = main_class.app_instance.getProperties().getPropertyStr("db_instance_name");
		
		db_server = "localhost";
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
		
		String sessionStartSql = "insert into session "+
		"(room_id, " +
		"room_name, " +
		"wowza_client_id, "+
		"application_name) values (?,?,?,?)";
	
		String sessionEndSql = "update session "+
		"set session_ended_at = NOW() " +
		"where session_id = ? ";
	
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
		"dropped_frames, " +
		"application_name) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
		try {
			_sessionReportPreparedStatment = _conn.prepareStatement(sessionReportSql);
			_sessionEndPreparedStatment = _conn.prepareStatement(sessionEndSql);
			_sessionStartPreparedStatment = _conn.prepareStatement(sessionStartSql, PreparedStatement.RETURN_GENERATED_KEYS);			
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
			_sessionStartPreparedStatment.clearParameters();
			
			_sessionStartPreparedStatment.setString(1, main_app.app_instance.getName());
			_sessionStartPreparedStatment.setString(2, room_name);
			_sessionStartPreparedStatment.setInt(3, client_id);			
			_sessionStartPreparedStatment.setString(4, main_app.app_instance.getApplication().getName());			
						
			_sessionStartPreparedStatment.execute();
		}catch(SQLException e){
			main_app.error("saveSessionStartReport(): execute(): " + e.toString());
		}
		
		ResultSet rs;
		try {
			rs = _sessionStartPreparedStatment.getGeneratedKeys();
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
		main_app.log("DatabaseManager.saveSessionReport() appName=" 
				+ main_app.app_instance.getApplication().getName() 
				+" session_id="
				+ _session_id
				+" room_id="
				+amfDataObj.getString("room_id")
				+" user_name="
				+amfDataObj.getString("user_name"));
		
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
		String application_name			= main_app.app_instance.getApplication().getName();	
		
		
		
		
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
			_sessionReportPreparedStatment.setString(17, application_name);
						
			_sessionReportPreparedStatment.execute();
		}catch(SQLException e){
			main_app.error("saveSessionReport() sqlexecuteException: " + e.toString());
		}
		
		return true;
	}
		

	public void saveSessionEndReport()
	{
		main_app.log("DatabaseManager.saveSessionEndReport() session_id=" + _session_id);		

		try{
			_sessionEndPreparedStatment.clearParameters();
			
			_sessionEndPreparedStatment.setInt(1, _session_id);

			_sessionEndPreparedStatment.execute();
		}catch(SQLException e){
			main_app.error("saveSessionEndReport(): execute(): " + e.toString());
		}		
	}	
}