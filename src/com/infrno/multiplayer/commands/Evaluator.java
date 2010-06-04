package com.infrno.multiplayer.commands;

public interface Evaluator {
	boolean isMatch( String token );
	ChatExpression createExpression( String token );
}
