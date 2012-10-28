package entities;

import java.util.*;

import game.*;
import util.*;

/**
 * An entity in the game. Factors out commonalities in game entities.
 * @author Terry
 */
public abstract class GameEntity
{
	protected boolean isAlive = true;
	
	protected int mID = 0;
	
	protected Vector2 mPosition = new Vector2();
	protected Vector2 mVelocity = new Vector2();
	
	/**
	 * Constructor.
	 * Sets the values of the game entity.
	 * @param nPosition The starting position.
	 * @param nVelocity The starting velocity.
	 * @param nIsAlive Whether the entity starts as alive or dead.
	 */
	public GameEntity(Vector2 nPosition, Vector2 nVelocity, boolean nIsAlive)
	{
		mPosition.SetEqual(nPosition);
		mVelocity.SetEqual(nVelocity);
		isAlive = nIsAlive;
	}
	
	public int GetID()
	{
		return mID;
	}
	
	public void SetID(int newID)
	{
		mID = newID;
	}
	
	/**
	 * Gets the position of the GameEntity.
	 * @return The position of the GameEntity.
	 */
	public Vector2 GetPosition()
	{
		return mPosition;
	}
	
	/**
	 * Gets the velocity of the GameEntity.
	 * @return The velocity of the GameEntity.
	 */
	public Vector2 GetVelocity()
	{
		return mVelocity;
	}
	
	/**
	 * Sets the position of the GameEntity,
	 * @param position The position this GameEntity should be set to.
	 */
	public void SetPosition(Vector2 position)
	{
		mPosition.SetEqual(position);
	}
	
	/**
	 * To be called when the GameEntity is to be Destroyed.
	 * Takes care of what happens when a GameEntity is destroyed.
	 */
	public void Destroy()
	{
		isAlive = false;
	}
	
	/**
	 * Gets whether this GameEntity is alive or not.
	 * @return Whether this GameEntity is alive or not.
	 */
	public boolean IsAlive()
	{
		return isAlive;
	}
	
	/**
	 * Updates the GameEntity.
	 * @param gameTime The GameTime to use for updates.
	 */
	public void Update(GameTime gameTime){}
	
	/**
	 * Draws the GameEntity.
	 */
	public void Draw(){}
	
	/**
	 * Updates a list of game entities.
	 * @param gameTime The GameTime to use for movement.
	 * @param list The list of entities to update.
	 */
	public static void UpdateList(GameTime gameTime, LinkedList<GameEntity> list)
	{
		ListIterator<GameEntity> i = list.listIterator();
		
		while (i.hasNext())
		{
			GameEntity current = i.next();
			
			current.Update(gameTime);
			
			if (!current.IsAlive())
			{
				i.remove();
			}
		}
	}
	
	/**
	 * Draws a list of entities.
	 * @param list The lost of entities to draw.
	 */
	public static void DrawList(LinkedList<GameEntity> list)
	{
		ListIterator<GameEntity> i = list.listIterator();
		
		while (i.hasNext())
		{
			GameEntity current = i.next();
			current.Draw();
		}
	}
}