package com.infrno.multiplayer.commands;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RollWithModifierCommandTestCase {

	@Before
	public void setUp( ) throws Exception {
	}

	@After
	public void tearDown( ) throws Exception {
	}
	
	@Test
	public void aRollWithModifierCommandToken( ) {
		Evaluator evaluator = RollWithModifierCommand.createEvaluator( ); 
		assertTrue( evaluator.isMatch( "3d4+5" ) );
		assertTrue( evaluator.isMatch( "55D4+5" ) );
		assertTrue( evaluator.isMatch( "3d4-5" ) );
	}
	
	@Test
	public void notARollWithModifierCommandToken( ) {
		Evaluator evaluator = RollWithModifierCommand.createEvaluator( );
		assertFalse( evaluator.isMatch( "" ) );
		assertFalse( evaluator.isMatch( "3d4" ) );
		assertFalse( evaluator.isMatch( "3d4+ 5" ) );
		assertFalse( evaluator.isMatch( "3d4++5" ) );
		assertFalse( evaluator.isMatch( "3e4+5" ) );
		assertFalse( evaluator.isMatch( "3de4+4" ) );
	}
	
	@Test
	public void verifyRollWithPlusModifier( ) {
		Evaluator evaluator = RollWithModifierCommand.createEvaluator( );
		RollWithModifierCommand rollWithModifierCommand = null;
		rollWithModifierCommand = ( RollWithModifierCommand ) evaluator.createExpression( "3d1+5" );
		Map <String, String> context = new HashMap<String, String>();
		context.put("user_name", "billy");

		assertEquals( 3, rollWithModifierCommand.getNumberOfDie( ).intValue( ) );
		assertEquals( 1, rollWithModifierCommand.getNumberOfSides( ).intValue( ) );
		assertEquals( 5, rollWithModifierCommand.getModifier( ).intValue( ) );
		assertEquals( "rolled 3d1+5: 1, 1, 1, total=8", rollWithModifierCommand.interpret(context) );
	}
	
	@Test
	public void verifyRollWithMinusModifier( ) {
		Evaluator evaluator = RollWithModifierCommand.createEvaluator( );
		RollWithModifierCommand rollWithModifierCommand = null;
		rollWithModifierCommand = ( RollWithModifierCommand ) evaluator.createExpression( "3d1-5" );
		Map <String, String> context = new HashMap<String, String>();
		context.put("user_name", "billy");

		assertEquals( 3, rollWithModifierCommand.getNumberOfDie( ).intValue( ) );
		assertEquals( 1, rollWithModifierCommand.getNumberOfSides( ).intValue( ) );
		assertEquals( -5, rollWithModifierCommand.getModifier( ).intValue( ) );
		assertEquals( "rolled 3d1-5: 1, 1, 1, total=-2", rollWithModifierCommand.interpret(context) );
	}
	

	
	@Test
	public void createRollWithPlusModifierCommandToken( ) {
		Evaluator evaluator = RollWithModifierCommand.createEvaluator( );
		RollWithModifierCommand rollWithModifierCommand = null;
		
		rollWithModifierCommand = ( RollWithModifierCommand ) evaluator.createExpression( "3d4+5" );
		assertNotNull( rollWithModifierCommand );
		assertEquals( 3, rollWithModifierCommand.getNumberOfDie( ).intValue( ) );
		assertEquals( 4, rollWithModifierCommand.getNumberOfSides( ).intValue( ) );
		assertEquals( 5, rollWithModifierCommand.getModifier( ).intValue( ) );
	}
	
	@Test
	public void createRollWithMinusModifierCommandToken( ) {
		Evaluator evaluator = RollWithModifierCommand.createEvaluator( );
		RollWithModifierCommand rollWithModifierCommand = null;
		
		rollWithModifierCommand = ( RollWithModifierCommand ) evaluator.createExpression( "3d4-5" );
		assertNotNull( rollWithModifierCommand );
		assertEquals( 3, rollWithModifierCommand.getNumberOfDie( ).intValue( ) );
		assertEquals( 4, rollWithModifierCommand.getNumberOfSides( ).intValue( ) );
		assertEquals( -5, rollWithModifierCommand.getModifier( ).intValue( ) );
	}
	
	@Test
	public void verifyParametersAreNotEmpty( ) {
		RollCommand rollCommand = new RollCommand( "5d10", Integer.valueOf( 5 ), Integer.valueOf( 10 ) );
		RollWithModifierCommand rollWithModifierCommand = new RollWithModifierCommand( rollCommand, Integer.valueOf( 5 ) );
		
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "" );
		
		assertTrue( rollWithModifierCommand.verifyParameters( matcher ) );
	}
	
	@Test
	public void verifyInvalidWhenParametersAreNotEmpty( ) {
		RollCommand rollCommand = new RollCommand( "5d10", Integer.valueOf( 5 ), Integer.valueOf( 10 ) );
		RollWithModifierCommand rollWithModifierCommand = new RollWithModifierCommand( rollCommand, Integer.valueOf( 5 ) );
		
		Pattern pattern = Pattern.compile( "\\S+" );
		Matcher matcher = pattern.matcher( "some value" );
		
		assertFalse( rollWithModifierCommand.verifyParameters( matcher ) );
	}
	
	@Test
	public void interpretRolls( ) {
		RollCommand rollCommand = new RollCommand( "5d10", Integer.valueOf( 5 ), Integer.valueOf( 10 ) );
		RollWithModifierCommand rollWithModifierCommand = new RollWithModifierCommand( rollCommand, Integer.valueOf( 5 ) );

		Map <String, String> context = new HashMap<String, String>();
		context.put("user_name", "billy");

		String rolls = rollWithModifierCommand.interpret( context );
		System.out.println( rolls );
		
		assertNotNull( rolls );
		rolls = rolls.trim( );
		assertFalse( rolls.isEmpty( ) );
		
		String[ ] rollList = rolls.split( "," );
		assertNotNull( rollList );
		assertEquals( 6, rollList.length );	
	}
}
