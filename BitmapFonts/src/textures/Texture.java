package textures;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class Texture
{
	public IntBuffer mTexture;
	public int mWidth;
	public int mHeight;
	
	public Texture(String filePath)
	{
		try
		{
			HandleBufferedImage(ImageIO.read(new File(filePath)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Texture(BufferedImage newImg)
	{
		HandleBufferedImage(newImg);
	}
	
	private void HandleBufferedImage(BufferedImage newImg)
	{
		if (!glIsEnabled(GL_TEXTURE_2D))
			glEnable(GL_TEXTURE_2D);
		
		mTexture = BufferUtils.createIntBuffer(1);
		glGenTextures(mTexture);
		
		ByteBuffer bBuffer = ConvertImage(newImg);

		glBindTexture(GL_TEXTURE_2D, mTexture.get(0));
		
		int imageType = newImg.getColorModel().hasAlpha() ? GL_RGBA : GL_RGB;
		
		glTexImage2D(GL_TEXTURE_2D,
				0,
				imageType,
				mWidth,
				mHeight,
				0,
				imageType,
				GL_UNSIGNED_BYTE,
				bBuffer);
		
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);	// Linear Filtering
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);	// Linear Filtering
		glTexEnvf(GL_TEXTURE_2D, GL_TEXTURE_ENV_MODE, GL_ADD);
	}
	
	private ByteBuffer ConvertImage(BufferedImage img)
	{
		mWidth = NextPowerOf2(img.getWidth());
		mHeight = NextPowerOf2(img.getHeight());
		
		ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
                
		ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);
		
		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;
		
		if (img.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,mWidth,mHeight,4,null);
            texImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable<String, Object>());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,mWidth,mHeight,3,null);
            texImage = new BufferedImage(glColorModel,raster,false,new Hashtable<String, Object>());
        }
		
		Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f,0f,0f,0f));
        g.fillRect(0,0,mWidth,mHeight);
        g.drawImage(img,0,0,null);
        
        // build a byte buffer from the temporary image

        // that be used by OpenGL to produce a texture.

        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length); 
        imageBuffer.order(ByteOrder.nativeOrder()); 
        imageBuffer.put(data, 0, data.length); 
        imageBuffer.flip();
		
		return imageBuffer;
	}
	
	private int NextPowerOf2(int num)
	{
		int start = 2;
		
		while (start <= num)
		{
			start *= 2;
		}
		
		return start;
	}
	
	public void Draw(float x, float y)
	{
		Draw(x, y, 1, 1);
	}
	
	public void Draw(float x, float y, float sx, float sy)
	{
		if (!glIsEnabled(GL_TEXTURE_2D))
			glEnable(GL_TEXTURE_2D);
		
		glBindTexture(GL_TEXTURE_2D, mTexture.get(0));
		
		// drawing code here
		glBegin(GL_QUADS);
		{
			float rWidth = mWidth * sx;
			float rHeight = mHeight * sy;
			
			glTexCoord2f(0.0f, 1.0f); glVertex2f(x, y + rHeight);	// Bottom Left Of The Texture and Quad
			glTexCoord2f(1.0f, 1.0f); glVertex2f(x + rWidth, y + rHeight);	// Bottom Right Of The Texture and Quad
			glTexCoord2f(1.0f, 0.0f); glVertex2f(x + rWidth, y);	// Top Right Of The Texture and Quad
			glTexCoord2f(0.0f, 0.0f); glVertex2f(x, y);	// Top Left Of The Texture and Quad
		}
		glEnd();
		
		glDisable(GL_TEXTURE_2D);
	}
}
