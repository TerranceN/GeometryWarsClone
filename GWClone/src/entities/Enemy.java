package entities;

import util.*;
import game.*;

import java.util.*;

/**
 * Defines an Enemy for the game.
 * @author Terry
 */
public abstract class Enemy extends GameEntity
{
	protected static LinkedList<GameEntity> mSparkList;
	
	protected float mEnemySpeed = 0.8f;
	
	protected float mAngle = 0.0f;
	protected float mAngleToPlayer = 0.0f;
	protected LinkedList<CollisionLine> mCollisionLines = new LinkedList<CollisionLine>();
	protected Vector2 mRadius = new Vector2(25.0f);
	protected GLColor mColor = new GLColor(1, 1, 1);
	
	protected int mPoints = 100;
	
	protected int mRank = 1;
	
	/**
	 * Constructor.
	 * Creates a new enemy at a specific position with a specific color
	 * @param nPosition The starting position of the Enemy.
	 * @param c The color of the Enemy.
	 */
	public Enemy(Vector2 nPosition, GLColor c)
	{
		super(nPosition, new Vector2(), true);
		
		for (int i = 0; i < 4; i++)
		{
			mCollisionLines.add(new CollisionLine(
					new Vector2(0, 0),
					new Vector2(0, 0)));
		}
		
		mColor.SetEqual(c);
	}
	
	/**
	 * Initializes the Enemy class.
	 * @param newSparkList The list of sparks the game is using. Used to make new sparks.
	 */
	public static void Init(LinkedList<GameEntity> newSparkList)
	{
		mSparkList = newSparkList;
	}
	
	protected void DrawTail()
	{
		Vector2 direction = mVelocity.Times(-1);
		float angle = (float)Math.atan2(direction.Y, direction.X);
		
		// make 3 tail sparks
		for (int j = 0; j < 2; j++)
		{
			mSparkList.add(new Spark(mPosition,
					Vector2.FromAngle(angle + (float)Math.random() * 0.5f - 0.25f).Times(10),
					400,
					mColor));
		}
	}
	
	/**
	 * Makes sparks and sets itself to dead.
	 */
	public void Destroy()
	{
		for (int j = 0; j < (Integer)GameProperties.GetVariable("sparks_per_enemy").GetData(); j++)
		{			
			float angle = (float)Math.random() * 6.28f;
			mSparkList.add(new Spark(mPosition,
					Vector2.FromAngle(angle).Times(new Vector2(10).Plus(new Vector2((float)Math.random() * 20))),
					1600,
					mColor));
		}
		
		super.Destroy();
	}
	
	public int GetRank()
	{
		return mRank;
	}
	
	protected Vector2 CollisionLinesCollide(LinkedList<CollisionLine> list1, LinkedList<CollisionLine> list2)
	{
		ListIterator<CollisionLine> i = list1.listIterator();
		ListIterator<CollisionLine> j = list2.listIterator();
		
		while (i.hasNext())
		{
			CollisionLine current1 = i.next();
			
			while (j.hasNext())
			{
				CollisionLine current2 = j.next();
				
				Vector2 intersection = current1.GetIntersectionWithLine(current2);
				
				if (intersection != null)
				{
					return intersection;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the CollisionLines that make up the Enemy.
	 * @return The CollisionLines that make up the Enemy.
	 */
	public LinkedList<CollisionLine> GetCollisionLines()
	{
		return mCollisionLines;
	}
	
	/**
	 * Moves the enemy towards a position at a default speed.
	 * @param point The player position.
	 */
	public void MoveTowards(Vector2 point)
	{
		MoveTowards(point, mEnemySpeed);
	}
	
	/**
	 * Moves the enemy towards a position at a given speed.
	 * @param point The player position.
	 * @param speed The speed of the enemy.
	 */
	public void MoveTowards(Vector2 point, float speed)
	{
		Vector2 difference = point.Minus(GetPosition());
		mAngleToPlayer = (float)Math.atan2(difference.Y, difference.X);
		mVelocity.PlusEquals(point.Minus(mPosition).GetNormalized().Times(new Vector2(speed)));
	}
	
	/**
	 * Moves the enemy away from a position at a given speed.
	 * @param point The player position.
	 * @param speed The speed of the enemy.
	 */
	public void MoveAwayFrom(Vector2 point, float speed)
	{
		mVelocity.MinusEquals(point.Minus(mPosition).GetNormalized().Times(new Vector2(speed)));
	}
	
	public void OnCollisionWithEnemy(Enemy e, Vector2 collisionPoint)
	{
		MoveAwayFrom(collisionPoint, 0.5f);
	}
	
	/**
	 * Checks for collision with a Enemy, then handles it.
	 * @param e The Enemy to handle collisions for.
	 */
	public void HandleCollisionWithEnemy(Enemy e)
	{
		Vector2 intersection = CollisionLinesCollide(GetCollisionLines(), e.GetCollisionLines());
		
		if (intersection != null)
		{
			OnCollisionWithEnemy(e, intersection);
			e.OnCollisionWithEnemy(this, intersection);
		}
	}
	
	public Vector2 GetBulletCollision(Bullet b)
	{
		for (int i = 0; i < mCollisionLines.size(); i++)
		{
			Vector2 intersection = b.MovementLine().GetIntersectionWithLine(mCollisionLines.get(i));
			
			if (intersection != null)
			{
				return intersection;
			}
		}
		
		return null;
	}
	
	public float GetAngle()
	{
		return mAngle;
	}
	
	public void SetAngle(float newAngle)
	{
		mAngle = newAngle;
	}
	
	public void SetSpeed(float newSpeed)
	{
		mEnemySpeed = newSpeed;
	}
	
	public Vector2 GetRadius()
	{
		return mRadius;
	}
	
	/**
	 * Checks for collision with a Bullet, then handles it.
	 * @param b The Bullet to handle collisions for.
	 */
	public int HandleCollisionWithBullet(Bullet b)
	{
		Vector2 intersection = GetBulletCollision(b);
		
		if (intersection != null)
		{
			boolean bothWereAlive = b.IsAlive() && IsAlive();
			
			b.SetPosition(mPosition);
			b.Destroy();
			
			if (IsAlive())
			{
				Destroy();
			}
			
			if (bothWereAlive)
			{
				return mPoints;
			}
		}
		
		return 0;
	}
	
	/**
	 * Updates the CollisionLines that make up the enemy.
	 */
	public void UpdateCollisionLines()
	{
		
	}
	
	public void Update(GameTime gameTime)
	{
		mPosition.PlusEquals(mVelocity.Times(new Vector2(gameTime.GetSpeedFactor())));
		mVelocity.DividedByEquals(new Vector2(1.1f));
		
		UpdateCollisionLines();
		
		DrawTail();
	}
	
	public void Draw()
	{		
		mColor.SetColor();
		
		for (int i = 0; i < mCollisionLines.size(); i++)
		{
			mCollisionLines.get(i).Draw();
		}
	}
}
