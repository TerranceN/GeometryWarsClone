package game;

import gameStates.*;
import java.util.Stack;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Handles the game loop and game-states. Entry point for the game.
 * @author Terry
 */
public class Game
{
	private GameTime mGameTime;
	private boolean mIsRunning = true;
	
	Stack<GameState> gameStates = new Stack<GameState>();
	
	/**
	 * Entry point. Makes new instance of Game and runs it.
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args)
	{		
		new Game().Run();
	}
	
	public static void ClearScreen()
	{
		glColor3f(0, 0, 0);
		glBegin(GL_QUADS);
		{
			glVertex2f(0, 0);
			glVertex2f(GameProperties.WindowWidth(), 0);
			glVertex2f(GameProperties.WindowWidth(), GameProperties.WindowHeight());
			glVertex2f(0, GameProperties.WindowHeight());
		}
		glEnd();
	}
	
	/**
	 * Runs game loop. Handles the creation and deletion of new states to the stack.
	 */
	public void Run()
	{
		// make sure game started properly
		if (Initialize())
		{			
			while(mIsRunning)
			{				
				// handles window events
				Display.update(true);
				
				mGameTime.Update();
				
				while (Keyboard.next())
				{
					GameProperties.GetInputHandler().HandleKeyboardEvent();
					gameStates.peek().HandleKeyboardEvent();
				}
				
				while (Mouse.next())
				{
					GameProperties.GetInputHandler().HandleMouseEvent();
					gameStates.peek().HandleMouseEvent();
				}
				
				// if the top gamestate is alive, update, otherwise remove it
				if (gameStates.peek().IsAlive() != false)
				{
					gameStates.peek().Update(mGameTime);
				}
				else
				{
					gameStates.pop();
					
					// if there are no more game-states, end the game, otherwise update the new top
					if (!gameStates.empty())
					{
						gameStates.peek().Update(mGameTime);
					}
					else
					{
						mIsRunning = false;
						break;
					}
				}
				
				// clear screen and set modelview matrix
				glClear(GL_COLOR_BUFFER_BIT);
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();
				ClearScreen();
				
				// draw top gamestate
				gameStates.peek().Draw();
				
				if ((Boolean)GameProperties.GetVariable("is_fps_capped").GetData())
				{
					// delay for constant framerate
					Display.sync((Integer)GameProperties.GetVariable("fps_cap").GetData());
				}
				
				// handle close message
				if (Display.isCloseRequested())
				{
					mIsRunning = false;
				}
				
				// handle adding a new state if current state requests a new state
				if (gameStates.peek().IsNextState())
				{
					gameStates.push(gameStates.peek().TakeNextState());
				}
			}
		}
		else
		{
			System.out.println("Failed To Initialize!");
		}
		
		// close display
		Display.destroy();
		
		System.exit(0);
	}
	
	/**
	 * Sets up game.
	 * @return Whether errors occurred.
	 */
	public boolean Initialize()
	{
		// Scale game speed as if running at 60 fps
		mGameTime = new GameTime(60);
		
		// initializes global game variables
		GameProperties.InitVariables();
		
		// loads game graphics
		GameProperties.LoadGraphicsSettings();
		
		// initializes things like fonts
		GameProperties.Initialize();
		
		// creates main menu
		gameStates.push(new GameState_MainMenu());
		
		return true;
	}
	
	
}
