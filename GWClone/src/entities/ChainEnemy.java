package entities;

import util.*;
import game.*;

import java.util.*;
import java.awt.*;

/**
 * Defines an enemy with growing parts.
 * @author Terry
 */
public class ChainEnemy extends Enemy
{
	private static int timeToDouble = 2000;
	private static int maxPieces = 20;
	
	private float mPlayerTurnSpeed = 0.02f;
	private float mEnemyTurnSpeed = 0.1f;
	private long mDoubleTimer = 0;
	private int mHealingBonus = timeToDouble - 500;
	private ChainEnemyTailPiece mFirstTailPiece = null;
	private int mCurrentTailPieces = 0;
	
	/**
	 * Constructor.
	 * Creates a new ChainEnemy at a specified position.
	 * @param position The starting position of the ChainEnemy.
	 */
	public ChainEnemy(Vector2 position)
	{	
		super(position, new GLColor(Color.MAGENTA));
		mFirstTailPiece = new ChainEnemyTailPiece(position, new GLColor(Color.RED));
		mCurrentTailPieces++;
		mEnemySpeed = 0.4f;
		mRank = 10;
		mDoubleTimer = -1;
	}
	
	public void Destroy()
	{
		ChainEnemyTailPiece iterator = mFirstTailPiece;
		
		while (iterator != null)
		{
			if (iterator.IsAlive())
			{
				iterator.Destroy();
			}
			
			iterator = iterator.GetNext();
		}
		
		isAlive = false;
	}
	
	public void MoveAwayFrom(Vector2 point, float speed)
	{
		Vector2 difference = point.Minus(GetPosition());
		mAngleToPlayer = (float)Math.atan2(-difference.Y, -difference.X);
		
		TurnTowardsPlayer(mEnemyTurnSpeed);
	}
	
	public Vector2 GetPosition()
	{
		return mFirstTailPiece.GetPosition();
	}
	
	public void MoveTowards(Vector2 point, float speed)
	{
		Vector2 difference = point.Minus(GetPosition());
		mAngleToPlayer = (float)Math.atan2(difference.Y, difference.X);
		
		TurnTowardsPlayer(mPlayerTurnSpeed);
	}
	
	private void TurnTowardsPlayer(float speed)
	{		
		float tempAngle = (float)(mAngleToPlayer % (Math.PI * 2));
		float tempAngle2 = (float)(mAngle % (Math.PI * 2));
		
		while (tempAngle > tempAngle2)
		{
			tempAngle -= (float)(Math.PI * 2);
		}
		
		tempAngle = (float)((tempAngle2 - tempAngle) % (Math.PI * 2));
		
		if (tempAngle > Math.PI)
		{
			mAngle += speed;
		}
		else
		{
			mAngle -= speed;
		}
	}
	
	public int HandleCollisionWithBullet(Bullet b)
	{
		ChainEnemyTailPiece iterator = mFirstTailPiece;
		
		while (iterator != null)
		{
			Vector2 intersection = iterator.GetBulletCollision(b);
			
			if (intersection != null)
			{
				int returnPoints = 0;
				
				if (b.IsAlive() && iterator.IsAlive() && iterator.GetNext() == null)
				{
					if (iterator == mFirstTailPiece)
					{
						Destroy();
						returnPoints = 1000;
					}
					else
					{
						iterator.Destroy();
						returnPoints = 50;
					}
				}
				
				b.SetPosition(intersection);
				b.Destroy();
				
				return returnPoints;
			}
			
			iterator = iterator.GetNext();
		}
		
		return 0;
	}
	
	public void OnCollisionWithEnemy(Enemy e, Vector2 collisionPoint)
	{
		if (e.GetRank() < mRank)
		{
			e.Destroy();
		}
		else
		{
			Vector2 intersection = CollisionLinesCollide(mFirstTailPiece.GetCollisionLines(), e.GetCollisionLines());
			
			if (intersection != null)
			{
				MoveAwayFrom(e.GetPosition(), 0.2f);
			}
		}
	}
	
	private void AddTailPiece()
	{
		if (mCurrentTailPieces < maxPieces)
		{
			ChainEnemyTailPiece iterator = mFirstTailPiece;
			
			while (iterator.GetNext() != null)
			{
				iterator = iterator.GetNext();
			}
			
			if (iterator != mFirstTailPiece)
			{
				iterator.SetColor(new GLColor(Color.MAGENTA));
			}
			
			Vector2 newPosition = iterator.GetPosition().Minus(Vector2.FromAngle(iterator.GetAngle()).Times(iterator.GetRadius()).Times(2));
			iterator.SetNext(new ChainEnemyTailPiece(newPosition, new GLColor(Color.YELLOW)));
			mCurrentTailPieces++;
		}
	}
	
	/**
	 * Creates new tail pieces, and removes destroyed ones.
	 */
	private void CheckTailPieces(GameTime gameTime)
	{
		if (mDoubleTimer == -1)
		{
			mDoubleTimer = gameTime.GetRuntime() + timeToDouble - mHealingBonus;
		}
		
		if (gameTime.GetRuntime() > mDoubleTimer && mCurrentTailPieces < maxPieces)
		{
			AddTailPiece();
			
			mDoubleTimer = gameTime.GetRuntime() + timeToDouble - mHealingBonus;
			
			mHealingBonus += 500;
			
			if (mHealingBonus > timeToDouble - 500)
			{
				mHealingBonus = timeToDouble - 500;
			}
		}
		
		ChainEnemyTailPiece iterator = mFirstTailPiece;
		
		while (iterator != null)
		{
			if (!iterator.IsAlive())
			{
				if (iterator == mFirstTailPiece)
				{
					mFirstTailPiece.Destroy();
					Destroy();
				}
				else
				{
					iterator = mFirstTailPiece;
					
					for (int i = 0; i < mCurrentTailPieces - 1; i++)
					{
						iterator = iterator.GetNext();
					}
					
					iterator.SetNext(null);
					
					if (iterator != mFirstTailPiece)
					{
						iterator.SetColor(new GLColor(Color.YELLOW));
					}
					
					mCurrentTailPieces--;
				}
				
				mHealingBonus = 0;
				mDoubleTimer = gameTime.GetRuntime() + timeToDouble;
				break;
			}
			
			iterator = iterator.GetNext();
		}
	}
	
	private void UpdateTailPieces(GameTime gameTime)
	{
		ChainEnemyTailPiece iterator = mFirstTailPiece;
		
		while (iterator != null)
		{
			iterator.Update(gameTime);
			iterator = iterator.GetNext();
		}
	}
	
	private void CheckTailCollision()
	{
		ChainEnemyTailPiece iterator1 = mFirstTailPiece;
		
		while (iterator1.GetNext() != null)
		{
			ChainEnemyTailPiece iterator2 = iterator1.GetNext();
			
			while(iterator2 != null)
			{
				iterator1.HandleCollisionWithEnemy(iterator2);
				iterator2 = iterator2.GetNext();
			}
			
			iterator1 = iterator1.GetNext();
		}
	}
	
	private void MoveTailPieces()
	{
		mFirstTailPiece.SetAngle(mAngle);
		mFirstTailPiece.SetSpeed(mEnemySpeed);
		
		ChainEnemyTailPiece iterator1 = mFirstTailPiece;
		ChainEnemyTailPiece iterator2 = iterator1.GetNext();
		
		while(iterator2 != null)
		{
			Vector2 difference = iterator2.GetPosition().Minus(iterator1.GetPosition());
			float strength = difference.Length() / 200;
			
			if (strength > 5)
			{
				strength = 5;
			}
			
			iterator2.SetAngle((float)Math.atan2(-difference.Y, -difference.X));
			iterator2.SetSpeed(strength);
			
			iterator1 = iterator2;
			iterator2 = iterator1.GetNext();
		}
	}
	
	public void Update(GameTime gameTime)
	{
		if (IsAlive())
		{
			CheckTailPieces(gameTime);
			MoveTailPieces();
			UpdateTailPieces(gameTime);
			UpdateCollisionLines();
			CheckTailCollision();
		}
	}
	
	public void Draw()
	{
		ChainEnemyTailPiece iterator = mFirstTailPiece;
		
		while (iterator != null)
		{
			iterator.Draw();
			iterator = iterator.GetNext();
		}
	}
	
	public void UpdateCollisionLines()
	{
		ChainEnemyTailPiece iterator = mFirstTailPiece;
		
		while (iterator != null)
		{
			iterator.UpdateCollisionLines();
			iterator = iterator.GetNext();
		}
	}
	
	public LinkedList<CollisionLine> GetCollisionLines()
	{
		LinkedList<CollisionLine> lines = new LinkedList<CollisionLine>();
		
		ChainEnemyTailPiece iterator = mFirstTailPiece;
		
		while (iterator != null)
		{
			LinkedList<CollisionLine> tailPieceLines = iterator.GetCollisionLines();
			
			ListIterator<CollisionLine> j = tailPieceLines.listIterator();
			
			while (j.hasNext())
			{
				CollisionLine current = j.next();
				
				lines.add(current);
			}
			
			iterator = iterator.GetNext();
		}
		
		return lines;
	}
}
