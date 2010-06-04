package com.infrno.multiplayer.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RollCommandTestCase {

	@Before
	public void setUp( ) throws Exception {
	}

	@After
	public void tearDown( ) throws Exception {
	}
	
	@Test
	public void notARollCommandToken( ) {
		Evaluator evaluator = RollCommand.createEvaluator( );
		assertFalse( evaluator.isMatch( "" ) );
		assertFalse( evaluator.isMatch( "fdfadsfsdfd" ) );
		assertFalse( evaluator.isMatch( "111111" ) );
		assertFalse( evaluator.isMatch( "44 d 33" ) );		
		assertFalse( evaluator.isMatch( "44aD33" ) );
		assertFalse( evaluator.isMatch( "44a433" ) );
		assertFalse( evaluator.isMatch( "44d33a" ) );
		assertFalse( evaluator.isMatch( "44d33 444") );
		assertFalse( evaluator.isMatch( "-4d33" ) );
		assertFalse( evaluator.isMatch( "+4d33" ) );
		assertFalse( evaluator.isMatch( "44d-33" ) );
		assertFalse( evaluator.isMatch( "44d33+" ) );
	}
	
	@Test
	public void aRollCommandToken( ) {
		Evaluator evaluator = RollCommand.createEvaluator( );
		assertTrue( evaluator.isMatch( "1d3" ) );
		assertTrue( evaluator.isMatch( "1D3" ) );
		assertTrue( evaluator.isMatch( "444D33" ) );
		assertTrue( evaluator.isMatch( "1d33" ) );
	}
	
	@Test
	public void createRollCommandToken( ) {
		Evaluator evaluator = RollCommand.createEvaluator( );
		RollCommand rollCommand = null;
		
		rollCommand = ( RollCommand ) evaluator.createExpression( "1d3" );
		assertNotNull( rollCommand );
		assertEquals( 1, rollCommand.getNumberOfDie( ).intValue( ) );
		assertEquals( 3, rollCommand.getNumberOfSides( ).intValue( ) );
		
		rollCommand = ( RollCommand ) evaluator.createExpression( "1D3" );
		assertNotNull( rollCommand );
		assertEquals( 1, rollCommand.getNumberOfDie( ).intValue( ) );
		assertEquals( 3, rollCommand.getNumberOfSides( ).intValue( ) );
	}
	
	@Test
	public void verifyParametersAreEmpty( ) {
		RollCommand rollCommand = new RollCommand( Integer.valueOf( 10 ), Integer.valueOf( 10 ) );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "" );
		
		assertTrue( rollCommand.verifyParameters( matcher ) );
	}
	
	@Test
	public void verfifyInvalidWhenParametersAreNotEmpty( ) {
		RollCommand rollCommand = new RollCommand( Integer.valueOf( 10 ), Integer.valueOf( 10 ) );
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "some value" );
		
		assertFalse( rollCommand.verifyParameters( matcher ) );
	}
	
	@Test
	public void interpretRolls( ) {
		RollCommand rollCommand = new RollCommand( Integer.valueOf( 5 ), Integer.valueOf( 10 ) );
		String rolls = rollCommand.interpret( null );
		System.out.println( rolls );
		
		assertNotNull( rolls );
		rolls = rolls.trim( );
		assertFalse( rolls.isEmpty( ) );
		
		String[ ] rollList = rolls.split( "," );
		assertNotNull( rollList );
		assertEquals( 6, rollList.length );	
	}
}
