package entities;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.util.*;

import org.lwjgl.input.Keyboard;

import game.*;
import grid.*;
import util.*;
import input.*;

public class Player extends GameEntity
{
	private static Grid mGrid;
	private static LinkedList<GameEntity> mBulletList = new LinkedList<GameEntity>();
	private static LinkedList<GameEntity> mSparkList = new LinkedList<GameEntity>();
	
	private float mAngle = 0;
	private float mSize = 40;
	private int mScore = 0;
	private LinkedList<CollisionLine> mCollisionLines = new LinkedList<CollisionLine>();
	private InputHandler mInput;
	
	private Vector2 mMousePosition = new Vector2();
	
	private long mBulletTimer = 0;
	private int mBulletDelay = 200;
	
	public Player(Vector2 newPosition, InputHandler newInput)
	{		
		super(newPosition, new Vector2(), true);
		
		mInput = newInput;
		
		mCollisionLines.add(new CollisionLine(new Vector2(), new Vector2()));
		mCollisionLines.add(new CollisionLine(new Vector2(), new Vector2()));
		mCollisionLines.add(new CollisionLine(new Vector2(), new Vector2()));
	}
	
	public void Set(float x, float y, float angle, float vx, float vy)
	{
		mPosition.SetEqual(new Vector2(x, y));
		mAngle = angle;
		mVelocity.SetEqual(new Vector2(vx, vy));
		UpdateCollisionLines();
	}

	public static void Init(Grid newGrid, LinkedList<GameEntity> newBulletList, LinkedList<GameEntity> newSparkList)
	{
		mGrid = newGrid;
		mBulletList = newBulletList;
		mSparkList = newSparkList;
	}
	
	public InputHandler GetInputHandler()
	{
		return mInput;
	}
	
	public void SetMousePosition(Vector2 newPosition)
	{
		mMousePosition.SetEqual(newPosition);
	}
	
	public int GetScore()
	{
		return mScore;
	}
	
	public float GetAngle()
	{
		return mAngle;
	}
	
	public void SetScore(int newScore)
	{
		mScore = newScore;
	}
	
	public void IncrementScore(int amount)
	{
		SetScore(GetScore() + amount);
	}
	
	private void UpdateCollisionLines()
	{
		Vector2 point1 = mPosition.Plus(Vector2.FromAngle(mAngle).Times(mSize));
		Vector2 point2 = mPosition.Plus(Vector2.FromAngle(mAngle + 2.5f).Times(mSize));
		Vector2 point3 = mPosition.Plus(Vector2.FromAngle(mAngle - 2.5f).Times(mSize));
		
		mCollisionLines.get(0).Set(point1, point2);
		mCollisionLines.get(1).Set(point2, point3);
		mCollisionLines.get(2).Set(point3, point1);
	}
	
	public boolean HasCollidedWithEnemy(Enemy enemy)
	{
		LinkedList<CollisionLine> enemyLines = enemy.GetCollisionLines();
		
		ListIterator<CollisionLine> i = mCollisionLines.listIterator();
		ListIterator<CollisionLine> j = enemyLines.listIterator();
		
		while (i.hasNext())
		{
			CollisionLine current1 = i.next();
			
			while (j.hasNext())
			{
				CollisionLine current2 = j.next();
				
				Vector2 intersection = current1.GetIntersectionWithLine(current2);
				
				if (intersection != null)
				{
					mScore = 0;
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void Update(GameTime gameTime)
	{
		// if mouse is down
		if (mInput.IsMouseDown(0) && System.currentTimeMillis() > mBulletTimer)
		{
			// fire bullets
			Vector2 difference = mMousePosition.Minus(mPosition);
			float angle = (float)Math.atan2(difference.Y, difference.X);
			
			for (float i = -1; i <= 1; i++)
			{
				float deviation = (float)Math.PI / 32 * i;
				mBulletList.add(new Bullet(mPosition, Vector2.FromAngle(angle + deviation).Times(30), this));
			}
			
			mBulletTimer = System.currentTimeMillis() + mBulletDelay;
		}
		
		// get WASD input
		Vector2 direction = new Vector2();
		if (mInput.IsKeyDown(Keyboard.KEY_W))
		{
			direction.Y -= 1;
		}
		if (mInput.IsKeyDown(Keyboard.KEY_A))
		{
			direction.X -= 1;
		}
		if (mInput.IsKeyDown(Keyboard.KEY_S))
		{
			direction.Y += 1;
		}
		if (mInput.IsKeyDown(Keyboard.KEY_D))
		{
			direction.X += 1;
		}
		
		// update player position and velocity based one WASD input
		mVelocity.PlusEquals(direction.GetNormalized());
		mPosition.PlusEquals(mVelocity.Times(gameTime.GetSpeedFactor()));
		mVelocity.DividedByEquals(1.1f);
		
		// keep player within boundaries
		if (mPosition.X - mSize < 0)
		{
			mPosition.X = mSize;
		}
		else if(mPosition.X + mSize > GameProperties.SizeX())
		{
			mPosition.X = GameProperties.SizeX() - mSize;
		}
		
		if (mPosition.Y - mSize < 0)
		{
			mPosition.Y = mSize;
		}
		else if(mPosition.Y + mSize > GameProperties.SizeY())
		{
			mPosition.Y = GameProperties.SizeY() - mSize;
		}
		
		// if player is moving
		if (mVelocity.Length() > 0)
		{
			// update angle
			mAngle = (float)Math.atan2(mVelocity.Y, mVelocity.X);
		}
		
		UpdateCollisionLines();
		
		// if WASD is pressed down
		if (direction.Length() > 0)
		{
			// backwards of the direction being pressed
			Vector2 direction2 = mVelocity.Times(-1);
			float angle = (float)Math.atan2(direction2.Y, direction2.X);
			
			// make 5 tail sparks
			for (int j = 0; j < 5; j++)
			{
				GLColor c;
				
				switch ((int)(Math.random() * 3))
				{
					case 0:
					{
						c = new GLColor(Color.YELLOW);
					}
					break;
					case 1:
					{
						c = new GLColor(Color.ORANGE);
					}
					break;
					default:
					{
						c = new GLColor(0.88f, 0.625f, 0.143f);
					}
				}
				
				mSparkList.add(new Spark(mPosition.Minus(mVelocity),
						Vector2.FromAngle(angle + (float)Math.random() * 0.5f - 0.25f).Times(mVelocity.Length() + 2),
						800,
						c));
			}
		}
		PullGrid();
	}
	
	public void PullGrid()
	{
		// pull grid towards player
		mGrid.Pull(mPosition, mVelocity.Length() * 200, 1000);
	}
	
	public void Draw()
	{
		// draw player
		glColor3f(1, 1, 1);
		for (int i = 0; i < mCollisionLines.size(); i++)
		{
			mCollisionLines.get(i).Draw();
		}
	}
}