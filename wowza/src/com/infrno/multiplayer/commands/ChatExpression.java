package com.infrno.multiplayer.commands;

import java.util.Map;
import java.util.regex.Matcher;

public interface ChatExpression {
	boolean verifyParameters( Matcher matcher );
	String interpret( Map< String, String > context );
}