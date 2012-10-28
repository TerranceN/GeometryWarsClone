package gameStates;

import java.awt.Color;
import java.util.ArrayList;

import input.*;
import entities.GameEntity;
import entities.LastingGridPush;
import entities.Spark;
import game.*;
import grid.Grid;

import menus.*;

import org.lwjgl.input.*;

import util.GLColor;
import util.Vector2;
import bitmapFonts.*;

import static org.lwjgl.opengl.GL11.*;

public class GameState_MainMenu extends GameState_GameBase
{	
	private InputHandler mInput = GameProperties.GetInputHandler();
	private Menu mMenu;
	private int mExplosionCounter = 0;
	
	public GameState_MainMenu()
	{
		Load();
	}
	
	public void Load()
	{
		super.Load();
		
		mMenu = new Menu(
				GameProperties.GetFont(),
				new Vector2(
						GameProperties.WindowWidth() / 2,
						150),
				new Vector2(0.5f),
				FontRenderer.Justify.CENTRE,
				FontRenderer.Justify.CENTRE);
		mMenu.AddMenuItem(new MenuItem("Singleplayer"));
		//mMenu.AddMenuItem(new MenuItem("Multiplayer Server"));
		//mMenu.AddMenuItem(new MenuItem("Multiplayer Client"));
		mMenu.AddMenuItem(new MenuItem("Graphics Options"));
		mMenu.AddMenuItem(new MenuItem("Quit"));
		
		mCameraFocus = new Vector2(GameProperties.SizeX(), GameProperties.SizeY()).DividedBy(2);
		
		MoveCamera(1);
	}
	
	public void UnLoad()
	{
		super.UnLoad();
	}
	
	private void MakeExposion(Vector2 pos, GLColor c)
	{
		for (int j = 0; j < 30; j++)
		{			
			float angle = (float)Math.random() * 6.28f;
			mSparkList.add(new Spark(pos,
					Vector2.FromAngle(angle).Times(new Vector2(10).Plus(new Vector2((float)Math.random() * 20))),
					1600,
					c));
		}
		
		mLastingGridPushes.add(new LastingGridPush(pos, mGrid, 400000, 200, 416));
	}
	
	private void HandleExplosions()
	{
		if (System.currentTimeMillis() > mExplosionCounter)
		{
			Vector2 scaledWindow = new Vector2(GameProperties.WindowWidth(), GameProperties.WindowHeight()).DividedBy(GameProperties.Scale());
			
			Vector2 spawnPosition = new Vector2(
					-mCameraOffset.X + scaledWindow.X * (float)Math.random(),
					-mCameraOffset.Y + scaledWindow.Y * (float)Math.random());
			
			float r = (float)Math.random();
			float g = (float)Math.random();
			float temp = (r + g) / 2;
			float b = (1-temp) + (float)Math.random() * temp;
			
			MakeExposion(spawnPosition, new GLColor(r, g, b));
		}
	}
	
	public void Update(GameTime gameTime)
	{
		//HandleExplosions();
		
		mMousePosition = new Vector2(mInput.GetMouseX(), GameProperties.WindowHeight() - mInput.GetMouseY()).DividedBy(GameProperties.Scale()).Minus(mCameraOffset);
		
		GameEntity.UpdateList(gameTime, mEnemyList);
		GameEntity.UpdateList(gameTime, mBulletList);
		
		GameEntity.UpdateList(gameTime, mSparkList);
		GameEntity.UpdateList(gameTime, mLastingGridPushes);

		mGrid.Pull(mMousePosition, 5000, 10000);
		
		mGrid.Update(gameTime);
		
		mCameraFocus.SetEqual(new Vector2(GameProperties.SizeX(), GameProperties.SizeY()).DividedBy(2));
		MoveCamera(10);
		
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
						mNextState = new GameState_Game(mGrid, mSparkList, mLastingGridPushes, mCameraOffset);
					}
					break;
					/*case 1:
					{
						mNextState = new GameState_GameServer(mGrid, mSparkList, mLastingGridPushes, mCameraOffset);
					}
					break;
					case 2:
					{
						mNextState = new GameState_GameClient(mGrid, mSparkList, mLastingGridPushes, mCameraOffset);
					}
					break;*/
					case 1:
					{
						mNextState = new GameState_GraphicsMenu(this);
					}
					break;
					case 2:
					{
						UnLoad();
					}
					break;
				}
			}
		}
	}
	
	public void Draw()
	{
		super.Draw();
		
		glLoadIdentity();
		
		glColor3f(1, 1, 1);
		
		FontRenderer.Draw(
				GameProperties.GetFont(),
				"Geometry Wars Clone",
				GameProperties.WindowWidth() / 2,
				75 * GameProperties.Scale() * 2,
				3 * GameProperties.Scale(),
				3 * GameProperties.Scale(),
				FontRenderer.Justify.CENTRE,
				FontRenderer.Justify.CENTRE);
		
		FontRenderer.Draw(
				GameProperties.GetFont(),
				"By Terrance Niechciol",
				GameProperties.WindowWidth() / 2,
				175 * GameProperties.Scale() * 2,
				2 * GameProperties.Scale(),
				2 * GameProperties.Scale(),
				FontRenderer.Justify.CENTRE,
				FontRenderer.Justify.CENTRE);
		
		mMenu.Draw(new Vector2(
					GameProperties.WindowWidth() / 2,
					300 * GameProperties.Scale() * 2),
				2 * GameProperties.Scale());
	}
}
