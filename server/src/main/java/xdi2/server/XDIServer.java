package xdi2.server;

import xdi2.server.exceptions.Xdi2ServerException;

public interface XDIServer {

	public void startServer() throws Xdi2ServerException;
	public void stopServer() throws Exception;
	public boolean isStarted();
}
