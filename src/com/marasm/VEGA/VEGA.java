package com.marasm.VEGA;

import com.marasm.ppc.CTRL;
import com.marasm.ppc.PPC;
import com.marasm.ppc.PPCDevice;
import com.marasm.ppc.Variable;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by sr3u on 08.10.2015.
 */

public class VEGA extends PPCDevice
{
    public final String ctrlPort="127.0";
    public final String dataPort="127.1";
    private VEGA_GUI gui;
    private GPU func=GPU.NOP;
    private Variable color=new Variable();
    private int bufsize=0;
    private ArrayList<Variable>buf=new ArrayList<>();
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
        PPC.connect(new Variable(dataPort), this);
        gui=new VEGA_GUI();
    }
    @Override
    public Variable in(Variable port)
    {
        switch (port.toString())
        {
            case ctrlPort:
                return ctrlIn();
            case dataPort:
                return new Variable();
        }
        return new Variable();
    }
    Variable v2=new Variable(2);
    @Override
    public void out(Variable port,Variable data)
    {
        switch (port.toString())
        {
            case ctrlPort:
                if(data.isEqual(CTRL.NOP)){vegaCtrl(data);ctrlOut(data);return;}
                if(data.isBigger(v2)){vegaCtrl(data);return;}
                if(data.isEqual(v2)){vegaCtrl(data);return;}
                ctrlOut(data);return;
            case dataPort:
                buf.add(data);
                if(buf.size()>=bufsize)
                {
                    execCMD();
                    buf=new ArrayList<>();
                }
                return;
        }
    }
    enum GPU
    {
        NOP,setColor,
        putPixel,
    }
    public static final String nop="0";
    public static final String setcolor="2";
    public static final String putpixel="2.1";

    private void vegaCtrl(Variable cmd)
    {
        switch (cmd.toString())
        {
            case nop:
                func=GPU.NOP;return;
            case setcolor:
                func=GPU.setColor;
                bufsize=1;
                return;
            case putpixel:
                bufsize=2;
                func=GPU.putPixel;return;
            default:
                return;
        }
    }
    public void execCMD()
    {
        switch (func)
        {
            case NOP:return;
            case putPixel:
                gui.screen.putPixel(buf.get(0).intValue(),buf.get(1).intValue(),color);
                return;
            case setColor:
                color=buf.get(0);
                return;
            default:return;
        }
    }
    static public void main(String args[]){System.out.println("This is just an mvm device!");}
}