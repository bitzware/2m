package com.bitzware.exm.listener;

/**
 * Interface for classes that need to be informed when this a connection to the
 * master server.
 * 
 * @author finagle
 */
public interface ConnectionListener {

	void onSuccessfulConnection();
	
}
