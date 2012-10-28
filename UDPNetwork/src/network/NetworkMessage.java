package network;

import java.net.*;

public class NetworkMessage
{
	private String mMessage;
	private InetAddress mAddress;
	private int mPort;
	
	public NetworkMessage(String newMessage, InetAddress newAddress, int newPort)
	{
		mMessage = newMessage;
		mAddress = newAddress;
		mPort = newPort;
	}
	
	public String GetMessage()
	{
		return mMessage;
	}
	
	public int GetPort()
	{
		return mPort;
	}
	
	public InetAddress GetAddress()
	{
		return mAddress;
	}
	
	public void Sanatize()
	{
		mMessage = StringSanitizer.Sanitize(mMessage);
	}
}