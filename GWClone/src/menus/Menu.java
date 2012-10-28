package menus;

import java.util.ArrayList;

import util.Vector2;

import input.*;
import game.*;
import bitmapFonts.*;

import static org.lwjgl.opengl.GL11.*;

public class Menu
{
	private static final float SPACING = 0.9f;
	
	private InputHandler mInput;
	private BitmapFont mFont;
	private ArrayList<MenuItem> mMenuItems = new ArrayList<MenuItem>();
	private int mSelected = -1;
	private Vector2 mAnchor;
	private Vector2 mScale;
	private FontRenderer.Justify mHori;
	private FontRenderer.Justify mVert;
	
	
	public Menu(BitmapFont font,
			Vector2 anchor,
			Vector2 scale,
			FontRenderer.Justify horizontalJustification,
			FontRenderer.Justify verticalJustification)
	{
		mFont = font;
		mInput = GameProperties.GetInputHandler();
		mAnchor = anchor;
		mScale = scale;
		mHori = horizontalJustification;
		mVert = verticalJustification;
	}
	
	public void SelectMenuItem(Vector2 point)
	{	
		for (int i = 0; i < mMenuItems.size(); i++)
		{
			MenuItem current = mMenuItems.get(i);
			
			float minX = 0;
			float minY = 0;
			float width = current.GetWidth(mFont, mScale.Y);
			float height = mFont.GetLineHeight() * mScale.Y * SPACING;
			
			if (mHori == FontRenderer.Justify.CENTRE)
			{
				minX = mAnchor.X - width / 2;
			}
			
			if (mVert == FontRenderer.Justify.CENTRE)
			{
				minY = mAnchor.Y + i * height - height / 2;
			}
			
			if (point.X > minX && point.X < minX + width
				&& point.Y > minY && point.Y < minY + height)
			{
				mSelected = i;
				return;
			}
			else
			{
				/*System.out.println(i);
				System.out.println(minX + ".." + (minX + width));
				System.out.println(minY + ".." + (minY + height));
				System.out.println();*/
			}
		}
		
		mSelected = -1;
	}
	
	public void AddMenuItem(MenuItem m)
	{
		mMenuItems.add(m);
	}
	
	public boolean IsMenuItemSelected()
	{
		if (mSelected >= 0 && mSelected < mMenuItems.size())
		{
			return mMenuItems.get(mSelected).IsBeingEdited();
		}
		
		return false;
	}
	
	public MenuItem GetMenuItem(int index)
	{
		return mMenuItems.get(index);
	}
	
	public void Update()
	{
		for (int i = 0; i < mMenuItems.size(); i++)
		{
			MenuItem current = mMenuItems.get(i);
			
			if (i == mSelected)
			{
				current.IncreaseTransition(0.2f);
			}
			else
			{
				current.DivideTransition(1.2f);
			}
			
			current.Update();
		}
	}
	
	public void MenuItemSelected()
	{
		mMenuItems.get(mSelected).OnSelection();
	}
	
	public int GetSelected()
	{
		return mSelected;
	}
	
	public void Draw(Vector2 newAnchor, float scale)
	{
		mAnchor = newAnchor;
		mScale = new Vector2(scale);
		Draw();
	}
	
	public void Draw()
	{
		for (int i = 0; i < mMenuItems.size(); i++)
		{
			MenuItem current = mMenuItems.get(i);
			
			float scaleMultiplier = 1 + 0.1f * current.GetTransitionPercent();
			
			glColor3f(1, 1, 1 - current.GetTransitionPercent());
			
			mMenuItems.get(i).Draw(
					mFont,
					mAnchor.Plus(new Vector2(0, i * mFont.GetLineHeight() * mScale.Y * SPACING)),
					mScale.Times(scaleMultiplier),
					mHori,
					mVert);
		}
	}
}
