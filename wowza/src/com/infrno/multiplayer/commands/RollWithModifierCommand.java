package com.infrno.multiplayer.commands;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollWithModifierCommand implements ChatExpression {
	public static Evaluator createEvaluator( ) {
		return new RollWithModifierEvaluator( );
	}
	
	private Integer m_modifier;
	private RollCommand m_rollCommand;
	
	public RollWithModifierCommand( RollCommand rollCommand, Integer modifier ) {
		m_rollCommand = rollCommand;
		m_modifier = modifier;
	}
	
	public Integer getNumberOfDie( ) {
		return m_rollCommand.getNumberOfDie( );
	}
	
	public Integer getNumberOfSides( ) {
		return m_rollCommand.getNumberOfSides( );
	}
	
	public Integer getModifier( ) {
		return m_modifier;
	}

	@Override
	public String interpret( Map< String, String > context ) {
		m_rollCommand.setModifier( m_modifier.intValue( ) );
		return m_rollCommand.interpret( context );
	}

	@Override
	public boolean verifyParameters( Matcher matcher ) {
		if( matcher.find( ) ) {
			return false;
		}
		
		return true;
	}
}

class RollWithModifierEvaluator implements Evaluator {

	private static Pattern m_pattern = Pattern.compile( "^(\\S+)([\\+-])(\\d+)$" );
	
	@Override
	public ChatExpression createExpression( String token ) {
		Matcher matcher = m_pattern.matcher( token );
		if( !matcher.find( ) ) {
			return null;
		}
		
		System.out.println( "this is a match" );
		System.out.println( "matcher group count = " + matcher.groupCount( ) );
		if( matcher.groupCount( ) != 3 ) {
			return null;
		}
		
		String rollCommandTokenString = matcher.group( 1 );
		Evaluator rollCommandEvaluator = RollCommand.createEvaluator( );
		if( !rollCommandEvaluator.isMatch( rollCommandTokenString ) ) {
			return null;
		}
				
		RollCommand rollCommand = ( RollCommand ) rollCommandEvaluator.createExpression( rollCommandTokenString );
		String modifierString = matcher.group( 3 );
		Integer modifier = Integer.decode( modifierString );
		
		String rollCommandSignString = matcher.group( 2 );
		if( rollCommandSignString.equals("-") ) {
			modifier = modifier * -1 ;
		}

		System.out.println( "modifier=" + modifier );
		
		RollWithModifierCommand rollWithModifierCommand = new RollWithModifierCommand( rollCommand, modifier );
		
		return rollWithModifierCommand;
	}

	@Override
	public boolean isMatch( String token ) {
		Matcher matcher = m_pattern.matcher( token );
		if( !matcher.find( ) ) {
			return false;
		}
		
		if( matcher.groupCount( ) != 3 ) {
			return false;
		}
		
		String rollCommandTokenString = matcher.group( 1 );
		Evaluator rollCommandEvaluator = RollCommand.createEvaluator( );
		if( !rollCommandEvaluator.isMatch( rollCommandTokenString ) ) {
			return false;
		}
		
		return true;
	}
	
}