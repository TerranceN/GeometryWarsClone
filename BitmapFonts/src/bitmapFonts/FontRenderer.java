package bitmapFonts;

public class FontRenderer
{
	public enum Justify
	{
		RIGHT,
		LEFT,
		TOP,
		BOTTOM,
		CENTRE
	}
	
	public static float GetStringWidth(String s, BitmapFont bf, float sx)
	{
		char last = 0;
		float sizeX = 0;
		
		for (int i = 0; i < s.length(); i++)
		{
			char current = s.charAt(i);
			
			if (last != 0)
			{				
				sizeX += bf.GetKerning((int)current, (int)last) * sx;
			}
			
			sizeX += bf.GetCharAdvance((int)current) * sx;
			
			last = current;
		}
		
		return sizeX;
	}
	
	public static void Draw(BitmapFont bf, String text, float x, float y)
	{
		Draw(bf, text, x, y, 1, 1, Justify.LEFT, Justify.TOP);
	}
	
	public static void Draw(BitmapFont bf, String text, float x, float y, float sx, float sy)
	{
		Draw(bf, text, x, y, sx, sy, Justify.LEFT, Justify.TOP);
	}
	
	public static void Draw(BitmapFont bf, String text, float x, float y, Justify horizontalJustification, Justify verticalJustification)
	{
		Draw(bf, text, x, y, 1, 1, horizontalJustification, verticalJustification);
	}
	
	public static void Draw(BitmapFont bf, String text, float x, float y, float sx, float sy, Justify horizontalJustification, Justify verticalJustification)
	{
		float maxX = x;
		
		float yOffset = 0;
		
		if (verticalJustification == Justify.BOTTOM)
		{
			yOffset = bf.GetLineHeight() * sy;
		}
		else if (verticalJustification == Justify.TOP)
		{
			yOffset = 0;
		}
		else if (verticalJustification == Justify.CENTRE)
		{
			yOffset = bf.GetLineHeight() / 2 * sy;
		}
		else
		{
			return;
		}
		
		if (horizontalJustification == Justify.RIGHT)
		{
			char last = 0;
			
			for (int i = text.length() - 1; i >= 0; i--)
			{
				char current = text.charAt(i);
				
				/*if (i == text.length())
				{
					maxX -= bf.GetWidth((int)current);
				}
				else
				{
					maxX -= bf.GetCharAdvance((int)current);
				}*/
				
				bf.Draw(maxX - bf.GetWidth((int)current), y - yOffset, sx, sy, (int)current);
				
				maxX -= bf.GetCharAdvance((int)current) * sx;
				
				if (last != 0)
				{				
					maxX -= bf.GetKerning((int)current, (int)last) * sx;
				}
				
				last = current;
			}
		}
		else if (horizontalJustification == Justify.LEFT)
		{
			char last = 0;
			
			for (int i = 0; i < text.length(); i++)
			{
				char current = text.charAt(i);
				
				if (last != 0)
				{				
					maxX += bf.GetKerning((int)current, (int)last) * sx;
				}
				
				bf.Draw(maxX, y - yOffset, sx, sy, (int)current);
				
				maxX += bf.GetCharAdvance((int)current) * sx;
				
				last = current;
			}
		}
		else if (horizontalJustification == Justify.CENTRE)
		{			
			char last = 0;
			maxX -= GetStringWidth(text, bf, sx) / 2;
			
			for (int i = 0; i < text.length(); i++)
			{
				char current = text.charAt(i);
				
				if (last != 0)
				{				
					maxX += bf.GetKerning((int)current, (int)last) * sx;
				}
				
				bf.Draw(maxX, y - yOffset, sx, sy, (int)current);
				
				maxX += bf.GetCharAdvance((int)current) * sx;
				
				last = current;
			}
		}
	}
}
