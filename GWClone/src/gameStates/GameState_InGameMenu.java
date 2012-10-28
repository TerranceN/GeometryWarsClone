package gameStates;

import input.*;
import game.*;

import menus.*;

import org.lwjgl.input.*;

import util.Vector2;
import bitmapFonts.*;

import static org.lwjgl.opengl.GL11.*;

public class GameState_InGameMenu extends GameState
{	
	private InputHandler mInput = GameProperties.GetInputHandler();
	private Menu mMenu;
	private GameState_GameBase mGame;
	private boolean mIsUnloading = false;
	private float mTransparency = 1f;
	
	public GameState_InGameMenu(GameState_GameBase game)
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
		mMenu.AddMenuItem(new MenuItem("Resume"));
		mMenu.AddMenuItem(new MenuItem("Graphics Options"));
		mMenu.AddMenuItem(new MenuItem("Quit To Main Menu"));
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
						gameTime.UnPause();
						UnLoad();
					}
					break;
					case 1:
					{
						mNextState = new GameState_GraphicsMenu(mGame);
					}
					break;
					case 2:
					{
						gameTime.UnPause();
						mGame.UnLoad();
						UnLoad();
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
				"In-Game Menu",
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