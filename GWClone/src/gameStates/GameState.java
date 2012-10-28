package gameStates;

import game.GameTime;

/**
 * Defines a game state.
 */
public abstract class GameState
{
	protected boolean mIsAlive = true;

	/**
	 * Gets whether this GameState is alive or dead.
	 * @return Whether this GameState is alive or dead.
 	 */
	public boolean IsAlive(){return mIsAlive;}
	
	protected GameState mNextState = null;

	/**
	 * Gets whether there is a next GameState waiting.
	 * @return Whether there is a next GameState waiting.
	 */
	public boolean IsNextState(){return mNextState!=null;}

	/**
	 * Takes the next state from this GameState, leaving
	 * this GameState's next state as null.
	 * @return This GameState's next GameState.
	 */
	public GameState TakeNextState()
	{
		GameState returnState = mNextState;
		
		mNextState = null;
		
		return returnState;
	}
	
	/**
	 * Loads all necessary content for this GameState to run.
	 */
	public void Load(){};

	/**
	 * Unloads all content that will not be automatically unloaded
	 * when this GameState is destroyed, and sets its status to dead.
	 */
	public void UnLoad()
	{
		mIsAlive = false;
	}
	
	/**
	 * Handles the current keyboard event.
	 */
	public void HandleKeyboardEvent(){}
	
	/**
	 * Handles the current mouse event.
	 */
	public void HandleMouseEvent(){}
	
	/**
	 * Updates this GameState.
	 * @param gameTime The GameTime to use for updates.
	 */
	public void Update(GameTime gameTime){};

	/**
	 * Draws this GameState.
	 */
	public void Draw(){};
}
