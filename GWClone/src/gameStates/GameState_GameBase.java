package gameStates;

import static org.lwjgl.opengl.GL11.*;
import input.InputHandler;

import java.awt.Color;
import java.util.*;

import org.lwjgl.input.Keyboard;

import util.*;
import bitmapFonts.*;
import entities.*;
import game.*;
import grid.Grid;

public class GameState_GameBase extends GameState
{
	protected int mKillingSpree = 0;
	
	protected InputHandler mInput = GameProperties.GetInputHandler();
	
	protected Vector2 mCameraOffset = new Vector2();
	protected Vector2 mMousePosition = new Vector2();
	protected Vector2 mCameraFocus = new Vector2();
	
	protected long mEnemySpawnTimer = 0;
	protected int mEnemySpawnDelay = 400;
	protected int mEcameraStopOffset = 50;
	
	protected LinkedList<CollisionLine> mBorder = new LinkedList<CollisionLine>();
	protected LinkedList<GameEntity> mEnemyList = new LinkedList<GameEntity>();
	protected LinkedList<GameEntity> mBulletList = new LinkedList<GameEntity>();
	protected LinkedList<GameEntity> mSparkList = new LinkedList<GameEntity>();
	protected LinkedList<GameEntity> mLastingGridPushes = new LinkedList<GameEntity>();
	
	protected Grid mGrid;
	
	/**
	 * Constructor.
	 * Initializes the GameState using the Load method.
	 */
	public GameState_GameBase()
	{
		Load();
	}
	
	/**
	 * Initializes the GameState.
	 */
	public void Load()
	{
		mInput.Clear();
		
		mCameraFocus = new Vector2(GameProperties.SizeX(), GameProperties.SizeY()).DividedBy(2);
		
		// move camera to player
		MoveCamera(1);
		
		// define borders for the game world
		mBorder.add(new CollisionLine(new Vector2(0), new Vector2(GameProperties.SizeX(), 0)));
		mBorder.add(new CollisionLine(new Vector2(GameProperties.SizeX(), GameProperties.SizeY()), new Vector2(GameProperties.SizeX(), 0)));
		mBorder.add(new CollisionLine(new Vector2(GameProperties.SizeX(), GameProperties.SizeY()), new Vector2(0, GameProperties.SizeY())));
		mBorder.add(new CollisionLine(new Vector2(0), new Vector2(0, GameProperties.SizeY())));
		
		// set the properties of the grid
		mGrid = new Grid(
				GameProperties.SizeX() / (Integer)GameProperties.GetVariable("grid_section_size_x").GetData(),
				GameProperties.SizeY() / (Integer)GameProperties.GetVariable("grid_section_size_y").GetData());
		
		// initialize entity classes
		Player.Init(mGrid, mBulletList, mSparkList);
		Bullet.Init(mGrid, mSparkList, mLastingGridPushes);
		Enemy.Init(mSparkList);
	}
	
	public void UnLoad()
	{
		DestroyAllEnemies();
		DestroyAllBullets();
		
		super.UnLoad();
	}
	
	/**
	 * Moves the camera towards the player, 1 / divisions the distance
	 * of the middle of the screen to the player.
	 * @param divisions What fraction (1 / divisions) of the distance should be used.
	 */
	protected void MoveCamera(float divisions)
	{
		// find middle of screen in game coordinates
		Vector2 middle = new Vector2(GameProperties.WindowWidth(), GameProperties.WindowHeight()).DividedBy(2 * GameProperties.Scale());

		// move the camera offset 1 / divisions toward the middle of the screen
		mCameraOffset.PlusEquals(middle.Minus(mCameraOffset).Minus(mCameraFocus).DividedBy(divisions));

		// make sure the camera is within the bounds of the game
		if (mCameraOffset.X < -GameProperties.SizeX() + GameProperties.WindowWidth() / GameProperties.Scale() - mEcameraStopOffset)
		{
			mCameraOffset.X = -GameProperties.SizeX() + GameProperties.WindowWidth() / GameProperties.Scale() - mEcameraStopOffset;
		}
		if (mCameraOffset.X > mEcameraStopOffset)
		{
			mCameraOffset.X = mEcameraStopOffset;
		}
		if (mCameraOffset.Y < -GameProperties.SizeY() + GameProperties.WindowHeight() / GameProperties.Scale() - mEcameraStopOffset)
		{
			mCameraOffset.Y = -GameProperties.SizeY() + GameProperties.WindowHeight() / GameProperties.Scale() - mEcameraStopOffset;
		}
		if (mCameraOffset.Y > mEcameraStopOffset)
		{
			mCameraOffset.Y = mEcameraStopOffset;
		}
	}
	
	/**
	 * Checks collision between all bullets and enemies,
	 * and lets the Enemy class handle the collision.
	 */
	protected void HandleBulletCollisionWithEnemies()
	{
		for (int i = 0; i < mEnemyList.size(); i++)
		{
			Enemy currentEnemy = (Enemy)mEnemyList.get(i);
			for (int j = 0; j < mBulletList.size(); j++)
			{
				Bullet currentBullet = (Bullet)mBulletList.get(j);
				
				int scoreToAdd = currentEnemy.HandleCollisionWithBullet(currentBullet);
				
				if (scoreToAdd > 0)
				{
					mKillingSpree++;
					
					if (mKillingSpree % 100 == 0)
					{
						mEnemyList.add(new ChainEnemy(GetRandomSpawnPosition()));
					}
					
					currentBullet.GetOwner().IncrementScore(scoreToAdd);
				}
			}
		}
	}
	
	/**
	 * Checks collision between all bullets and walls,
	 * and destroys any bullets that hit.
	 */
	protected void HandleBulletCollisionWithWalls()
	{
		for (int i = 0; i < mBulletList.size(); i++)
		{
			Bullet current = (Bullet)mBulletList.get(i);
			
			for (int j = 0; j < mBorder.size(); j++)
			{
				Vector2 intersection = mBorder.get(j).GetIntersectionWithLine(current.MovementLine());
				
				if (intersection != null)
				{
					current.SetPosition(intersection);
					current.Destroy();
				}
			}
		}
	}
	
	protected Vector2 GetRandomSpawnPosition()
	{
		Vector2 spawnPosition = new Vector2();
		
		// get a random number from 0-3
		int rand = (int)(Math.random() * 4);
		
		// if 0 or 1
		if (rand <= 1)
		{
			// left or right side
			spawnPosition.X = GameProperties.SizeX() * rand;
			
			// randomly along that the side
			spawnPosition.Y = GameProperties.SizeY() * (float)Math.random();
		}
		// must be 2 or 3
		else
		{
			// randomly along the top or bottom
			spawnPosition.X = GameProperties.SizeX() * (float)Math.random();
			
			// top or bottom
			spawnPosition.Y = GameProperties.SizeY() * (rand - 2);
		}
		
		return spawnPosition;
	}
	
	protected Color GetRandomColor()
	{
		Color c = Color.WHITE;
		// get a random number from 0-3
		int rand = (int)(Math.random() * 4);
		
		// select random colour
		switch(rand)
		{
			case 0:
			{
				c = Color.GREEN;
			}
			break;
			case 1:
			{
				c = Color.CYAN;
			}
			break;
			case 2:
			{
				c = Color.RED;
			}
			break;
			case 3:
			{
				c = Color.MAGENTA;
			}
			break;
		}
		
		return c;
	}
	
	/**
	 * Spawns enemies randomly after mEnemySpawnDelay
	 * time has passed.
	 */
	protected void SpawnEnemies()
	{
		// wait until enough time has passed
		if (System.currentTimeMillis() > mEnemySpawnTimer
			&& mEnemyList.size() < 50)
		{
			Vector2 spawnPosition = GetRandomSpawnPosition();
			Color c = GetRandomColor();
			
			// add new enemy with calculated values
			mEnemyList.add(new BasicEnemy(spawnPosition, new GLColor(c)));
			
			// set enemy timer to wait another mEnemySpawnDelay
			mEnemySpawnTimer = System.currentTimeMillis() + mEnemySpawnDelay;
		}
	}
	
	/**
	 * Lets enemies handle collisions between themselves.
	 */
	protected void HandleEnemyCollisionWithEnemies()
	{
		for (int i = 0; i < mEnemyList.size(); i++)
		{
			Enemy enemy1 = (Enemy)mEnemyList.get(i);
			
			for (int j = i; j < mEnemyList.size(); j++)
			{
				Enemy enemy2= (Enemy)mEnemyList.get(j);
				
				if (i != j)
				{
					enemy1.HandleCollisionWithEnemy(enemy2);
				}
			}
		}
	}
	
	protected void HandlePlayerCollisionWithEnemies(Player p)
	{
		for (int i = 0; i < mEnemyList.size(); i++)
		{
			Enemy current = (Enemy)mEnemyList.get(i);
			
			if (p.HasCollidedWithEnemy(current))
			{
				DestroyAllEnemies();
			}
		}
	}
	
	protected void DestroyAllEnemies()
	{
		for (int i = 0; i < mEnemyList.size(); i++)
		{
			mEnemyList.get(i).Destroy();
		}
	}
	
	protected void DestroyAllBullets()
	{
		for (int i = 0; i < mBulletList.size(); i++)
		{
			mBulletList.get(i).Destroy();
		}
	}
	
	public void Update(GameTime gameTime)
	{
		
	}
	
	/**
	 * Calls the Draw method on each GameEntity in a list.
	 * @param list The List of GameEntities.
	 */
	protected void DrawEntityList(ArrayList<GameEntity> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			GameEntity current = list.get(i);
			
			current.Draw();
		}
	}
	
	/**
	 * Clears horizontal areas past game boundaries.
	 * @param topLeft Where to start drawing clear area.
	 */
	protected void DrawHorizontalBorderClear(Vector2 topLeft)
	{
		glBegin(GL_QUADS);
		{
			glVertex2f(topLeft.X, topLeft.Y);
			glVertex2f(topLeft.X + GameProperties.SizeX() + 200, topLeft.Y);
			glVertex2f(topLeft.X + GameProperties.SizeX() + 200, topLeft.Y + 100);
			glVertex2f(topLeft.X, topLeft.Y + 100);
		}
		glEnd();
	}
	
	/**
	 * Clears vertical areas past game boundaries.
	 * @param topLeft Where to start drawing clear area.
	 */
	protected void DrawVerticalBorderClear(Vector2 topLeft)
	{
		glBegin(GL_QUADS);
		{
			glVertex2f(topLeft.X, topLeft.Y);
			glVertex2f(topLeft.X + 100, topLeft.Y);
			glVertex2f(topLeft.X + 100, topLeft.Y + GameProperties.SizeY() + 200);
			glVertex2f(topLeft.X, topLeft.Y + GameProperties.SizeY() + 200);
		}
		glEnd();
	}
	
	/**
	 * Draws the border of the game world.
	 */
	protected void DrawBorder()
	{
		// clears areas outside game area
		glColor3f(0, 0, 0);
		DrawHorizontalBorderClear(new Vector2(-100));
		DrawHorizontalBorderClear(new Vector2(-100, GameProperties.SizeY()));
		DrawVerticalBorderClear(new Vector2(-100));
		DrawVerticalBorderClear(new Vector2(GameProperties.SizeX(), -100));
		
		// draws border
		glColor3f(1, 1, 1);
		for (int i = 0; i < mBorder.size(); i++)
		{
			mBorder.get(i).Draw();
		}
	}
	
	public void DrawGameBase()
	{
		// apply transformations
		glScalef(GameProperties.Scale(), GameProperties.Scale(), 1);
		glTranslatef(mCameraOffset.X, mCameraOffset.Y, 0);
		
		// draw grid
		mGrid.Draw();
		
		// draw mouse position
		glBegin(GL_QUADS);
		{
			float mSize = 10;
			glVertex2f(mMousePosition.X - mSize, mMousePosition.Y - mSize);
			glVertex2f(mMousePosition.X + mSize, mMousePosition.Y - mSize);
			glVertex2f(mMousePosition.X + mSize, mMousePosition.Y + mSize);
			glVertex2f(mMousePosition.X - mSize, mMousePosition.Y + mSize);
		}
		glEnd();
		
		// draw entities
		GameEntity.DrawList(mEnemyList);
		GameEntity.DrawList(mBulletList);
		GameEntity.DrawList(mSparkList);
		
		// draw border
		DrawBorder();
	}
	
	public void Draw()
	{
		DrawGameBase();
	}
	
	protected String GetScoreString(int score)
	{
		String scoreString = "";
		String temp2 = "";
		String temp = Integer.toString(score);
		
		for (int i = 0; i < temp.length(); i++)
		{
			int invI = temp.length() - (i + 1);
			
			if (i >= 3 && i % 3 == 0)
			{
				temp2 += ",";
			}
			
			temp2 += temp.substring(invI, invI + 1);
		}
		
		for (int i = temp2.length() - 1; i >= 0; i--)
		{
			scoreString += temp2.substring(i, i + 1);
		}
		
		return scoreString;
	}
}
