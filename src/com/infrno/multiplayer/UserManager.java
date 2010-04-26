package com.infrno.multiplayer;

import com.wowza.wms.amf.AMFData;
import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.client.IClient;

public class UserManager 
{
	public AMFDataObj users_obj;

	private Application main_app;
	
	public UserManager(Application app) 
	{
		main_app = app;
		users_obj = new AMFDataObj();
	}
	
	public void userConnect(IClient client, AMFDataList params)
	{
		//TODO: put in user authentication stuff here
		main_app.log("onConnect: " + client.getClientId());
		
		AMFDataObj curr_user_obj = (AMFDataObj) params.get(3);
//		AMFDataObj curr_user_obj = new AMFDataObj();
		curr_user_obj.put("suid", client.getClientId());
		
		client.setStreamReadAccess(IClient.READ_ACCESS_ALL);
		client.setStreamWriteAccess(IClient.WRITE_ACCESS_ALL);
		
		client.call("initUser",null,curr_user_obj);
//		updateUserInfo(Integer.toString(client.getClientId()), (AMFData) curr_user_obj);
		
		client.acceptConnection();
	}
	
	public void userDisconnect(IClient client)
	{
		main_app.log("onDisconnect: " + client.getClientId());
		
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
	
	public void updateUserInfo(String suid, AMFData user_obj)
	{
		users_obj.put(suid,user_obj);
		main_app.app_instance.broadcastMsg("updateUsers", users_obj);
		
		main_app.streamManager.checkStreamSupport();
	}
	
	public AMFDataObj getClientInfo(String suid)
	{
		return (AMFDataObj) users_obj.get(suid);
	}
}

/*

User properties to expose:
uname
suid (maybe use this for the stream)
peer_enabled //is connected and supports peer connections
nearID (used for stream)

*/