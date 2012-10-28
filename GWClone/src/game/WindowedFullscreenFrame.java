package game;

import java.awt.*;
import javax.swing.JFrame;

public class WindowedFullscreenFrame extends JFrame
{
	private Canvas mCanvas;
	
	public WindowedFullscreenFrame(int width, int height)
	{
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, height);
		setVisible(true);
		setLocation(0, 0);
		setFocusable(false);
		
		mCanvas = new Canvas();
		mCanvas.setSize(width, height);
		mCanvas.setFocusable(true);
		mCanvas.setIgnoreRepaint(true);
		
		add(mCanvas);
	}
	
	public Canvas GetCanvas()
	{
		return mCanvas;
	}
}