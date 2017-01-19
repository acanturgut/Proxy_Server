import java.net.*;

public class ProxyServer extends Thread
{
	private ServerSocket server = null;
	private int thisPort = 8080;
	
	public ProxyServer (int port){
		thisPort = port;
	}

	public void run()
	{
		try {
			server = new ServerSocket(thisPort);
			while (true)
			{
				Socket client = server.accept();
				ProxyThread t = new ProxyThread(client);
				t.start();
			}
		}catch (Exception e) {}
		try {server.close();} 
		catch(Exception e)  {}

		server = null;
	}

}

