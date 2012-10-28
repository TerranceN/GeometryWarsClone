package entities;

import util.*;
import game.*;

public class BasicEnemy extends Enemy
{
	public BasicEnemy(Vector2 nPosition, GLColor c)
	{
		super(nPosition, c);
	}
	
	public void Update(GameTime gameTime)
	{
		super.Update(gameTime);
		
		mAngle = (float)Math.atan2(mVelocity.Y, mVelocity.X);
	}
	
	public void UpdateCollisionLines()
	{
		Vector2 point = mPosition.Plus(Vector2.FromAngle(mAngle).Times(mRadius.X * 3));
		Vector2 left = mPosition.Plus(Vector2.FromAngle(mAngle - (float)Math.PI / 2).Times(mRadius.X));
		Vector2 right = mPosition.Plus(Vector2.FromAngle(mAngle + (float)Math.PI / 2).Times(mRadius.X));
		Vector2 back = mPosition.Plus(Vector2.FromAngle(mAngle + (float)Math.PI).Times(mRadius.X));
		
		mCollisionLines.get(0).Set(point, left);
		mCollisionLines.get(1).Set(point, right);
		mCollisionLines.get(2).Set(back, left);
		mCollisionLines.get(3).Set(back, right);
		
		
		/*mCollisionLines.get(0).Set(mPosition.Minus(mRadius),
				mPosition.Plus(new Vector2(mRadius.X, -mRadius.Y)));
		mCollisionLines.get(1).Set(mPosition.Plus(mRadius),
				mPosition.Plus(new Vector2(mRadius.X, -mRadius.Y)));
		mCollisionLines.get(2).Set(mPosition.Plus(mRadius),
				mPosition.Plus(new Vector2(-mRadius.X, mRadius.Y)));
		mCollisionLines.get(3).Set(mPosition.Minus(mRadius),
				mPosition.Plus(new Vector2(-mRadius.X, mRadius.Y)));*/
		
		/*mCollisionLines.get(0).Set(mPosition.Plus(mRadius),
				mPosition.Plus(new Vector2(-mRadius.X, mRadius.Y)));
		mCollisionLines.get(1).Set(mPosition.Plus(mRadius),
				mPosition.Plus(new Vector2(0, -mRadius.Y)));
		mCollisionLines.get(2).Set(mPosition.Plus(new Vector2(0, -mRadius.Y)),
				mPosition.Plus(new Vector2(-mRadius.X, mRadius.Y)));*/
	}
}
