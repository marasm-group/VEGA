package com.marasm.VEGA;

import com.marasm.ppc.*;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by sr3u on 08.10.2015.
 */

public class VEGA extends PPCDevice
{
    public final String ctrlPort="127.0";
    public final String colorPort="127.1";
    public final String pixelPort="127.2";
    public final String linePort="127.3";
    public final String rectPort="127.4";
    public final String memPort="127.5";
    private VEGA_GUI gui;
    private Variable color=new Variable();
    public String jarLocation()
    {
        String path=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(path.endsWith("!/")){path=path.substring(0,path.length()-2);}
        String fileName=path.substring(path.lastIndexOf(File.separatorChar) + 1);
        if(fileName.contains(".jar")){path=path.substring(0, path.lastIndexOf(File.separatorChar)+1).trim();}
        if(path.startsWith("file:")){path=path.substring(5);}
        return path.trim();
    }
    @Override public String manufacturer(){return "marasm.VEGA";}
    public void connected()
    {
        PPC.connect(new Variable(ctrlPort), this);
        PPC.connect(new Variable(colorPort), this);
        PPC.connect(new Variable(pixelPort), this);
        PPC.connect(new Variable(linePort), this);
        PPC.connect(new Variable(rectPort), this);
        PPC.connect(new Variable(memPort), this);
        gui=new VEGA_GUI();
        new Thread()
        {
            @Override
            public void run() {
                while (true)
                {synchronized (color){
                    retrace(color);
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }}
            }
        }.start();
    }
    @Override
    public Variable in(Variable port)
    {
        switch (port.toString())
        {
            case ctrlPort:
                return ctrlIn();
            case colorPort:
                return color;
            case pixelPort:
            case linePort:
            case rectPort:
            case memPort:
                return new Variable(0);
        }
        return new Variable();
    }
    ArrayList<Variable> pixelBuf = new ArrayList<Variable>();
    ArrayList<Variable> lineBuf = new ArrayList<Variable>();
    ArrayList<Variable> rectBuf = new ArrayList<Variable>();
    ArrayList<Variable> memBuf = new ArrayList<Variable>();
    @Override
    public void out(Variable port,Variable data)
    {
        switch (port.toString())
        {
            case ctrlPort:
                ctrlOut(data);return;
            case colorPort:
                synchronized (color) {
                    retrace(color);
                    color = data;
                }
                break;
            case pixelPort:
                synchronized (pixelBuf) {
                    pixelBuf.add(data);
                }
                break;
            case linePort:
                synchronized (lineBuf) {
                    lineBuf.add(data);
                }
                break;
            case rectPort:
                synchronized (rectBuf) {
                    rectBuf.add(data);
                }
                break;
            case memPort:
                synchronized (memBuf) {
                    memBuf.add(data);
                }
                break;
        }
    }
    void retrace(Variable color)
    {
        synchronized (pixelBuf){
            while(pixelBuf.size()>1)
            {
                int x,y;
                x=pixelBuf.remove(0).intValue();
                y=pixelBuf.remove(0).intValue();
                gui.screen.putPixel(x,y,color);
            }
        }
        synchronized (rectBuf){
            while(rectBuf.size()>3)
            {
                int x,y,w,h;
                x=rectBuf.remove(0).intValue();
                y=rectBuf.remove(0).intValue();
                w=rectBuf.remove(0).intValue();
                h=rectBuf.remove(0).intValue();
                gui.screen.drawRect(x,y,w,h,color);
            }
        }
        synchronized (lineBuf){
            while (lineBuf.size()>4)
            {
                int x1=lineBuf.remove(0).intValue();
                int y1=lineBuf.remove(0).intValue();
                int x2=lineBuf.remove(0).intValue();
                int y2=lineBuf.remove(0).intValue();
                int width=lineBuf.remove(0).intValue();
                gui.screen.drawLine(x1,y1,x2,y2,width,color);
            }
        }
        synchronized (memBuf){
            while(memBuf.size()>3)
            {
                int x=memBuf.remove(0).intValue();
                int y=memBuf.remove(0).intValue();
                Variable memPointer=memBuf.remove(0);
                RAM ram=RAM.getInstance();
                int w=ram._load(memPointer).intValue();
                int h=ram._load(memPointer.add(new Variable(1))).intValue();
                memPointer=memPointer.add(new Variable(2));
                for(int _x=0;_x<w;_x++)
                {
                    for(int _y=0;_y<h;_y++)
                    {
                        gui.screen.putPixel(x+_x,y+_y,ram._load(memPointer));
                        memPointer=memPointer.add(new Variable(1));
                    }
                }
            }
        }
    }
    static public void main(String args[]){System.out.println("This is just an mvm device!");}

}