package com.infrno.multiplayer;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.infrno.multiplayer.util.AeSimpleSHA1;
import com.wowza.wms.amf.AMFData;
import com.wowza.wms.amf.AMFDataArray;
import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.amf.AMFDataMixedArray;
import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.amf.AMFObj;
import com.wowza.wms.client.IClient;
import com.wowza.wms.sharedobject.ISharedObject;
import com.wowza.wms.sharedobject.ISharedObjects;
import com.wowza.wms.sharedobject.SharedObject;

public class UserManager 
{
	private static String SHARED_KEY = "871a3f2c392e10ca2e04c442f1eedb65";
	private static String SHARED_OBJECT_NAME = "whiteboard_contents";
	
	public AMFDataObj users_obj;
	public String room_id;
	public String room_name;
	
	private Application main_app;
	
	public UserManager(Application app) 
	{
		main_app = app;
		users_obj = new AMFDataObj();
	}
	

	
	public void getUserStats()
	{
		main_app.app_instance.broadcastMsg("getUserStats");
	}
	
	public Boolean userConnect(IClient client, AMFDataList params)
	{
		//TODO: put in user authentication stuff here
		main_app.log("UserManager.userConnect() clientId:" + client.getClientId() +", client count:"+ main_app.app_instance.getClientCount());
		
		AMFDataObj curr_user_obj = (AMFDataObj) params.get(3);
		String auth_key = params.getString(4);
		room_id = params.getString(5);
		room_name = params.getString(6);
		String application_name = params.getString(7);
		String application_version = params.getString(8);
		String capabilities =  params.getString(9);

		if(!validateKey(auth_key)){
			main_app.log("UserManager.userConnect() user key invalid");
			client.rejectConnection();
			return false;
		}
		
		// TODO: allow client to connect when room is full, send a 'no vacancy' message, then disconnect them.
//		if(main_app.app_instance.getClientCount() > 3){
//			main_app.log("UserManager.userConnect() room is full");
//			client.rejectConnection();
//			return false;
//		}
		
		client.setSharedObjectReadAccess(SHARED_OBJECT_NAME);
		client.setSharedObjectWriteAccess(SHARED_OBJECT_NAME);
		
		curr_user_obj.put("suid", client.getClientId());
		
		client.setStreamReadAccess(IClient.READ_ACCESS_ALL);
		client.setStreamWriteAccess(IClient.WRITE_ACCESS_ALL);
		
		client.call("initUser",null,curr_user_obj);
//		updateUserInfo(Integer.toString(client.getClientId()), (AMFData) curr_user_obj);
		
		client.acceptConnection();
		
//		main_app.log("client getPageUrl "+client.getPageUrl());
//		main_app.log("client getUri "+client.getUri());
//		main_app.log("client getQueryStr "+client.getQueryStr());
//		main_app.log("client getReferrer "+client.getReferrer());
//		main_app.log("client getUri "+client.getUri());
		
		try {
			main_app.databaseManager.saveSessionStart(curr_user_obj);	
			main_app.databaseManager.saveSessionMemberStart(room_id, 
				room_name,
				curr_user_obj.getString("user_id"), 
				curr_user_obj.getString("user_name"), 
				application_name,
				application_version, 
				client.getClientId(),
				client.getFlashVer(),
				client.getIp(),
				capabilities);	
		} catch (Exception e) {
			main_app.error("DatabaseManager not online"+ e.getMessage());
		}
		
		return true;
	}
	
	public void userDisconnect(IClient client)
	{
		main_app.log("UserManager.onDisconnect() " + client.getClientId());
		
		try {
			main_app.databaseManager.saveSessionMemberEnd(client.getClientId());
		} catch (Exception e) {
			main_app.error("DatabaseManager not online"+ e.getMessage());
		}
		
//		ISharedObjects sharedObjects = main_app.app_instance.getSharedObjects(true);
//		ISharedObject whiteboard_contents = sharedObjects.get("whiteboard_contents");
//		
//		main_app.log("so size: " +whiteboard_contents.size() );
		
		
		
		String curr_user_suid = Integer.toString(client.getClientId());
		removeUser(curr_user_suid);
	}
	
	public void removeUser(String suid)
	{
		if(!users_obj.containsKey( suid ))
			return;
		
		users_obj.remove(suid);
		main_app.app_instance.broadcastMsg("updateUsers", users_obj);
	}
	
	public void reportUserStats(IClient client, AMFDataList params)
	{
		main_app.log("client getLastValidateTime "+client.getLastValidateTime());

		AMFDataObj amfDataObj = (AMFDataObj) params.get(3);		

		try {
			main_app.databaseManager.saveSessionReport(amfDataObj,
				client.getLastValidateTime(),
				client.getPingRoundTripTime(),
				(long) client.getMediaIOPerformanceCounter().getFileInBytesRate(),
				(long) client.getMediaIOPerformanceCounter().getFileOutBytesRate(),
				(long) client.getMediaIOPerformanceCounter().getMessagesInBytesRate(),
				client.getMediaIOPerformanceCounter().getMessagesInCountRate(),
				(long) client.getMediaIOPerformanceCounter().getMessagesLossBytesRate(),
				client.getMediaIOPerformanceCounter().getMessagesLossCountRate(),
				(long) client.getMediaIOPerformanceCounter().getMessagesOutBytesRate(),
				client.getMediaIOPerformanceCounter().getMessagesOutCountRate());
		} catch (Exception e) {
			main_app.error("DatabaseManager not online"+ e.getMessage());
		}
	}
	
	public void updateUserInfo(IClient client,String suid, AMFDataObj user_obj)
	{
//		AMFDataObj user_obj = data_obj.getObject("my_info");

		users_obj.put(suid,user_obj);

		if(getClientInfo(suid).getBoolean("report_connection_status")){
			//need to report flapping
			
			String application_name = client.getApplication().getName();
			
			String server_mode = Boolean.toString(getClientInfo(suid).getBoolean("peer_connection_status"));
			String user_id = getClientInfo(suid).getString("user_id");
			String user_name = getClientInfo(suid).getString("user_name");
			
			try{
				main_app.databaseManager.saveSessionMemberFlap(application_name, 
						room_name, 
						room_id, 
						user_name, 
						user_id, 
						server_mode);
			} catch (Exception e) {
				main_app.error("DatabaseManager not online"+ e.getMessage());
			}
		}
		
		main_app.app_instance.broadcastMsg("updateUsers", users_obj);
		main_app.streamManager.checkStreamSupport();
	}
	
	public AMFDataObj getClientInfo(String suid)
	{
		return (AMFDataObj) users_obj.get(suid);
	}
	
	private Boolean validateKey(String auth_string)
	{
		// TODO: restrict this to trusted hosts (localhost)
		if (auth_string.equals("sample_auth_key")) {
			main_app.log( "UserManger.validateKey() using sample_auth_key, just come right in." );
			return true;
		}
		
		main_app.log( "UserManager.validateKey() verifing key:" + auth_string );
		try{
			String auth_hash = auth_string.split(":")[0];
			String auth_time = auth_string.split(":")[1];
			
			main_app.log("UserManager.validateKey() curr time: "+new Date().getTime()/1000);
			main_app.log("UserManager.validateKey() passed time: "+Integer.parseInt(auth_time));
			
			if(new Date().getTime()/1000 > Integer.parseInt(auth_time)){
				main_app.log("UserManager.Authentication time is stale");
				return false;
			}
			
			String generated_hash=AeSimpleSHA1.SHA1(SHARED_KEY+auth_time);
			main_app.log("UserManager.Java output");
			main_app.log("UserManager.validateKey() "+generated_hash);
			
			return generated_hash.equals(auth_hash);
		} catch (NoSuchAlgorithmException e){
			main_app.log("UserManager.validateKey() Unable to generate hash");
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			main_app.log("UserManager.validateKey() Unable to generate hash");
			return false;
		}
	}
}

/*

User properties to expose:
user_name
suid (maybe use this for the stream)
peer_enabled //is connected and supports peer connections
nearID (used for stream)

*/