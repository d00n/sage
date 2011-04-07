package com.infrno.multiplayer;

import com.wowza.wms.client.IClient;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.module.IModulePingResult;

class PingResult implements IModulePingResult
{
  public void onResult(IClient client, long pingTime, int pingId, boolean result)
  {
    WMSLogger log = WMSLoggerFactory.getLogger(null);
    log.debug("onResult: result:"+result);
    if (!result)
    {
      // client has died lets kill it
      client.getAppInstance().shutdownClient(client);
    }
    else
      log.debug("lastPingTime: "+client.getPingRoundTripTime());
  }
}