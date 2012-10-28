package network;

import java.net.*;
import java.util.*;

public class Client implements Runnable
{
	private int mID;
	
	private String mServerName;
	private int mPort;
	private InetAddress mServerAddress;
	
	private DatagramSocket mSocket;
	
	private boolean mConnected;
	private boolean mDisconnected;
	
	private ArrayList<NetworkMessage> mUnsanitizedMessages;
	private ArrayList<NetworkMessage> mMessages;
	
	private TimeoutTimer mTimer;
	private MessageSanitizer mSanitizer;
	
	public Client(String newServerName, int newPort)
	{
		mID = 0;
		
		mServerName = newServerName;
		mPort = newPort;
		
		mConnected = false;
		mDisconnected = false;
		
		mUnsanitizedMessages = new ArrayList<NetworkMessage>();
		mMessages = new ArrayList<NetworkMessage>();
		
		mTimer = new TimeoutTimer();
		mSanitizer = new MessageSanitizer();
	}
	
	public int GetID()
	{
		return mID;
	}
	
	public synchronized boolean IsConnected()
	{
		return mConnected;
	}
	
	public synchronized boolean IsDisconnected()
	{
		return mDisconnected;
	}
	
	public void Disconnect()
	{
		mDisconnected = true;
	}
	
	public synchronized ArrayList<NetworkMessage> GetMessages()
	{
		return GetMessages(mMessages);
	}
	
	private synchronized void AddMessage(String message, InetAddress address, int port, ArrayList<NetworkMessage> list)
	{
		list.add(new NetworkMessage(message, address, port));
	}
	
	private synchronized void ClearMessages(ArrayList<NetworkMessage> list)
	{
		list.clear();
	}
	
	private synchronized ArrayList<NetworkMessage> GetMessages(ArrayList<NetworkMessage> list)
	{
		ArrayList<NetworkMessage> newList = new ArrayList<NetworkMessage>();
		
		for (int i = 0; i < list.size(); i++)
		{
			newList.add(list.get(i));
		}
		
		ClearMessages(list);
		
		return newList;
	}
	
	public void Connect()
	{
		try
		{
			mServerAddress = InetAddress.getByName(mServerName);
			
			mSocket = new DatagramSocket(mPort + 2, mServerAddress);
			
			new Thread(mTimer).start();
			new Thread(new ConnectionListener()).start();
			
			while (!mConnected && !mDisconnected)
			{
				Send("connect");
				
				try
				{
					Thread.sleep(250);
				}
				catch(Exception e){}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("failed to connect");
			mDisconnected = true;
		}
	}
	
	public synchronized void Send(String message)
	{
		byte[] buffer = (mID + " " + message).getBytes();
		DatagramPacket packet = new DatagramPacket(
				buffer,
				buffer.length,
				mServerAddress,
				mPort);
		
		try
		{
			mSocket.send(packet);
		}
		catch(Exception e)
		{
			System.out.println("Failed to send packet: " + message);
		}
	}
	
	class ConnectionListener implements Runnable
	{
		public void run()
		{
			while (!mConnected && !mDisconnected)
			{			
				try
				{
					byte[] buffer = new byte[256];
					DatagramPacket packet = new DatagramPacket(
							buffer,
							buffer.length);
					mSocket.receive(packet);
					
					mConnected = true;
					
					System.out.println("connected");
				}
				catch(Exception e)
				{
					
				}
			}
		}
	}
	
	public void run()
	{
		Connect();
		
		new Thread(mSanitizer).start();
		
		while (!mDisconnected)
		{			
			try
			{
				byte[] buffer = new byte[256];
				DatagramPacket packet = new DatagramPacket(
						buffer,
						buffer.length);
				mSocket.receive(packet);
				
				AddMessage(
						new String(buffer),
						packet.getAddress(),
						packet.getPort(),
						mUnsanitizedMessages);
				
				mTimer.Update();
			}
			catch(Exception e)
			{
				
			}
		}
		
		mSocket.close();
	}
	
	class TimeoutTimer implements Runnable
	{
		private long mTimeout = 2000;
		private long mTimer = 0;
		
		public void Update()
		{
			mTimer = System.currentTimeMillis() + mTimeout;
		}
		
		public void run()
		{
			Update();
			
			while (!mDisconnected)
			{				
				if (System.currentTimeMillis() > mTimer)
				{
					System.out.println("disconnected");
					mDisconnected = true;
				}
				
				try
				{
					Thread.sleep(100);
				}
				catch(Exception e)
				{
					
				}
			}
		}
	}
	
	class MessageSanitizer implements Runnable
	{
		public void run()
		{			
			while (!mDisconnected)
			{				
				ArrayList<NetworkMessage> messages = GetMessages(mUnsanitizedMessages);
				
				for (int i = 0; i < messages.size(); i++)
				{
					NetworkMessage current = messages.get(i);
					
					current.Sanatize();
					
					String message = current.GetMessage();
					
					StringTokenizer st = new StringTokenizer(message);
					
					try
					{
						int id = Integer.parseInt(st.nextToken());
						
						String command = st.nextToken();
						
						if (command.equalsIgnoreCase("ping"))
						{
							mID = id;
							Send("ping");
						}
						else
						{
							throw new Exception();
						}
					}
					catch(Exception e)
					{
						AddMessage(
								message,
								current.GetAddress(),
								current.GetPort(),
								mMessages);
					}
				}
				
				try
				{
					Thread.sleep(10);
				}
				catch(Exception e){}
			}
		}
	}
}
