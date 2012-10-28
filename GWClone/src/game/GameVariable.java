package game;

import java.util.*;

public class GameVariable<T>
{
	private String mName;
	private T mData;
	
	public GameVariable(String newName, T initData)
	{
		mName = newName;
		mData = initData;
	}
	
	public String GetName()
	{
		return mName;
	}
	
	public T GetData()
	{
		return mData;
	}
	
	public void SetData(T newData)
	{
		mData = newData;
	}
	
	public void HandleCommand(StringTokenizer st) throws Exception
	{
		if (mData instanceof Integer)
		{
			int temp = Integer.parseInt(st.nextToken());
			mData = (T)new Integer(temp);
		}
		else if (mData instanceof Float)
		{
			float temp = Float.parseFloat(st.nextToken());
			mData = (T)new Float(temp);
		}
		else if (mData instanceof String)
		{
			mData = (T)st.nextToken();
		}
		else if (mData instanceof Boolean)
		{
			boolean temp = Boolean.parseBoolean(st.nextToken());
			mData = (T)new Boolean(temp);
		}
	}
}
