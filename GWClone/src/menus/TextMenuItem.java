package menus;

import org.lwjgl.input.Keyboard;

import util.Vector2;
import bitmapFonts.BitmapFont;
import bitmapFonts.FontRenderer;

public class TextMenuItem extends MenuItem
{
	protected String mValue;
	protected String mStringToDraw;
	
	public void OnSelection()
	{
		mIsBeingEdited = true;
		mInput.BeginRecordingKeys();
		mInput.SetInputString(mValue);
	}
	
	protected void HandleStoppingEditing()
	{
		mIsBeingEdited = false;
		mValue = mInput.TakeInputString();
		mInput.EndRecordingKeys();
	}
	
	public void Update()
	{
		if (mIsBeingEdited)
		{
			if (mInput.IsKeyDown(Keyboard.KEY_RETURN)
					|| mInput.IsMouseHit(0))
			{
				HandleStoppingEditing();
			}
		}
		
		super.Update();
	}
	
	public TextMenuItem(String newMessage)
	{
		this(newMessage, "");
	}
	
	public float GetWidth(BitmapFont font, float scaleY)
	{
		return FontRenderer.GetStringWidth(
				mMessage + " " + mValue,
				font,
				scaleY);
	}
	
	public TextMenuItem(String newMessage, String defaultValue)
	{
		super(newMessage);
		mValue = defaultValue;
	}
	
	public String GetValue()
	{
		return mValue;
	}
	
	protected void SetStringToDraw()
	{
		if (mIsBeingEdited)
		{
			mStringToDraw = mInput.GetInputString() + "|";
		}
		else
		{
			mStringToDraw = mValue;
		}
	}
	
	public void Draw(
			BitmapFont font,
			Vector2 drawPos,
			Vector2 scale,
			FontRenderer.Justify horizontalJustification,
			FontRenderer.Justify verticalJustification)
	{
		SetStringToDraw();
		
		FontRenderer.Draw(font,
				mMessage + " " + mStringToDraw,
				drawPos.X,
				drawPos.Y,
				scale.X,
				scale.Y,
				horizontalJustification,
				verticalJustification);
	}
}
