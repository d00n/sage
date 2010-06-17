package com.infrno.multiplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.wowza.wms.amf.AMFDataByteArray;
import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.IModuleCallResult;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;


class CallResult extends ModuleBase implements IModuleCallResult {
	public void onResult(IClient client, RequestFunction function, AMFDataList params) {

		// String returnValue = getParamString(params, PARAM1);
		// getLogger().info("onResult: "+ returnValue);
	}
}
public class WhiteboardManager 
{
	private Application main_app;
	
//	private static String IMAGE_CACHE_DIR = "C:/Program Files/Apache Software Foundation/Apache2.2/htdocs/";
//	private static String IMAGE_HOST = "http://localhost";
	
	private static String IMAGE_HOST = "http://admin.infrno.net/";
	private static String IMAGE_CACHE_DIR = "images/";
	private static String DOC_ROOT = "/var/www/html/";
	
	public WhiteboardManager(Application app) 
	{
		main_app = app;
	}
	
	public void myFunction(IClient client, RequestFunction function, AMFDataObj params) {
		client.call("receiveJPG", new CallResult(), params);
	}
	
	public void sendImage(IClient client, RequestFunction function,	AMFDataList params) {
		main_app.log("WhiteboardManager.sendImage()");
		
		//byte[] bArray = getParam(params, PARAM1).serialize();
		

		AMFDataByteArray image_amfba 	= (AMFDataByteArray) params.get(3);
		String imageName 				= params.getString(4);
		String sdID		 				= params.getString(5);
//		AMFDataItem x_amfdi 			= new AMFDataItem(params.getString(3));
//		AMFDataItem y_amfdi 			= new AMFDataItem(params.getString(4));
//		AMFDataItem width_amfdi 		= new AMFDataItem(params.getString(5));
//		AMFDataItem height_amfdi 		= new AMFDataItem(params.getString(6));
//		AMFDataItem zIndex_amfdi 		= new AMFDataItem(params.getString(7));
//		AMFDataItem origHeight_amfdi 	= new AMFDataItem(params.getString(8));
//		AMFDataItem origWidth_amfdi 	= new AMFDataItem(params.getString(9));
//		AMFDataItem styleName_amfdi 	= new AMFDataItem(params.getString(10));
//		
//		AMFDataObj returnObj = new AMFDataObj();		
//		returnObj.put("image", image_amfba);
//		returnObj.put("sdID", sdID_amfdi);
//		returnObj.put("x", x_amfdi);
//		returnObj.put("y", y_amfdi);
//		returnObj.put("width", width_amfdi);
//		returnObj.put("height", height_amfdi);
//		returnObj.put("zIndex", zIndex_amfdi);
//		returnObj.put("origHeight", origHeight_amfdi);
//		returnObj.put("origWidth", origWidth_amfdi);
//		returnObj.put("styleName", styleName_amfdi);

		String hash = new String();
		try {
			hash = computeSum(imageName + Math.random() );
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String hashPath = convertHashToPath(hash);
		
		String fullPath = DOC_ROOT + IMAGE_CACHE_DIR + hashPath;
		
		File fullPathDirs = new File(fullPath);
		fullPathDirs.mkdirs();
		
		main_app.log("WhiteboardManager.sendImage() about to save: " + fullPathDirs + imageName);
		
		try {
			FileOutputStream fos = new FileOutputStream(fullPath + imageName);
			fos.write(image_amfba.toArray());
			fos.close();
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException : " + ex);
		} catch (IOException ioe) {
			System.out.println("IOException : " + ioe);
		}
		

		String imageURL = IMAGE_HOST + IMAGE_CACHE_DIR + hashPath + imageName;
		main_app.returnImageURL(client, params, imageURL, sdID);
		
		String appInstanceName = main_app.app_instance.getName();
		
//		main_app.databaseManager.saveImage();
//
//		Client jpg_client;
//		Iterator i = clientList.iterator();
//		while (i.hasNext()) {
//			jpg_client = (Client) i.next();
//			if (jpg_client != client)
//				myFunction(jpg_client, function, returnObj);
//
//		}
	}
	
	private static final String convertHashToPath(String hash) {
		StringBuffer sbuf = new StringBuffer();		

		for (int i=0; i<32; i=i+4){
			sbuf.append( hash.substring(i, i+4) );
			sbuf.append("/");
		}
		
		return sbuf.toString();
	}
	
	private static final String computeSum(String input) throws NoSuchAlgorithmException {

		if (input == null) {
		   throw new IllegalArgumentException("Input cannot be null!");
		}
	
		StringBuffer sbuf = new StringBuffer();
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte [] raw = md.digest(input.getBytes());
		
		for (int i = 0; i < raw.length; i++) {
			int c = (int) raw[i];
			if (c < 0) {
				c = (Math.abs(c) - 1) ^ 255;
			}
			String block = toHex(c >>> 4) + toHex(c & 15);
			sbuf.append(block);
		}
		
		return sbuf.toString();	
	}
	
	private static final String toHex(int s) {
		if (s < 10) {
		   return new StringBuffer().
	                            append((char)('0' + s)).
	                            toString();
		} else {
		   return new StringBuffer().
	                            append((char)('A' + (s - 10))).
	                            toString();
		}
	}

}
