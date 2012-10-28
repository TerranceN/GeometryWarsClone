package bitmapFonts;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

public class BitmapFont
{
	ArrayList<BitmapCharacter> characters;
	Kerning[] kernings;
	
	int mNumKernings = 0;
	int mLineHeight = 0;
	int mMaxHeight = 0;
	
	public BitmapFont(String bitmapFile, String fontFile)
	{
		characters = new ArrayList<BitmapCharacter>();
		
		try
		{
			File f = new File(bitmapFile);
			BufferedImage image = ImageIO.read(f);
			
			BufferedInputStream fin = new BufferedInputStream((new FileInputStream(fontFile)));
			
			String str = "";
			while (!(str = ReadOneLine(fin)).equals(""))
			{
				String command = str.substring(0, str.length() - 1);
				HandleCommand(command, image);
			}
			
			fin.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private BitmapCharacter GetCharacter(int id)
	{
		for (int i = 0; i < characters.size(); i++)
		{
			if (id == characters.get(i).GetID())
			{
				return characters.get(i);
			}
		}
		
		return null;
	}
	
	public int GetKerning(int index1, int index2)
	{
		if (kernings != null)
		{
			for (int i = 0; i < kernings.length; i++)
			{
				if (kernings[i].IsCorrectKerning(index1, index2))
				{
					return kernings[i].Spacing();
				}
			}
		}
		
		return 0;
	}
	
	public int GetCharAdvance(int index)
	{
		BitmapCharacter character = GetCharacter(index);
		
		if (character != null)
		{
			return character.GetXAdvance();
		}
		else
		{
			return 0;
		}
	}
	
	public int GetWidth(int index)
	{
		BitmapCharacter character = GetCharacter(index);
		
		if (character != null)
		{
			return character.GetWidth();
		}
		else
		{
			return 0;
		}
	}
	
	public int GetLineHeight()
	{
		return mLineHeight;
	}
	
	public int GetMaxHeight()
	{
		return mMaxHeight;
	}
	
	private String ReadOneLine(BufferedInputStream in)
	{
		String str = "";
		
		try
		{
			byte[] buffer = new byte[1];
			int bytesRead = 0;
			
			
			while ((bytesRead = in.read(buffer)) != -1)
			{
				String temp = new String(buffer, 0, bytesRead);
				if (temp.equals("\n"))
				{
					break;
				}
				str += temp;
			}
		}
		catch(Exception e)
		{
			System.out.print("Fail * 2");
		}
		
		return str;
	}
	
	private void HandleCommand(String line, BufferedImage image)
	{
		try
		{
			StringTokenizer commands = new StringTokenizer(line);
			
			String command = commands.nextToken();
			
			if (command.equals("common"))
			{
				mLineHeight = Integer.parseInt(commands.nextToken().substring(11));
			}
			else if (command.equals("chars"))
			{
				//int numCharacters = Integer.parseInt(commands.nextToken().substring(6));
				//characters = new BitmapCharacter[numCharacters];
			}
			else if (command.equals("char"))
			{
				int id = Integer.parseInt(commands.nextToken().substring(3));
				
				int x = Integer.parseInt(commands.nextToken().substring(2));
				int y = Integer.parseInt(commands.nextToken().substring(2));
				int width = Integer.parseInt(commands.nextToken().substring(6));
				int height = Integer.parseInt(commands.nextToken().substring(7));
				int xOffset = Integer.parseInt(commands.nextToken().substring(8));
				int yOffset = Integer.parseInt(commands.nextToken().substring(8));
				int xAdvance = Integer.parseInt(commands.nextToken().substring(9));
				
				if (height > mMaxHeight)
				{
					mMaxHeight = height;
				}
				
				if (width <= 0)
				{
					width = 1;
				}
				
				if (height <= 0)
				{
					height = 1;
				}
				
				characters.add(new BitmapCharacter(id, image.getSubimage(x, y, width, height), xOffset, yOffset, xAdvance));
			}
			else if (command.equals("kernings"))
			{
				int numKernings = Integer.parseInt(commands.nextToken().substring(6));
				
				kernings = new Kerning[numKernings];
			}
			else if (command.equals("kerning"))
			{
				kernings[mNumKernings] = new Kerning(
						Integer.parseInt(commands.nextToken().substring(6)),
						Integer.parseInt(commands.nextToken().substring(7)),
						Integer.parseInt(commands.nextToken().substring(7)));
				
				mNumKernings++;
			}
		}
		catch(Exception e)
		{
			System.out.println("Font command failed!");
			System.out.println(line);
			e.printStackTrace();
		}
	}
	
	public void Draw(float x, float y, int index)
	{
		Draw(x, y, 1, 1, index);
	}
	
	public void Draw(float x, float y, float sx, float sy, int index)
	{
		BitmapCharacter character = GetCharacter(index);
		
		if (character != null)
		{
			character.Draw(x, y, sx, sy);
		}
	}
}
