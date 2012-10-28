package entities;

import game.*;
import grid.Grid;
import util.*;

import java.awt.Color;
import java.util.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * A bullet that flies until it hits a wall or an enemy.
 * @author Terry
 *
 */
public class Bullet extends GameEntity
{
	private static LinkedList<GameEntity> sparkList;
	private static LinkedList<GameEntity> lastingGridPushes;
	private static Grid grid;
	
	private static final float bulletSizeFactor = 30.0f;
	
	private Player mOwner;
	private Vector2 mPrePosition = new Vector2();
	private CollisionLine mMovementLine = new CollisionLine(mPosition, mPrePosition);
	
	/**
	 * Constructor.
	 * Creates new Bullet with specified values.
	 * @param nPosition The starting position of the Bullet.
	 * @param nVelocity The starting velocity of the Bullet.
	 */
	public Bullet(Vector2 nPosition, Vector2 nVelocity, Player nOwner)
	{
		super(nPosition, nVelocity, true);
		mPrePosition.SetEqual(mPosition);
		mOwner = nOwner;
	}
	
	/**
	 * Initializes the Bullet class.
	 * @param newGrid The grid the game is using. Used to make grid pushes.
	 * @param newSparkList The list of sparks the game is using. Used to make new sparks.
	 * @param newLastingGridPushes The list of grid pushes the game is using. Used to make new grid pushes.
	 */
	public static void Init(Grid newGrid, LinkedList<GameEntity> newSparkList, LinkedList<GameEntity> newLastingGridPushes)
	{
		grid = newGrid;
		sparkList = newSparkList;
		lastingGridPushes = newLastingGridPushes;
	}
	
	public Player GetOwner()
	{
		return mOwner;
	}
	
	/**
	 * To be called when the Bullet is destroyed. Makes sparks and grid push, then sets itself as dead.
	 */
	public void Destroy()
	{
		for (int j = 0; j < (Integer)GameProperties.GetVariable("sparks_per_bullet").GetData(); j++)
		{			
			float angle = (float)Math.random() * 6.28f;
			sparkList.add(new Spark(mPosition,
					Vector2.FromAngle(angle).Times(new Vector2(10).Plus(new Vector2((float)Math.random() * 20))),
					1600,
					new GLColor(Color.YELLOW)));
		}
		
		lastingGridPushes.add(new LastingGridPush(mPosition, grid, 400000, 200, 416));
		
		super.Destroy();
	}
	
	/**
	 * Gets the line from where the Bullet last was to where it is now.
	 * @return The line from where the Bullet last was to where it is now.
	 */
	public CollisionLine MovementLine()
	{
		return mMovementLine;
	}
	
	/**
	 * Updates the Bullet.
	 */
	public void Update(GameTime gameTime)
	{
		mPrePosition.SetEqual(mPosition);
		mPosition.PlusEquals(mVelocity.Times(new Vector2(gameTime.GetSpeedFactor())));
		mMovementLine.Set(mPosition, mPrePosition);
		grid.Push(mPosition, 90000, 100);
	}
	
	/**
	 * Draws the Bullet.
	 */
	public void Draw()
	{
		Vector2 direction = mVelocity.GetNormalized().Times(new Vector2(bulletSizeFactor));
		Vector2 forward = mPosition.Plus(direction.DividedBy(new Vector2(2)));
		Vector2 backward = mPosition.Minus(direction.DividedBy(new Vector2(2)));
		Vector2 left = new Vector2(-direction.Y, direction.X).DividedBy(new Vector2(8));
		Vector2 p1 = backward.Plus(left);
		Vector2 p2 = backward.Minus(left);
		
		glColor3f(1.0f, 1.0f, 0);
		
		glBegin(GL_LINES);
		{
			glVertex2f(forward.X, forward.Y);
			glVertex2f(p1.X, p1.Y);
			
			glVertex2f(p1.X, p1.Y);
			glVertex2f(p2.X, p2.Y);
			
			glVertex2f(p2.X, p2.Y);
			glVertex2f(forward.X, forward.Y);
		}
		glEnd();
	}
}
