package com.infrno.multiplayer.commands;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollCommand implements ChatExpression {
	public static Evaluator createEvaluator( ) {
		return new RollCommandEvaluator( );
	}
	
	private Integer m_numberOfDie;
	private Integer m_numberOfSides;
	private int m_modifier;
	
	public RollCommand( Integer numberOfDie, Integer numberOfSides ) {
		m_numberOfDie = numberOfDie;
		m_numberOfSides = numberOfSides;
		m_modifier = 0;
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
		
		Random random = new Random( );
		StringBuffer result = new StringBuffer( );
		int total = 0;
		for( int i = 0; i < m_numberOfDie.intValue( ); i++ ) {
			int roll = random.nextInt( m_numberOfSides ) + 1 + m_modifier;
			total += roll;
			result.append( roll );
			result.append( ", " );
		}
		
		result.append( "total = " );
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
	
	@Override
	public ChatExpression createExpression( String token ) {
		Matcher matcher = m_pattern.matcher( token );
		
		if( !matcher.find( ) ) {
			return null;
		}
		
		System.out.println( "this is a match." );
		System.out.println( "matcher group count = " + matcher.groupCount( ) );
		
		if( matcher.groupCount( ) != 2 ) {
			return null;
		}
		
		String numberOfDieString = matcher.group( 1 );
		String numberOfSidesString = matcher.group( 2 );
		Integer numberOfDie = Integer.decode( numberOfDieString );
		Integer numberOfSides = Integer.decode( numberOfSidesString );
		
		System.out.println( "number of die=" + numberOfDie );
		System.out.println( "number of sides=" + numberOfSides );
		
		return new RollCommand( numberOfDie, numberOfSides );
	}

	@Override
	public boolean isMatch( String token ) {	
		Matcher matcher = m_pattern.matcher( token );
		return matcher.find( );	
	}
}