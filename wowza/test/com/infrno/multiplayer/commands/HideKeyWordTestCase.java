package com.infrno.multiplayer.commands;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HideKeyWordTestCase {

	@Before
	public void setUp( ) throws Exception {
	}

	@After
	public void tearDown( ) throws Exception {
	}
	
	@Test
	public void aHideKeyWordToken( ) {
		Evaluator evaluator = HideKeyWord.createEvaluator( );
		assertTrue( evaluator.isMatch( "hide" ) );
		assertTrue( evaluator.isMatch( "Hide" ) );
		assertTrue( evaluator.isMatch( "HIDE" ) );
		assertTrue( evaluator.isMatch( "hIdE" ) );
	}
	
	@Test
	public void notAHideKeyWordToken( ) {
		Evaluator evaluator = HideKeyWord.createEvaluator( );
		assertFalse( evaluator.isMatch( "h1de" ) );
		assertFalse( evaluator.isMatch( " hide" ) );
		assertFalse( evaluator.isMatch( "+hide" ) );
		assertFalse( evaluator.isMatch( "hide " ) );
		assertFalse( evaluator.isMatch( "hi33de" ) );
	}
	
	@Test
	public void createHideKeyWordToken( ) {
		Evaluator evaluator = HideKeyWord.createEvaluator( );
		HideKeyWord hideKeyWord = null;
		
		hideKeyWord = ( HideKeyWord ) evaluator.createExpression( "hide" );
		assertNotNull( hideKeyWord );
	}
	
	@Test
	public void verifyRollCommandAsAParameter( ) {
		HideKeyWord hideKeyWord = new HideKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5" );
		
		assertTrue( hideKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollCommandAsAParameterAndBadParameters( ) {
		HideKeyWord hideKeyWord = new HideKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5 bad parameter" );
		
		assertFalse( hideKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollWithModifierCommandAsAParameter( ) {
		HideKeyWord hideKeyWord = new HideKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5+5" );
		
		assertTrue( hideKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyRollWithModifierCommandAsAParameterAndBadParameters( ) {
		HideKeyWord hideKeyWord = new HideKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "3d5+5 bad parameters" );
		
		assertFalse( hideKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyWithBadParameters( ) {
		HideKeyWord hideKeyWord = new HideKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "a b c d e f" );
		
		assertFalse( hideKeyWord.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyWithNoParameters( ) {
		HideKeyWord hideKeyWord = new HideKeyWord( );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "" );
		
		assertFalse( hideKeyWord.verifyParameters( matcher ) );
	}
}
