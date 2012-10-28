package gameStates;

import entities.*;
import game.*;
import grid.*;
import util.*;
import input.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.util.*;
import org.lwjgl.input.*;

import bitmapFonts.FontRenderer;

/**
 * The main game.
 * @author Terry
 */
public class GameState_Game extends GameState_GameBase
{
	private Player mPlayer;
	
	/**
	 * Constructor.
	 * Initializes the GameState using the Load method.
	 */
	public GameState_Game(Grid newGrid, LinkedList<GameEntity> newSparks, LinkedList<GameEntity> newGridPushes, Vector2 newOffset)
	{
		Load(newGrid, newSparks, newGridPushes, newOffset);
	}
	
	/**
	 * Initializes the GameState.
	 */
	public void Load(Grid newGrid, LinkedList<GameEntity> newSparks, LinkedList<GameEntity> newGridPushes, Vector2 newOffset)
	{		
		mInput.Clear();
		
		// move camera to player
		MoveCamera(1);
		
		// define borders for the game world
		mBorder.add(new CollisionLine(new Vector2(0), new Vector2(GameProperties.SizeX(), 0)));
		mBorder.add(new CollisionLine(new Vector2(GameProperties.SizeX(), GameProperties.SizeY()), new Vector2(GameProperties.SizeX(), 0)));
		mBorder.add(new CollisionLine(new Vector2(GameProperties.SizeX(), GameProperties.SizeY()), new Vector2(0, GameProperties.SizeY())));
		mBorder.add(new CollisionLine(new Vector2(0), new Vector2(0, GameProperties.SizeY())));
		
		// set the properties of the grid
		mGrid = newGrid;
		mSparkList = newSparks;
		mLastingGridPushes = newGridPushes;
		mCameraOffset = newOffset;
		
		// initialize entity classes
		Player.Init(mGrid, mBulletList, mSparkList);
		Bullet.Init(mGrid, mSparkList, mLastingGridPushes);
		Enemy.Init(mSparkList);
		
		mPlayer = new Player(mCameraFocus, GameProperties.GetInputHandler());
		
		mEnemyList.add(new ChainEnemy(GetRandomSpawnPosition()));
	}
	
	public void UnLoad()
	{		
		super.UnLoad();
	}
	
	
	
	public void Update(GameTime gameTime)
	{
		// go back to main menu if escape is pressed
		if (mInput.IsKeyHit(Keyboard.KEY_ESCAPE))
		{
			gameTime.Pause();
			mNextState = new GameState_InGameMenu(this);
		}
		
		// calculate the mouse position in game coordinates
		mMousePosition = new Vector2(mInput.GetMouseX(), GameProperties.WindowHeight() - mInput.GetMouseY()).DividedBy(GameProperties.Scale()).Minus(mCameraOffset);
		
		mPlayer.SetMousePosition(mMousePosition);
		mPlayer.Update(gameTime);
		
		// move enemies towards player
		for (int i = 0; i < mEnemyList.size(); i++)
		{
			((Enemy)mEnemyList.get(i)).MoveTowards(mPlayer.GetPosition());
		}
		
		GameEntity.UpdateList(gameTime, mEnemyList);
		GameEntity.UpdateList(gameTime, mBulletList);
		
		HandleBulletCollisionWithEnemies();
		HandleBulletCollisionWithWalls();
		HandlePlayerCollisionWithEnemies(mPlayer);
		
		GameEntity.UpdateList(gameTime, mSparkList);
		GameEntity.UpdateList(gameTime, mLastingGridPushes);
		
		HandleEnemyCollisionWithEnemies();

		mGrid.Update(gameTime);

		SpawnEnemies();
		
		mCameraFocus.SetEqual(mPlayer.GetPosition());
		MoveCamera(10);
	}
	
	public void DrawGameBase()
	{
		super.DrawGameBase();
		
		mPlayer.Draw();
		
		glLoadIdentity();
		
		glColor3f(1, 1, 1);
		FontRenderer.Draw(
				GameProperties.GetFont(),
				"Score", 
				50 * GameProperties.Scale() * 2,
				50 * GameProperties.Scale() * 2,
				2 * GameProperties.Scale(),
				2 * GameProperties.Scale());
		FontRenderer.Draw(
				GameProperties.GetFont(),
				GetScoreString(mPlayer.GetScore()),
				50 * GameProperties.Scale() * 2,
				110 * GameProperties.Scale() * 2,
				2 * GameProperties.Scale(),
				2 * GameProperties.Scale());
	}
}
