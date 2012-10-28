package entities;

import java.awt.Color;

import game.GameTime;
import util.*;

class ChainEnemyTailPiece extends Enemy
{
	private ChainEnemyTailPiece mNextTailPiece = null;
	
	public ChainEnemyTailPiece(Vector2 position, GLColor c)
	{
		super(position, c);
		mRadius = new Vector2(50.0f);
	}
	
	public ChainEnemyTailPiece GetNext()
	{
		return mNextTailPiece;
	}
	
	public void SetColor(GLColor c)
	{
		mColor.SetEqual(c);
	}
	
	public void SetNext(ChainEnemyTailPiece next)
	{
		mNextTailPiece = next;
	}
	
	public void OnCollisionWithEnemy(Enemy e, Vector2 collisionPoint)
	{
		MoveAwayFrom(e.GetPosition(), 0.5f);
	}
	
	private void DrawTailAngle(float angle)
	{
		mSparkList.add(new Spark(mPosition,
				Vector2.FromAngle(angle + (float)Math.random() * 0.5f - 0.25f).Times(10),
				1600,
				mColor));
	}
	
	protected void DrawTail()
	{		
		DrawTailAngle(mAngle + 2.25f);
		DrawTailAngle(mAngle - 2.25f);
	}
	
	public void Update(GameTime gameTime)
	{
		mVelocity.PlusEquals(Vector2.FromAngle(mAngle).Times(mEnemySpeed));
		super.Update(gameTime);
	}
	
	public void UpdateCollisionLines()
	{
		Vector2 top = Vector2.FromAngle(mAngle).Times(mRadius.Times(2)).Plus(GetPosition());
		Vector2 left = Vector2.FromAngle(mAngle + 2).Times(mRadius).Plus(GetPosition());
		Vector2 right = Vector2.FromAngle(mAngle - 2).Times(mRadius).Plus(GetPosition());
		
		mCollisionLines.get(0).Set(top, left);
		mCollisionLines.get(1).Set(top, right);
		mCollisionLines.get(2).Set(left, GetPosition());
		mCollisionLines.get(3).Set(GetPosition(), right);
	}
}
