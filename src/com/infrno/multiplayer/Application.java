package com.infrno.multiplayer;

import com.infrno.multiplayer.util.DatabaseClass;
import com.wowza.wms.application.*;
import com.wowza.wms.amf.*;
import com.wowza.wms.client.*;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.module.*;
import com.wowza.wms.request.*;
import com.wowza.wms.sharedobject.ISharedObject;
import com.wowza.wms.sharedobject.ISharedObjects;
import com.wowza.wms.sharedobject.SharedObject;

public class Application extends ModuleBase {
	public IApplicationInstance app_instance;
	public ChatManager chatManager;
	public DatabaseClass databaseManager;
	public StreamManager streamManager;
	public UserManager userManager;
	public WhiteboardManager whiteboardManager;

	// public ISharedObject sharedObject;

	public void onAppStart(IApplicationInstance appInstance) {
		String fullname = appInstance.getApplication().getName() + "/"
				+ appInstance.getName();
		getLogger().info(
				"Application.onAppStart() Infrno version 0.8.4 " + fullname);

		app_instance = appInstance;
		chatManager = new ChatManager(this);
		databaseManager = new DatabaseClass(this);
		streamManager = new StreamManager(this);
		userManager = new UserManager(this);
		whiteboardManager = new WhiteboardManager(this);

		// ISharedObjects sharedObjects = app_instance.getSharedObjects(true);
		// sharedObject = sharedObjects.getOrCreate("whiteboard_contents");
		// sharedObjects.put("whiteboard_contents", sharedObject);
		// sharedObject.lock();
		// try
		// {
		// sharedObject.acquire();
		// }
		// catch (Exception e)
		// {
		//			
		// }
		// finally
		// {
		// sharedObject.unlock();
		// }
	}

	public void onAppStop(IApplicationInstance appInstance) {
		String fullname = appInstance.getApplication().getName() + "/"
				+ appInstance.getName();
		getLogger().info("Application.onAppStop() " + fullname);

		// ISharedObjects sharedObjects = app_instance.getSharedObjects(false);
		// ISharedObject so_streams = sharedObjects.get(app_instance.getName());
		//		
		// if (so_streams != null)
		// {
		// so_streams.lock();
		// try
		// {
		// so_streams.release();
		// }
		// catch (Exception e)
		// {
		//				
		// }
		// finally
		// {
		// so_streams.unlock();
		// }
		// }
		//				
		chatManager = null;
		databaseManager = null;
		streamManager = null;
		userManager = null;
		whiteboardManager = null;
		app_instance = null;

	}

	public void onConnect(IClient client, RequestFunction function,
			AMFDataList params) {
		userManager.userConnect(client, params);
	}

	public void onDisconnect(IClient client) {
		getLogger().info("Application.onDisconnect() ");
		userManager.userDisconnect(client);

		// sharedObject.flush();

		// ISharedObjects sharedObjects = app_instance.getSharedObjects( true );
		// sharedObject = sharedObjects.get( "whiteboard_contents" );
		// sharedObject.lock();
		// try{
		// WMSLoggerFactory.getLogger(null).info("Application.onDisconnect() sharedObject.getName = "+
		// sharedObject.getName());
		// sharedObject.acquire();
		// sharedObject.flush();
		// }catch (Exception e){
		// WMSLoggerFactory.getLogger(null).info("Application.onDisconnect() Error "+e.toString());
		// }finally{
		// WMSLoggerFactory.getLogger(null).info("Application.onDisconnect() sharedObject.unlock ");
		// sharedObject.unlock();
		// }

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

	public void chatToServer(IClient client, RequestFunction function,
			AMFDataList params) {
		chatManager.chatToServer(client, params);
	}

	public void getUserStats(IClient client, RequestFunction function,
			AMFDataList params) {
		userManager.getUserStats();
	}

	public void reportUserStats(IClient client, RequestFunction function,
			AMFDataList params) {
		userManager.reportUserStats(client, params);
	}

	public void updateUserInfo(IClient client, RequestFunction function,
			AMFDataList params) {
		userManager.updateUserInfo(Integer.toString(client.getClientId()),
				params.get(PARAM1));
	}

	public void sendImage(IClient client, RequestFunction function,
			AMFDataList params) {
		whiteboardManager.sendImage(client, function, params);
	}

	public void returnImageURL(IClient client, AMFDataList params,
			String imageURL, String sdID) {

		AMFDataObj returnObj = new AMFDataObj();
		returnObj.put("imageURL", imageURL);
		returnObj.put("sdID", sdID);

		sendResult(client, params, returnObj);

	}

}
