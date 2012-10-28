package game;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.util.*;

import input.*;
import bitmapFonts.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.DisplayMode;

@SuppressWarnings("rawtypes")
public class GameProperties
{
	private static float mScale = 1;
	private static BitmapFont mGameFont;
	private static InputHandler mInput;
	private static ArrayList<GameVariable> mGameVariables = new ArrayList<GameVariable>();
	private static WindowedFullscreenFrame mFrame = null;
	
	public static void Initialize()
	{
		InitOpenGL();
		mGameFont = new BitmapFont("Kootenay.png", "Kootenay.fnt");
		mInput = new InputHandler();
	}
	
	public static GameVariable GetVariable(String name)
	{
		for (int i = 0; i < mGameVariables.size(); i++)
		{
			if (mGameVariables.get(i).GetName().equals(name))
			{
				return mGameVariables.get(i);
			}
		}
		
		return null;
	}
	
	public static void InitVariables()
	{
		mGameVariables.add(new GameVariable<Integer>("screen_width", 800));
		mGameVariables.add(new GameVariable<Integer>("screen_height", 600));
		mGameVariables.add(new GameVariable<Boolean>("fullscreen", false));
		
		mGameVariables.add(new GameVariable<Integer>("sparks_per_bullet", 50));
		mGameVariables.add(new GameVariable<Integer>("sparks_per_enemy", 250));
		
		mGameVariables.add(new GameVariable<Boolean>("is_fps_capped", true));
		mGameVariables.add(new GameVariable<Integer>("fps_cap", 60));
		
		mGameVariables.add(new GameVariable<Integer>("game_width", 4000));
		mGameVariables.add(new GameVariable<Integer>("game_height", 4000));
		
		mGameVariables.add(new GameVariable<Integer>("grid_section_size_x", 40));
		mGameVariables.add(new GameVariable<Integer>("grid_section_size_y", 40));
	}
	
	public static void HandleCommand(String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		
		try
		{
			if (st.hasMoreTokens())
			{
				String s = st.nextToken();
				
				if (s.charAt(0) == '#')
				{
					return;
				}
				
				for (int i = 0; i < mGameVariables.size(); i++)
				{
					if (mGameVariables.get(i).GetName().equals(s))
					{
						mGameVariables.get(i).HandleCommand(st);
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Invalid Command");
		}
	}
	
	public static void LoadGraphicsSettings()
	{
		try
		{
			Scanner s = new Scanner(new File("graphics_settings.txt"));
			
			while(s.hasNextLine())
			{
				HandleCommand(s.nextLine());
			}
		}
		catch(Exception e)
		{
			System.out.println("no settings file loaded, using defaults");
		}
		
		UpdateWindow();
	}
	
	public void SaveGraphicsSettings()
	{
		
	}
	
	public static InputHandler GetInputHandler()
	{
		return mInput;
	}
	
	public static BitmapFont GetFont()
	{
		return mGameFont;
	}
	
	public static void InitOpenGL()
	{
		glDisable(GL_DEPTH_TEST);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static boolean UpdateWindow()
	{
		try
		{
			DisplayMode dm = null;
			DisplayMode[] dms = Display.getAvailableDisplayModes();
			
			int width = (Integer)GetVariable("screen_width").GetData();
			int height = (Integer)GetVariable("screen_height").GetData();
			boolean fullscreen = (Boolean)GetVariable("fullscreen").GetData();
			
			//if (mFrame != null)
			//{
			//	mFrame.dispose();
			//}
			
			//mFrame = new WindowedFullscreenFrame(width, height);
			
			//Display.setParent(mFrame.GetCanvas());
			
			for (int i = 0; i < dms.length; i++)
			{				
				if (dms[i].getWidth() == width
					&& dms[i].getHeight() == height)
				{
					dm = dms[i];
					break;
				}
			}
			
			if (dm == null)
			{
				dm = new DisplayMode(width, height);
				Display.setDisplayMode(dm);
			}
			else
			{
				Display.setDisplayMode(dm);
				Display.setFullscreen(fullscreen);
				Display.setVSyncEnabled(fullscreen);
			}
			
			if (!Display.isCreated())
			{
				Display.create();
			}
			
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, width, height, 0, -1, 1);
			
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glViewport(0, 0, width, height);
			
			mScale = 0.5f * (WindowWidth() / 1600.0f + WindowHeight() / 900.0f) / 2;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean Fullscreen()
	{
		return (Boolean)GetVariable("fullscreen").GetData();
	}
	
	public static void SetFullscreen(boolean newFullscreen)
	{
		HandleCommand("fullscreen " + newFullscreen);
	}
	
	public static void SetWindowSize(int newX, int newY)
	{
		HandleCommand("screen_width " + newX);
		HandleCommand("screen_height " + newY);
	}
	
	public static int WindowWidth()
	{
		return (Integer)GetVariable("screen_width").GetData();
	}
	
	public static int WindowHeight()
	{
		return (Integer)GetVariable("screen_height").GetData();
	}
	
	public static void ToggleFullscreen()
	{
		boolean newFullscreen = !Fullscreen();
		HandleCommand("fullscreen " + newFullscreen);
	}
	
	public static void SetGameSize(int newSizeX, int newSizeY)
	{
		HandleCommand("game_width " + newSizeX);
		HandleCommand("game_height " + newSizeY);
	}
	
	public static void SetScale(float newScale)
	{
		mScale = newScale;
	}
	
	public static int SizeX()
	{
		return (Integer)GetVariable("game_width").GetData();
	}
	
	public static int SizeY()
	{
		return (Integer)GetVariable("game_height").GetData();
	}
	
	public static float Scale()
	{
		return mScale;
	}
}
