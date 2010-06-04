package com.infrno.multiplayer.commands;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RollKeyWordTestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void aRollKeyWordToken( ) {
		Evaluator evaluator = RollKeyWord.createEvaluator( );
		assertTrue( evaluator.isMatch( "roll" ) );
		assertTrue( evaluator.isMatch( "Roll" ) );
		assertTrue( evaluator.isMatch( "ROLL" ) );
		assertTrue( evaluator.isMatch( "rOlL" ) );
	}
	
	@Test
	public void notARollKeyWordToken( ) {
		Evaluator evaluator = RollKeyWord.createEvaluator( );
		assertFalse( evaluator.isMatch( "r0ll" ) );
		assertFalse( evaluator.isMatch( " roll" ) );
		assertFalse( evaluator.isMatch( "+roll" ) );
		assertFalse( evaluator.isMatch( "roll " ) );
		assertFalse( evaluator.isMatch( "rol33l" ) );
	}
	
	@Test
	public void createRollKeyWordToken( ) {
		Evaluator evaluator = RollKeyWord.createEvaluator( );
		RollKeyWord rollKeyWord = null;
		
		rollKeyWord = ( RollKeyWord ) evaluator.createExpression( "roll" );
		assertNotNull( rollKeyWord );
	}
	
	@Test
	public void interpretRollWhenRollCommandAsParameters( ) {
		RollKeyWord rollKeyWord = new RollKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "5d10" );
		
		rollKeyWord.verifyParameters( matcher );
		
		String rolls = rollKeyWord.interpret( null );
		System.out.println( rolls );
		
		assertNotNull( rolls );
		rolls = rolls.trim( );
		assertFalse( rolls.isEmpty( ) );
		
		String[ ] rollList = rolls.split( "," );
		assertNotNull( rollList );
		assertEquals( 6, rollList.length );		
	}
	
	@Test
	public void interpretRollWhenRollWithModifierCommandAsParameters( ) {
		RollKeyWord rollKeyWord = new RollKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "5d10+5" );
		
		rollKeyWord.verifyParameters( matcher );
		
		String rolls = rollKeyWord.interpret( null );
		System.out.println( rolls );
		
		assertNotNull( rolls );
		rolls = rolls.trim( );
		assertFalse( rolls.isEmpty( ) );
		
		String[ ] rollList = rolls.split( "," );
		assertNotNull( rollList );
		assertEquals( 6, rollList.length );		
	}
	
	@Test
	public void verifyRollCommandAsAParameter( ) {
		RollKeyWord rollKeyWord = new RollKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5" );
		
		assertTrue( rollKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollCommandAsAParameterAndBadParameters( ) {
		RollKeyWord rollKeyWord = new RollKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5 bad parameter" );
		
		assertFalse( rollKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollWithModifierCommandAsAParameter( ) {
		RollKeyWord rollKeyWord = new RollKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5+5" );
		
		assertTrue( rollKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollWithModifierCommandAsAParameterAndBadParameters( ) {
		RollKeyWord rollKeyWord = new RollKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5+5 bad parameter" );
		
		assertFalse( rollKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyWithBadParameters( ) {
		RollKeyWord rollKeyWord = new RollKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "a b c d e f" );
		
		assertFalse( rollKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyWithNoParameters( ) {
		RollKeyWord rollKeyWord = new RollKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "" );
		
		assertFalse( rollKeyWord.verifyParameters( matcher ) );
	}
}
