package com.infrno.multiplayer.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageEvaluator {
	private ChatExpression m_chatExpression;
	
	public MessageEvaluator( String message ) {
		
		String trimmedMessage = message.trim( );
		if( trimmedMessage.length( ) == 0 ) {
			m_chatExpression = new StringExpression( "" );
			return;
		}
		
		List< Evaluator > evaluators = new ArrayList< Evaluator >( );
		evaluators.add( HideKeyWord.createEvaluator( ) );
		evaluators.add( RollKeyWord.createEvaluator( ) );
		evaluators.add( RKeyWord.createEvaluator( ) );
		evaluators.add( RollCommand.createEvaluator( ) );
		evaluators.add( RollWithModifierCommand.createEvaluator( ) );
		evaluators.add( StringExpression.createEvaluator( ) );
		
		Pattern tokenPattern = Pattern.compile( "\\S+" );
		Matcher tokenMatcher = tokenPattern.matcher( message.trim( ) );
		
		if( !tokenMatcher.find( ) ){
			m_chatExpression = new StringExpression( message );
			return;
		}
		
		String match = tokenMatcher.group( );
		
		//
		// TODO: 
		// this should be logging using logger.
		//
		System.out.println( match );
		
		Iterator< Evaluator > evaluatorsIterator = evaluators.iterator( );
		while( evaluatorsIterator.hasNext( ) ) {
			Evaluator evaluator = evaluatorsIterator.next( );
			if( !evaluator.isMatch( match ) ) {
				continue;
			}
			
			m_chatExpression = evaluator.createExpression( match );
			break;
		}
		
		if( null == m_chatExpression ) {
			m_chatExpression = new StringExpression( message );
			return;
		}
		
		if( !m_chatExpression.verifyParameters( tokenMatcher ) ) {
			m_chatExpression = new StringExpression( message ); 
		}
	}
	
	public ChatExpression getChatExpression( ) {
		return m_chatExpression;
	}
}
