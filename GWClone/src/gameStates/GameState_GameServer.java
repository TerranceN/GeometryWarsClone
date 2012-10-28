package gameStates;

import entities.*;
import game.*;
import grid.*;
import util.*;
import input.*;
import static org.lwjgl.opengl.GL11.*;

import network.*;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.*;
import org.lwjgl.input.*;

import bitmapFonts.FontRenderer;

/**
 * The main game.
 * @author Terry
 */
public class GameState_GameServer extends GameState_GameBase
{
	private LinkedList<Player> mPlayers = new LinkedList<Player>();
	private Server mServer;
	
	private int mPlayerID = 0;
	private int mEnemyID = 0;
	private int mBulletID = 0;
	
	private int mSendDelay = 20;
	private long mSendTimer = 0;
	
	/**
	 * Constructor.
	 * Initializes the GameState using the Load method.
	 */
	public GameState_GameServer(Grid newGrid, LinkedList<GameEntity> newSparks, LinkedList<GameEntity> newGridPushes, Vector2 newOffset)
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
		mGrid = new Grid(1, 1);
		mSparkList = newSparks;
		mLastingGridPushes = newGridPushes;
		mCameraOffset = newOffset;
		
		//mEnemyList.add(new ChainEnemy(new Vector2(1000)));
		
		// initialize entity classes
		Player.Init(mGrid, mBulletList, mSparkList);
		Bullet.Init(mGrid, mSparkList, mLastingGridPushes);
		Enemy.Init(mSparkList);
		
		mServer = new Server(12345);
		new Thread(mServer).start();
	}
	
	private int NextPlayerID()
	{
		mPlayerID++;
		return mPlayerID;
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
				new InputHandler());
		newPlayer.SetID(NextPlayerID());
		mPlayers.add(newPlayer);
		return newPlayer;
	}
	
	public void HandleNetworkMessages()
	{
		ArrayList<NetworkMessage> messages = mServer.GetMessages();
		
		for (int i = 0; i < messages.size(); i++)
		{
			try
			{
				StringTokenizer st = new StringTokenizer(messages.get(i).GetMessage());
				
				int id = Integer.parseInt(st.nextToken());
				
				if (id == 0)
				{
					continue;
				}
				
				Player p = GetPlayer(id);
				
				String command = st.nextToken();

				if (command.equals("keys"))
				{
					int w = Integer.parseInt(st.nextToken());
					int a = Integer.parseInt(st.nextToken());
					int s = Integer.parseInt(st.nextToken());
					int d = Integer.parseInt(st.nextToken());
					
					if (w == 1)
						p.GetInputHandler().KeyDown(Keyboard.KEY_W);
					else
						p.GetInputHandler().KeyUp(Keyboard.KEY_W);
					if (a == 1)
						p.GetInputHandler().KeyDown(Keyboard.KEY_A);
					else
						p.GetInputHandler().KeyUp(Keyboard.KEY_A);
					if (s == 1)
						p.GetInputHandler().KeyDown(Keyboard.KEY_S);
					else
						p.GetInputHandler().KeyUp(Keyboard.KEY_S);
					if (d == 1)
						p.GetInputHandler().KeyDown(Keyboard.KEY_D);
					else
						p.GetInputHandler().KeyUp(Keyboard.KEY_D);
				}
			}
			catch(Exception e)
			{
				System.out.println("Invalid Command");
			}
		}
	}
	
	public void UnLoad()
	{
		mServer.Disconnect();
		super.UnLoad();
	}
	
	public void Update(GameTime gameTime)
	{
		// go back to main menu if escape is pressed
		if (mInput.IsKeyHit(Keyboard.KEY_ESCAPE))
		{
			UnLoad();
		}
		
		HandleNetworkMessages();
		
		// calculate the mouse position in game coordinates
		mMousePosition = new Vector2(mInput.GetMouseX(), GameProperties.WindowHeight() - mInput.GetMouseY()).DividedBy(GameProperties.Scale()).Minus(mCameraOffset);
		
		for (int i = 0; i < mPlayers.size(); i++)
		{
			Player player = mPlayers.get(i);
			player.SetMousePosition(mMousePosition);
			player.Update(gameTime);
		}
		
		// move enemies towards player
		for (int i = 0; i < mEnemyList.size(); i++)
		{
			float minDistance = 99999;
			Player closestPlayer = null;
			
			for (int j = 0; j < mPlayers.size(); j++)
			{
				Player player = mPlayers.get(j);
				
				Vector2 difference = mEnemyList.get(i).GetPosition().Minus(player.GetPosition());
				
				float distance = (float)Math.sqrt(
						Math.pow(difference.X, 2)
						+ Math.pow(difference.Y, 2));
				
				if (distance < minDistance)
				{
					closestPlayer = player;
					minDistance = distance;
				}
			}
			
			if (closestPlayer != null)
			{
				((Enemy)mEnemyList.get(i)).MoveTowards(closestPlayer.GetPosition());
			}
		}
		
		GameEntity.UpdateList(gameTime, mEnemyList);
		GameEntity.UpdateList(gameTime, mBulletList);
		
		HandleBulletCollisionWithEnemies();
		HandleBulletCollisionWithWalls();
		
		for (int i = 0; i < mPlayers.size(); i++)
		{
			Player player = mPlayers.get(i);
			HandlePlayerCollisionWithEnemies(player);
		}
		
		HandleEnemyCollisionWithEnemies();
		
		mSparkList.clear();
		mLastingGridPushes.clear();

		SpawnEnemies();
		
		SendNetworkMessages();
		
		//MoveCamera(10);
	}
	
	public void SendNetworkMessages()
	{
		if (System.currentTimeMillis() > mSendTimer)
		{
			for (int i = 0; i < mPlayers.size(); i++)
			{
				Player player = mPlayers.get(i);
				
				mServer.Send(
						player.GetID()
						+ " player "
						+ player.GetPosition().X
						+ " "
						+ player.GetPosition().Y
						+ " "
						+ player.GetAngle()
						+ " "
						+ player.GetVelocity().X
						+ " "
						+ player.GetVelocity().Y);
			}
			
			mSendTimer = System.currentTimeMillis() + mSendDelay;
		}
	}
	
	public void DrawGameBase()
	{
		/*super.DrawGameBase();
		
		mPlayer.Draw();
		
		glLoadIdentity();
		
		glColor3f(1, 1, 1);
		FontRenderer.Draw(
				GameProperties.GetFont(),
				"Score", 
				50 * GameProperties.Scale() * 2,
				50 * GameProperties.Scale() * 2,
				2 * GameProperties.Scale(),
				2 * GameProperties.Scale());
		FontRenderer.Draw(
				GameProperties.GetFont(),
				GetScoreString(mPlayer.GetScore()),
				50 * GameProperties.Scale() * 2,
				110 * GameProperties.Scale() * 2,
				2 * GameProperties.Scale(),
				2 * GameProperties.Scale());*/
	}
}
