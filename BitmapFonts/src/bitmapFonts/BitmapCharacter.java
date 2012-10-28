package bitmapFonts;

import java.awt.image.BufferedImage;
import textures.*;

public class BitmapCharacter
{
	private Texture mTexture;
	
	private int mID;
	private int mXOffset;
	private int mYOffset;
	private int mXAdvance;
	private int mWidth;
	
	public BitmapCharacter(int ID, BufferedImage tex, int xOffset, int yOffset, int xAdvance)
	{
		mID = ID;
		mWidth = tex.getWidth();
		mTexture = new Texture(tex);
		mXOffset = xOffset;
		mYOffset = yOffset;
		mXAdvance = xAdvance;
	}
	
	public int GetID()
	{
		return mID;
	}
	
	public int GetXAdvance()
	{
		return mXAdvance;
	}
	
	public int GetWidth()
	{
		return mWidth;
	}
	
	public void Draw(float x, float y, float sx, float sy)
	{
		mTexture.Draw(x + mXOffset * sx, y + mYOffset * sy, sx, sy);
	}
}