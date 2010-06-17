package com.infrno.multiplayer.commands;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RKeyWordTestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void aRKeyWordToken( ) {
		Evaluator evaluator = RKeyWord.createEvaluator( );
		assertTrue( evaluator.isMatch( "r" ) );
		assertTrue( evaluator.isMatch( "R" ) );
	}
	
	@Test
	public void notARKeyWordToken( ) {
		Evaluator evaluator = RKeyWord.createEvaluator( );
		assertFalse( evaluator.isMatch( "r0ll" ) );
		assertFalse( evaluator.isMatch( " r" ) );
		assertFalse( evaluator.isMatch( "+r" ) );
		assertFalse( evaluator.isMatch( "r " ) );
		assertFalse( evaluator.isMatch( "rl" ) );
	}
	
	@Test
	public void createRKeyWordToken( ) {
		Evaluator evaluator = RKeyWord.createEvaluator( );
		RKeyWord rKeyWord = null;
		
		rKeyWord = ( RKeyWord ) evaluator.createExpression( "r" );
		assertNotNull( rKeyWord );
	}
	
	@Test
	public void interpretRollWhenRollCommandAsParameters( ) {
		RKeyWord rKeyWord = new RKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "5d10" );
		
		rKeyWord.verifyParameters( matcher );
		
		String rolls = rKeyWord.interpret( null );
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
		RKeyWord rKeyWord = new RKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "5d10+5" );
		
		rKeyWord.verifyParameters( matcher );
		
		String rolls = rKeyWord.interpret( null );
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
		RKeyWord rKeyWord = new RKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5" );
		
		assertTrue( rKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollCommandAsAParameterAndBadParameters( ) {
		RKeyWord rKeyWord = new RKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5 bad parameter" );
		
		assertFalse( rKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollWithModifierCommandAsAParameter( ) {
		RKeyWord rKeyWord = new RKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5+5" );
		
		assertTrue( rKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollWithModifierCommandAsAParameterAndBadParameters( ) {
		RKeyWord rKeyWord = new RKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5+5 bad parameter" );
		
		assertFalse( rKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyWithBadParameters( ) {
		RKeyWord rKeyWord = new RKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "a b c d e f" );
		
		assertFalse( rKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyWithNoParameters( ) {
		RKeyWord rKeyWord = new RKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "" );
		
		assertFalse( rKeyWord.verifyParameters( matcher ) );
	}
}
