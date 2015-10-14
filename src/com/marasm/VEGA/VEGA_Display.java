package com.marasm.VEGA;

import com.marasm.ppc.Variable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by sr3u on 08.10.2015.
 */
public class VEGA_Display extends JPanel
{
    public BufferedImage screen;
    Thread refresh=new Thread()
    {
        @Override public void run()
        {
            while (true)
            {
                VEGA_Display.this.repaint();
                try{Thread.sleep(20);}catch(InterruptedException e){e.printStackTrace();}
            }
        }
    };
    public VEGA_Display(){this(320,240);}
    VEGA_Display(int sizeX,int sizeY)
    {
        this(sizeX,sizeY,new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB));
    }
    VEGA_Display(int sizeX,int sizeY,BufferedImage img)
    {
        screen= toBufferedImage(img.getScaledInstance(sizeX,sizeY,Image.SCALE_DEFAULT));
        refresh.start();
    }
    static long pixel(int r,int g,int b) {return ((long)r)|(((long)g)<<8)|(((long)b)<<16);}
    void putPixel(int x,int y,Variable pixel){screen.setRGB(x,y,pixel.intValue()&0xFFFFFF);}
    public void setSize(int newX, int newY)
    {
        screen= toBufferedImage(screen.getScaledInstance(newX,newY,Image.SCALE_DEFAULT));
        repaint();
    }
    @Override public void paint(Graphics g) {g.drawImage(screen,0,0,this);}
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage) {return (BufferedImage) img;}
        // Create a buffered image without transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }
}
