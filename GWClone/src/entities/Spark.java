package entities;

import game.*;
import util.*;
import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;

/**
 * A visual spark that fades and dies.
 * @author Terry
 */
public class Spark extends GameEntity
{
	private float sparkSizeFactor = 20.0f;
	
	protected long lifeTimer = 0;
	protected int maxLife = 0;
	protected GLColor originalColor = new GLColor();
	protected GLColor color = new GLColor();
	protected float lifePercentage;
	
	/**
	 * Constructor.
	 * Creates a new Spark from specified values. Default color set to red.
	 * @param nPosition The starting position for the Spark.
	 * @param nVelocity The starting velocity for the Spark.
	 * @param life The amount of time the Spark should live for.
	 */
	public Spark(Vector2 nPosition, Vector2 nVelocity, int life)
	{
		this(nPosition, nVelocity, life, new GLColor(Color.RED));
	}
	
	/**
	 * Constructor.
	 * Creates a new Spark from specified values.
	 * @param nPosition The starting position for the Spark.
	 * @param nVelocity The starting velocity for the Spark.
	 * @param life The amount of time the Spark should live for.
	 * @param c The color of the Spark.
	 */
	public Spark(Vector2 nPosition, Vector2 nVelocity, int life, GLColor c)
	{
		super(nPosition, nVelocity, true);
		maxLife = life;
		lifeTimer = -1;
		originalColor.SetEqual(c);
	}
	
	/**
	 * Gets the life of this Spark.
	 * @return The life of this Spark.
	 */
	public float GetLife()
	{
		return lifePercentage;
	}

	/**
	 * Updates the Spark.
	 */
	public void Update(GameTime gameTime)
	{
		if (lifeTimer == -1)
		{
			lifeTimer = gameTime.GetRuntime() + maxLife;
		}
		
		mPosition.PlusEquals(mVelocity.Times(new Vector2(1)));
		mVelocity.DividedByEquals(new Vector2(1.05f));
		sparkSizeFactor /= 1.05f;
		
		float lifeDifference = lifeTimer - gameTime.GetRuntime();
		if (lifeDifference < 0)
		{
			Destroy();
		}
		else
		{
			lifePercentage = lifeDifference / maxLife;
		}
		
		color.SetEqual(originalColor.Times(lifePercentage));
		
		if (mPosition.X < -5 || mPosition.X > GameProperties.SizeX() + 5 || mPosition.Y < -5 || mPosition.Y > GameProperties.SizeY() + 5)
		{
			Destroy();
		}
	}
	
	/**
	 * Draws the spark.
	 */
	public void Draw()
	{
		glBegin(GL_LINES);
		{
			Vector2 direction = mVelocity.GetNormalized().Times(new Vector2(sparkSizeFactor));
			Vector2 otherEnd = mPosition.Minus(direction.Times(new Vector2(-4)));

			color.SetColor();
			
			glVertex2f(mPosition.X, mPosition.Y);
			glVertex2f(otherEnd.X, otherEnd.Y);
		}
		glEnd();
	}
}
