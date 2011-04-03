package com.infrno.multiplayer;

import java.sql.*;

import com.wowza.wms.amf.AMFDataObj;

import com.infrno.multiplayer.Application;

public class DatabaseManager {
  private Application main_app;

  private String db_server;
  private String db_username;
  private String db_password;
  private String db_instance_name;

  private Connection _conn;
  private PreparedStatement _saveImage_ps;
  private PreparedStatement _sessionStart_ps;
  private PreparedStatement _sessionEnd_ps;
  private PreparedStatement _sessionFlap_ps;
  private PreparedStatement _sessionMemberStart_ps;
  private PreparedStatement _sessionMemberEnd_ps;
  private PreparedStatement _sessionReport_ps;

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

    db_server = "localhost";
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
    
    String saveImageSql = "insert into image (session_id, path) values (?,?)";

    String sessionStartSql = "insert into session " 
      + "(room_id, "
      + "room_name, "
      + "application_name) values (?,?,?)";

    String sessionEndSql = "update session "
      + "set session_ended_at = NOW() " + "where session_id = ? ";

    String sessionMemberStartSql = "insert into session_member "
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
      + "pixelAspectRatio, "
      + "wowza_client_id, "
      + "user_id "
      + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String sessionMemberEndSql = "update session_member "
      + "set disconnected_at = NOW() " 
      + "where session_id = ? "
      + "and wowza_client_id = ? ";

    String sessionReportSql = "insert into session_report "
      + "(session_id, "
      + "room_id, "
      + "room_name, "
      + "user_name, "
      + "c_audioBytesPerSecond, "
      + "c_videoBytesPerSecond, "
      + "c_dataBytesPerSecond, "
      + "c_currentBytesPerSecond, "
      + "c_maxBytesPerSecond, "
      + "c_byteCount, "
      + "c_dataByteCount, "
      + "c_videoByteCount, "
      + "c_audioLossRate, "
      + "c_srtt, "
      + "c_wowzaProtocol, "
      + "c_droppedFrames, "

      + "application_name, "

      + "s_lastValidatedTime, "
      + "s_pingRtt, "
      + "s_fileInBytesRate, "
      + "s_fileOutBytesRate, "
      + "s_messagesInBytesRate, "
      + "s_messagesInCountRate, "
      + "s_messagesLossBytesRate, "
      + "s_messagesLossCountRate, "
      + "s_messagesOutBytesRate, "
      + "s_messagesOutCountRate, "

      + "user_id "
      + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String sessionFlapSql = "insert into session_flap"
      + "(session_id, " 
      + "application_name, " 
      + "room_name, "
      + "room_id, " 
      + "user_name, " 
      + "user_id, "
      + "peer_connection_status "
      + ") values (?,?,?,?,?,?,?)";

    try {
      _saveImage_ps = _conn.prepareStatement(saveImageSql);
      _sessionMemberStart_ps = _conn.prepareStatement(sessionMemberStartSql);
      _sessionMemberEnd_ps = _conn.prepareStatement(sessionMemberEndSql);
      _sessionReport_ps = _conn.prepareStatement(sessionReportSql);
      _sessionFlap_ps = _conn.prepareStatement(sessionFlapSql);
      _sessionEnd_ps = _conn.prepareStatement(sessionEndSql);
      _sessionStart_ps = _conn.prepareStatement(sessionStartSql, PreparedStatement.RETURN_GENERATED_KEYS);
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
  
  
  
  public void saveImage(String path) {
    main_app.log("DatabaseManager.saveImage() session_id="+ _session_id);

    try {
      _saveImage_ps.clearParameters();

      _saveImage_ps.setInt(1, _session_id);
      _saveImage_ps.setString(2, path);

      _saveImage_ps.execute();
    } catch (SQLException e) {
      main_app.error("saveSessionStartReport(): execute(): "
          + e.toString());
    }

  }

  public Boolean saveSessionStart(AMFDataObj amfDataObj, String room_name) {
    main_app.log("DatabaseManager.saveSessionStart() appName="
    + main_app.app_instance.getApplication().getName()
    + " session_id=" + _session_id 
    + " room_name=" + room_name 
    + " user_name=" + amfDataObj.getString("user_name"));    

    if (_session_id > 0) {
      main_app.log("DatabaseManager.saveSessionStartReport() aborting because session_id has already been set");
      return true;
    }
    
    try {
      _sessionStart_ps.clearParameters();

      _sessionStart_ps.setString(1, main_app.app_instance.getName());
      _sessionStart_ps.setString(2, room_name);
      _sessionStart_ps.setString(3, main_app.app_instance.getApplication().getName());

      _sessionStart_ps.execute();
    } catch (SQLException e) {
      main_app.error("saveSessionStartReport(): execute(): "
          + e.toString());
    }

    ResultSet rs;
    try {
      rs = _sessionStart_ps.getGeneratedKeys();
      if (rs.next()) {
        _session_id = rs.getInt(1);
      }
    } catch (SQLException e) {
      main_app
      .error("saveSessionStartReport(): getGeneratedKeys() or next() failed: "+ e.toString());
    }

    return true;
  }

  public boolean saveSessionReport(AMFDataObj amfDataObj, 
      long lastValidateTime, 
      long pingRoundTripTime,
      long fileInBytesRate,
      long fileOutBytesRate,
      long messagesInBytesRate,
      long messagesInCountRate,
      long messagesLossBytesRate,
      long messagesLossCountRate,
      long messagesOutBytesRate,
      long messagesOutCountRate) {


//    main_app.log("DatabaseManager.saveSessionReport() appName="
//        + main_app.app_instance.getApplication().getName()
//        + " session_id=" + _session_id + " room_id="
//        + amfDataObj.getString("room_id") + " user_name="
//        + amfDataObj.getString("user_name"));

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
    String room_name = amfDataObj.getString("room_name");
    
    String user_name = amfDataObj.getString("user_name");
    String user_id = amfDataObj.getString("user_id");
    
    String application_name = main_app.app_instance.getApplication().getName();

    try {
      _sessionReport_ps.clearParameters();

      _sessionReport_ps.setInt(1, _session_id);
      _sessionReport_ps.setString(2, room_id);
      _sessionReport_ps.setString(3, room_name);
      _sessionReport_ps.setString(4, user_name);
      _sessionReport_ps.setInt(5, audioBytesPerSecond);
      _sessionReport_ps.setInt(6, videoBytesPerSecond);
      _sessionReport_ps.setInt(7, dataBytesPerSecond);
      _sessionReport_ps.setInt(8, currentBytesPerSecond);
      _sessionReport_ps.setInt(9, maxBytesPerSecond);
      _sessionReport_ps.setInt(10, byteCount);
      _sessionReport_ps.setInt(11, dataByteCount);
      _sessionReport_ps.setInt(12, videoByteCount);
      _sessionReport_ps.setInt(13, audioLossRate);
      _sessionReport_ps.setInt(14, srtt);
      _sessionReport_ps.setString(15, wowza_protocol);
      _sessionReport_ps.setInt(16, droppedFrames);
      _sessionReport_ps.setString(17, application_name);

      _sessionReport_ps.setTimestamp(18, new Timestamp(lastValidateTime));
      _sessionReport_ps.setLong(19, pingRoundTripTime);
      _sessionReport_ps.setLong(20, fileInBytesRate);
      _sessionReport_ps.setLong(21, fileOutBytesRate);
      _sessionReport_ps.setLong(22, messagesInBytesRate);
      _sessionReport_ps.setLong(23, messagesInCountRate);
      _sessionReport_ps.setLong(24, messagesLossBytesRate);
      _sessionReport_ps.setLong(25, messagesLossCountRate);
      _sessionReport_ps.setLong(26, messagesOutBytesRate);
      _sessionReport_ps.setLong(27, messagesOutCountRate);

      _sessionReport_ps.setString(28, user_id);

      _sessionReport_ps.execute();
    } catch (SQLException e) {
      main_app.error("saveSessionReport() sqlexecuteException: "+ e.toString());
    }

    return true;
  }

  public void saveSessionMemberStart(String room_id,
      String room_name,  
      String user_id, 
      String user_name, 			
      String application_name,
      String application_version, 
      int client_id,
      String flash_ver,
      String ip,
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
    if (capabilities != null) {
      String[] capability_array = capabilities.split("&");
      for (int i = 0; i < capability_array.length; i++) {
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
    }

    try {
      _sessionMemberStart_ps.clearParameters();
      _sessionMemberStart_ps.setInt(1, _session_id);
      _sessionMemberStart_ps.setString(2, user_name);
      _sessionMemberStart_ps.setString(3, room_id);
      _sessionMemberStart_ps.setString(4, room_name);
      _sessionMemberStart_ps.setString(5, application_name);
      _sessionMemberStart_ps.setString(6, application_version);
      _sessionMemberStart_ps.setString(7, av_hardware_disable);
      _sessionMemberStart_ps.setString(8, localFileReadDisable);
      _sessionMemberStart_ps.setString(9, windowless);
      _sessionMemberStart_ps.setString(10, hasTLS);
      _sessionMemberStart_ps.setString(11, hasAudio);
      _sessionMemberStart_ps.setString(12, hasStreamingAudio);
      _sessionMemberStart_ps.setString(13, hasStreamingVideo);
      _sessionMemberStart_ps.setString(14, hasEmbeddedVideo);
      _sessionMemberStart_ps.setString(15, hasMP3);
      _sessionMemberStart_ps.setString(16, hasAudioEncoder);
      _sessionMemberStart_ps.setString(17, hasVideoEncoder);
      _sessionMemberStart_ps.setString(18, hasAccessibility);
      _sessionMemberStart_ps.setString(19, hasPrinting);
      _sessionMemberStart_ps.setString(20, hasScreenPlayback);
      _sessionMemberStart_ps.setString(21, isDebugger);
      _sessionMemberStart_ps.setString(22, hasIME);
      _sessionMemberStart_ps.setString(23, p32bit_support);
      _sessionMemberStart_ps.setString(24, p64bit_support);
      _sessionMemberStart_ps.setString(25, version);
      _sessionMemberStart_ps.setString(26, manufacturer);
      _sessionMemberStart_ps.setString(27, screenResolution);
      _sessionMemberStart_ps.setString(28, screenDPI);
      _sessionMemberStart_ps.setString(29, screenColor);
      _sessionMemberStart_ps.setString(30, os);
      _sessionMemberStart_ps.setString(31, arch);
      _sessionMemberStart_ps.setString(32, language);
      _sessionMemberStart_ps.setString(33, playerType);
      _sessionMemberStart_ps.setString(34, maxLevelIDC);
      _sessionMemberStart_ps.setString(35, hasScreenBroadcast);
      _sessionMemberStart_ps.setString(36, pixelAspectRatio);
      _sessionMemberStart_ps.setInt(37, client_id);
      _sessionMemberStart_ps.setString(38, user_id);
      _sessionMemberStart_ps.execute();

    } catch (SQLException e) {
      main_app.error("saveSessionMemberStart() sqlexecuteException: "
          + e.toString());
    }
  }

  public void saveSessionMemberFlap(String application_name,
      String room_name,  
      String room_id,
      String user_name, 			
      String user_id, 
      String peer_connection_status) {

    main_app.log("DatabaseManager.saveSessionMemberFlap() appName="
        + main_app.app_instance.getApplication().getName()
        + " room_id=" + room_id
        + " room_name=" + room_name
        + " user_name=" + user_name
        + " peer_connection_status=" +peer_connection_status);

    try {
      _sessionFlap_ps.clearParameters();

      _sessionFlap_ps.setInt(1, _session_id);
      _sessionFlap_ps.setString(2, application_name);
      _sessionFlap_ps.setString(3, room_name);
      _sessionFlap_ps.setString(4, room_id);
      _sessionFlap_ps.setString(5, user_name);
      _sessionFlap_ps.setString(6, user_id);
      _sessionFlap_ps.setString(7, peer_connection_status);
      _sessionFlap_ps.execute();
    } catch(SQLException e){
      main_app.error("DatabaseManager.saveSessionMemberFlap() sqlexecuteException: "
          + e.toString());
    }

  }

  public void saveSessionMemberEnd(int wowza_client_id) {
    main_app.log("DatabaseManager.saveSessionMemberEnd() appName="
        + main_app.app_instance.getApplication().getName()
        + " session_id=" + _session_id   
        + " wowza_client_id=" + wowza_client_id );    
    
    // TODO: add up key totals from session_report rows, and save to session_member    

    try {
      _sessionMemberEnd_ps.clearParameters();

      _sessionMemberEnd_ps.setInt(1, _session_id);
      _sessionMemberEnd_ps.setInt(2, wowza_client_id);

      _sessionMemberEnd_ps.execute();
    } catch (SQLException e) {
      main_app.error("saveSessionMemberEnd(): execute(): " + e.toString());
    }
  }

  public void saveSessionEndReport() {
    main_app.log("DatabaseManager.saveSessionEndReport() appName="
        + main_app.app_instance.getApplication().getName()
        + " session_id=" + _session_id );    
    

    // TODO: add up key totals from session_report rows, and save to session

    try {
      _sessionEnd_ps.clearParameters();

      _sessionEnd_ps.setInt(1, _session_id);

      _sessionEnd_ps.execute();
    } catch (SQLException e) {
      main_app.error("DatabaseManager.saveSessionEndReport(): execute(): " + e.toString());
    }
  }
}