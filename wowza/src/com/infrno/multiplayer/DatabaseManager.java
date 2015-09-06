package com.infrno.multiplayer;

import java.sql.*;

import javax.naming.InitialContext;

import com.wowza.wms.amf.AMFDataObj;

import com.infrno.multiplayer.Application;

public class DatabaseManager {
  private Application main_app;

  private String      db_server;
  private String      db_username;
  private String      db_password;
  private String      db_instance_name;

  private int         _session_id = 0;

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

    db_server = "sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com";
    db_username = "sage_rw";
    db_password = "sk00bysnack99";
    db_instance_name = "sage";

    initDBConnection();
  }

  private void initDBConnection() {

    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      main_app
          .error("DatabaseManager.setupDBConnection() Unable to load jdbc driver. ClassNotFoundException "
              + e.getMessage());
    }
  }

  private Connection getConnection() {
    Connection _conn = null;
    try {
      _conn = DriverManager.getConnection("jdbc:mysql://" + db_server + "/"
          + db_instance_name + "?user=" + db_username + "&password="
          + db_password + "");
    } catch (SQLException e) {
      main_app.error("DatabaseManager.setupDBConnection() get DB connection "
          + e.getMessage() + "jdbc:mysql://" + db_server + "/"
          + db_instance_name + "?user=" + db_username + "&password="
          + db_password + "");
    }

    return _conn;
  }

  private void closeConnection(Connection conn, PreparedStatement stmt,
      String caller) {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
      }
      stmt = null;
    }

    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
      }
      conn = null;
    }
  }

  public void saveImage(String path) {
    main_app.log("DatabaseManager.saveImage() session_id=" + _session_id);

    String saveImageSql = "insert into image (session_id, path) values (?,?)";

    Connection conn = null;
    PreparedStatement preparedStatement = null;

    try {
      conn = getConnection();
      preparedStatement = conn.prepareStatement(saveImageSql);
      preparedStatement.clearParameters();

      preparedStatement.setInt(1, _session_id);
      preparedStatement.setString(2, path);

      preparedStatement.execute();
    } catch (SQLException e) {
      main_app.error("saveSessionStartReport(): execute(): " + e.toString());
    } finally {
      closeConnection(conn, preparedStatement, "saveImage");
    }

  }

  public Boolean saveSessionStart(AMFDataObj amfDataObj, String room_name) {
    main_app.log("DatabaseManager.saveSessionStart() appName="
        + main_app.app_instance.getApplication().getName() + " session_id="
        + _session_id + " room_name=" + room_name + " user_name="
        + amfDataObj.getString("user_name"));

    if (_session_id > 0) {
      main_app
          .log("DatabaseManager.saveSessionStartReport() aborting because session_id has already been set");
      return true;
    }

    String sessionStartSql = "insert into session " + "(room_id, "
        + "room_name, " + "application_name) values (?,?,?)";

    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet rs = null;

    try {
      conn = getConnection();

      preparedStatement = conn.prepareStatement(sessionStartSql,
          PreparedStatement.RETURN_GENERATED_KEYS);
      preparedStatement.clearParameters();
      preparedStatement.setString(1, main_app.app_instance.getName());
      preparedStatement.setString(2, room_name);
      preparedStatement.setString(3, main_app.app_instance.getApplication()
          .getName());
      preparedStatement.execute();

      rs = preparedStatement.getGeneratedKeys();
      if (rs.next()) {
        _session_id = rs.getInt(1);
      }

      preparedStatement.close();
      preparedStatement = null;

      conn.close();
      conn = null;

    } catch (SQLException e) {
      main_app.error("saveSessionStartReport(): execute(): " + e.toString());
    } finally {
      closeConnection(conn, preparedStatement, "saveSessionStart");
    }

    return true;
  }

  public boolean saveSessionReport(AMFDataObj amfDataObj,
      long lastValidateTime, long pingRoundTripTime, long fileInBytesRate,
      long fileOutBytesRate, long messagesInBytesRate,
      long messagesInCountRate, long messagesLossBytesRate,
      long messagesLossCountRate, long messagesOutBytesRate,
      long messagesOutCountRate) {

    // main_app.log("DatabaseManager.saveSessionReport() appName="
    // + main_app.app_instance.getApplication().getName()
    // + " session_id=" + _session_id + " room_id="
    // + amfDataObj.getString("room_id") + " user_name="
    // + amfDataObj.getString("user_name"));

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

    String sessionReportSql = "insert into session_report " + "(session_id, "
        + "room_id, " + "room_name, " + "user_name, "
        + "c_audioBytesPerSecond, " + "c_videoBytesPerSecond, "
        + "c_dataBytesPerSecond, " + "c_currentBytesPerSecond, "
        + "c_maxBytesPerSecond, " + "c_byteCount, " + "c_dataByteCount, "
        + "c_videoByteCount, " + "c_audioLossRate, " + "c_srtt, "
        + "c_wowzaProtocol, " + "c_droppedFrames, "

        + "application_name, "

        + "s_lastValidatedTime, " + "s_pingRtt, " + "s_fileInBytesRate, "
        + "s_fileOutBytesRate, " + "s_messagesInBytesRate, "
        + "s_messagesInCountRate, " + "s_messagesLossBytesRate, "
        + "s_messagesLossCountRate, " + "s_messagesOutBytesRate, "
        + "s_messagesOutCountRate, "

        + "user_id "
        + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    Connection conn = null;
    PreparedStatement preparedStatement = null;

    try {
      conn = getConnection();
      preparedStatement = conn.prepareStatement(sessionReportSql);
      preparedStatement.clearParameters();

      preparedStatement.setInt(1, _session_id);
      preparedStatement.setString(2, room_id);
      preparedStatement.setString(3, room_name);
      preparedStatement.setString(4, user_name);
      preparedStatement.setInt(5, audioBytesPerSecond);
      preparedStatement.setInt(6, videoBytesPerSecond);
      preparedStatement.setInt(7, dataBytesPerSecond);
      preparedStatement.setInt(8, currentBytesPerSecond);
      preparedStatement.setInt(9, maxBytesPerSecond);
      preparedStatement.setInt(10, byteCount);
      preparedStatement.setInt(11, dataByteCount);
      preparedStatement.setInt(12, videoByteCount);
      preparedStatement.setInt(13, audioLossRate);
      preparedStatement.setInt(14, srtt);
      preparedStatement.setString(15, wowza_protocol);
      preparedStatement.setInt(16, droppedFrames);
      preparedStatement.setString(17, application_name);

      preparedStatement.setTimestamp(18, new Timestamp(lastValidateTime));
      preparedStatement.setLong(19, pingRoundTripTime);
      preparedStatement.setLong(20, fileInBytesRate);
      preparedStatement.setLong(21, fileOutBytesRate);
      preparedStatement.setLong(22, messagesInBytesRate);
      preparedStatement.setLong(23, messagesInCountRate);
      preparedStatement.setLong(24, messagesLossBytesRate);
      preparedStatement.setLong(25, messagesLossCountRate);
      preparedStatement.setLong(26, messagesOutBytesRate);
      preparedStatement.setLong(27, messagesOutCountRate);

      preparedStatement.setString(28, user_id);

      preparedStatement.execute();
    } catch (SQLException e) {
      main_app
          .error("saveSessionReport() sqlexecuteException: " + e.toString());
    } finally {
      closeConnection(conn, preparedStatement, "saveSessionReport");
    }

    return true;
  }

  public void saveSessionMemberStart(String room_id, String room_name,
      String user_id, String user_name, String application_name,
      String application_version, int client_id, String flash_ver, String ip,
      String capabilities) {

    String av_hardware_disable = "";
    String localFileReadDisable = "";
    String windowless = "";
    String hasTLS = "";
    String hasAudio = "";
    String hasStreamingAudio = "";
    String hasStreamingVideo = "";
    String hasEmbeddedVideo = "";
    String hasMP3 = "";
    String hasAudioEncoder = "";
    String hasVideoEncoder = "";
    String hasAccessibility = "";
    String hasPrinting = "";
    String hasScreenPlayback = "";
    String isDebugger = "";
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
    String hasScreenBroadcast = "";
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

//        if (token[0].toUpperCase().equals("IME"))
//          hasIME = token[1];

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

    Connection conn = null;
    PreparedStatement preparedStatement = null;

    try {
      conn = getConnection();
      preparedStatement = conn.prepareStatement(sessionMemberStartSql);
      preparedStatement.clearParameters();
      preparedStatement.setInt(1, _session_id);
      preparedStatement.setString(2, user_name);
      preparedStatement.setString(3, room_id);
      preparedStatement.setString(4, room_name);
      preparedStatement.setString(5, application_name);
      preparedStatement.setString(6, application_version);
      preparedStatement.setString(7, av_hardware_disable);
      preparedStatement.setString(8, localFileReadDisable);
      preparedStatement.setString(9, windowless);
      preparedStatement.setString(10, hasTLS);
      preparedStatement.setString(11, hasAudio);
      preparedStatement.setString(12, hasStreamingAudio);
      preparedStatement.setString(13, hasStreamingVideo);
      preparedStatement.setString(14, hasEmbeddedVideo);
      preparedStatement.setString(15, hasMP3);
      preparedStatement.setString(16, hasAudioEncoder);
      preparedStatement.setString(17, hasVideoEncoder);
      preparedStatement.setString(18, hasAccessibility);
      preparedStatement.setString(19, hasPrinting);
      preparedStatement.setString(20, hasScreenPlayback);
      preparedStatement.setString(21, isDebugger);
      preparedStatement.setString(22, hasIME);
      preparedStatement.setString(23, p32bit_support);
      preparedStatement.setString(24, p64bit_support);
      preparedStatement.setString(25, version);
      preparedStatement.setString(26, manufacturer);
      preparedStatement.setString(27, screenResolution);
      preparedStatement.setString(28, screenDPI);
      preparedStatement.setString(29, screenColor);
      preparedStatement.setString(30, os);
      preparedStatement.setString(31, arch);
      preparedStatement.setString(32, language);
      preparedStatement.setString(33, playerType);
      preparedStatement.setString(34, maxLevelIDC);
      preparedStatement.setString(35, hasScreenBroadcast);
      preparedStatement.setString(36, pixelAspectRatio);
      preparedStatement.setInt(37, client_id);
      preparedStatement.setString(38, user_id);
      preparedStatement.execute();

    } catch (SQLException e) {
      main_app.error("saveSessionMemberStart() sqlexecuteException: "
          + e.toString());
    } finally {
      closeConnection(conn, preparedStatement, "saveSessionMemberStart");
    }
  }

  public void saveSessionMemberFlap(String application_name, String room_name,
      String room_id, String user_name, String user_id,
      String peer_connection_status) {

    main_app.log("DatabaseManager.saveSessionMemberFlap() appName="
        + main_app.app_instance.getApplication().getName() + " room_id="
        + room_id + " room_name=" + room_name + " user_name=" + user_name
        + " peer_connection_status=" + peer_connection_status);

    String sessionFlapSql = "insert into session_flap" + "(session_id, "
        + "application_name, " + "room_name, " + "room_id, " + "user_name, "
        + "user_id, " + "peer_connection_status " + ") values (?,?,?,?,?,?,?)";

    Connection conn = null;
    PreparedStatement preparedStatement = null;

    try {
      conn = getConnection();
      preparedStatement = conn.prepareStatement(sessionFlapSql);
      preparedStatement.clearParameters();

      preparedStatement.setInt(1, _session_id);
      preparedStatement.setString(2, application_name);
      preparedStatement.setString(3, room_name);
      preparedStatement.setString(4, room_id);
      preparedStatement.setString(5, user_name);
      preparedStatement.setString(6, user_id);
      preparedStatement.setString(7, peer_connection_status);
      preparedStatement.execute();
    } catch (SQLException e) {
      main_app
          .error("DatabaseManager.saveSessionMemberFlap() sqlexecuteException: "
              + e.toString());
    } finally {
      closeConnection(conn, preparedStatement, "saveSessionMemberFlap");
    }

  }

  public void saveSessionMemberEnd(int wowza_client_id) {
    main_app.log("DatabaseManager.saveSessionMemberEnd() appName="
        + main_app.app_instance.getApplication().getName() + " session_id="
        + _session_id + " wowza_client_id=" + wowza_client_id);

    String sessionMemberEndSql = "update session_member "
        + "set disconnected_at = NOW() " + "where session_id = ? "
        + "and wowza_client_id = ? ";

    Connection conn = null;
    PreparedStatement preparedStatement = null;

    // TODO: add up key totals from session_report rows, and save to
    // session_member

    try {
      conn = getConnection();
      preparedStatement = conn.prepareStatement(sessionMemberEndSql);
      preparedStatement.clearParameters();

      preparedStatement.setInt(1, _session_id);
      preparedStatement.setInt(2, wowza_client_id);

      preparedStatement.execute();
    } catch (SQLException e) {
      main_app.error("saveSessionMemberEnd(): execute(): " + e.toString());
    } finally {
      closeConnection(conn, preparedStatement, "saveSessionMemberEnd");
    }
  }

  public void saveSessionEndReport() {
    main_app.log("DatabaseManager.saveSessionEndReport() appName="
        + main_app.app_instance.getApplication().getName() + " session_id="
        + _session_id);

    String sessionEndSql = "update session " + "set session_ended_at = NOW() "
        + "where session_id = ? ";

    Connection conn = null;
    PreparedStatement preparedStatement = null;

    // TODO: add up key totals from session_report rows, and save to session

    try {
      conn = getConnection();
      preparedStatement = conn.prepareStatement(sessionEndSql);
      preparedStatement.clearParameters();
      preparedStatement.setInt(1, _session_id);

      preparedStatement.execute();
    } catch (SQLException e) {
      main_app.error("DatabaseManager.saveSessionEndReport(): execute(): "
          + e.toString());
    } finally {
      closeConnection(conn, preparedStatement, "saveSessionEndReport");
    }
  }
}