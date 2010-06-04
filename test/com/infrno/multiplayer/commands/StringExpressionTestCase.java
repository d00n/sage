package com.infrno.multiplayer.commands;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class StringExpressionTestCase {

	@Before
	public void setUp( ) throws Exception {
	}

	@After
	public void tearDown( ) throws Exception {
	}
	
	@Test
	public void aStringExpressionToken( ) {
		Evaluator evaluator = StringExpression.createEvaluator( );
		assertTrue( evaluator.isMatch( "jdkd" ) );
		assertTrue( evaluator.isMatch( "ffdf1_!!" ) );
		assertTrue( evaluator.isMatch( "_fdfas121982982091" ) );
		assertTrue( evaluator.isMatch( "2121" ) );
	}
	
	@Test
	public void notAStringExpressionToken( ) {
		Evaluator evaluator = StringExpression.createEvaluator( );
		assertFalse( evaluator.isMatch( "fdsf fasdfd" ) );
		assertFalse( evaluator.isMatch( " fdsfjkds" ) );
		assertFalse( evaluator.isMatch( "+ fdkfd 1121" ) );
		assertFalse( evaluator.isMatch( "1212 " ) );
		assertFalse( evaluator.isMatch( "jfdks _ fdfkd" ) );
	}
	
	@Test
	public void createStringExpressionToken( ) {
		Evaluator evaluator = StringExpression.createEvaluator( );
		StringExpression stringExpression = null;
		
		stringExpression = ( StringExpression ) evaluator.createExpression( "hide" );
		assertNotNull( stringExpression );
		
		MessageEvaluator messageEvaluator = new MessageEvaluator( "one two three four five" );
		ChatExpression chatExpression = messageEvaluator.getChatExpression( );
		System.out.println( "--->" + chatExpression.interpret( null ) + "<---"  );
	}
	
	@Test
	public void interpretWhenNoParametersWereVerified( ) {
		StringExpression stringExpression = new StringExpression( "one" );
		assertNotNull( "there was no string expression", stringExpression.interpret( null ) );
		assertEquals( "did not get the interpret result", "one", stringExpression.interpret( null ) );
	}
	
	@Test
	public void interpretWhenParametersVerified( ) {
		StringExpression stringExpression = new StringExpression( "one" );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "a b c d e" );
		stringExpression.verifyParameters( matcher );
		assertNotNull( "there was no string expression", stringExpression.interpret( null ) );
		assertEquals( "did not get the interpret result", "one a b c d e", stringExpression.interpret( null ) );
	}
	
	@Test
	public void interpretWhenParametersVerifiedWithNoFinds( ) {
		StringExpression stringExpression = new StringExpression( "one" );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "" );
		stringExpression.verifyParameters( matcher );
		assertNotNull( "there was no string expression", stringExpression.interpret( null ) );
		assertEquals( "did not get the interpret result", "one", stringExpression.interpret( null ) );
	}
	
	@Test
	public void verifyParametersWithNoFinds( ) {
		StringExpression stringExpression = new StringExpression( "one" );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "" );
		
		assertTrue( stringExpression.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyParametersWithSomeFinds( ) {
		StringExpression stringExpression = new StringExpression( "one" );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "a b c d e f" );
		
		assertTrue( stringExpression.verifyParameters( matcher ) );
	}
}
