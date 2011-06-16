package com.infrno.multiplayer.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class RollCommand implements ChatExpression {
	public static Evaluator createEvaluator( ) {
		return new RollCommandEvaluator( );
	}
	
	private static String MAGIC_ROLL = "total=42";
	private Integer m_numberOfDie;
	private Integer m_numberOfSides;
	private int m_modifier;
	private String m_token;
	
	public RollCommand( String token, Integer numberOfDie, Integer numberOfSides ) {
		m_numberOfDie = numberOfDie;
		m_numberOfSides = numberOfSides;
		m_modifier = 0;
		m_token = token;
	}	
	
	public void setModifier( int modifier ) {
		m_modifier = modifier;
	}
	
	public Integer getNumberOfDie( ) {
		return m_numberOfDie;
	}
	
	public Integer getNumberOfSides( ) {
		return m_numberOfSides;
	}

	@Override
	public String interpret( Map< String, String > context ) {
		
		StringBuffer result = new StringBuffer( );
		
		// This will be used when hide roll gets implemented
//		String user_name = context.get("user_name");
//		result.append(user_name);
		
		result.append("rolled ");
		result.append(m_token);
		
		if (m_modifier < 0) {
			result.append(m_modifier);
		} else if (m_modifier > 0) {
			result.append('+');			
			result.append(m_modifier);			
		}
		
		result.append(": ");

		if( m_numberOfDie.intValue( ) <= 0 ) {
			result.append(MAGIC_ROLL);
			return result.toString();
		}
		
		if( m_numberOfDie.intValue( ) > 100 ) {
			result.append(MAGIC_ROLL);
			return result.toString();
		}
		
		if( m_numberOfSides.intValue( ) <= 0 ) {
			result.append(MAGIC_ROLL);
			return result.toString();
		}
		
		if( m_numberOfSides.intValue( ) > 10000 ) {
			result.append(MAGIC_ROLL);
			return result.toString();
		}
		
		
		Random random = new Random( );
		Integer[] rolls = new Integer[m_numberOfDie];
	
		int total = 0;
		for( int i = 0; i < m_numberOfDie.intValue( ); i++ ) {
			int roll = random.nextInt( m_numberOfSides ) + 1;
			total += roll;
			rolls[i] = roll;
		}
		
		Arrays.sort(rolls);
		
    for(int i = 0; i < m_numberOfDie.intValue( ); i++ ) {
      result.append( rolls[i] );
      result.append( ", " );
    }	
		
		total += m_modifier;
		
		result.append( "total=" );
		result.append( total );
		
		return result.toString( );
	}

	@Override
	public boolean verifyParameters( Matcher matcher ) {
		if( matcher.find( ) ) {
			return false;
		}
		
		return true;
	}
}

class RollCommandEvaluator implements Evaluator {

	private static Pattern m_pattern = Pattern.compile( "^(\\d+)[dD](\\d+)$" );
	private static Logger m_logger = Logger.getLogger( RollCommandEvaluator.class );
	
	@Override
	public ChatExpression createExpression( String token ) {
		Matcher matcher = m_pattern.matcher( token );
		
		if( !matcher.find( ) ) {
			return null;
		}
		
		m_logger.debug( "this is a match." );
		m_logger.debug( "matcher group count = " + matcher.groupCount( ) );
		
		if( matcher.groupCount( ) != 2 ) {
			return null;
		}
		
		String numberOfDieString = matcher.group( 1 );
		String numberOfSidesString = matcher.group( 2 );
		Integer numberOfDie = Integer.decode( numberOfDieString );
		Integer numberOfSides = Integer.decode( numberOfSidesString );
		
		m_logger.debug( "number of die=" + numberOfDie );
		m_logger.debug( "number of sides=" + numberOfSides );
		
		return new RollCommand( token, numberOfDie, numberOfSides );
	}

	@Override
	public boolean isMatch( String token ) {	
		Matcher matcher = m_pattern.matcher( token );
		return matcher.find( );
	}
}