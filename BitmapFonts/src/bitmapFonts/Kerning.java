package bitmapFonts;

public class Kerning
{
	int mFirst;
	int mSecond;
	int mSpacing;
	
	public Kerning(int first, int second, int spacing)
	{
		mFirst = first;
		mSecond = second;
		mSpacing = spacing;
	}
	
    public int First()
	{
		return mFirst;
	}
	
	public int Second()
	{
		return mSecond;
	}
	
	public int Spacing()
	{
		return mSpacing;
	}
	
	public boolean IsCorrectKerning(int index1, int index2)
	{	
		return (index1 == First() && index2 == Second())
			|| (index2 == First() && index1 == Second());
	}
}