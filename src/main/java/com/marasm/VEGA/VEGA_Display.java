package com.marasm.VEGA;

import com.marasm.ppc.Variable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * Created by sr3u on 08.10.2015.
 */
public class VEGA_Display extends JPanel
{
    static final int colorMask=0xFFFFFF;
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
    static public long pixel(int r,int g,int b) {return ((long)b)|(((long)g)<<8)|(((long)r)<<16);}
    public void putPixel(int x,int y,Variable pixel){screen.setRGB(x%screen.getWidth(),y%screen.getHeight(),pixel.intValue()&colorMask);}
    public void setSize(int newX, int newY)
    {
        screen= toBufferedImage(screen.getScaledInstance(newX,newY,Image.SCALE_DEFAULT));
        repaint();
    }
    public void drawLine(int x1,int y1,int x2,int y2,int width,Variable color)
    {
        Shape dLines = new Line2D.Float(x1,y1,x2,y2);
        int c=color.intValue()&colorMask;
        Graphics2D g=(Graphics2D)screen.getGraphics();
        g.setColor(new Color(c));
        g.setStroke(new BasicStroke(width));
        g.drawLine(x1,y1,x2,y2);
    }
    @Override public void paint(Graphics g)
    {
        g.drawImage(screen,0,0,getWidth(),getHeight(),this);
    }
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

    void drawRect(int x,int y,int w,int h,Variable color)
    {
        x=Math.min(x,screen.getWidth());
        y=Math.min(y,screen.getHeight());
        w=Math.min(w,screen.getWidth()-x);
        h=Math.min(h,screen.getHeight()-y);
        int[] rgb=new int[w*h];
        int c=color.intValue()&colorMask;
        for(int i=0;i<w*h;i++){rgb[i]=c;}
        screen.setRGB(x,y,w,h,rgb,0,w);
    }
}
