package com.infrno.multiplayer;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.infrno.multiplayer.util.AeSimpleSHA1;
import com.wowza.wms.amf.AMFData;
import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.client.IClient;

public class UserManager 
{
	public static String SHARED_KEY = "871a3f2c392e10ca2e04c442f1eedb65";
	
	public AMFDataObj users_obj;

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
	
	public void userConnect(IClient client, AMFDataList params)
	{
		//TODO: put in user authentication stuff here
		main_app.log("UserManager.userConnect() " + client.getClientId());
		
		AMFDataObj curr_user_obj = (AMFDataObj) params.get(3);
		String auth_key = params.getString(4);
		String room_id = params.getString(5);
		String room_name = params.getString(6);
		String user_name = params.getString(7);
		
		//TODO: need to compare passed in key with encrypted key
		if(!validateKey(auth_key)){
			main_app.log("UserManager.userConnect() user key invalid");
			client.rejectConnection();
			return;
		}
		
		curr_user_obj.put("suid", client.getClientId());
		
		client.setStreamReadAccess(IClient.READ_ACCESS_ALL);
		client.setStreamWriteAccess(IClient.WRITE_ACCESS_ALL);
		
		client.call("initUser",null,curr_user_obj);
//		updateUserInfo(Integer.toString(client.getClientId()), (AMFData) curr_user_obj);
		
		client.acceptConnection();
	}
	
	public void userDisconnect(IClient client)
	{
		main_app.log("UserManager.onDisconnect() " + client.getClientId());
		
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
		main_app.log("UserManager.reportUserStats() user stats: "+params.get(3).toString());
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
	
	private Boolean validateKey(String auth_string)
	{
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
uname
suid (maybe use this for the stream)
peer_enabled //is connected and supports peer connections
nearID (used for stream)

*/