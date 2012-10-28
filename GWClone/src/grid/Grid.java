package grid;

import game.*;
import util.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Defines a mutable grid.
 * @author Terry
 */
public class Grid
{	
	private float mGridTension = 1.2f;
	private int mNumPointsX = 0;
	private int mNumPointsY = 0;
	private GLColor mBrightBlue = new GLColor(0, 0, 0.8f);
	private GLColor mDarkBlue = new GLColor(0, 0, 0.4f);
	private Vector2 mGridOffset = new Vector2();
	private GridPoint[][] mGridPoints;
	
	/**
	 * Constructor.
	 * Creates a new grid with the specified number of sections.
	 * @param sectionsX
	 * @param sectionsY
	 */
	public Grid(int sectionsX, int sectionsY)
	{
		mGridOffset.SetEqual(new Vector2(GameProperties.SizeX() / sectionsX,
				GameProperties.SizeY() / sectionsY));
		mNumPointsX = sectionsX + 1;
		mNumPointsY = sectionsY + 1;
		
		mGridPoints = new GridPoint[mNumPointsX][mNumPointsY];
		
		for (int i = 0; i < mNumPointsX; i++)
		{
			for (int j = 0; j < mNumPointsY; j++)
			{
				mGridPoints[i][j] = new GridPoint(mGridOffset.Times(new Vector2(i, j)));
			}
		}
	}
	
	/**
	 * Updates the points on the grid.
	 * @param gameTime The GameTime to use for updates.
	 */
	public void Update(GameTime gameTime)
	{
		for (int i = 0; i < mNumPointsX; i++)
		{
			for (int j = 0; j < mNumPointsY; j++)
			{
				mGridPoints[i][j].Update(gameTime, mGridTension);
			}
		}
	}
	
	/**
	 * Pushes grid points.
	 * @param position The position to push points away from.
	 * @param strength The strength to push them.
	 * @param maxDistance The maximum distance away from position in which grid points should be pushed.
	 */
	public void Push(Vector2 position, float strength, float maxDistance)
	{		
		Vector2 XRange = GetXRange(position, maxDistance);
		Vector2 YRange = GetYRange(position, maxDistance);
		
		float maxDistanceSquared = maxDistance*maxDistance;
		
		for (int i = (int)XRange.X; i < (int)XRange.Y; i++)
		{
			for (int j = (int)YRange.X; j < (int)YRange.Y; j++)
			{
				Vector2 difference = position.Minus(mGridPoints[i][j].GetPosition());
				
				if (difference.LengthSquared() <= maxDistanceSquared)
				{
					mGridPoints[i][j].Push(position, strength);
				}
			}
		}
	}
	
	/**
	 * Pulls GridPoints.
	 * @param position The position to pull points towards.
	 * @param strength The strength to pull them.
	 * @param maxDistance The maximum distance away from position in which GridPoints should be pulled.
	 */
	public void Pull(Vector2 position, float strength, float maxDistance)
	{		
		Vector2 XRange = GetXRange(position, maxDistance);
		Vector2 YRange = GetYRange(position, maxDistance);
		
		float maxDistanceSquared = maxDistance*maxDistance;
		
		for (int i = (int)XRange.X; i < (int)XRange.Y; i++)
		{
			for (int j = (int)YRange.X; j < (int)YRange.Y; j++)
			{
				Vector2 difference = position.Minus(mGridPoints[i][j].GetPosition());
				
				if (difference.LengthSquared() <= maxDistanceSquared)
				{
					mGridPoints[i][j].Pull(position, strength);
				}
			}
		}
	}
	
	/**
	 * Gets the range of GridPoints along the x axis that should be considered.
	 * @param position The start position
	 * @param maxDistance The maximum distance from position.
	 * @return
	 */
	private Vector2 GetXRange(Vector2 position, float maxDistance)
	{
		Vector2 range = new Vector2();
		int gridPointRadius = (int)(maxDistance / mGridOffset.X);
		
		range.X = (int)position.X / (int)mGridOffset.X - gridPointRadius;
		range.Y = (int)position.X / (int)mGridOffset.X + 1 + gridPointRadius;
		
		if (range.X < 1)
		{
			range.X = 1;
		}
		
		if (range.Y >= mNumPointsX - 1)
		{
			range.Y = mNumPointsX - 1;
		}
		
		return range;
	}
	
	/**
	 * Gets the range of GridPoints along the y axis that should be considered.
	 * @param position The start position
	 * @param maxDistance The maximum distance from position.
	 * @return
	 */
	private Vector2 GetYRange(Vector2 position, float maxDistance)
	{
		Vector2 range = new Vector2();
		int gridPointRadius = (int)(maxDistance / mGridOffset.Y);
		
		range.X = (int)position.Y / (int)mGridOffset.Y - gridPointRadius;
		range.Y = (int)position.Y / (int)mGridOffset.Y + 1 + gridPointRadius;
		
		if (range.X < 1)
		{
			range.X = 1;
		}
		
		if (range.Y >= mNumPointsY - 1)
		{
			range.Y = mNumPointsY - 1;
		}
		
		return range;
	}
	
	/**
	 * Draws the horizontal lines of the Grid.
	 */
	private void DrawHorizontalLines()
	{
		for (int i = 0; i < mNumPointsX; i++)
		{
			if (i % 4 == 0)
			{
				mBrightBlue.SetColor();
			}
			else
			{
				mDarkBlue.SetColor();
			}
			
			glBegin(GL_LINE_STRIP);
			{
				for (int j = 0; j < mNumPointsY; j++)
				{
					Vector2 position = mGridPoints[i][j].GetPosition();
					glVertex2f(position.X, position.Y);
				}
			}
			glEnd();
		}
	}
	
	/**
	 * Draws the vertical lines of the Grid.
	 */
	private void DrawVerticalLines()
	{
		for (int j = 0; j < mNumPointsY; j++)
		{			
			if (j % 4 == 0)
			{
				mBrightBlue.SetColor();
			}
			else
			{
				mDarkBlue.SetColor();
			}
			
			glBegin(GL_LINE_STRIP);
			{
				for (int i = 0; i < mNumPointsX; i++)
				{
					Vector2 position = mGridPoints[i][j].GetPosition();
					glVertex2f(position.X, position.Y);
				}
			}
			glEnd();
		}
	}
	
	/**
	 * Draws the Grid.
	 */
	public void Draw()
	{
		DrawHorizontalLines();
		DrawVerticalLines();
	}
}
