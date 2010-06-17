package com.infrno.multiplayer.util;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

public class InfrnoFilter extends Filter {
	
	@Override
	public int decide( LoggingEvent loggingEvent ) {
		LocationInfo locationInfo = loggingEvent.getLocationInformation( );
		String callerClass = locationInfo.getClassName( );
		if( null == callerClass ) {
			return Filter.NEUTRAL;
		}
		
		if( callerClass.startsWith( "com.infrno" ) ) {
			return Filter.ACCEPT;
		}
		
		return Filter.DENY;
			
	}
}
