package menus;

public class IntegerMenuItem extends TextMenuItem
{
	public IntegerMenuItem(String newMessage, String defaultValue)
	{
		super(newMessage, defaultValue);
		
		mValue = RemoveNonNumbers(mValue);
	}

	protected void SetStringToDraw()
	{
		if (mIsBeingEdited)
		{
			mStringToDraw = RemoveNonNumbers(mInput.GetInputString()) + "|";
		}
		else
		{
			mStringToDraw = RemoveNonNumbers(mValue);
		}
	}
	
	protected String RemoveNonNumbers(String input)
	{
		String temp = "";
		
		for (int i = 0; i < input.length(); i++)
		{
			int c = (int)input.charAt(i);
			
			if ((c >= 48 && c <= 57)
					|| c == 10)
			{
				temp += (char)c;
			}
		}
		
		return temp;
	}
	
	public int GetIntValue()
	{
		return Integer.parseInt(mValue);
	}
}
