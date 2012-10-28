package entities;

import util.Vector2;
import game.GameTime;
import grid.Grid;

public class LastingGridPush extends GameEntity
{
	private Grid mGrid;
	private long mDurationTimer = 0;
	private int mMaxDuration = 0;
	private float mStrength = 0;
	private float mDistance = 0;
	
	public LastingGridPush(Vector2 newPosition, Grid newGrid, float newStrength, float newDistance, int newDuration)
	{
		super(newPosition, new Vector2(), true);
		mGrid = newGrid;
		mStrength = newStrength;
		mDistance = newDistance;
		mMaxDuration = newDuration;
		mDurationTimer = -1;
	}
	
	public void Update(GameTime gameTime)
	{
		if (mDurationTimer == -1)
		{
			mDurationTimer = gameTime.GetRuntime() + mMaxDuration;
		}
		
		long duration = mDurationTimer - gameTime.GetRuntime();
		
		if (duration <= 0)
		{
			Destroy();
		}
		else
		{
			mGrid.Push(mPosition, mStrength * duration / (float)mMaxDuration, mDistance);
		}
	}
}
