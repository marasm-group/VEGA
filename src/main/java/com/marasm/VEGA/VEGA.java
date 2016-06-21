package com.marasm.VEGA;

import com.marasm.ppc.*;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


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
                    try {
                        retrace();
                        sleep(25);
                    } catch (Exception e) {
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
    Queue<Operation> buf = new LinkedBlockingQueue<>();
    Operation colorOp = null, pixelOp = null, lineOp = null, rectOp = null, memOp = null;
    @Override
    public void out(Variable port,Variable data)
    {
        switch (port.toString())
        {
            case ctrlPort:
                ctrlOut(data);
                return;
            case colorPort:
                colorOp = new Operation(port,1);
                colorOp.args[0] = data;
                buf.offer(colorOp);
                break;
            case pixelPort:
                if(pixelOp == null)
                {
                    pixelOp = new Operation(port,2);
                    pixelOp.args[0] = data;
                }
                else
                {
                    pixelOp.args[1] = data;
                    buf.offer(pixelOp);
                    pixelOp = null;
                }
                break;
            case linePort:
                if(lineOp == null)
                {
                    lineOp = new Operation(port,5);
                    lineOp.args[0] = data;
                    lineOp.args_count++;
                }
                else
                {
                    if(lineOp.args_count >= 4)
                    {
                        lineOp.args[4] = data;
                        buf.offer(lineOp);
                        lineOp = null;
                    }
                    else
                    {
                        lineOp.args[lineOp.args_count] = data;
                        lineOp.args_count++;
                    }
                }
                break;
            case rectPort:
                if(rectOp == null)
                {
                    rectOp = new Operation(port,4);
                    rectOp.args[0] = data;
                    rectOp.args_count++;
                }
                else
                {
                    if(rectOp.args_count >= 3)
                    {
                        rectOp.args[3] = data;
                        buf.offer(rectOp);
                        rectOp = null;
                    }
                    else
                    {
                        rectOp.args[rectOp.args_count] = data;
                        rectOp.args_count++;
                    }
                }
                break;
            case memPort:
                if(memOp == null)
                {
                    memOp = new Operation(port,3);
                    memOp.args[0] = data;
                    memOp.args_count++;
                }
                else
                {
                    if(memOp.args_count >= 2)
                    {
                        memOp.args[3] = data;
                        buf.offer(memOp);
                        memOp = null;
                    }
                    else
                    {
                        memOp.args[memOp.args_count] = data;
                        memOp.args_count++;
                    }
                }
                break;
        }
    }
    long prevTime = 1;
    public long frameTime = 1;
    public long retraceTime = 1;
    void retrace()
    {
        frameTime = System.currentTimeMillis() - prevTime;
        prevTime = System.currentTimeMillis();
        while (true)
        {
            Operation op = buf.poll();
            if (op == null)
            {
                break;
            }
            int x, y, w, h;
            switch (op.opcode.toString())
            {
                case colorPort:
                    color = op.args[0];
                    break;
                case pixelPort:
                    x = op.args[0].intValue();
                    y = op.args[1].intValue();
                    gui.screen.putPixel(x, y, color);
                    break;
                case linePort:
                    int x1 = op.args[0].intValue();
                    int y1 = op.args[1].intValue();
                    int x2 = op.args[2].intValue();
                    int y2 = op.args[3].intValue();
                    int width = op.args[4].intValue();
                    gui.screen.drawLine(x1, y1, x2, y2, width, color);
                    break;
                case rectPort:
                    x = op.args[0].intValue();
                    y = op.args[1].intValue();
                    w = op.args[2].intValue();
                    h = op.args[3].intValue();
                    gui.screen.drawRect(x, y, w, h, color);
                    break;
                case memPort:
                    x = op.args[0].intValue();
                    y = op.args[1].intValue();
                    Variable memPointer = op.args[2];
                    RAM ram = RAM.getInstance();
                    w = ram._load(memPointer).intValue();
                    h = ram._load(memPointer.add(new Variable(1))).intValue();
                    memPointer = memPointer.add(new Variable(2));
                    for (int _x = 0; _x < w; _x++)
                    {
                        for (int _y = 0; _y < h; _y++)
                        {
                            gui.screen.putPixel(x + _x, y + _y, ram._load(memPointer));
                            memPointer = memPointer.add(new Variable(1));
                        }
                    }
                    break;
            }
        }
        retraceTime = System.currentTimeMillis() - prevTime;
    }
    static public void main(String args[]){System.out.println("This is just an mvm device!");}
    class Operation
    {
        Variable opcode;
        Variable[] args;
        int args_count = 0;
        Operation(Variable code,int argsCount)
        {
            opcode = code;
            args = new Variable[argsCount];
        }
    }
}