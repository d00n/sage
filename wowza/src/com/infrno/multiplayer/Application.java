package com.infrno.multiplayer;

import org.apache.log4j.Logger;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class Application extends ModuleBase {
  private static String       VERSION  = "v0.8.1";
  public IApplicationInstance app_instance;
  public ChatManager          chatManager;
  public DatabaseManager      databaseManager;
  public StreamManager        streamManager;
  public UserManager          userManager;
  public WhiteboardManager    whiteboardManager;
  private Thread              reportLoopThread;

  private static Logger       m_logger = Logger.getLogger(Application.class);

  private static class ReportLoop implements Runnable {
    private static final int SECONDS_BETWEEN_REPORTS = 3;
    public Application       main_app;

    public void run() {
      while (true) {
        try {
          Thread.sleep(SECONDS_BETWEEN_REPORTS * 1000);
          main_app.app_instance.broadcastMsg("collectClientStats");
          main_app.userManager.collectServerStats();
        } catch (InterruptedException e) {
          main_app.log("ReportLoop.run() " + e.toString());
          return;
        }
      }
    }
  }

  public void onAppStart(IApplicationInstance appInstance) {

    String appPath = appInstance.getApplication().getApplicationPath();
    String appName = appInstance.getApplication().getName();
    String contextString = appInstance.getContextStr();

    String fullname = appInstance.getApplication().getName() + "/"
        + appInstance.getName();
    getLogger().info(
        "Application.onAppStart() " + VERSION + ", appName=" + appName
            + ", contextString=" + contextString);

    m_logger.info("starting application");

    app_instance = appInstance;
    chatManager = new ChatManager(this);
    streamManager = new StreamManager(this);
    userManager = new UserManager(this);
    whiteboardManager = new WhiteboardManager(this);
    databaseManager = new DatabaseManager(this);
  }

  public void onAppStop(IApplicationInstance appInstance) {
    String fullname = appInstance.getApplication().getName() + "/"
        + appInstance.getName();
    getLogger().info("Application.onAppStop() " + fullname);

    stopReportLoop();

    try {
      databaseManager.saveSessionEndReport();
      databaseManager.close();
    } catch (Exception e) {
      error("Application.onAppStop DatabaseManager not online" + e.getMessage());
    }
    databaseManager = null;

    chatManager = null;
    streamManager = null;
    userManager = null;
    whiteboardManager = null;
    app_instance = null;
  }

  public void startReportLoop() {
    if (reportLoopThread == null) {
      ReportLoop reportLoop = new ReportLoop();
      reportLoop.main_app = this;
      reportLoopThread = new Thread(reportLoop);
      reportLoopThread.start();
    }
  }

  public void stopReportLoop() {
    try {
      reportLoopThread.interrupt();
    } catch (NullPointerException e) {
      getLogger().info("Application.stopReportLoop() " + e.toString());
    }

    try {
      reportLoopThread.join();
    } catch (InterruptedException e) {
      getLogger().info("Application.stopReportLoop() " + e.toString());
    }
  }

  public void onConnect(IClient client, RequestFunction function,
      AMFDataList params) {
    m_logger.info("onConnect");

    String appName = app_instance.getApplication().getName();
    log("Application.onConnect() appName=" + appName);

    if (userManager.userConnect(client, params)) {
      startReportLoop();
    }
  }

  public void onDisconnect(IClient client) {
    getLogger().info("Application.onDisconnect() ");
    userManager.userDisconnect(client);
  }

  public void log(String msgIn) {
    getLogger().info(msgIn);
  }

  public void error(String msgIn) {
    getLogger().error(msgIn);
  }

  /*
   * Client Methods
   */

  public void receiveClientStats(IClient client, RequestFunction function,
      AMFDataList params) {
    userManager.relayClientStats(client, params);
  }

  // public void receiveClientPeerStats(IClient client, RequestFunction
  // function, AMFDataList params) {
  // userManager.relayClientPeerStats(client, params);
  // }

  public void updateUserInfo(IClient client, RequestFunction function,
      AMFDataList params) {
    userManager.updateUserInfo(client, Integer.toString(client.getClientId()),
        params.getObject(PARAM1));
  }

  public void sendImage(IClient client, RequestFunction function,
      AMFDataList params) {
    whiteboardManager.sendImage(client, function, params);
  }

  public void chatToServer(IClient client, RequestFunction function,
      AMFDataList params) {
    chatManager.chatToServer(client, params);
  }

  public void returnImageURL(IClient client, AMFDataList params,
      String imageURL, String sdID) {
    getLogger().info(
        "Application.returnImageURL() sdID=" + sdID + ", imageURL=" + imageURL);

    AMFDataObj returnObj = new AMFDataObj();
    returnObj.put("imageURL", imageURL);
    returnObj.put("sdID", sdID);

    sendResult(client, params, returnObj);
  }

  public void logMessage(IClient client, RequestFunction function, AMFDataList params) {
    
    String user_name;
    String room_id;
      
    // It is possible for clients to log before their details have been recorded by userManager
    // Specifically, during the initial server connection
    try {
      user_name = userManager.getClientInfo(Integer.toString(client.getClientId())).getString("user_name");
      room_id = userManager.room_id;
    } catch (java.lang.NullPointerException e) {
      user_name = "user_name_XXX";
      room_id = "room_id_XXX";
    }
    
    String msgIn = params.getString(3);
    getLogger().info("room_id="+ room_id +" "+ user_name +" "+ msgIn);
  }

}
