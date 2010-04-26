package com.infrno.multiplayer;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.client.IClient;

public class ChatManager 
{
	private Application main_app;
	
	public ChatManager(Application app) 
	{
		main_app = app;
	}
	
	public void chatToServer(IClient client, AMFDataList msgObj)
	{
		String msgIn = msgObj.getString(3);
		//4th param may one day contain targetd users to recieve the chat
		
		String uname = main_app.userManager.getClientInfo(Integer.toString(client.getClientId())).getString("uname");
		
		main_app.log("chat from client came in: "+msgIn);
		main_app.app_instance.broadcastMsg("chatToUser", uname+": "+msgIn);
	}
	
}
