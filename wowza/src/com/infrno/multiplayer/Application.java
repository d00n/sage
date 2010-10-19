package com.infrno.multiplayer;

import org.apache.log4j.Logger;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class Application extends ModuleBase {
	public IApplicationInstance app_instance;
	public ChatManager chatManager;
	public DatabaseManager databaseManager;
	public StreamManager streamManager;
	public UserManager userManager;
	public WhiteboardManager whiteboardManager;
	private Thread reportLoopThread;

	private static Logger m_logger = Logger.getLogger(Application.class);

	private static class ReportLoop implements Runnable {
		private static final int SECONDS_BETWEEN_REPORTS = 30;
		public Application main_app;

		public void run() {
			while (true) {
				try {
					Thread.sleep(SECONDS_BETWEEN_REPORTS * 1000);
					main_app.app_instance.broadcastMsg("getUserStats");
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
				"Application.onAppStart() Infrno v0.8.6 appName=" + appName
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
			error("DatabaseManager not online" + e.getMessage());
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

	public void onConnect(IClient client, RequestFunction function,	AMFDataList params) {
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

	public void chatToServer(IClient client, RequestFunction function,AMFDataList params) {
		chatManager.chatToServer(client, params);
	}

	public void getUserStats(IClient client, RequestFunction function,AMFDataList params) {
		userManager.getUserStats();
	}

	public void reportUserStats(IClient client, RequestFunction function,AMFDataList params) {
		userManager.reportUserStats(client, params);
	}	

	public void updateUserInfo(IClient client, RequestFunction function, AMFDataList params) {
		userManager.updateUserInfo(client, Integer.toString(client.getClientId()), params.getObject(PARAM1));
	}

	public void sendImage(IClient client, RequestFunction function, AMFDataList params) {
		whiteboardManager.sendImage(client, function, params);
	}

	public void returnImageURL(IClient client, AMFDataList params, String imageURL, String sdID) {

		AMFDataObj returnObj = new AMFDataObj();
		returnObj.put("imageURL", imageURL);
		returnObj.put("sdID", sdID);

		sendResult(client, params, returnObj);
	}

}
