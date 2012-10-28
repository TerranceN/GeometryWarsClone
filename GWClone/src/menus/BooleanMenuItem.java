package menus;

public class BooleanMenuItem extends TextMenuItem
{
	public BooleanMenuItem(String newMessage, String defaultValue)
	{
		super(newMessage, defaultValue);

		try
		{
			boolean b = Boolean.parseBoolean(mValue);
			
			if (b)
			{
				mValue = "True";
			}
			else
			{
				mValue = "False";
			}
		}
		catch(Exception e)
		{
			mValue = "False";
		}
	}
	
	public void OnSelection()
	{
		if (mValue.equals("False"))
		{
			mValue = "True";
		}
		else
		{
			mValue = "False";
		}
	}
	
	public boolean GetBooleanValue()
	{
		if (mValue.equals("False"))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
