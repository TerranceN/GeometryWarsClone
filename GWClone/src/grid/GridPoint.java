package grid;

import game.*;
import util.*;

import java.util.ArrayList;

/**
 * Defines a point on a Grid.
 * @author Terry
 */
public class GridPoint
{
	private Vector2 mOriginalPosition = new Vector2();
	private Vector2 mLastCalculated = new Vector2();
	private Vector2 mOffset = new Vector2();
	private ArrayList<GridForce> mPushForces = new ArrayList<GridForce>();
	private ArrayList<GridForce> mPullForces = new ArrayList<GridForce>();
	
	/**
	 * Constructor.
	 * Creates a new GridPoint with a specific starting position.
	 * @param startingPosition The starting position.
	 */
	public GridPoint(Vector2 startingPosition)
	{
		mOriginalPosition.SetEqual(startingPosition);
		mLastCalculated.SetEqual(mOriginalPosition);
	}
	
	/**
	 * Adds a Push GridForce to this GridPoint.
	 * @param position The origin of the GridForce.
	 * @param strength The strength of the GridForce.
	 */
	public void Push(Vector2 position, float strength)
	{
		mPushForces.add(new GridForce(position, strength));
	}
	
	/**
	 * Adds a Pull GridForce to this GridPoint.
	 * @param position The origin of the GridForce.
	 * @param strength The strength of the GridForce.
	 */
	public void Pull(Vector2 position, float strength)
	{
		mPullForces.add(new GridForce(position, strength));
	}

	/**
	 * Moves the GridPoint towards the starting position, and applies GridForces.
	 * @param gameTime The GameTime to use for updates.
	 * @param tension The tension of the Grid this GridPoint belongs to.
	 */
	public void Update(GameTime gameTime, float tension)
	{
		for (int i = 0; i < mPushForces.size(); i++)
		{
			ApplyPush(gameTime, mPushForces.get(i));
		}
		
		mOffset.DividedByEquals(new Vector2(tension));
		CalculatePoint();
		
		for (int i = 0; i < mPullForces.size(); i++)
		{
			ApplyPull(gameTime, mPullForces.get(i));
		}
		
		mPushForces.clear();
		mPullForces.clear();
	}
	
	/**
	 * Gets the original position of this GridPoint.
	 * @return The original position of this GridPoint.
	 */
	public Vector2 GetOriginalPosition()
	{
		return mOriginalPosition;
	}
	
	/**
	 * Gets the last calculated position of the GridPoint.
	 * @return The last calculated position of the GridPoint.
	 */
	public Vector2 GetPosition()
	{
		return mLastCalculated;
	}
	
	/**
	 * Calculates the position of the GridPoint.
	 */
	private void CalculatePoint()
	{
		mLastCalculated = mOriginalPosition.Plus(mOffset);
	}
	
	/**
	 * Applies a Push GridForce to this GridPoint.
	 * @param gameTime The GameTime to use for updates.
	 * @param force The GridForce to apply.
	 */
	private void ApplyPush(GameTime gameTime, GridForce force)
	{
		if (force.GetForce() > 0)
		{
			Vector2 direction = GetPosition().Minus(force.GetForceOrigin());
			float distance = direction.Length();
			if (distance > 0)
			{
				if (distance < 50)
				{
					distance = 50;
				}
				direction.Normalize();
				float finalStrength = force.GetForce() / ((float)Math.pow(distance, 2)) * gameTime.GetSpeedFactor();
				Vector2 plusEquals = direction.Times(new Vector2(
						finalStrength));
				mOffset.PlusEquals(plusEquals);
				
				CalculatePoint();
			}
		}
	}
	
	/**
	 * Applies a Pull GridForce to this GridPoint.
	 * @param gameTime The GameTime to use for updates.
	 * @param force The GridForce to apply.
	 */
	private void ApplyPull(GameTime gameTime, GridForce force)
	{
		if (force.GetForce() > 0)
		{
			Vector2 direction = force.GetForceOrigin().Minus(GetPosition());
			float distance = direction.Length();
			if (distance > 0)
			{				
				direction.Normalize();
				float finalStrength = force.GetForce() / distance * gameTime.GetSpeedFactor();
				Vector2 plusEquals = direction.Times(new Vector2(
						finalStrength));
				
				if (finalStrength > distance)
				{
					mOffset.SetEqual(force.GetForceOrigin().Minus(mOriginalPosition));
				}
				else
				{
					mOffset.PlusEquals(plusEquals);
				}
				
				CalculatePoint();
			}
		}
	}
}

/**
 * Defines a force on a GridPoint.
 * @author Terry
 */
class GridForce
{
	private Vector2 forceOrigin = new Vector2();
	private float strength = 0;
	
	/**
	 * Constructor.
	 * Creates a new GridForce based on the origin of the force
	 * and the strength of the force.
	 * @param newPosition The origin of the force.
	 * @param newStrength The strength of the force.
	 */
	public GridForce(Vector2 newPosition, float newStrength)
	{
		forceOrigin = newPosition;
		strength = newStrength;
	}
	
	/**
	 * Gets the origin of the force.
	 * @return The origin of the force.
	 */
	public Vector2 GetForceOrigin()
	{
		return forceOrigin;
	}
	
	/**
	 * Gets the strength of the force.
	 * @return The strength of the force.
	 */
	public float GetForce()
	{
		return strength;
	}
}
