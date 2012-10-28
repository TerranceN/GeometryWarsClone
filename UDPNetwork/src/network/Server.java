package network;

import java.net.*;
import java.util.*;

public class Server implements Runnable
{
	private ArrayList<NetworkMessage> mMessages;
	private ArrayList<NetworkMessage> mUnsanitizedMessages;
	private boolean mServerDisconnected;
	
	private ArrayList<ConnectedClient> mClients;
	
	private int mServerPort;
	
	private int mPlayerID = 0;
	
	private DatagramSocket mServerSocket;
	
	public Server(int port)
	{
		mServerPort = port;
		
		mServerDisconnected = false;
		
		mMessages = new ArrayList<NetworkMessage>();
		mUnsanitizedMessages = new ArrayList<NetworkMessage>();
		mClients = new ArrayList<ConnectedClient>();
	}
	
	private int GetNextID()
	{
		mPlayerID++;
		return mPlayerID;
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
	
	public void Send(String message)
	{
		for (int i = 0; i < mClients.size(); i++)
		{
			ConnectedClient client = mClients.get(i);
			
			client.Send(message);
		}
	}
	
	public boolean IsDisconnected()
	{
		return mServerDisconnected;
	}
	
	public void Disconnect()
	{
		mServerDisconnected = true;
	}
	
	public void run()
	{
		try
		{
			mServerSocket = new DatagramSocket(mServerPort);
			
			new Thread(new MessageSanitizer()).start();
			new Thread(new ClientDisconnector()).start();
			
			byte[] buffer;
			DatagramPacket p;
			
			while (!mServerDisconnected)
			{
				buffer = new byte[256];
				p = new DatagramPacket(buffer, buffer.length);
				
				mServerSocket.receive(p);
				
				AddMessage(
						new String(buffer),
						p.getAddress(),
						p.getPort(),
						mUnsanitizedMessages);
			}
		}
		catch(Exception e)
		{
			mServerDisconnected = true;
		}
		
		mServerSocket.close();
	}
	
	public ConnectedClient GetClient(int id)
	{
		for (int i = 0; i < mClients.size(); i++)
		{
			if (mClients.get(i).GetID() == id)
			{
				return mClients.get(i);
			}
		}
		
		return null;
	}
	
	class ClientDisconnector implements Runnable
	{
		public void run()
		{
			while (!mServerDisconnected)
			{
				for (int i = mClients.size() - 1; i >= 0; i--)
				{
					ConnectedClient current = mClients.get(i);
					
					if (current.IsDisconnected())
					{
						System.out.println("someone disconnected");
						AddMessage(
								current.GetID() + " dc",
								null,
								0,
								mMessages);
						mClients.remove(i);
					}
				}
				
				try
				{
					Thread.sleep(100);
				}
				catch(Exception e){}
			}
		}
	}
	
	class MessageSanitizer implements Runnable
	{
		public void run()
		{
			while (!mServerDisconnected)
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
						
						ConnectedClient client = GetClient(id);
						
						if (client != null)
						{
							client.MessageRecieved();
						}
						
						String command = st.nextToken();
						
						if (command.equalsIgnoreCase("ping"))
						{							
							client.PingRecieved();
						}
						else if (command.equalsIgnoreCase("connect"))
						{
							if (client == null)
							{
								System.out.println("someone connected");
								
								ConnectedClient newClient = new ConnectedClient(
										GetNextID(),
										current.GetAddress(),
										current.GetPort());
								
								mClients.add(newClient);
							}
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
	
	class ConnectedClient
	{
		private InetAddress mAddress;
		private int mPort;
		
		private int mID;
		
		private long mPing = 0;
		private long mPingTimer = 0;
		
		private static final int mTimeout = 2000;
		private long mTimer;
		
		private boolean mDisconnected;
		
		public ConnectedClient(int id, InetAddress address, int port)
		{
			mID = id;
			mAddress = address;
			mPort = port;
			
			mDisconnected = false;
			
			new Thread(new TimeoutTimer()).start();
			new Thread(new PingSender()).start();
		}
		
		public int GetID()
		{
			return mID;
		}
		
		public int GetPing()
		{
			return (int)mPing;
		}
		
		public boolean IsDisconnected()
		{
			return mDisconnected;
		}
		
		public void Ping()
		{
			mPingTimer = System.currentTimeMillis();
			Send("ping");
		}
		
		public void PingRecieved()
		{
			mPing = System.currentTimeMillis() - mPingTimer;
		}
		
		public void MessageRecieved()
		{
			mTimer = System.currentTimeMillis() + mTimeout;
		}
		
		public void Send(String message)
		{
			byte[] buffer = message.getBytes();
			DatagramPacket packet = new DatagramPacket(
					buffer,
					buffer.length,
					mAddress,
					mPort);
			
			try
			{
				mServerSocket.send(packet);
			}
			catch(Exception e)
			{
				System.out.println("Failed to send packet: " + message);
			}
		}
		
		class PingSender implements Runnable
		{
			public void run()
			{
				while (!mServerDisconnected && !mDisconnected)
				{
					Send(GetID() + " ping");
					
					try
					{
						Thread.sleep(250);
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		
		class TimeoutTimer implements Runnable
		{
			public void run()
			{
				MessageRecieved();
				
				while (!mServerDisconnected && !mDisconnected)
				{
					if (System.currentTimeMillis() > mTimer)
					{
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
	}
}
