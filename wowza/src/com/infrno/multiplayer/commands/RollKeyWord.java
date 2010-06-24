package com.infrno.multiplayer.commands;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollKeyWord implements ChatExpression {
	public static Evaluator createEvaluator( ) {
		return new RollKeyWordEvaluator( );
	}
	
	private RollCommand m_rollCommand;
	private RollWithModifierCommand m_rollWithModifierCommand;
	
	public RollKeyWord( ) {
	}

	@Override
	public String interpret( Map< String, String > context ) {
		if( null != m_rollCommand ) { 
			return m_rollCommand.interpret( context );
		}
		
		return m_rollWithModifierCommand.interpret( context );
	}

	@Override
	public boolean verifyParameters( Matcher matcher ) {
		if( !matcher.find( ) ){
			return false;
		}
		
		Evaluator evaluator = RollCommand.createEvaluator( );
		if( evaluator.isMatch( matcher.group( ) ) ) {
			m_rollCommand = ( RollCommand ) evaluator.createExpression( matcher.group( ) );
			if( !m_rollCommand.verifyParameters( matcher ) ) {
				m_rollCommand = null;
				return false;
			}
			
			return true;
		}
		
		evaluator = RollWithModifierCommand.createEvaluator( );
		if( evaluator.isMatch( matcher.group( ) ) ) {
			m_rollWithModifierCommand = ( RollWithModifierCommand ) evaluator.createExpression( matcher.group( ) );
			if( !m_rollWithModifierCommand.verifyParameters( matcher ) ){
				m_rollWithModifierCommand = null;
				return false;
			}
			
			return true;
		}
		
		return false;
	}
}

class RollKeyWordEvaluator implements Evaluator {
	private static Pattern m_pattern = Pattern.compile( "^roll$", Pattern.CASE_INSENSITIVE );
	
	@Override
	public ChatExpression createExpression(String token) {
		Matcher matcher = m_pattern.matcher( token );
		if( !matcher.find( ) ) {
			return null;
		}
		
		return new RollKeyWord( );
		
	}

	@Override
	public boolean isMatch( String token ) {
		Matcher matcher = m_pattern.matcher( token );
		return matcher.find( );
	}
}