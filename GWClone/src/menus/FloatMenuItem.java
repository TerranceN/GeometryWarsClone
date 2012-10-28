package menus;

import org.lwjgl.input.Keyboard;

import util.Vector2;
import bitmapFonts.BitmapFont;
import bitmapFonts.FontRenderer;

public class FloatMenuItem extends TextMenuItem
{
	public FloatMenuItem(String newMessage, String defaultValue)
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
					|| c == 10
					|| c == 46)
			{
				temp += (char)c;
			}
		}
		
		return temp;
	}
	
	public float GetFloatValue()
	{
		return Float.parseFloat(mValue);
	}
}
