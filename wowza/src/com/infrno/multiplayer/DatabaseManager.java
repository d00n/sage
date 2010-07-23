package com.infrno.multiplayer;

import java.sql.*;

import com.mysql.jdbc.Driver;
import com.wowza.wms.amf.AMFDataObj;

import com.infrno.multiplayer.Application;

public class DatabaseManager {
	private Application main_app;

	private String db_server;
	private String db_username;
	private String db_password;
	private String db_instance_name;

	private Connection _conn;
	private PreparedStatement _sessionReportPreparedStatment;
	private PreparedStatement _sessionMemberPreparedStatment;
	private PreparedStatement _sessionStartPreparedStatment;
	private PreparedStatement _sessionEndPreparedStatment;

	private int _session_id = 0;

	public DatabaseManager(Application app) {
		main_app = app;

		// db_server =
		// main_class.app_instance.getProperties().getPropertyStr("db_server");
		// db_username =
		// main_class.app_instance.getProperties().getPropertyStr("db_username");
		// db_password =
		// main_class.app_instance.getProperties().getPropertyStr("db_password");
		// db_instance_name =
		// main_class.app_instance.getProperties().getPropertyStr("db_instance_name");

		db_server = "gold";
		db_username = "sage_rw";
		db_password = "sk00bysnack99";
		db_instance_name = "sage";

		setupDBConnection();
		setupPreparedStatements();
	}

	private void setupDBConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			main_app.error("DatabaseManager.setupDBConnection() Unable to load jdbc driver. ClassNotFoundException "
							+ e.getMessage());
		}

		try {
			_conn = DriverManager.getConnection("jdbc:mysql://" + db_server
					+ "/" + db_instance_name + "?user=" + db_username
					+ "&password=" + db_password + "");
		} catch (SQLException e) {
			main_app.error("DatabaseManager.setupDBConnection() get DB connection "
							+ e.getMessage());
		}
	}

	private void setupPreparedStatements() {

		String sessionStartSql = "insert into session " + "(room_id, "
				+ "room_name, " + "wowza_client_id, "
				+ "application_name) values (?,?,?,?)";

		String sessionEndSql = "update session "
				+ "set session_ended_at = NOW() " + "where session_id = ? ";

		String sessionMemberSql = "insert into session_member "
				+ "(session_id, " 
				+ "user_name, " 
				+ "room_id, " 
				+ "room_name, "
				+ "application_name, " 
				+ "application_version, "
				+ "avHardwareDisable, "				
				+ "localFileReadDisable, "
				+ "windowless, "
				+ "hasTLS, "
				+ "hasAudio, "
				+ "hasStreamingAudio, "
				+ "hasStreamingVideo, "
				+ "hasEmbeddedVideo, "
				+ "hasMP3, "
				+ "hasAudioEncoder, "
				+ "hasVideoEncoder, "
				+ "hasAccessibility, "
				+ "hasPrinting, "
				+ "hasScreenPlayback, "
				+ "isDebugger, "
				+ "hasIME, "
				+ "p32bit_support, "
				+ "p64bit_support, "				
				+ "version, "
				+ "manufacturer, "
				+ "screenResolution, "
				+ "screenDPI, "
				+ "screenColor, "
				+ "os, "
				+ "arch, "
				+ "language, "
				+ "playerType, "
				+ "maxLevelIDC, "				
				+ "hasScreenBroadcast, "
				+ "pixelAspectRatio "
				+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		String sessionReportSql = "insert into session_report "
				+ "(session_id, "
				+ "room_id, "
				+ "room_name, "
				+ "user_name, "
				+ "audio_bytes_per_second, "
				+ "video_bytes_per_second, "
				+ "data_bytes_per_second, "
				+ "current_bytes_per_second, "
				+ "max_bytes_per_second, "
				+ "byte_count, "
				+ "data_byte_count, "
				+ "video_byte_count, "
				+ "audio_loss_rate, "
				+ "srtt, "
				+ "wowza_protocol, "
				+ "dropped_frames, "
				+ "application_name) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			_sessionMemberPreparedStatment = _conn
					.prepareStatement(sessionMemberSql);
			_sessionReportPreparedStatment = _conn
					.prepareStatement(sessionReportSql);
			_sessionEndPreparedStatment = _conn.prepareStatement(sessionEndSql);
			_sessionStartPreparedStatment = _conn.prepareStatement(
					sessionStartSql, PreparedStatement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			main_app.error("DatabaseManager.setupPreparedStatements() "
					+ e.getMessage());
		}
	}

	public void close() {
		try {
			_conn.close();
		} catch (SQLException e) {
			main_app.error("DatabaseManager.close() " + e.getMessage());
		}
	}

	public Boolean saveSessionStartReport(AMFDataObj amfDataObj, int client_id) {
		main_app.log("DatabaseManager.saveSessionStartReport() session_id="
				+ _session_id);

		if (_session_id > 0) {
			main_app.log("DatabaseManager.saveSessionStartReport() aborting because session_id has already been set");
			return true;
		}

		String room_name = amfDataObj.getString("room_name");

		try {
			_sessionStartPreparedStatment.clearParameters();

			_sessionStartPreparedStatment.setString(1, main_app.app_instance
					.getName());
			_sessionStartPreparedStatment.setString(2, room_name);
			_sessionStartPreparedStatment.setInt(3, client_id);
			_sessionStartPreparedStatment.setString(4, main_app.app_instance
					.getApplication().getName());

			_sessionStartPreparedStatment.execute();
		} catch (SQLException e) {
			main_app.error("saveSessionStartReport(): execute(): "
					+ e.toString());
		}

		ResultSet rs;
		try {
			rs = _sessionStartPreparedStatment.getGeneratedKeys();
			if (rs.next()) {
				_session_id = rs.getInt(1);
			}
		} catch (SQLException e) {
			main_app
					.error("saveSessionStartReport(): getGeneratedKeys() or next() failed: "
							+ e.toString());
		}

		return true;
	}

	public boolean saveSessionReport(AMFDataObj amfDataObj) {
		main_app.log("DatabaseManager.saveSessionReport() appName="
				+ main_app.app_instance.getApplication().getName()
				+ " session_id=" + _session_id + " room_id="
				+ amfDataObj.getString("room_id") + " user_name="
				+ amfDataObj.getString("user_name"));

		String uname = amfDataObj.getString("uname");
		String wowza_protocol = amfDataObj.getString("wowza_protocol");
		int currentBytesPerSecond = amfDataObj.getInt("currentBytesPerSecond");
		int dataBytesPerSecond = amfDataObj.getInt("dataBytesPerSecond");
		int videoBytesPerSecond = amfDataObj.getInt("videoBytesPerSecond");
		int audioBytesPerSecond = amfDataObj.getInt("audioBytesPerSecond");
		int maxBytesPerSecond = amfDataObj.getInt("maxBytesPerSecond");
		int byteCount = amfDataObj.getInt("byteCount");
		int dataByteCount = amfDataObj.getInt("dataByteCount");
		int audioLossRate = amfDataObj.getInt("audioLossRate");
		int droppedFrames = amfDataObj.getInt("droppedFrames");
		int videoByteCount = amfDataObj.getInt("videoByteCount");
		int srtt = amfDataObj.getInt("SRTT");
		String room_id = amfDataObj.getString("room_id");
		String user_name = amfDataObj.getString("user_name");
		String room_name = amfDataObj.getString("room_name");
		String application_name = main_app.app_instance.getApplication()
				.getName();

		try {
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
		} catch (SQLException e) {
			main_app.error("saveSessionReport() sqlexecuteException: "
					+ e.toString());
		}

		return true;
	}

	public void saveSessionMemberReport(String user_name, 
			String room_id, 
			String room_name, 
			String application_name,
			String application_version, 
			String capabilities) {
		
		String av_hardware_disable = "";
		String localFileReadDisable =  "";
		String windowless =  "";
		String hasTLS =  "";
		String hasAudio =  "";
		String hasStreamingAudio = "";
		String hasStreamingVideo =  "";
		String hasEmbeddedVideo =  "";
		String hasMP3 =  "";
		String hasAudioEncoder =  "";
		String hasVideoEncoder =  "";
		String hasAccessibility =  "";
		String hasPrinting =  "";
		String hasScreenPlayback  = "";
		String isDebugger  = "";
		String hasIME = "";
		String p32bit_support = "";
		String p64bit_support = "";				
		String version = "";
		String manufacturer = "";
		String screenResolution = "";
		String screenDPI = "";
		String screenColor = "";
		String os = "";
		String arch = "";
		String language = "";
		String playerType = "";
		String maxLevelIDC = "";
		String hasScreenBroadcast =  "";
		String pixelAspectRatio = "";
		
		String[] token;

		String[] capability_array = capabilities.split("&");
		for (int i = 0; i < capability_array.length; i++) {
			main_app.log("capability: " + capability_array[i]);
			
			token = capability_array[i].split("=");
			
			if (token[0].toUpperCase().equals("AVD"))
				av_hardware_disable = token[1];

			if (token[0].toUpperCase().equals("LFD"))
				localFileReadDisable = token[1];

			if (token[0].toUpperCase().equals("WD"))
				windowless = token[1];

			if (token[0].toUpperCase().equals("A"))
				hasAudio = token[1];

			if (token[0].toUpperCase().equals("SA"))
				hasStreamingAudio = token[1];

			if (token[0].toUpperCase().equals("SV"))
				hasStreamingVideo = token[1];

			if (token[0].toUpperCase().equals("EV"))
				hasEmbeddedVideo = token[1];

			if (token[0].toUpperCase().equals("MP3"))
				hasMP3 = token[1];

			if (token[0].toUpperCase().equals("TLS"))
				hasMP3 = token[1];

			if (token[0].toUpperCase().equals("AE"))
				hasAudioEncoder = token[1];

			if (token[0].toUpperCase().equals("VE"))
				hasVideoEncoder = token[1];

			if (token[0].toUpperCase().equals("ACC"))
				hasAccessibility = token[1];

			if (token[0].toUpperCase().equals("PR"))
				hasPrinting = token[1];

			if (token[0].toUpperCase().equals("SP"))
				hasScreenPlayback = token[1];

			if (token[0].toUpperCase().equals("DEB"))
				isDebugger = token[1];

			if (token[0].toUpperCase().equals("IME"))
				hasIME = token[1];

			if (token[0].toUpperCase().equals("PR32"))
				p32bit_support = token[1];

			if (token[0].toUpperCase().equals("PR64"))
				p64bit_support = token[1];

			if (token[0].toUpperCase().equals("V"))
				version = token[1].replaceAll("%20", " ").replaceAll("%2C", ".");

			if (token[0].toUpperCase().equals("M"))
				manufacturer = token[1].replaceAll("%20", " ").replaceAll("%2C", ".");

			if (token[0].toUpperCase().equals("R"))
				screenResolution = token[1];

			if (token[0].toUpperCase().equals("DP"))
				screenDPI = token[1];

			if (token[0].toUpperCase().equals("COL"))
				screenColor = token[1];

			if (token[0].toUpperCase().equals("OS"))
				os = token[1].replaceAll("%20", " ").replaceAll("%2C", ".");

			if (token[0].toUpperCase().equals("ARCH"))
				arch = token[1];

			if (token[0].toUpperCase().equals("L"))
				language = token[1];

			if (token[0].toUpperCase().equals("PT"))
				playerType = token[1];

			if (token[0].toUpperCase().equals("ML"))
				maxLevelIDC = token[1];

			if (token[0].toUpperCase().equals("SB"))
				hasScreenBroadcast = token[1];

			if (token[0].toUpperCase().equals("AR"))
				pixelAspectRatio = token[1];
		}

		try {
			_sessionMemberPreparedStatment.clearParameters();
			_sessionMemberPreparedStatment.setInt(1, _session_id);
			_sessionMemberPreparedStatment.setString(2, user_name);
			_sessionMemberPreparedStatment.setString(3, room_id);
			_sessionMemberPreparedStatment.setString(4, room_name);
			_sessionMemberPreparedStatment.setString(5, application_name);
			_sessionMemberPreparedStatment.setString(6, application_version);
			_sessionMemberPreparedStatment.setString(7, av_hardware_disable);
			_sessionMemberPreparedStatment.setString(8, localFileReadDisable);
			_sessionMemberPreparedStatment.setString(9, windowless);
			_sessionMemberPreparedStatment.setString(10, hasTLS);
			_sessionMemberPreparedStatment.setString(11, hasAudio);
			_sessionMemberPreparedStatment.setString(12, hasStreamingAudio);
			_sessionMemberPreparedStatment.setString(13, hasStreamingVideo);
			_sessionMemberPreparedStatment.setString(14, hasEmbeddedVideo);
			_sessionMemberPreparedStatment.setString(15, hasMP3);
			_sessionMemberPreparedStatment.setString(16, hasAudioEncoder);
			_sessionMemberPreparedStatment.setString(17, hasVideoEncoder);
			_sessionMemberPreparedStatment.setString(18, hasAccessibility);
			_sessionMemberPreparedStatment.setString(19, hasPrinting);
			_sessionMemberPreparedStatment.setString(20, hasScreenPlayback);
			_sessionMemberPreparedStatment.setString(21, isDebugger);
			_sessionMemberPreparedStatment.setString(22, hasIME);
			_sessionMemberPreparedStatment.setString(23, p32bit_support);
			_sessionMemberPreparedStatment.setString(24, p64bit_support);
			_sessionMemberPreparedStatment.setString(25, version);
			_sessionMemberPreparedStatment.setString(26, manufacturer);
			_sessionMemberPreparedStatment.setString(27, screenResolution);
			_sessionMemberPreparedStatment.setString(28, screenDPI);
			_sessionMemberPreparedStatment.setString(29, screenColor);
			_sessionMemberPreparedStatment.setString(30, os);
			_sessionMemberPreparedStatment.setString(31, arch);
			_sessionMemberPreparedStatment.setString(32, language);
			_sessionMemberPreparedStatment.setString(33, playerType);
			_sessionMemberPreparedStatment.setString(34, maxLevelIDC);
			_sessionMemberPreparedStatment.setString(35, hasScreenBroadcast);
			_sessionMemberPreparedStatment.setString(36, pixelAspectRatio);
			_sessionMemberPreparedStatment.execute();
			
		} catch (SQLException e) {
			main_app.error("saveSessionMemberReport() sqlexecuteException: "
					+ e.toString());
		}
	}

	public void saveSessionEndReport() {
		main_app.log("DatabaseManager.saveSessionEndReport() session_id="
				+ _session_id);

		// TODO: add up key totals from session_report rows, and save to session

		try {
			_sessionEndPreparedStatment.clearParameters();

			_sessionEndPreparedStatment.setInt(1, _session_id);

			_sessionEndPreparedStatment.execute();
		} catch (SQLException e) {
			main_app
					.error("saveSessionEndReport(): execute(): " + e.toString());
		}
	}
}