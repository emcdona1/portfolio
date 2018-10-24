import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.stage.*;

public class ManipulatedImage
{
    private int width;
    private int height;
    private Image img;
    private Color[][] colors;
    private WritableImage writableImg;
    
    public ManipulatedImage(String filename)
    {
        this.img = new Image(filename);
        this.width = (int)this.img.getWidth();
        this.height = (int)this.img.getHeight();
        
        this.setColors();
        
        this.writableImg = new WritableImage(this.width, this.height);
        this.setWritableImg();
    }

    public void setColors()
    {
        this.colors = new Color[this.height][this.width];
        PixelReader reader = this.img.getPixelReader(); 
        
        for(int i = 0; i < this.colors.length; i++)
        {
            for(int j = 0; j < this.colors[i].length; j++)
            {
                this.colors[i][j] = reader.getColor(j, i);
            }
        }
    }
    
    public WritableImage getWritableImg()
    {
        return this.writableImg;
    }

    public void setWritableImg()
    {
        PixelWriter writer = this.writableImg.getPixelWriter();
        
        for(int i = 0; i < this.colors.length; i++)
        {
            for(int j = 0; j < this.colors[i].length; j++)
            {
                writer.setColor(j, i, this.colors[i][j]);
            }
        }
    }
    
    public void mirrorY()
    {
        Color[][] newColors = new Color[colors.length][colors[0].length];
        
        for(int i = 0; i < this.colors.length; i++)
        {
             for(int j = 0; j < this.colors[i].length; j++)
             {
                  newColors[i][j] = this.colors[i][this.width - 1 - j];
             }
        }
        
        this.colors = newColors;
        
        this.setWritableImg();
    }
    
    public void grayscale()
    {        
        for(int i = 0; i < this.colors.length; i++)
        {
            for(int j = 0; j < this.colors[0].length; j++)
            {
                Color temp = this.colors[i][j];
                double red = temp.getRed();
                double green = temp.getGreen();
                double blue = temp.getBlue();
                
                double gray = (red + blue + green) / 3;
                
                this.colors[i][j] = new Color(gray, gray, gray, 1.0);
            }
        }
        
        this.setWritableImg();
    }
    
    public void rotate90()
    {
        Color[][] newColors = new Color[colors[0].length][colors.length];
        
        for(int i = 0; i < this.colors.length; i++)
        {
             for(int j = 0; j < this.colors[i].length; j++)
             {
                  newColors[j][i] = this.colors[i][j];
             }
        }
        
        this.colors = newColors;
        this.writableImg = new WritableImage(colors[0].length, colors.length);
        this.setWritableImg();
    }
}