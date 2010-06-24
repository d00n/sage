package com.infrno.multiplayer.commands;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringExpression implements ChatExpression {
	public static StringExpressionEvaluator createEvaluator( ) {
		return new StringExpressionEvaluator( );
	}
	
	private String m_stringExpression;
	private StringBuffer m_message;
	
	public StringExpression( String stringExpression ) {
		m_stringExpression = stringExpression;
		m_message = new StringBuffer( stringExpression );
	}
	
	public String getStringExpression( ) {
		return m_stringExpression;
	}

	@Override
	public String interpret( Map< String, String > context ) {
		return m_message.toString( );
	}

	@Override
	public boolean verifyParameters( Matcher matcher ) {
		m_message = new StringBuffer( );
		m_message.append( m_stringExpression );
		
		while( matcher.find( ) ) {
			m_message.append( " " );
			m_message.append( matcher.group( ) );
		}
		
		return true;
	}
}

class StringExpressionEvaluator implements Evaluator {
	
	private static Pattern m_pattern = Pattern.compile( "^\\S+$" );
	
	@Override
	public ChatExpression createExpression(String token) {
		Matcher matcher = m_pattern.matcher( token );
		if( !matcher.find( ) ) {
			return null;
		}
		
		return new StringExpression( token );
		
	}

	@Override
	public boolean isMatch( String token ) {
		Matcher matcher = m_pattern.matcher( token );
		return matcher.find( );
	}	
}

