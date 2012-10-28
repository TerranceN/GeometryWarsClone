package menus;

import game.GameProperties;
import input.InputHandler;
import util.Vector2;
import bitmapFonts.*;

public class MenuItem
{
	protected InputHandler mInput;
	
	protected String mMessage = "";
	protected boolean mIsBeingEdited = false;
	protected float mTransitionPercent = 0.0f;
	
	public MenuItem(String newMessage)
	{
		mMessage = newMessage;
		mInput = GameProperties.GetInputHandler();
	}
	
	public float GetTransitionPercent()
	{
		return mTransitionPercent;
	}
	
	public void IncreaseTransition(float amount)
	{
		mTransitionPercent += amount;
		
		if (mTransitionPercent > 1)
		{
			mTransitionPercent = 1;
		}
		else if (mTransitionPercent < 0)
		{
			mTransitionPercent = 0;
		}
	}
	
	public void DivideTransition(float div)
	{
		mTransitionPercent /= div;
	}
	
	public boolean IsBeingEdited()
	{
		return mIsBeingEdited;
	}
	
	public void OnSelection()
	{
		
	}
	
	public void Update()
	{
		
	}
	
	public float GetWidth(BitmapFont font, float scaleY)
	{
		return FontRenderer.GetStringWidth(
				mMessage,
				font,
				scaleY);
	}
	
	public void Draw(
			BitmapFont font,
			Vector2 drawPos,
			Vector2 scale,
			FontRenderer.Justify horizontalJustification,
			FontRenderer.Justify verticalJustification)
	{
		FontRenderer.Draw(font,
				mMessage,
				drawPos.X,
				drawPos.Y,
				scale.X,
				scale.Y,
				horizontalJustification,
				verticalJustification);
	}
}
