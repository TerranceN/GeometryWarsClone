package gameStates;

import input.*;
import game.*;

import menus.*;

import org.lwjgl.input.*;

import util.Vector2;
import bitmapFonts.*;

import static org.lwjgl.opengl.GL11.*;

public class GameState_GraphicsMenu extends GameState
{	
	private InputHandler mInput = GameProperties.GetInputHandler();
	private Menu mMenu;
	private GameState_GameBase mGame;
	private boolean mIsUnloading = false;
	private float mTransparency = 1f;
	
	public GameState_GraphicsMenu(GameState_GameBase game)
	{
		mGame = game;
		Load();
	}
	
	public void Load()
	{
		mMenu = new Menu(
				GameProperties.GetFont(),
				new Vector2(
						GameProperties.WindowWidth() / 2,
						150),
				new Vector2(0.5f),
				FontRenderer.Justify.CENTRE,
				FontRenderer.Justify.CENTRE);
		mMenu.AddMenuItem(new MenuItem("Back"));
		mMenu.AddMenuItem(new IntegerMenuItem("Screen Width",
				((Integer)GameProperties.GetVariable("screen_width").GetData()).toString()));
		mMenu.AddMenuItem(new IntegerMenuItem("Screen Height",
				((Integer)GameProperties.GetVariable("screen_height").GetData()).toString()));
		mMenu.AddMenuItem(new BooleanMenuItem("Fullscreen",
				((Boolean)GameProperties.GetVariable("fullscreen").GetData()).toString()));
		mMenu.AddMenuItem(new MenuItem("Apply Settings"));
	}
	
	public void UnLoad()
	{
		super.UnLoad();
	}
	
	
	
	public void Update(GameTime gameTime)
	{
		mMenu.Update();
		
		if (!mMenu.IsMenuItemSelected())
		{
			mMenu.SelectMenuItem(new Vector2(mInput.GetMouseX(), GameProperties.WindowHeight() - mInput.GetMouseY()));
			
			if (mInput.IsMouseHit(0))
			{
				switch(mMenu.GetSelected())
				{
					case 0:
					{
						UnLoad();
					}
					break;
					case 1:
					{
						mMenu.MenuItemSelected();
					}
					break;
					case 2:
					{
						mMenu.MenuItemSelected();
					}
					break;
					case 3:
					{
						mMenu.MenuItemSelected();
					}
					break;
					case 4:
					{
						try
						{
							int x = ((IntegerMenuItem)mMenu.GetMenuItem(1)).GetIntValue();
							int y = ((IntegerMenuItem)mMenu.GetMenuItem(2)).GetIntValue();
							Boolean f = ((BooleanMenuItem)mMenu.GetMenuItem(3)).GetBooleanValue();
							
							GameProperties.HandleCommand("screen_width " + x);
							GameProperties.HandleCommand("screen_height " + y);
							GameProperties.HandleCommand("fullscreen " + f);
							
							GameProperties.UpdateWindow();
						}
						catch(Exception e)
						{
							
						}
					}
					break;
				}
			}
		}
	}
	
	public void Draw()
	{		
		mGame.DrawGameBase();
		
		glLoadIdentity();
		
		glColor4f(0, 0, 0, 0.5f);
		glBegin(GL_QUADS);
		{
			glVertex2f(0, 0);
			glVertex2f(GameProperties.WindowWidth(), 0);
			glVertex2f(GameProperties.WindowWidth(), GameProperties.WindowHeight());
			glVertex2f(0, GameProperties.WindowHeight());
		}
		glEnd();
		
		glLoadIdentity();
		
		glColor3f(1, 1, 1);
		
		FontRenderer.Draw(
				GameProperties.GetFont(),
				"Graphics Menu",
				GameProperties.WindowWidth() / 2,
				75 * GameProperties.Scale() * 2,
				4 * GameProperties.Scale(),
				4 * GameProperties.Scale(),
				FontRenderer.Justify.CENTRE,
				FontRenderer.Justify.CENTRE);
		
		mMenu.Draw(new Vector2(
					GameProperties.WindowWidth() / 2,
					200 * GameProperties.Scale() * 2),
				2 * GameProperties.Scale());
	}
}