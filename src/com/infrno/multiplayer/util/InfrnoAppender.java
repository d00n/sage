package com.infrno.multiplayer.util;

import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.varia.DenyAllFilter;

public class InfrnoAppender extends FileAppender {

	private boolean m_initialized = false;
	
	public InfrnoAppender( ) {
		super( );
		initialize( );
	}
	
	public InfrnoAppender( Layout layout, String filename ) throws IOException {
		super( layout, filename );
		initialize( );
	}
	
	public InfrnoAppender( Layout layout, String filename, boolean append ) throws IOException {
		super( layout, filename, append );
		initialize( );
	}
	
	public InfrnoAppender( Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize ) throws IOException {
		super( layout, filename, append, bufferedIO, bufferSize );
		initialize( );
	}
	
	private void initialize( ) {
		if ( m_initialized ) {
			return;
		}
		
		m_initialized = true;
		
		addFilter( new InfrnoFilter( ) );
		addFilter( new DenyAllFilter( ) );
	}
}
