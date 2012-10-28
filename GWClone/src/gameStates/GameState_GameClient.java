package gameStates;

import entities.*;
import game.*;
import grid.*;
import util.*;
import static org.lwjgl.opengl.GL11.*;

import network.*;

import java.util.*;

import org.lwjgl.input.*;

import bitmapFonts.FontRenderer;

/**
 * The main game.
 * @author Terry
 */
public class GameState_GameClient extends GameState_GameBase
{
	private Client mClient;
	private int mSendDelay = 20;
	private long mSendTimer = 0;
	
	private ArrayList<Player> mPlayers = new ArrayList<Player>();
	
	/**
	 * Constructor.
	 * Initializes the GameState using the Load method.
	 */
	public GameState_GameClient(Grid newGrid, LinkedList<GameEntity> newSparks, LinkedList<GameEntity> newGridPushes, Vector2 newOffset)
	{
		Load(newGrid, newSparks, newGridPushes, newOffset);
	}
	
	/**
	 * Initializes the GameState.
	 */
	public void Load(Grid newGrid, LinkedList<GameEntity> newSparks, LinkedList<GameEntity> newGridPushes, Vector2 newOffset)
	{		
		mInput.Clear();
		
		// move camera to player
		MoveCamera(1);
		
		// define borders for the game world
		mBorder.add(new CollisionLine(new Vector2(0), new Vector2(GameProperties.SizeX(), 0)));
		mBorder.add(new CollisionLine(new Vector2(GameProperties.SizeX(), GameProperties.SizeY()), new Vector2(GameProperties.SizeX(), 0)));
		mBorder.add(new CollisionLine(new Vector2(GameProperties.SizeX(), GameProperties.SizeY()), new Vector2(0, GameProperties.SizeY())));
		mBorder.add(new CollisionLine(new Vector2(0), new Vector2(0, GameProperties.SizeY())));
		
		// set the properties of the grid
		mGrid = newGrid;
		mSparkList = newSparks;
		mLastingGridPushes = newGridPushes;
		mCameraOffset = newOffset;
		
		//mEnemyList.add(new ChainEnemy(new Vector2(1000)));
		
		// initialize entity classes
		Player.Init(mGrid, mBulletList, mSparkList);
		Bullet.Init(mGrid, mSparkList, mLastingGridPushes);
		Enemy.Init(mSparkList);
		
		mClient = new Client("127.0.0.1", 12345);
		new Thread(mClient).start();
	}
	
	public void UnLoad()
	{
		mClient.Disconnect();
		
		super.UnLoad();
	}
	
	public Player GetThisPlayer()
	{
		for (int i = 0; i < mPlayers.size(); i++)
		{
			Player current = mPlayers.get(i);
			
			if (current.GetID() == mClient.GetID())
			{
				return current;
			}
		}
		
		return null;
	}
	
	public Player GetPlayer(int id)
	{
		for (int i = 0; i < mPlayers.size(); i++)
		{
			Player current = mPlayers.get(i);
			
			if (current.GetID() == id)
			{
				return current;
			}
		}
		
		Player newPlayer = new Player(
				new Vector2(GameProperties.SizeX(), GameProperties.SizeY()).DividedBy(2),
				GameProperties.GetInputHandler());
		newPlayer.SetID(id);
		mPlayers.add(newPlayer);
		return newPlayer;
	}
	
	/*public void HandleKeyboardEvent()
	{
		int key = Keyboard.getEventKey();
		
		if (Keyboard.getEventKeyState())
		{
			mClient.Send("keyp " + key);
		}
		else
		{
			mClient.Send("keyr " + key);
		}
	}*/
	
	public void HandleNetworkMessages()
	{
		ArrayList<NetworkMessage> messages = mClient.GetMessages();
		
		for (int i = 0; i < messages.size(); i++)
		{
			try
			{
				StringTokenizer st = new StringTokenizer(messages.get(i).GetMessage());
				
				int id = Integer.parseInt(st.nextToken());
				
				String command = st.nextToken();

				if (command.equals("player"))
				{
					Player p = GetPlayer(id);
					
					float x = Float.parseFloat(st.nextToken());
					float y = Float.parseFloat(st.nextToken());
					float angle = Float.parseFloat(st.nextToken());
					float vx = Float.parseFloat(st.nextToken());
					float vy = Float.parseFloat(st.nextToken());
					
					p.Set(x, y, angle, vx, vy);
				}
			}
			catch(Exception e)
			{
				System.out.println("Invalid Command");
			}
		}
	}
	
	public void Update(GameTime gameTime)
	{
		// go back to main menu if escape is pressed
		if (mInput.IsKeyHit(Keyboard.KEY_ESCAPE))
		{
			mNextState = new GameState_InGameMenu(this);
		}
		
		int w = 0;
		int a = 0;
		int s = 0;
		int d = 0;
		
		if (mInput.IsKeyDown(Keyboard.KEY_W))
			w = 1;
		if (mInput.IsKeyDown(Keyboard.KEY_A))
			a = 1;
		if (mInput.IsKeyDown(Keyboard.KEY_S))
			s = 1;
		if (mInput.IsKeyDown(Keyboard.KEY_D))
			d = 1;
		
		if (System.currentTimeMillis() > mSendTimer)
		{
			mClient.Send(
					"keys "
					+ w
					+ " "
					+ a
					+ " "
					+ s
					+ " "
					+ d);
			mSendTimer = System.currentTimeMillis() + mSendDelay;
		}

		HandleNetworkMessages();
		
		
		
		for (int i = 0; i < mPlayers.size(); i++)
		{
			Player current = mPlayers.get(i);
			
			if (current.GetID() == mClient.GetID())
			{
				if (w == 1)
					current.GetInputHandler().KeyDown(Keyboard.KEY_W);
				else
					current.GetInputHandler().KeyUp(Keyboard.KEY_W);
				if (a == 1)
					current.GetInputHandler().KeyDown(Keyboard.KEY_A);
				else
					current.GetInputHandler().KeyUp(Keyboard.KEY_A);
				if (s == 1)
					current.GetInputHandler().KeyDown(Keyboard.KEY_S);
				else
					current.GetInputHandler().KeyUp(Keyboard.KEY_S);
				if (d == 1)
					current.GetInputHandler().KeyDown(Keyboard.KEY_D);
				else
					current.GetInputHandler().KeyUp(Keyboard.KEY_D);
			}
			
			current.Update(gameTime);
		}
		
		// calculate the mouse position in game coordinates
		mMousePosition = new Vector2(mInput.GetMouseX(), GameProperties.WindowHeight() - mInput.GetMouseY()).DividedBy(GameProperties.Scale()).Minus(mCameraOffset);
		
		GameEntity.UpdateList(gameTime, mBulletList);
		GameEntity.UpdateList(gameTime, mSparkList);
		GameEntity.UpdateList(gameTime, mLastingGridPushes);

		mGrid.Update(gameTime);
		
		Player p = GetThisPlayer();
		
		if (p != null)
		{
			mCameraFocus.SetEqual(p.GetPosition());
			MoveCamera(10);
		}
	}
	
	public void DrawGameBase()
	{
		super.DrawGameBase();
		
		for (int i = 0; i < mPlayers.size(); i++)
		{
			Player current = mPlayers.get(i);
			current.Draw();
		}
		
		glLoadIdentity();
		
		glColor3f(1, 1, 1);
		FontRenderer.Draw(
				GameProperties.GetFont(),
				"Score", 
				50 * GameProperties.Scale() * 2,
				50 * GameProperties.Scale() * 2,
				2 * GameProperties.Scale(),
				2 * GameProperties.Scale());
		
		int score = 0;
		
		Player me = GetThisPlayer();
		
		if (me != null)
		{
			score = me.GetScore();
		}
		
		FontRenderer.Draw(
				GameProperties.GetFont(),
				GetScoreString(score),
				50 * GameProperties.Scale() * 2,
				110 * GameProperties.Scale() * 2,
				2 * GameProperties.Scale(),
				2 * GameProperties.Scale());
	}
}
